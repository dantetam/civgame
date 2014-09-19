package game;

import units.City;
import data.EntityData;

//An entity that moves around the map i.e. a unit

public class GameEntity extends BaseEntity {

	public GameEntity(String name)
	{
		super(name);
	}

	public GameEntity(GameEntity other)
	{
		super(other);
	}

	public void tick()
	{
		GameEntity en = this;
		if (name.equals("Worker") && queue != null)
		{
			queueTurns--;
			if (queueTurns <= 0)
			{
				location.grid.addUnit(EntityData.get(queue), owner, location.row, location.col);
				queueTurns = 0; //just to be sure
				queue = null;
			}
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
						if (en.location.biome >= 3 && en.location.biome <= 6)
						{
							en.queueTurns = 6;
							en.queue = "Farm";
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
				//Make the city and set its surrounding tiles to 
				City city = (City)EntityData.get("City");
				city.owner = en.owner;
				city.owner.cities.add(city);
				location.grid.addUnit(city, en.owner, en.location.row, en.location.col);
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
						if (en.queue == null)
							location.grid.move(en,r,c);
						if (en.location.improvement != null)
						{
							if (en.location.improvement.name.equals("City") && !en.owner.equals(en.location.improvement.owner) && en.name.equals("Warrior"))
							{
								City city = (City)en.location.improvement;
								for (int k = city.land.size() - 1; k >= 0; k--)
								{
									Tile t = city.land.get(k);
									city.land.remove(t);
									t.owner = null;
									//System.out.println("Destroyed");
									//en.owner.
									//t.owner = en.owner;
								}
								city.owner.cities.remove(city);
								en.location.improvement = null;
								//city = null;
							}
						}
					}
				}
			}
		}
	}

}
