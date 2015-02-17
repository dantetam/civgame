package units;

import java.util.ArrayList;

import data.EntityData;
import game.GameEntity;
import game.Tile;

public class Worker extends GameEntity {

	public double workTime = 1;
	public ArrayList<Tile> roadQueue = new ArrayList<Tile>(); //list of roads to be built

	public Worker(String name, float o, float d, float r) {
		super(name,o,d,r);
		health = 5; maxHealth = 5;
	}

	public Worker(GameEntity en) {
		super(en);
		health = 5; maxHealth = 5;
	}

	public void playerTick()
	{
		while (action > 0)
		{
			if (queue != null)
			{
				//System.out.println(queue);
				queueTurns--;
				if (queueTurns <= 0)
				{
					if (queue.equals("Road"))
						location.road = true;
					else
					{
						location.grid.addUnit(EntityData.get(queue), owner, location.row, location.col);
						queueTurns = 0; //just to be sure
						queue = null;
						//action = 0;
					}
				}
				action--;
			}
			else if (roadQueue.size() > 0)
			{
				if (location.equals(roadQueue.get(0)))
				{
					EntityData.queueTileImprovement(this, "Road");
				}
				else
				{
					if (queueTiles.size() > 0)
						super.waddle();
					else
						super.waddleToExact(roadQueue.get(0).row, roadQueue.get(0).col);
				}
			}
			else if (queueTiles.size() > 0)
			{
				//location.grid.moveTo(this, queueTiles.get(0).row, queueTiles.get(0).col);
				super.recordPos();
				passiveWaddle(queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col);
				if (moved())
				{
					queueTiles.remove(queueTiles.size()-1);
					//action--;
				}
				else
				{
					queueTiles.clear();
				}
			}
			else
			{
				action--;
			}
		}
	}

	public void barbarianTick()
	{
		tick();
	}

	public void tick()
	{
		GameEntity en = this;
		if (queue != null)
		{
			//System.out.println(queue);
			queueTurns--;
			if (queueTurns <= 0)
			{
				if (queue.equals("Road"))
					location.road = true;
				else
				{
					location.grid.addUnit(EntityData.get(queue), owner, location.row, location.col);
					queueTurns = 0;
					queue = null;
				}
			}
		}
		else
		{
			if (roadQueue.size() > 0)
			{
				if (en.location.equals(roadQueue.get(0)))
				{
					EntityData.queueTileImprovement(en, "Road");
				}
				else
				{
					if (queueTiles.size() > 0)
						super.waddle();
					else
						super.waddleToExact(roadQueue.get(0).row, roadQueue.get(0).col);
				}
			}
			else if (en.location.city != null && en.location.improvement == null && en.location.owner.equals(owner))
			{
				City city = en.location.city;
				//Factor in the city later
				//if (city.location.owner.equals(owner)) //just in case
				EntityData.queueTileImprovement(this, EntityData.optimalImpr(en.location));
				//en.queueTurns = Math.max(1,(int)(en.queueTurns*((Worker)en).workTime));
			}
		}
		if (queue == null)
		{
			//waddle();
			waddleInTerritory();
		}
	}

	public String getName() {return "Worker";}

}
