package units;

import game.GameEntity;

public class Galley extends GameEntity {

	public Galley(String name, float o, float d, float r) {
		super(name,o,d,r);
		health = 10; maxHealth = 10;
	}

	public Galley(GameEntity en) {
		super(en);
		health = 10; maxHealth = 10;
	}

	public void playerTick()
	{
		if (queueTiles.size() > 0)
		{
			//location.grid.moveTo(this, queueTiles.get(0).row, queueTiles.get(0).col);
			aggressiveWaddle(queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col);
			if (!(location.row == previous[0]) || !(location.col == previous[1]))
				queueTiles.remove(queueTiles.size()-1);
		}
	}
	
	public void barbarianTick()
	{
		tick();
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
	
	public String getName() {return "Galley";}

}