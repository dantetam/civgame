package units;

import data.EntityData;
import data.Field;
import game.GameEntity;
import game.Tile;

public class Settler extends GameEntity {

	public Settler(String name, float o, float d, float r) {
		super(name,o,d,r);
		health = 5; maxHealth = 5;
	}

	public Settler(GameEntity en) {
		super(en);
		health = 5; maxHealth = 5;
	}

	public void playerTick()
	{
		if (queueTiles.size() > 0)
		{
			//location.grid.moveTo(this, queueTiles.get(0).row, queueTiles.get(0).col);
			passiveWaddle(queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col);
			/*System.out.println("list VVV");
			for (int i = 0; i < queueTiles.size(); i++)
			{
				System.out.println(queueTiles.get(i).row + " " + queueTiles.get(i).col);
			}*/
			queueTiles.remove(queueTiles.size()-1);
		}
	}

	public void barbarianTick()
	{
		if (settle())
			return;
		waddle();
	}

	/*public void tick()
	{
		GameEntity en = this;
		if (en.location.owner == null && Math.random() < 0.2)
		{
			//Make the city and set its surrounding tiles to the civilization's territory
			settle();
			return;
		}
		waddle();
	}*/

	public void tick()
	{
		if (queueTiles.size() == 0)
		{
			if (owner.cities.size() == 0)
			{
				//settle(); return;
			}
			Tile t = settleLocation();
			//System.out.println(t.owner);
			waddleToExact(t.row,t.col);
		}
		else
		{	
			while (action > 0)
			{
				if (queueTiles.size() == 0) 
				{
					Tile t = settleLocation();
					waddleToExact(t.row,t.col);
					return;
				}
				if (queueTiles.get(0).equals(location))
				{
					settle();
					return;
				}
				//super.recordPos();
				passiveWaddle(queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col);
				//location.grid.move(this,queueTiles.get(queueTiles.size()-1).row - location.row, queueTiles.get(queueTiles.size()-1).col - location.col);
				queueTiles.remove(queueTiles.size()-1);
				//If it reaches the destination
				if (queueTiles.size() == 0)
				{
					if (location.owner == null)
					{
						settle();
						return;
					}
					else
					{
						queueTiles.clear();
						Tile t = settleLocation();
						waddleToExact(t.row,t.col);
					}
				}
				/*else if (queueTiles.get(0).owner != null)
				{
					if (!queueTiles.get(0).owner.equals(owner))
					{
						queueTiles.clear();
					}
				}*/
			}
		}
		/**/
	}

	public Tile settleLocation()
	{
		/*Tile[] candidates = location.grid.returnBestCityScores(location.row, location.col, 1);
		return candidates[(int)(Math.random()*candidates.length)];*/
		if (owner.capital != null)
			return location.grid.returnBestCityScoresMod(location.row, location.col, owner.capital.location.row, owner.capital.location.col, 2);
		return location.grid.returnBestCityScoresMod(location.row, location.col, location.row, location.col, 2.5);
		//return candidates[0];
	}

	public boolean settle()
	{
		if (location.owner == null) //TODO: Allow player to settle in own territory
		{
			/*for (int i = 0; i < location.grid.civs.length; i++)
			{
				for (int j = 0; j < location.grid.civs[i].cities.size(); j++)
				{
					City c = location.grid.civs[i].cities.get(j);
					if (c.location.dist(location) <= 3)
						return false;
				}
			}*/
			GameEntity en = this;
			City city = (City)EntityData.get("City");
			city.owner = en.owner;
			city.core = en.owner;
			city.owner.cities.add(city);
			city.owner.improvements.add(city);
			location.grid.addUnit(city, en.owner, en.location.row, en.location.col);
			if (owner.cities.size() == 1)
			{
				owner.capital = city;
			}
			city.expand(1);
			city.id = city.owner.cities.size() - 1;
			location.road = true;
			//Remove the settler
			location.grid.removeUnit(this);
			return true;
		}
		return false;
	}

	public String getName() {return "Settler";}

}