package units;

import game.GameEntity;
import game.Tile;

public class Caravan extends GameEntity {

	public City home, target;

	public Caravan(String name, float o, float d, float r) {
		super(name,o,d,r);
		health = 5; maxHealth = 5;
	}

	public Caravan(GameEntity en) {
		super(en);
		health = 5; maxHealth = 5;
	}

	public boolean setRoute(City t)
	{
		int num = owner.trait("Imperialistic") ? 3 : 2;
		if (home.activeCaravansOut.size() < num)
		{
			home.activeCaravansOut.add(this);
			t.activeCaravansIn.add(this);
			target = t;
			return true;
		}
		return false;
	}
	
	public void endRoute()
	{
		home.activeCaravansOut.remove(this);
		target.activeCaravansIn.remove(this);
		target = null;
	}

	public void tick() 
	{
		if (target == null)
		{
			Tile t = nearestAlliedCityNotAt(home.location);
			waddleToExact(t.row, t.col);
			//If a path was found
			if (queueTiles.size() > 0)
			{
				setRoute((City)t.improvement);
			}
		}
	}

	public void playerTick() 
	{

	}

	public void barbarianTick() 
	{

	}

	public String getName() {return "Caravan";}

}
