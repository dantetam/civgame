package game;

import java.util.ArrayList;

import units.City;
import data.EntityData;

//An entity that moves around the map i.e. a unit

public abstract class GameEntity extends BaseEntity {

	public ArrayList<Tile> queueTiles = new ArrayList<Tile>();
	public int action = 1, maxAction = 1;

	public GameEntity(String name)
	{
		super(name);
	}

	public GameEntity(GameEntity other)
	{
		super(other);
	}

	//public abstract String getName();

	public abstract void tick();
	public abstract void playerTick();

	public void waddle()
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

	public void waddleTo(int r, int c)
	{
		/*System.out.println("------");
		System.out.println(location.grid.getTile(location.row,location.col));
		System.out.println(location.grid.getTile(location.row+r,location.col+c));*/
		if (location.grid.getTile(location.row+r,location.col+c) != null)
		{
			ArrayList<Tile> tiles = location.grid.pathFinder.findAdjustedPath(location.row,location.col,location.row+r,location.col+c);
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
				}
			}
		}
	}

	//Waddles into the specified space and returns true if the unit is alive
	public boolean aggressiveWaddle(int r, int c)
	{
		GameEntity en = this;
		if (location.grid.getTile(en.location.row+r,en.location.col+c) != null)
		{
			//if (main.grid.getTile(en.location.row+r,en.location.col+c).owner == en.owner ||
			//main.grid.getTile(en.location.row+r,en.location.col+c).owner == null)
			if (location.grid.getTile(en.location.row+r,en.location.col+c).biome != -1)
			{
				GameEntity enemy = location.grid.hasEnemy(en,en.location.row+r,en.location.col+c);
				if (enemy == null)
				{
					/*if (en.location.improvement != null && !owner.equals(location.improvement.owner))//owner.enemies.contains(en.location.owner))
					{
						System.out.println("takeover2");
						if (en.location.improvement.name.equals("City"))
						{
							System.out.println("takeover");
							City city = (City)en.location.improvement;
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
							System.out.println("_________");
							System.out.println(city.owner);
							System.out.println(owner);
						}
						//Just in case
						//The first condition is not needed
						else if (!(en.location.improvement instanceof City) && !owner.equals(location.improvement.owner))//owner.enemies.contains(en.location.improvement.owner)) 
						{
							//System.out.println("raze");
							location.grid.removeUnit(en.location.improvement);
							//en.location.improvement = null;
						}
					}*/
					if (owner.enemies.contains(location.grid.getTile(en.location.row+r,en.location.col+c).owner) ||
							location.grid.getTile(en.location.row+r,en.location.col+c).owner == null)
						location.grid.move(this, r, c);
				}
				if (enemy != null)
				{
					if (owner.cities.size() > 5 && enemy.owner.cities.size() < 6)
					{
						if (Math.random() < 0.75)
						{
							location.grid.removeUnit(enemy);
							location.grid.move(en,r,c);
						}
						else
						{
							location.grid.removeUnit(en);
							return false;
						}
					}
					else
					{
						if (Math.random() < 0.5)
						{
							location.grid.removeUnit(enemy);
							location.grid.move(en,r,c);
						}
						else
						{
							location.grid.removeUnit(en);
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public boolean raze()
	{
		System.out.println(location + " " + location.improvement);
		if (location.improvement != null)
		{
			System.out.println(owner + " " + location.improvement.owner + " Name: " + location.improvement.id);
			if (!owner.equals(location.owner))
			{
				System.out.println("takeover");
				if (location.improvement.name.equals("City"))
				{
					System.out.println("takeovercity");
					City city = (City)location.improvement;
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
					System.out.println("_________");
					System.out.println(city.owner);
					System.out.println(owner);
					return true;
				}
				//Just in case
				//The first condition is not needed
				else if (!(location.improvement instanceof City))//owner.enemies.contains(en.location.improvement.owner)) 
				{
					System.out.println("raze");
					location.grid.removeUnit(location.improvement);
					return true;
					//en.location.improvement = null;
				}
			}
		}
		return false;
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
		if (owner.enemies.size() > 0)
		{
			for (int i = 0; i < owner.enemies.size(); i++)
			{
				for (int j = 0; j < owner.enemies.get(i).cities.size(); j++)
				{
					City candidate = owner.enemies.get(i).cities.get(j);
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
		}
		if (nearest != null)
			return nearest.location;
		return null;
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

}
