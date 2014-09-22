package units;

import game.GameEntity;

public class Galley extends GameEntity {

	public Galley(String name) {
		super(name);
	}

	public Galley(GameEntity en) {
		super(en);
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