package units;

import data.EntityData;
import game.GameEntity;

public class Worker extends GameEntity {

	public Worker(String name) {
		super(name);
		health = 10;
		offensiveStr = 0; rangedStr = 0; defensiveStr = 2;
	}

	public Worker(GameEntity en) {
		super(en);
		health = 10;
		offensiveStr = 0; rangedStr = 0; defensiveStr = 2;
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
				location.grid.addUnit(EntityData.get(queue), owner, location.row, location.col);
				queueTurns = 0; //just to be sure
				queue = null;
			}
		}
		else
		{
			if (en.location.city != null && en.location.improvement == null && en.location.owner.equals(owner))
			{
				City city = en.location.city;
				//Factor in the city later
				//if (city.location.owner.equals(owner)) //just in case
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
				if (en.location.biome >= 3 && en.location.biome <= 6 && location.grid.irrigated(location.row, location.col))
				{
					en.queueTurns = 6;
					en.queue = "Farm";
				}
				if (en.location.shape == 1)
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
		if (queue == null)
		{
			waddle();
		}
	}

	public String getName() {return "Worker";}

}
