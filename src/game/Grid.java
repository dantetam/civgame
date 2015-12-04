package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import menugame.Tutorial;
import units.Caravan;
import units.City;
import data.EntityData;
import data.Field;

public class Grid {

	private Tile[][] tiles;
	public int rows, cols;
	public Civilization[] civs;
	public boolean[][] verticalRivers;
	public boolean[][] horizontalRivers;

	public Pathfinder pathFinder;
	//public Civilization playerCiv;
	//Player's civilization will always be the first

	public final int aggroDistance = 500;
	public boolean won = false;

	public static int difficultyLevel;

	//Keep track of barbarian civs
	public static int barbarians;

	//Ensure that random numbers are the same (i.e. seeded)
	public Random rand;

	//Handle combat scenarios in the context of this grid
	public ConflictSystem conflictSystem;

	public Grid(String playerCiv, double[][] terrain, int[][] biomes, int[][] resources, int[][] fields, int numCivs, int numCityStates, int difficultyLevel, int numBarbarians, int cutoff, long seed)
	{
		rand = new Random(seed);
		conflictSystem = new ConflictSystem(this);
		civs = new Civilization[numCivs+numCityStates+numBarbarians];
		barbarians = numCivs + numCityStates;
		this.difficultyLevel = difficultyLevel;
		tiles = new Tile[terrain.length][terrain[0].length];
		rows = tiles.length; cols = tiles[0].length;
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				int hill = 0;
				double random = rand.nextDouble();
				//Assign a shape
				if (random < 0.03)
				{
					hill = 2;
				}
				else if (random < 0.2)
				{
					hill = 1;
				}
				boolean forest = false;
				if (biomes[r][c] == 3)
				{
					forest = rand.nextDouble() < 0.1; //0.1
				}
				else if (biomes[r][c] >= 4 && biomes[r][c] <= 5)
				{
					forest = rand.nextDouble() < 0.15; //0.3
				}
				else if (biomes[r][c] == 6)
				{
					forest = rand.nextDouble() < 0.2; //0.45
				}
				if (terrain[r][c] >= cutoff)
				{
					//2-4 fields
					tiles[r][c] = new Tile(this,"Land",(int)terrain[r][c],biomes[r][c],hill,resources[r][c],forest,r,c,(int)(Math.random()*3)+2); 
					if (random > 0.98)
						addUnit(EntityData.get("Ruins"), null, r, c);
				}
				else
				{
					tiles[r][c] = new Tile(this,"Sea",(int)terrain[r][c],-1,0,resources[r][c],forest,r,c,0);
					if (random > 0.995)
						addUnit(EntityData.get("Ruins"), null, r, c);
				}
			}
		}
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				tiles[r][c].maxFields = fields[r][c];
				if (tiles[r][c].biome == -1 && adjacentLand(r,c).size() > 0) //Shore check i.e. borders a coast
					tiles[r][c].maxFields = 1;
				double random = rand.nextDouble(); int n = 0;
				if (random < 0.04)
					n = 4;
				else if (random < 0.1)
					n = 3;
				else if (random < 0.2)
					n = 2;
				else if (random < 0.34)
					n = 1;
				else 
					n = 0;
				for (int i = 0; i < n; i++)
				{
					if (n > tiles[r][c].maxFields) break;
					Field f = EntityData.getField("TestField");
					tiles[r][c].fields.add(f);
					//city.location.fields.add(f);
					f.owner = null;
					double random2 = rand.nextDouble();
					if (random2 < 0.02)
						f.status = 3;
					else if (random2 < 0.1)
						f.status = 2;
					else if (random2 < 0.2)
						f.status = 1;
					else
						f.status = 0;
					f.autonomy = 0.5;
				}
			}
		}

		ArrayList<GameEntity> settlers = new ArrayList<GameEntity>();
		for (int i = 0; i < numCivs; i++)
		{
			/*Civilization civ = new Civilization("Civilization " + Double.toString(
					Math.floor(Math.sqrt(System.currentTimeMillis()*rand.nextDouble()))
					));*/
			Civilization civ;
			if (i == 0)
			{
				civ = new Civilization(EntityData.civs.get(playerCiv));
				EntityData.civs.remove(playerCiv);
			}
			else
			{
				//Get another random civ
				Set<String> keys = EntityData.civs.keySet();
				String key = (String)keys.toArray()[(int)(Math.random()*keys.size())];
				civ = new Civilization(EntityData.civs.get(key));
				//Remove it from the available list
				EntityData.civs.remove(key);
			}
			/*Civilization civ = new Civilization("Civilization " + Double.toString(
					Math.floor(Math.sqrt(System.currentTimeMillis()*rand.nextDouble()))
					),new ArrayList<String>(),255,255,255);*/
			/*civ.r = (float)(rand.nextDouble()*255); civ.sR = civ.r;
			civ.g = (float)(rand.nextDouble()*255); civ.sG = civ.g;
			civ.b = (float)(rand.nextDouble()*255); civ.sB = civ.b;*/
			civ.sR = civ.r;
			civ.sG = civ.g;
			civ.sB = civ.b;
			civ.revealed = new int[terrain.length][terrain[0].length];

			civ.opinions = new int[numCivs + numCityStates + numBarbarians];
			civs[i] = civ;
			civ.id = i;

			switch (difficultyLevel)
			{
			case 1:
				civ.war -= 0.3;
				civ.peace += 0.2;
				break;
			case 2:
				civ.war -= 0.15;
				civ.peace += 0.2;
				break;
			case 3:
				//personalities remain the same
				break;
			case 4:
				civ.war += 0.15;
				break;
			case 5:
				civ.war += 0.3;
				civ.peace -= 0.2;
				break;
			default: System.out.println("Invalid difficulty: " + difficultyLevel); break;
			}
			civ.war = Math.min(1,civ.war);
			civ.peace = Math.min(1,civ.peace);
			civ.war = Math.max(0,civ.war);
			civ.peace = Math.max(0,civ.peace);

			//This will bias but not force settlers to stay away from each other
			int r,c; float dist = 30;
			while (true)
			{
				r = (int)(rand.nextDouble()*tiles.length);
				c = (int)(rand.nextDouble()*tiles[0].length);
				Tile t = getTile(r,c);
				if (t.biome != -1)
				{
					//4:33 AM, there's probably a better way to do this
					boolean valid = true;
					for (int j = 0; j < settlers.size(); j++)
					{
						GameEntity s = settlers.get(j); 
						if (t.dist(s.location) > dist)
							continue;
						else
						{
							valid = false; break;
						}
					}
					if (valid)
						break;
					//if (dist > 6)
					dist -= 1; 
				}
			}
			/*Tile t = findIsolated();
			int r = t.row, c = t.col;*/
			//Test out giving a civilization land and a unit 
			//with proper encapsulation
			//addTile(civs[i], tiles[r][c]);

			BaseEntity en = EntityData.get("Settler");
			settlers.add((GameEntity)en);
			/*if (i == 0) //Give extra settlers to the player as a test
				for (int j = 0; j < 4; j++)
					{
						int nr,nc;
						while (true)
						{
							nr = (int)(rand.nextDouble()*tiles.length);
							nc = (int)(rand.nextDouble()*tiles[0].length);
							if (getTile(nr,nc).biome != -1) break;
						}
						addUnit(EntityData.get("Settler"),civs[i],nr,nc);
					}*/
			addUnit(en,civs[i],r,c);
			addUnit(EntityData.get("Warrior"),civs[i],r,c);
			civ.techTree.researched("Civilization").unlockForCiv(civ);

			if (i == numCivs - 1) break;
		}
		for (int i = numCivs; i < numCivs + numCityStates; i++)
		{
			/*CityState civ = new CityState("City State " + Double.toString(
					Math.floor(Math.sqrt(System.currentTimeMillis()*rand.nextDouble()))
					));*/
			//Get another random civ
			Set<String> keys = EntityData.cityStates.keySet();
			String key = (String)keys.toArray()[(int)(Math.random()*keys.size())];
			CityState civ = new CityState(EntityData.cityStates.get(key));
			//Remove it from the available list
			EntityData.civs.remove(key);

			civ.r = (float)(rand.nextDouble()*255); civ.sR = 255;
			civ.g = (float)(rand.nextDouble()*255); civ.sG = 255;
			civ.b = (float)(rand.nextDouble()*255); civ.sB = 255;
			civ.revealed = new int[terrain.length][terrain[0].length];
			civ.opinions = new int[numCivs + numCityStates + numBarbarians];
			civs[i] = civ;
			civ.id = i;

			int r,c;
			do
			{
				r = (int)(rand.nextDouble()*tiles.length);
				c = (int)(rand.nextDouble()*tiles[0].length);
			} while (tiles[r][c].type.equals("Sea"));

			/*Tile t = findIsolated();
			int r = t.row, c = t.col;*/

			addUnit(EntityData.get("Settler"),civs[i],r,c);
			addUnit(EntityData.get("Warrior"),civs[i],r,c);
			civ.techTree.researched("Civilization").unlockForCiv(civ);
		}
		//Barbarian state(s)
		for (int i = numCivs + numCityStates; i < civs.length; i++)
		{
			//Civilization civ = new Civilization("Barbarians");
			Civilization civ = new Civilization("Barbarians"+(i-numCivs-numCityStates+1),new ArrayList<String>(),0,0,0,1,0,0);
			civ.r = 50; civ.sR = 0;
			civ.g = 50; civ.sG = 255;
			civ.b = 50; civ.sB = 0;
			civ.revealed = new int[terrain.length][terrain[0].length];
			civ.opinions = new int[numCivs + numCityStates + numBarbarians];
			civs[i] = civ;
			civ.id = i;

			/*int r,c;
			do
			{
				r = (int)(rand.nextDouble()*tiles.length);
				c = (int)(rand.nextDouble()*tiles[0].length);
			} while (tiles[r][c].biome == -1);*/

			/*for (int j = 0; j < 3; j++)
			{
				addUnit(EntityData.get("Settler"),civ,r,c);
			}*/
			civ.techTree.researched("Civilization").unlockForCiv(civ);

			//Declare war on everyone
			//Civilization class takes care of multiple declarations of war
			for (int j = 0; j < i; j++)
			{
				civ.war(civs[j]);
			}
		}
		//Reset civilizations
		EntityData.setupCivBonuses();
		//makeRivers(terrain);
		pathFinder = new Pathfinder(this);
	}

	public City nearestCivCity(Civilization civ, int r, int c)
	{
		City city = null; double dist = -1;
		for (City candidate: civ.cities)
		{
			double d = candidate.location.dist(r,c);
			if (d < dist || city == null)
			{
				city = candidate;
				dist = d;
			}
		}
		return city;
	}
	
	public Tile findIsolated()
	{
		ArrayList<GameEntity> all = new ArrayList<GameEntity>();
		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < cols; c++)
			{
				if (tiles[r][c].occupants.size() > 0)
					all.add(tiles[r][c].occupants.get(0));
			}
		}
		int r = 0, c = 0, maxDist = 15;
		out:
			while (true)
			{
				do 
				{
					r = (int)(Math.random()*rows); c = (int)(Math.random()*cols);
				} while (tiles[r][c].biome == -1);
				for (int i = 0; i < all.size(); i++)
				{
					if (all.get(i).location.dist(tiles[r][c]) < maxDist)
						break;
					if (i == all.size() - 1) break out;
				}
				maxDist--;
			}
		return tiles[r][c];
	}

	public void moveBy(BaseEntity en, int rDis, int cDis)
	{
		int r = en.location.row; int c = en.location.col;
		if (r+rDis >= 0 && r+rDis < tiles.length && c+cDis >= 0 && c+cDis < tiles.length)
		{
			tiles[r][c].occupants.remove(en);
			//en.location = tiles[r+rDis][c+cDis];
			//en.location.addUnit(en);
			addUnit(en, en.owner, r+rDis, c+cDis);
		}
	}

	public void moveTo(BaseEntity en, int r, int c)
	{
		tiles[en.location.row][en.location.col].occupants.remove(en);
		if (en instanceof TileEntity && en.equals(en.location.improvement))
			tiles[en.location.row][en.location.col].improvement = null;
		en.location = tiles[r][c];
		//en.location.addUnit(en);
		addUnit(en, en.owner, r, c);
	}

	public BaseEntity addUnit(BaseEntity en, Civilization civ, int r, int c)
	{
		en.owner = civ;
		en.location = tiles[r][c];
		tiles[r][c].addUnit(en);
		if (civ != null)
		{
			if (en instanceof GameEntity)
				if (!civ.units.contains(en))
					civ.units.add((GameEntity)en);
				else if (en instanceof TileEntity)
					if (civ.improvements.contains(en))
						civ.improvements.add((TileEntity)en);
			en.reveal();
		}
		return en;
	}

	public void removeUnit(BaseEntity en)
	{
		if (en instanceof GameEntity)
		{
			if (en.name.equals("Caravan"))
				((Caravan)en).endRoute();
			tiles[en.location.row][en.location.col].occupants.remove(en);
			en.owner.units.remove((GameEntity)en);
		}
		else if (en instanceof TileEntity)
		{
			tiles[en.location.row][en.location.col].improvement = null;
			if (en.owner != null) //In the case that the unit being removed is ruins
				en.owner.improvements.remove((TileEntity)en);
		}
		en.location = null;
		en.owner = null;
		en = null;
	}

	public void addTile(Civilization civ, Tile tile)
	{
		tile.owner = civ;
		//civ.tiles.add(tile);
	}

	public boolean stackAttack(ArrayList<GameEntity> attacker, ArrayList<GameEntity> defender)
	{
		if (attacker.size() == 0) return false;
		if (defender.size() == 0) return true;
		int iDef = 0;
		for (int i = 0; i < attacker.size(); i++)
		{
			//attacker.get(i).attack(conflictSystem.attack(attacker.get(i), defender.get(iDef)), defender.get(iDef), r, c);
			System.out.println(defender.get(iDef).owner);
			attacker.get(i).attackAndDamage(defender.get(iDef));
			//Don't attack defenders that are dead
			for (int j = 0; j < defender.size(); j++)
			{
				if (defender.get(j).health <= 0 || defender.get(j).owner == null)
				{
					defender.remove(j);
					j--;
					iDef--;
				}
			}
			//End the attack and return true
			if (defender.size() == 0) return true;
			if (++iDef >= defender.size())
				iDef = 0;
		}
		//Some defenders are still alive, return that the attack has 'failed' i.e. no passage
		return false;
	}

	public GameEntity hasEnemy(GameEntity attacker, int r, int c)
	{
		if (getTile(r,c) != null)
		{
			for (int i = 0; i < tiles[r][c].occupants.size(); i++)
			{
				GameEntity occupant = tiles[r][c].occupants.get(i);
				if (occupant.owner.isWar(attacker.owner))
					return occupant;
			}
		}
		return null;
	}

	public Tile nearestFriendly(Civilization civ, int r0, int c0)
	{
		Tile candidate = null, loc = getTile(r0,c0);
		int sight = 3;
		for (int r = r0 - sight; r <= r0 + sight; r++)
		{
			for (int c = c0 - sight; c <= c0 + sight; c++)
			{
				Tile t = getTile(r,c);
				if (t != null)
					if (t.biome != -1 && (t.owner == null || civ.isWar(t.owner) || civ.isOpenBorder(t.owner) || civ.equals(t.owner)))
					{
						if (candidate == null) candidate = t;
						else if (loc.dist(t) < loc.dist(candidate))
						{
							candidate = t;
						}
					}
			}
		}
		return candidate;
	}

	public void setupCivs()
	{

	}

	public void revealPlayer()
	{
		Civilization civ = civs[0];
		for (int r = 0; r < civ.revealed.length; r++)
			for (int c = 0; c < civ.revealed[0].length; c++)
				civ.revealed[r][c] = 1;
	}

	//Check if a tile is bordered by a river
	public boolean irrigated(int r, int c)
	{
		boolean temp = false;
		if (c > 0)
		{
			temp = temp || verticalRivers[r][c-1];
		}
		if (c < cols - 1)
		{
			temp = temp || verticalRivers[r][c];
		}
		if (r > 0)
		{
			temp = temp || horizontalRivers[r-1][c];
		}
		if (r < rows - 1)
		{
			temp = temp || horizontalRivers[r][c];
		}
		return temp;
	}

	public ArrayList<Tile> adjacent(int r, int c)
	{
		ArrayList<Tile> temp = new ArrayList<Tile>();
		if (getTile(r+1,c) != null) {temp.add(getTile(r+1,c));} 
		if (getTile(r-1,c) != null) {temp.add(getTile(r-1,c));} 
		if (getTile(r,c-1) != null) {temp.add(getTile(r,c-1));} 
		if (getTile(r,c+1) != null) {temp.add(getTile(r,c+1));} 
		if (getTile(r+1,c+1) != null) {temp.add(getTile(r+1,c+1));} 
		if (getTile(r+1,c-1) != null) {temp.add(getTile(r+1,c-1));} 
		if (getTile(r-1,c+1) != null) {temp.add(getTile(r-1,c+1));} 
		if (getTile(r-1,c-1) != null) {temp.add(getTile(r-1,c-1));} 
		return temp;
	}

	//Check if a tile borders the sea
	public ArrayList<Tile> coastal(int r, int c)
	{
		ArrayList<Tile> candidates = adjacent(r, c);
		for (int i = candidates.size() - 1; i >= 0; i--)
			if (candidates.get(i).biome != -1)
				candidates.remove(i);
		return candidates;
	}

	public ArrayList<Tile> adjacentLand(int r, int c)
	{
		ArrayList<Tile> candidates = adjacent(r, c);
		for (int i = candidates.size() - 1; i >= 0; i--)
			if (candidates.get(i).biome == -1)
				candidates.remove(i);
		return candidates;
	}

	public Tile getTile(int r, int c)
	{
		if (r >= 0 && r < tiles.length && c >= 0 && c < tiles[0].length)
		{
			return tiles[r][c];
		}
		return null;
	}

	//Return the percentage of the world explored (do not count sea tiles)
	public double explored(Civilization civ)
	{
		double n = 0, land = 0;
		for (int r = 0; r < civ.revealed.length; r++)
		{
			for (int c = 0; c < civ.revealed[0].length; c++)
			{
				Tile t = getTile(r,c);
				if (t.biome != -1)
				{
					land++;
					if (civ.revealed[r][c] != 0)
						n++;
				}
			}
		}
		return n/land;
	}
	
	//Return best unimproved tiles of a city, pre-calculate optimal improvements
	public Tile bestToImprove(City c)
	{
		//ArrayList<TileEntity> imprTiles = new ArrayList<TileEntity>();
		Tile bestestTile = null; int bestestSum = -1;
		for (Tile t: c.land)
		{
			if (t.improvement != null && t.improvement.name.equals("City"))
				continue;
			//Find the best improvement for this individual tile t
			int bestSum = -1;
			for (String impr: c.owner.techTree.allowedTileImprovements)
			{
				if (!EntityData.allowedTileImprovement(t, impr)) continue;
				double[] yieldBefore = City.staticEval(t), yieldAfter = City.staticEval(t, impr);
				int sum = 0;
				for (int i = 0; i < yieldBefore.length; i++)
					sum += yieldAfter[i] - yieldBefore[i];
				if (sum > bestSum)
					bestSum = sum;
			}
			//See if this tile's best is the best overall of every piece of the city's land
			if (bestSum > bestestSum)
			{
				bestestTile = t;
				bestestSum = bestSum;
			}
		}
		return bestestTile;
	}
	
	//Return the best city scores factoring in previous settlements
	public Tile returnBestCityScoresMod(int settlerR, int settlerC, int capitalR, int capitalC, double distBias, int maxDist)
	{
		evalBefore();
		int[][] cityScores = new int[rows][cols];
		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < cols; c++)
			{
				int dist = (int)Math.sqrt(Math.pow(r-settlerR,2) + Math.pow(c-settlerC,2));
				int capitalDist = (int)Math.sqrt(Math.pow(r-settlerR,2) + Math.pow(c-settlerC,2));
				if (dist > maxDist || getTile(r,c).owner != null || getTile(r,c).biome == -1)
					cityScores[r][c] = -9999;
				else
				{
					cityScores[r][c] = returnCityScoreNoOwner(r,c)*5 - (int)(distBias*dist)*2 - (int)(distBias*capitalDist);
					/*out:
						for (int rr = r - 2; rr <= r + 2; rr++)
						{
							for (int cc = c - 2; cc <= c + 2; cc++)
							{
								Tile t = getTile(rr,cc);
								if (t == null) continue;
								if (t.improvement != null)
								{
									if (t.improvement instanceof City)
									{
										cityScores[r][c] = 0;
										break out;
									}
									else
										cityScores[r][c]--;
								}
								else
								{
									if (t.owner == null)
										cityScores[r][c] += 4;
									else
										cityScores[r][c] += 2;
								}
							}
						}*/
				}
			}
		}
		Tile candidate = tiles[0][0];
		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < cols; c++)
			{
				if (cityScores[r][c] > cityScores[candidate.row][candidate.col])
					candidate = getTile(r,c); 
			}
		}
		return candidate;
	}

	public Tile[] returnBestCityScores(int settlerR, int settlerC, double distBias)
	{
		evalBefore();
		int[][] cityScores = new int[rows][cols];
		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < cols; c++)
			{
				int dist = (int)Math.sqrt(Math.pow(r-settlerR,2) + Math.pow(c-settlerC,2));
				if (dist > 10 || getTile(r,c).owner != null || getTile(r,c).biome == -1)
					cityScores[r][c] = -9999;
				else
					cityScores[r][c] = returnCityScoreNoOwner(r,c) - (int)(distBias*dist);
			}
		}
		Tile[] temp = new Tile[10];

		//There will be a more efficient algorithm here,
		//currently the number of calculations is 10*r*c

		//Find the ten most productive tiles in the game
		for (int i = 0; i < 10; i++)
		{
			Tile maxTile = tiles[0][0];
			for (int r = 0; r < rows; r++)
			{
				for (int c = 0; c < cols; c++)
				{
					//if (cityScores[r][c] <= 0) continue;
					if (cityScores[r][c] > cityScores[maxTile.row][maxTile.col])
					{
						maxTile = getTile(r,c);
					}
				}
			}
			cityScores[maxTile.row][maxTile.col] = 0;
			temp[i] = maxTile;
		}

		return temp;
	}

	//Get all the scores of all the tiles so they only have to be evaluated once
	private int[][] tileScores;
	private void evalBefore()
	{
		tileScores = new int[rows][cols]; 
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				double[] e = City.staticEval(getTile(i,j));
				//Give a little bias to food
				tileScores[i][j] = (int)(e[0]*1.2+e[1]+e[2]+e[3]);
			}
		}	
	}

	//Returns the composite of a city and its surrounding tiles,
	//not including tiles claimed by other cities
	private int returnCityScoreNoOwner(int r, int c)
	{
		if (getTile(r,c).owner != null) return 0;
		int score = 0;
		for (int i = r - 2; i <= r + 2; i++)
		{
			for (int j = c - 2; j <= c + 2; j++)
			{
				if (getTile(i,j) != null)
				{
					if (getTile(i,j).owner == null)
						score += tileScores[i][j];
				}
			}
		}
		return score;
	}

	//public Tile[][] getTiles() {return tiles;}

}
