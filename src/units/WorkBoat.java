package units;

import data.EntityData;
import game.GameEntity;

public class WorkBoat extends GameEntity {

	public WorkBoat(String name) {
		super(name);
	}

	public WorkBoat(GameEntity en) {
		super(en);
	}

	public String getName()
	{
		return "Work Boat";
	}
	
	public void tick()
	{
		GameEntity en = this;
		int r = (int)(Math.random()*3) - 1;
		int c = (int)(Math.random()*3) - 1;
		if (location.grid.getTile(en.location.row+r,en.location.col+c) != null)
		{
			if (location.grid.getTile(en.location.row+r,en.location.col+c).biome == -1)
			{
				//if (location.grid.getTile(en.location.row+r,en.location.col+c).improvement.name.equals("City"))
				GameEntity enemy = location.grid.hasEnemy(this, location.row, location.col);
				if (enemy == null)
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
			}
		}
		return;
	}
	
}