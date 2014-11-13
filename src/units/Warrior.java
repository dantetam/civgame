package units;

import java.util.ArrayList;

import game.GameEntity;
import game.Tile;

public class Warrior extends GameEntity {

	public Warrior(String name, float o, float d, float r) {
		super(name,o,d,r);
		health = 10; maxHealth = 10;
	}

	public Warrior(GameEntity en) {
		super(en);
		health = 10; maxHealth = 10;
	}

	public void playerTick()
	{
		if (queueTiles.size() > 0)
		{
			//location.grid.moveTo(this, queueTiles.get(0).row, queueTiles.get(0).col);
			aggressiveWaddle(queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col);
			queueTiles.remove(queueTiles.size()-1);
			//System.out.println("okigenyo");
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
		if (!explorer)
		{
			//System.out.println("beginning");
			Tile nearestA = nearestAlliedCity();
			
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
					if (queueTiles.size() == 0)
						return;

					Tile q = queueTiles.get(queueTiles.size()-1);
					if (!aggressiveWaddle(q.row - location.row, q.col - location.col))
						return;
					if (queueTiles.size() == 0)
						return;

					queueTiles.remove(queueTiles.size()-1);

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
				Tile nearestE = nearestEnemyCity();
				//System.out.println(nearest);
				//System.out.println(location);
				if (nearestE != null)
				{
					//waddleTo(nearest.row, nearest.col);
					//int r = nearestE.row - location.row;
					//int c = nearestE.col - location.col;
					//queueTiles.clear(); //just in case
					super.waddleToExact(nearestE.row, nearestE.col);
					//System.out.println("pathfinding start " + queueTiles.size());
				}
				else if (nearestA != null)
				{
					//waddleTo(nearest.row, nearest.col);
					int r = nearestA.row - location.row;
					int c = nearestA.col - location.col;
					//queueTiles.clear(); //just in case
					super.waddleTo(r,c);
					//System.out.println("pathfinding start " + queueTiles.size());
				}
				//else
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
						return;
					}
					if (queueTiles.size() == 0)
						break;
					queueTiles.remove(queueTiles.size()-1);
				}
				for (int i = 0; i < queueTiles.size(); i++)
				{
					if (!owner.isOpenBorder(queueTiles.get(i).owner))
					{
						explore();
						break;
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
