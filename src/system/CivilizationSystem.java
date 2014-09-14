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
						int r = en.location.row;
						int c = en.location.col;
						main.grid.move(en,(int)(Math.random()*3) - 1,(int)(Math.random()*3) - 1);
						if (Math.random() < 0.5)
						{
							sacrifice(en);
						}
					}
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
			main.grid.addUnit(city, en.owner, en.location.row, en.location.col);
			for (int i = en.location.row - 2; i <= en.location.row + 2; i++)
			{
				for (int j = en.location.col - 2; j <= en.location.col + 2; j++)
				{
					if (i >= 0 && i < main.grid.getTiles().length && j >= 0 && j < main.grid.getTiles()[0].length)
					{
						Tile t = main.grid.getTiles()[i][j];
						if (t.owner == null)
						{
							main.grid.addTile(en.owner, t);
						}
					}
				}
			}
			//Remove the settler
			main.grid.removeUnit(en);
		}
	}

}
