package units;

import game.GameEntity;
import game.Tile;

public class Warrior extends GameEntity {

	public Warrior(String name) {
		super(name);
	}

	public Warrior(GameEntity en) {
		super(en);
	}

	public void tick()
	{
		GameEntity en = this;
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
					if (en.location.improvement != null)
					{
						if (en.location.improvement instanceof City && !en.owner.equals(en.location.improvement.owner))
						{
							//System.out.println("Destroyed");
							/*City city = (City)en.location.improvement;
							for (int k = city.land.size() - 1; k >= 0; k--)
							{
								Tile t = city.land.get(k);
								//if (t.equals(city.location)) continue;
								if (t.improvement != null)
								{
									if (!(t.improvement instanceof City))
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
							en.location.improvement = null;*/
							City city = (City)en.location.improvement;
							for (int k = city.land.size() - 1; k >= 0; k--)
							{
								Tile t = city.land.get(k);
								//if (t.equals(city.location)) continue;
								if (t.improvement != null)
								{
									//if (!(t.improvement instanceof City))
										t.improvement.owner = owner;
								}
								//city.owner.tiles.remove(t);
								t.owner = owner;
								//t.city = null;
								//city.land.remove(k);
								//System.out.println("Destroyed");
								//en.owner.
								//t.owner = en.owner;
							}
							city.owner.cities.remove(city);
							city.owner = owner;
							owner.cities.add(city);
						}
					}
					location.grid.move(this, r, c);
				}
			}
		}
	}
	
	public String getName() {return "Warrior";}

}
