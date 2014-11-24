package game;

import java.util.ArrayList;

import units.City;
import data.EntityData;

//An entity that moves around the map i.e. a unit

public abstract class GameEntity extends BaseEntity {

	public ArrayList<Tile> queueTiles = new ArrayList<Tile>();
	public int action = 1, maxAction = 1;
	public boolean explorer = false; //For the AI only
	public int mode = 1; //0 non-violent, 1 melee, 2 ranged
	protected int[] previous = new int[2];
	public int sight = 2;
	
	public GameEntity(String name, float o, float d, float r)
	{
		super(name,o,d,r);
	}

	public GameEntity(GameEntity other)
	{
		super(other);
	}

	//public abstract String getName();

	public abstract void tick();
	public abstract void playerTick();
	public abstract void barbarianTick();

	public void waddle()
	{
		while (action > 0)
		{
			if (queueTiles.size() > 0)
			{
				//location.grid.moveTo(this, queueTiles.get(0).row, queueTiles.get(0).col);
				passiveWaddle(queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col);
				queueTiles.remove(queueTiles.size()-1);
			}
			else
			{
				int r = (int)(Math.random()*3) - 1;
				int c = (int)(Math.random()*3) - 1;
				passiveWaddle(r,c);
			}
		}
	}

	public void waddleInTerritory()
	{
		if (queueTiles.size() > 0)
		{
			//location.grid.moveTo(this, queueTiles.get(0).row, queueTiles.get(0).col);
			passiveWaddle(queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col);
			queueTiles.remove(queueTiles.size()-1);
		}
		else
		{
			while (action > 0)
			{
				int r,c;
				int trials = 0;
				while (true)
				{
					r = (int)(Math.random()*3) - 1;
					c = (int)(Math.random()*3) - 1;
					Tile t = location.grid.getTile(location.row + r, location.col + c);
					if (t != null)
						if (owner.equals(t.owner)) 
							break;
					trials++;
					if (trials >= 10)
					{
						r = (int)(Math.random()*3) - 1;
						c = (int)(Math.random()*3) - 1;
						break;
					}
				}
				passiveWaddle(r,c);
			}
		}
	}

	public boolean moved()
	{
		return !(previous[0] == location.row && previous[1] == location.col);
	}
	
	public void recordPos()
	{
		previous[0] = location.row;
		previous[1] = location.col;
	}
	
	public void waddleTo(int r, int c)
	{
		/*System.out.println("------");
		System.out.println(location.grid.getTile(location.row,location.col));
		System.out.println(location.grid.getTile(location.row+r,location.col+c));*/
		queueTiles.clear();
		if (location.grid.getTile(location.row+r,location.col+c) != null)
		{
			ArrayList<Tile> tiles = location.grid.pathFinder.findAdjustedPath(owner,location.row,location.col,location.row+r,location.col+c);
			/*if (owner.name.equals("Player"))
			{
				for (int i = 0; i < tiles.size(); i++)
				{
					System.out.println(tiles.get(i).row + " " + tiles.get(i).col);
				}
			}*/
			if (tiles != null)
			{
				if (tiles.size() > 0)
				{
					queueTiles = tiles;
				}
			}
		}
	}

	public boolean waddleToExact(int r, int c)
	{
		queueTiles.clear();
		if (location.grid.getTile(r,c) != null)
		{
			ArrayList<Tile> tiles = location.grid.pathFinder.findAdjustedPath(owner,location.row,location.col,r,c);
			if (tiles != null)
			{
				if (tiles.size() > 0)
				{
					queueTiles = tiles;
					return true;
				}
			}
		}
		return false;
	}
	
	//Calcualte a path to a tile that does not go through enemy territory
	public void calculateNewPath()
	{
		if (queueTiles.size() == 0) return;
		Tile t = queueTiles.get(0);
		if (!owner.isWar(t.owner) && !owner.isOpenBorder(t.owner) && !owner.equals(t.owner))
		{
			queueTiles.clear();
		}
		for (int i = 0; i < queueTiles.size(); i++)
		{
			if (!owner.isWar(queueTiles.get(i).owner) && !owner.isOpenBorder(queueTiles.get(i).owner) && !owner.equals(t.owner))
			{
				waddleToExact(t.row, t.col);
				return;
			}
		}
	}

	//Try to queue a certain set of tiles
	//If it works, return null, otherwise, return the "problem"
	public String playerWaddleToExact(int r, int c)
	{
		queueTiles.clear();
		Tile t = location.grid.getTile(r,c);
		if (sortie != null)
		{
			if (!sortie.land.contains(t))
			{
				return "You cannot use a sortie unit outside its city.";
			}
		}
		if (t != null)
		{
			if (t.owner == null)
			{
				waddleToExact(r,c);	
				return null;
			}
			else
			{
				//Allow the operation if they're at war
				if (owner.isWar(t.owner) || owner.isOpenBorder(t.owner) || owner.equals(t.owner))
				{
					waddleToExact(r,c);	
					return null;
				}
				else 
				{
					return "You do not have access. Declare war or request open borders.";
				}
			}
		}
		return "You cannot go to this tile.";
	}

	public void passiveWaddle(int r, int c)
	{
		GameEntity en = this;
		if (location.grid.getTile(en.location.row+r,en.location.col+c) != null)
		{
			if (location.grid.getTile(en.location.row+r,en.location.col+c).biome != -1)
			{
				GameEntity enemy = location.grid.hasEnemy(en,en.location.row+r,en.location.col+c);
				if (enemy == null)
				{
					location.grid.move(this, r, c);
					action--;
				}
			}
		}
	}

	//Waddles into the specified space and returns true if the unit is alive
	public boolean aggressiveWaddle(int r, int c)
	{
		GameEntity en = this;
		Tile t = location.grid.getTile(en.location.row+r,en.location.col+c);
		if (t != null)
		{
			//if (main.grid.getTile(en.location.row+r,en.location.col+c).owner == en.owner ||
			//main.grid.getTile(en.location.row+r,en.location.col+c).owner == null)
			if (t.biome != -1)
			{
				GameEntity enemy = location.grid.hasEnemy(en,en.location.row+r,en.location.col+c);
				if (enemy == null)
				{
					if (owner.isOpenBorder(t.owner) ||
							owner.isWar(t.owner) ||
							t.owner == null ||
							owner.equals(t.owner))
					{
						passiveWaddle(r,c);
					}
				}
				if (enemy != null)
				{
					queueTiles.clear(); //Solve some complex problems
					int[] damages;
					if (mode == 1)
						damages = location.grid.conflictSystem.attack(this, enemy);
					else
						damages = location.grid.conflictSystem.fire(this, enemy);
					if (enemy.health - damages[0] <= 0 && health - damages[1] <= 0) //Both may kill each other
					{
						if (health >= enemy.health)
						{
							location.grid.removeUnit(enemy);
							passiveWaddle(r,c);
							health = 1;
							return true;
						}
						else
						{
							enemy.health = 1;
							location.grid.removeUnit(this);
							return false;
						}
					}
					else if (enemy.health - damages[0] <= 0) //Killed the enemy
					{
						location.grid.removeUnit(enemy);
						passiveWaddle(r,c);
						health -= damages[1];
						return true;
					}
					else if (health - damages[1] <= 0) //Killed in an attack
					{
						location.grid.removeUnit(this);
						return false;
					}
					else //Damage to each other
					{
						enemy.health -= damages[0];
						health -= damages[1];
						return true;
					}
				}
			}
		}
		return true;
	}

	public boolean raze()
	{
		//System.out.println(location + " " + location.improvement);
		if (location.improvement != null)
		{		
			action--;
			//System.out.println(owner + " " + location.improvement.owner + " Name: " + location.improvement.id);
			if (location.improvement.owner == null && location.improvement.name.equals("Ruins"))
			{
				location.grid.removeUnit(location.improvement);
				owner.food += 10;
			}
			else if (owner.isWar(location.owner))
			{
				//System.out.println("takeover");
				if (location.improvement.name.equals("City"))
				{
					//System.out.println("takeovercity");
					City city = (City)location.improvement;
					city.queue = null;
					city.queueFood = 0;
					city.queueMetal = 0;
					city.adm = 0; city.art = 0; city.sci = 0;
					if (city.owner.capital != null)
					{
						if (city.equals(city.owner.capital))
						{
							city.owner.capital = null;
						}
					}
					for (int k = city.land.size() - 1; k >= 0; k--)
					{
						Tile t = city.land.get(k);
						if (t.improvement != null)
						{
							t.improvement.owner = owner;
							city.owner.improvements.remove(t.improvement);
						}
						t.owner = owner;
					}
					city.owner.cities.remove(city);
					if (city.owner.cities.size() > 0)
					{
						city.owner.capital = city.owner.cities.get(0);
					}
					city.owner = owner;
					city.takeover = 5;
					owner.cities.add(city);
					return true;
				}
				//Just in case
				//The first condition is not needed
				else if (!(location.improvement instanceof City))//owner.enemies.contains(en.location.improvement.owner)) 
				{
					location.grid.removeUnit(location.improvement);
					return true;
					//en.location.improvement = null;
				}
			}
		}
		return false;
	}

	public void explore()
	{
		queueTiles.clear();
		int r,c,trials = 0;
		while (true)
		{
			r = (int)(Math.random()*location.grid.rows);
			c = (int)(Math.random()*location.grid.cols);
			Tile t = location.grid.getTile(r,c); //guaranteed to exist. i think.
			if (t.biome != -1)
				if (t.owner == null && t.dist(location) > 20)
					break;
			trials++;
			if (trials > 10) break;
		}
		//System.out.println("Exploring " + id);
		waddleToExact(r,c);
	}
	
	public ArrayList<GameEntity> fireAtTargets()
	{
		ArrayList<GameEntity> temp = new ArrayList<GameEntity>();
		for (int r = location.row - range; r <= location.row + range; r++)
		{
			for (int c = location.col - range; c <= location.col + range; c++)
			{
				Tile t = location.grid.getTile(r, c);
				if (t.occupants.size() > 0)
				{
					for (int i = 0; i < t.occupants.size(); i++)
					{
						GameEntity en = t.occupants.get(i);
						if (owner.isWar(en.owner))
						{
							temp.add(en);
						}
					}
				}
			}
		}
		return temp;
	}
	
	public void fire(GameEntity target)
	{
		if (action > 0)
			action--;
		else
			return;
		queueTiles.clear(); //Solve some complex problems
		int[] damages = location.grid.conflictSystem.fire(this, target);
		target.health -= damages[0];
		System.out.println("Ranged damage: " + damages[0]);
		if (target.health <= 0)
		{
			location.grid.removeUnit(target);
		}
	}
	
	public Tile adjacentEnemy()
	{
		if (owner == null || location == null) return null;
		//this is not random
		if (location.grid.hasEnemy(this,location.row+1,location.col+1) != null) return location.grid.getTile(location.row+1,location.col+1);
		if (location.grid.hasEnemy(this,location.row+1,location.col) != null) return location.grid.getTile(location.row+1,location.col);
		if (location.grid.hasEnemy(this,location.row+1,location.col-1) != null) return location.grid.getTile(location.row+1,location.col-1);
		if (location.grid.hasEnemy(this,location.row-1,location.col+1) != null) return location.grid.getTile(location.row-1,location.col+1);
		if (location.grid.hasEnemy(this,location.row-1,location.col) != null) return location.grid.getTile(location.row-1,location.col);
		if (location.grid.hasEnemy(this,location.row-1,location.col-1) != null) return location.grid.getTile(location.row-1,location.col-1);
		if (location.grid.hasEnemy(this,location.row,location.col+1) != null) return location.grid.getTile(location.row,location.col+1);
		if (location.grid.hasEnemy(this,location.row,location.col-1) != null) return location.grid.getTile(location.row,location.col-1);
		return null;
	}

	public Tile nearestEnemyCity()
	{
		City nearest = null;
		/*System.out.println("********");
		System.out.println(owner);
		System.out.println(owner.enemies.size());*/
		ArrayList<Civilization> e = owner.enemies();
		if (e.size() > 0)
		{
			for (int i = 0; i < e.size(); i++)
			{
				for (int j = 0; j < e.get(i).cities.size(); j++)
				{
					City candidate = e.get(i).cities.get(j);
					if (!owner.revealed[candidate.location.row][candidate.location.col])
					{
						continue;
					}
					if (nearest != null)
					{
						if (candidate.location.dist(location) < nearest.location.dist(location)) 
							nearest = candidate;
					}
					else
					{
						nearest = candidate;
					}
				}
			}
		}
		if (nearest != null)
			return nearest.location;
		return null;
	}

	public Tile nearestAlliedCity()
	{
		City nearest = null;
		if (owner.cities.size() > 0)
		{
			for (int i = 0; i < owner.cities.size(); i++)
			{
				City candidate = owner.cities.get(i);
				/*if (!owner.revealed[candidate.location.row][candidate.location.col])
				{
					continue;
				}*/
				if (nearest != null)
				{
					if (candidate.location.dist(location) < nearest.location.dist(location)) nearest = candidate;
				}
				else
				{
					nearest = candidate;
				}
			}
		}
		if (nearest != null)
			return nearest.location;
		return null;
	}

	public void heal()
	{
		if (health < maxHealth)
		{
			health++;
			action--;
		}
	}
	
	//public void tick()
	{
		/*GameEntity en = this;
		if (name.equals("Worker") && queue != null)
		{
			queueTurns--;
			if (queueTurns <= 0)
			{
				location.grid.addUnit(EntityData.get(queue), owner, location.row, location.col);
				queueTurns = 0; //just to be sure
				queue = null;
			}
			return;
		}
		else if (name.equals("Worker"))
		{
			if (en.name.equals("Worker") && en.queue == null)
			{
				if (en.location.city != null && en.location.improvement == null)
				{
					City city = en.location.city;
					//Factor in the city later
					//if (city.location.owner.equals(owner)) //just in case
					{
						if (en.location.resource == 1 || en.location.resource == 2)
						{
							en.queueTurns = 6;
							en.queue = "Farm";
						}
						else if (en.location.resource == 10 || en.location.resource == 11)
						{

						}
						else if (en.location.resource >= 20 && en.location.resource <= 22)
						{
							en.queueTurns = 6;
							en.queue = "Mine";
						}
						else if (en.location.resource >= 30 && en.location.resource <= 30)
						{

						}
						if (en.location.biome >= 3 && en.location.biome <= 6)
						{
							if (location.grid.irrigated(location.row, location.col))
							{
								en.queueTurns = 6;
								en.queue = "Farm";
							}
						}
						else if (en.location.shape == 1)
						{
							en.queueTurns = 6;
							en.queue = "Mine";
						}
						else if (en.location.shape == 2)
						{
							if (en.location.biome >= 0 && en.location.biome <= 3)
							{
								en.queueTurns = 6;
								en.queue = "Mine";
							}
						}
					}
				}
			}
		}
		else if (en.name.equals("Settler"))
		{
			if (en.location.owner == null && Math.random() < 0.2)
			{
				//Make the city and set its surrounding tiles to the civilization's territory
				City city = (City)EntityData.get("City");
				city.owner = en.owner;
				city.owner.cities.add(city);
				location.grid.addUnit(city, en.owner, en.location.row, en.location.col);
				if (owner.cities.size() == 1)
				{
					city.capital = true;
				}
				for (int i = en.location.row - 2; i <= en.location.row + 2; i++)
				{
					for (int j = en.location.col - 2; j <= en.location.col + 2; j++)
					{
						if (i >= 0 && i < location.grid.rows && j >= 0 && j < location.grid.cols)
						{
							Tile t = location.grid.getTile(i,j);
							if (t != null)
							{
								if (t.owner == null)
								{
									t.city = city;
									city.land.add(t);
									location.grid.addTile(en.owner, t);
								}
								if (t.owner == city.owner && t.city == null)
								{
									t.city = city;
									city.land.add(t);
								}
							}
						}
					}
				}
				//Remove the settler
				location.grid.removeUnit(en);
				return;
			}
		}
		else if (name.equals("Work Boat") || name.equals("Galley"))
		{
			int r = (int)(Math.random()*3) - 1;
			int c = (int)(Math.random()*3) - 1;
			if (location.grid.getTile(en.location.row+r,en.location.col+c) != null)
			{
				if (location.grid.getTile(en.location.row+r,en.location.col+c).biome == -1)
				{
					//if (location.grid.getTile(en.location.row+r,en.location.col+c).improvement.name.equals("City"))
					if (name.equals("Galley"))
					{
						if (queue == null)
						{
							location.grid.move(en,r,c);
							//System.out.println(location.grid.getTile(en.location.row+r,en.location.col+c));
							//if (location == null) return;
							if (location.resource == 10 ||
									location.resource == 11)
							{
								en.queueTurns = 6;
								en.queue = "Fishing Boats";
							}
						}
						else
						{
							en.queueTurns--;
							if (queueTurns <= 0)
							{
								location.grid.addUnit(EntityData.get(queue), owner, location.row, location.col);
								queueTurns = 0;
								queue = null;
							}
						}
					}
					else
					{
						GameEntity enemy = location.grid.hasEnemy(this, location.row, location.col);
						if (enemy != null)
						{
							if (Math.random() < 0.6)
							{
								location.grid.removeUnit(enemy);
								location.grid.move(en,r,c);
							}
							else
							{
								location.grid.removeUnit(en);
								return;
							}
						}
						else
						{
							location.grid.move(en,r,c);
						}
					}
				}
			}
			return;
		}
		//if (!name.equals("Worker") || (name.equals("Worker") && queue == null))
		if (!name.equals("Work Boat"))
		{
			int r = (int)(Math.random()*3) - 1;
			int c = (int)(Math.random()*3) - 1;
			if (location.grid.getTile(en.location.row+r,en.location.col+c) != null)
			{
				//if (main.grid.getTile(en.location.row+r,en.location.col+c).owner == en.owner ||
				//main.grid.getTile(en.location.row+r,en.location.col+c).owner == null)
				if (location.grid.getTile(en.location.row+r,en.location.col+c).biome != -1)
				{
					GameEntity enemy = location.grid.hasEnemy(en,en.location.row+r,en.location.col+c);
					if (enemy != null)
					{
						if (en.name.equals("Warrior"))
						{
							if (enemy.name.equals("Warrior"))
							{
								if (Math.random() < 0.5)
								{
									location.grid.removeUnit(enemy);
									location.grid.move(en,r,c);
								}
								else
								{
									location.grid.removeUnit(en);
									return;
								}
							}
							else
							{
								location.grid.removeUnit(enemy);
								location.grid.move(en,r,c);
							}
						}
					}
					else
					{
						//en.tick();
						if ((name.equals("Settler") || name.equals("Worker")) && !owner.equals(location.grid.getTile(en.location.row+r,en.location.col+c).owner))
						{
							return;
						}
						if (en.queue == null)
							location.grid.move(en,r,c);
						if (en.location.improvement != null)
						{
							if (en.location.improvement.name.equals("City") && !en.owner.equals(en.location.improvement.owner) && en.name.equals("Warrior"))
							{
								//System.out.println("Destroyed");
								City city = (City)en.location.improvement;
								for (int k = city.land.size() - 1; k >= 0; k--)
								{
									Tile t = city.land.get(k);
									//if (t.equals(city.location)) continue;
									if (t.improvement != null)
									{
										if (!t.improvement.name.equals("City"))
											location.grid.removeUnit(t.improvement);
										t.improvement = null;
									}
									//city.owner.tiles.remove(t);
									t.owner = null;
									t.city = null;
									city.land.remove(k);
									//System.out.println("Destroyed");
									//en.owner.
									//t.owner = en.owner;
								}
								city.owner.cities.remove(city);
								location.grid.removeUnit(city);
								en.location.improvement = null;
								//city = null;
							}
						}
					}
				}
			}
		}*/
	}
	
	public GameEntity range(int n) {range = n; return this;}
	public GameEntity mode(int n) {mode = n; return this;}
	public GameEntity maxAction(int n) {maxAction = n; action = n; return this;}
	
}
