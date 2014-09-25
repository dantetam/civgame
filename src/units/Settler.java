package units;

import data.EntityData;
import game.GameEntity;
import game.Tile;

public class Settler extends GameEntity {

	public Settler(String name) {
		super(name);
		health = 10;
		offensiveStr = 0; rangedStr = 0; defensiveStr = 2;
	}
	
	public Settler(GameEntity en) {
		super(en);
		health = 10;
		offensiveStr = 0; rangedStr = 0; defensiveStr = 2;
	}
	
	public void playerTick()
	{
		if (queueTiles.size() > 0)
		{
			//location.grid.moveTo(this, queueTiles.get(0).row, queueTiles.get(0).col);
			aggressiveWaddle(queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col);
			System.out.println("list VVV");
			for (int i = 0; i < queueTiles.size(); i++)
			{
				System.out.println(queueTiles.get(i).row + " " + queueTiles.get(i).col);
			}
			queueTiles.remove(queueTiles.size()-1);
		}
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
				owner.capital = city;
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
							else if (t.owner == city.owner && t.city == null)
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
		waddle();
	}
	
	public String getName() {return "Settler";}

}