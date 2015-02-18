package units;

import java.util.ArrayList;

import game.GameEntity;
import game.Tile;

public class Warrior extends GameEntity {

	public Warrior(String name, float o, float d, float r) {
		super(name,o,d,r);
		this.name = name;
		health = 10; maxHealth = 10;
	}

	public Warrior(GameEntity en) {
		super(en);
		this.name = en.name;
		health = 10; maxHealth = 10;
	}

	public void playerTick()
	{
		while (action > 0)
		{
			if (queueTiles.size() > 0)
			{
				//location.grid.moveTo(this, queueTiles.get(0).row, queueTiles.get(0).col);
				aggressiveWaddle(queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col);
				if (queueTiles.size() > 0)
					queueTiles.remove(queueTiles.size()-1);
				//System.out.println("okigenyo");
			}
			else
			{
				break;
			}
		}
	}

	public void barbarianTick()
	{
		tick();
	}

	/*public boolean raze()
	{
		System.out.println(super.raze());
		return true;
		//return super.raze();
	}*/

	public void tick()
	{
		calculateNewPath();
		if (!explorer)
		{
			//System.out.println("beginning");

			if (mode == 2)
			{
				ArrayList<GameEntity> targets = fireAtTargets();
				if (targets.size() > 0)
					fire(targets.get((int)(Math.random()*targets.size())));
			}

			if (queueTiles.size() > 0)
			{
				while (action > 0)
				{
					Tile t = adjacentEnemy();
					if (t != null)
					{
						queueTiles.clear();
						if (!aggressiveWaddle(t.row - location.row, t.col - location.col))
						{
							return;
						}
						continue;
					}

					if (queueTiles.size() == 0)
						return;

					//System.out.println("Start");
					//System.out.println(previous[0] + " " + previous[1] + "; " + location.row + " " + location.col);
					//super.recordPos();

					Tile q = queueTiles.get(queueTiles.size()-1);
					if (!aggressiveWaddle(q.row - location.row, q.col - location.col))
						return;
					if (queueTiles.size() == 0)
						return;

					//If it moved
					//System.out.println(previous[0] + " " + previous[1] + "; " + location.row + " " + location.col);
					//if (moved())
					queueTiles.remove(queueTiles.size()-1);
					//else 
					//queueTiles.clear();

					//raze();
					if (raze()) return;
					/*if (queueTiles.size() > 0)
						if (queueTiles.get(0).owner.equals(owner) || queueTiles.get(0).equals(location))
						{
							nearestA = nearestAlliedCity();
							if (nearestA != null)
							{
								//queueTiles.clear(); queueTiles = new ArrayList<Tile>();
								//waddleTo(nearest.row, nearest.col);
								//int r = nearestA.row - location.row;
								//int c = nearestA.col - location.col;
								//queueTiles.clear(); //just in case
								super.waddleToExact(nearestA.row,nearestA.col);
								//System.out.println("pathfinding start " + queueTiles.size());
							}
						}*/

					/*Tile t = adjacentEnemy();
					if (t != null)
					{
						queueTiles.clear();
						if (!aggressiveWaddle(t.row - location.row, t.col - location.col))
						{
							queueTiles.remove(queueTiles.size()-1);
							return;
						}
					}
					else
					{
						return;
					}*/
				}
			}
			else if (health < maxHealth)
			{
				while (action > 0)
				{
					heal();
					if (health == maxHealth) break;
				}
			}
			else //if (queueTiles.size() == 0) //See if the list has been cleared in the previous section of code 
			{
				Tile nearestE = nearestEnemyCity(true);
				Tile nearestA = nearestAlliedCity();
				Tile nearestU = nearestUndefendedCity();
				//System.out.println(nearest);
				//System.out.println(location);
				/*if (nearestU != null)
				{
					super.waddleToExact(nearestU.row, nearestU.col);
				}*/
				//System.out.println(nearestE);
				if (nearestE != null) //else if (nearestE != null)
				{
					//System.out.println("pathfinding");
					super.waddleToExact(nearestE.row, nearestE.col);
				}
				else if (nearestA != null)
				{
					super.waddleToExact(nearestA.row, nearestA.col);
				}
				else
				{
					while (action > 0)
					{
						int r = (int)(Math.random()*3) - 1;
						int c = (int)(Math.random()*3) - 1;
						//if (!aggressiveWaddle(r,c)) return;
						aggressiveWaddle(r,c);
					}
				}
			}
			//System.out.println("End");
		}
		else
		{
			if (queueTiles.size() > 0)
			{
				while (action > 0)
				{
					if (queueTiles.size() == 0)
						break;
					if (!aggressiveWaddle(queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col))
					{
						queueTiles.remove(queueTiles.size()-1);
						return;
					}
					if (queueTiles.size() == 0)
						break;
					//if (moved())
					queueTiles.remove(queueTiles.size()-1);
				}
				for (int i = 0; i < queueTiles.size(); i++)
				{
					if (queueTiles.get(i).owner != null)
					{
						if (!owner.isOpenBorder(queueTiles.get(i).owner) && !owner.equals(queueTiles.get(i).owner))
						{
							explore();
							return;
						}
					}
				}
			}
			else
			{
				explore();
			}
		}
		//System.out.println("end");
	}

	public void waddle()
	{
		/*if (queueTiles.size() > 0)
		{
			//location.grid.moveTo(this, queueTiles.get(0).row, queueTiles.get(0).col);
			aggressiveWaddle(queueTiles.get(0).row - location.row, queueTiles.get(0).col - location.col);
			queueTiles.remove(0);
		}
		else
		{
			int r = (int)(Math.random()*3) - 1;
			int c = (int)(Math.random()*3) - 1;
			aggressiveWaddle(r,c);
		}*/
	}
	/*public void waddle()
	{
		if (queueTiles.size() > 0)
		{
			//location.grid.moveTo(this, queueTiles.get(0).row, queueTiles.get(0).col);
			aggressiveWaddle(queueTiles.get(0).row - location.row, queueTiles.get(0).col - location.col);
			queueTiles.remove(0);
		}
		else
		{
			GameEntity en = this;
			int r = (int)(Math.random()*3) - 1;
			int c = (int)(Math.random()*3) - 1;
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
	}*/

	public String getName() {return "Warrior";}

}
