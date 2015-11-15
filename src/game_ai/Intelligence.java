package game_ai;

import java.util.ArrayList;

import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Grid;
import game.Tech;
import game.Tile;
import units.City;

//All around wrapper class for advanced AI concepts used in the game

public class Intelligence {

	private Civilization civilization;

	public Intelligence(Civilization civ)
	{
		civilization = civ;
	}

	public int prosperityScore(Civilization civ)
	{
		return unitsScore(civ) + (int)(techScore(civ)*1.5f) + developmentScore(civ);
	}

	public int unitsScore(Civilization civ) //Not adaptive.
	{
		int sum = 0;
		for (int i = 0; i < civ.units.size(); i++)
		{
			GameEntity en = civ.units.get(i);
			if (en.mode != 0)
			{
				if (en.mode == 1)
					sum += en.offensiveStr + en.defensiveStr;
				else if (en.mode == 2)
					sum += en.rangedStr + en.defensiveStr;
			}
		}
		return sum;
	}
	public int techScore(Civilization civ)
	{
		int sum = evalTech(civ.techTree.first);
		//Was I planning to do anything here? Hmmm...
		return sum;
	}
	public int evalTech(Tech tech)
	{
		int sum = 0;
		if (tech.researched())
			sum += tech.requiredR;
		for (int i = 0; i < tech.techs.length; i++)
			sum += evalTech(tech.techs[i]);
		return sum;
	}
	public int developmentScore(Civilization civ) //Cities, improvements, territory
	{
		float sum = 0;
		for (int i = 0; i < civ.cities.size(); i++)
		{
			City city = civ.cities.get(i);
			double[] product = new double[4];
			for (Tile t: city.workedLand)
			{
				double[] e = City.staticEval(t);
				for (int j = 0; j < e.length; j++)
					product[j] += e[j];
			}
			sum += product[0]*0.5f + product[1]*0.25f + product[2]*0.5f + product[3];
		}
		return (int)sum;
	}

	public int oneUnitScore(GameEntity en) //Calculates score of a unit. Adaptive.
	{
		Grid grid = en.location.grid;
		ArrayList<GameEntity> enemies = new ArrayList<GameEntity>();
		ArrayList<GameEntity> rivals = new ArrayList<GameEntity>();
		for (int r = 0; r < en.owner.revealed.length; r++)
		{
			for (int c = 0; c < en.owner.revealed[r]; c++)
			{
				
			}
		}
	}

}
