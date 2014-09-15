package system;

import data.EntityData;
import render.CivGame;
import game.*;
import units.*;

public class CivilizationSystem extends BaseSystem {

	public boolean requestTurn = false;

	public CivilizationSystem(CivGame civGame) 
	{
		super(civGame);
	}

	public void tick() 
	{
		if (requestTurn) 
		{
			requestTurn = false;
			for (int i = 0; i < main.grid.civs.length; i++)
			{
				Civilization civ = main.grid.civs[i];
				//Automatically move the computer players' units
				if (i != 0)
				{
					for (int j = 0; j < civ.units.size(); j++)
					{
						GameEntity en = civ.units.get(j);
						int r = (int)(Math.random()*3) - 1;
						int c = (int)(Math.random()*3) - 1;
						if (main.grid.getTile(en.location.row+r,en.location.col+c) != null)
						{
							if (main.grid.getTile(en.location.row+r,en.location.col+c).owner == en.owner ||
									main.grid.getTile(en.location.row+r,en.location.col+c).owner == null)
							{
								main.grid.move(en,r,c);
							}
						}
						if (Math.random() < 0.5 && en.location.owner == null)
						{
							sacrifice(en);
						}
					}
					for (int j = 0; j < civ.cities.size(); j++)
					{
						City c = civ.cities.get(j);
						if (c.queue == null && civ.units.size() < 3)
						{
							c.queue = "Settler";
							c.queueTurns = 10;
						}
						else if (c.queue != null)
						{
							c.queueTurns--;
							if (c.queueTurns == 0)
							{
								c.queue = null;
								main.grid.addUnit(EntityData.get("Settler"),civ,c.location.row,c.location.col);
							}
						}
					}
				}
			}
		}
		for (int r = 0; r < main.grid.rows; r++)
		{
			for (int c = 0; c < main.grid.cols; c++)
			{
				Tile t = main.grid.getTile(r,c);
				if (t.improvement != null)
				{
					t.improvement.tick();
				}
				for (int i = 0; i < t.occupants.size(); i++)
				{
					t.occupants.get(i).tick();
				}
			}
		}
	}	

	public void sacrifice(GameEntity en)
	{
		if (en.name.equals("Settler"))
		{
			//Make the city and set its surrounding tiles to 
			City city = (City)EntityData.get("City");
			city.owner = en.owner;
			city.owner.cities.add(city);
			main.grid.addUnit(city, en.owner, en.location.row, en.location.col);
			for (int i = en.location.row - 2; i <= en.location.row + 2; i++)
			{
				for (int j = en.location.col - 2; j <= en.location.col + 2; j++)
				{
					if (i >= 0 && i < main.grid.rows && j >= 0 && j < main.grid.cols)
					{
						Tile t = main.grid.getTile(i,j);
						if (t != null)
						{
							if (t.owner == null)
							{
								main.grid.addTile(en.owner, t);
							}
						}
					}
				}
			}
			//Remove the settler
			main.grid.removeUnit(en);
		}
	}

}
