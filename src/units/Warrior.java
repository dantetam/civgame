package units;

import game.GameEntity;
import game.Tile;

public class Warrior extends GameEntity {

	public Warrior(String name) {
		super(name);
		health = 10;
		offensiveStr = 2; rangedStr = 0; defensiveStr = 2;
	}

	public Warrior(GameEntity en) {
		super(en);
		health = 10;
		offensiveStr = 2; rangedStr = 0; defensiveStr = 2;
	}

	public void playerTick()
	{
		if (queueTiles.size() > 0)
		{
			//location.grid.moveTo(this, queueTiles.get(0).row, queueTiles.get(0).col);
			aggressiveWaddle(queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col);
			queueTiles.remove(queueTiles.size()-1);
		}
	}
	
	public void tick()
	{
		if (queueTiles.size() > 0)
		{
			aggressiveWaddle(queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col);
			queueTiles.remove(queueTiles.size()-1);
			Tile t = adjacentEnemy();
			if (t != null)
			{
				queueTiles.clear();
				aggressiveWaddle(t.row - location.row, t.col - location.col);
			}
		}
		else
		{
			Tile nearest = nearestEnemyCity();
			if (nearest != null)
			{
				waddleTo(nearest.row, nearest.col);
			}
			else
			{
				int r = (int)(Math.random()*3) - 1;
				int c = (int)(Math.random()*3) - 1;
				aggressiveWaddle(r,c);
			}
		}
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