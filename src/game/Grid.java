package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import units.City;
import data.EntityData;

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
	
	//Ensure that random numbers are the same
	public Random rand;
	
	public Grid(double[][] terrain, int[][] biomes, int[][] resources, int numCivs, int numCityStates, int cutoff, long seed)
	{
		rand = new Random(seed);
		civs = new Civilization[numCivs+numCityStates+1];
		tiles = new Tile[terrain.length][terrain[0].length];
		rows = tiles.length; cols = tiles[0].length;
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				int hill = 0;
				double random = rand.nextDouble();
				//Assign a shape
				if (random < 0.025)
				{
					hill = 2;
				}
				else if (random < 0.3)
				{
					hill = 1;
				}
				boolean forest = false;
				if (biomes[r][c] == 3)
				{
					forest = rand.nextDouble() < 0.02;
				}
				else if (biomes[r][c] >= 4 && biomes[r][c] <= 6)
				{
					forest = rand.nextDouble() < 0.15;
				}
				if (terrain[r][c] >= cutoff)
				{
					tiles[r][c] = new Tile(this,"Land",(int)terrain[r][c],biomes[r][c],hill,resources[r][c],forest,r,c);
					if (random > 0.98)
						addUnit(EntityData.get("Ruins"), null, r, c);
				}
				else
				{
					tiles[r][c] = new Tile(this,"Sea",(int)terrain[r][c],-1,0,resources[r][c],forest,r,c);
					if (random > 0.995)
						addUnit(EntityData.get("Ruins"), null, r, c);
				}
			}
		}
		for (int i = 0; i < numCivs; i++)
		{
			Civilization civ = new Civilization("Civilization " + Double.toString(
					Math.floor(Math.sqrt(System.currentTimeMillis()*rand.nextDouble()))
					));
			civ.r = (float)(rand.nextDouble()*255); civ.sR = civ.r;
			civ.g = (float)(rand.nextDouble()*255); civ.sG = civ.g;
			civ.b = (float)(rand.nextDouble()*255); civ.sB = civ.b;
			civ.revealed = new boolean[terrain.length][terrain[0].length];
			civ.opinions = new int[numCivs + numCityStates + 1];
			civs[i] = civ;
			civ.id = i;

			int r,c;
			do
			{
				r = (int)(rand.nextDouble()*tiles.length);
				c = (int)(rand.nextDouble()*tiles[0].length);
			} while (tiles[r][c].type.equals("Sea"));
			//Test out giving a civilization land and a unit 
			//with proper encapsulation
			//addTile(civs[i], tiles[r][c]);

			BaseEntity en = EntityData.get("Settler");
			if (i == 1)
			{
				for (int j = 0; j < 4; j++)
				{
					addUnit(EntityData.get("Settler"),civs[i],r,c);
				}
			}
			addUnit(en,civs[i],r,c);
			civ.techTree.researched("Civilization").unlockForCiv(civ);
		}
		for (int i = numCivs; i < numCivs + numCityStates; i++)
		{
			CityState civ = new CityState("City State " + Double.toString(
					Math.floor(Math.sqrt(System.currentTimeMillis()*rand.nextDouble()))
					));
			civ.r = (float)(rand.nextDouble()*255); civ.sR = 255;
			civ.g = (float)(rand.nextDouble()*255); civ.sG = 255;
			civ.b = (float)(rand.nextDouble()*255); civ.sB = 255;
			civ.revealed = new boolean[terrain.length][terrain[0].length];
			civ.opinions = new int[numCivs + numCityStates + 1];
			civs[i] = civ;
			civ.id = i;

			int r,c;
			do
			{
				r = (int)(rand.nextDouble()*tiles.length);
				c = (int)(rand.nextDouble()*tiles[0].length);
			} while (tiles[r][c].type.equals("Sea"));

			addUnit(EntityData.get("Settler"),civs[i],r,c);
			civ.techTree.researched("Civilization").unlockForCiv(civ);
		}
		//Barbarian state(s)
		//for (int i = 0; i < 1; i++)
		{
			Civilization civ = new Civilization("Barbarians");
			civ.r = 0; civ.sR = 0;
			civ.g = 0; civ.sG = 255;
			civ.b = 0; civ.sB = 0;
			civ.revealed = new boolean[terrain.length][terrain[0].length];
			civ.opinions = new int[numCivs + numCityStates + 1];
			civs[civs.length - 1] = civ;
			civ.id = numCivs + numCityStates;

			int r,c;
			do
			{
				r = (int)(rand.nextDouble()*tiles.length);
				c = (int)(rand.nextDouble()*tiles[0].length);
			} while (tiles[r][c].type.equals("Sea"));

			/*for (int j = 0; j < 3; j++)
			{
				addUnit(EntityData.get("Settler"),civ,r,c);
			}*/
			civ.techTree.researched("Civilization").unlockForCiv(civ);
			
			//Declare war on everyone
			for (int i = 0; i < civs.length - 1; i++)
			{
				civ.enemies.add(civs[i]);
				civs[i].enemies.add(civ);
			}
		}
		//makeRivers(terrain);
		pathFinder = new Pathfinder(this);
	}

	public void move(BaseEntity en, int rDis, int cDis)
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

	public GameEntity hasEnemy(GameEntity attacker, int r, int c)
	{
		if (getTile(r,c) != null)
		{
			for (int i = 0; i < tiles[r][c].occupants.size(); i++)
			{
				GameEntity occupant = tiles[r][c].occupants.get(i);
				if (occupant.owner.war(attacker.owner))
				{
					return occupant;
				}
			}
		}
		return null;
	}

	public void setupCivs()
	{

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
	public boolean coastal(int r, int c)
	{
		ArrayList<Tile> candidates = new ArrayList<Tile>();
		for (int i = 0; i < candidates.size(); i++)
		{
			if (candidates.get(i).biome == -1)
				return true;
		}
		return false;
	}

	public Tile getTile(int r, int c)
	{
		if (r >= 0 && r < tiles.length && c >= 0 && c < tiles[0].length)
		{
			return tiles[r][c];
		}
		return null;
	}

	public Tile[] returnBestCityScores(int settlerR, int settlerC)
	{
		evalBefore();
		int[][] cityScores = new int[rows][cols];
		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < cols; c++)
			{
				int dist = (int)Math.sqrt(Math.pow(r-settlerR,2) + Math.pow(c-settlerC,2));
				if (dist > 10 || getTile(r,c).owner != null || getTile(r,c).biome == -1)
					cityScores[r][c] = 0;
				else
					cityScores[r][c] = returnCityScoreNoOwner(r,c) - (int)(0.25*dist);
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
					if (cityScores[r][c] <= 0) continue;
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
				tileScores[i][j] = (int)(e[0]*1.1+e[1]+e[2]+e[3]);
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
