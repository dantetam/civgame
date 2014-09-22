package units;

import data.EntityData;
import game.GameEntity;
import game.Tile;

public class Settler extends GameEntity {

	public Settler(String name) {
		super(name);
	}
	
	public Settler(GameEntity en) {
		super(en);
	}
	
	public void tick()
	{
		GameEntity en = this;
		if (en.location.owner == null && Math.random() < 0.2)
		{
			//Make the city and set its surrounding tiles to the civilization's territory
			City city = (City)EntityData.get("City");
			city.owner = en.owner;
			city.owner.cities.add(city);
			location.grid.addUnit(city, en.owner, en.location.row, en.location.col);
			if (owner.cities.size() == 1)
			{
				city.capital = true;
			}
			for (int i = en.location.row - 2; i <= en.location.row + 2; i++)
			{
				for (int j = en.location.col - 2; j <= en.location.col + 2; j++)
				{
					if (i >= 0 && i < location.grid.rows && j >= 0 && j < location.grid.cols)
					{
						Tile t = location.grid.getTile(i,j);
						if (t != null)
						{
							if (t.owner == null)
							{
								t.city = city;
								city.land.add(t);
								location.grid.addTile(en.owner, t);
							}
							if (t.owner == city.owner && t.city == null)
							{
								t.city = city;
								city.land.add(t);
							}
						}
					}
				}
			}
			//Remove the settler
			location.grid.removeUnit(en);
			return;
		}
	}
	
	public String getName() {return "Settler";}

}