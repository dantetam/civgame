package system;

import render.CivGame;
import game.Civilization;
import game.GameEntity;

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
						en.location.occupants.remove(en);
						en.location = main.grid.tiles[r+1][c+1];
						en.location.addUnit(en);
					}
				}
			}
		}
	}	

}
