package game_ai;

import java.util.ArrayList;

import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Grid;
import game.Tech;
import game.Tile;
import system.MenuSystem;
import units.City;

//All around wrapper class for advanced AI concepts used in the game

public class Intelligence {

	public static int[] civScores = null;
	
	public static void calculateCivScores(Grid grid)
	{
		civScores = new int[grid.civs.length];
		for (int i = 0; i < grid.civs.length; i++)
		{
			civScores[i] = Intelligence.developmentScore(grid.civs[i]);
		}
	}
	
	public static int prosperityScore(Civilization civ)
	{
		return unitsScore(civ) + (int)(techScore(civ)*1.5f) + developmentScore(civ);
	}

	public static int unitsScore(Civilization civ) //Not adaptive.
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
	public static int techScore(Civilization civ)
	{
		int sum = evalTech(civ.techTree.first);
		//Was I planning to do anything here? Hmmm...
		return sum;
	}
	public static int evalTech(Tech tech)
	{
		int sum = 0;
		if (tech.researched())
			sum += tech.requiredR;
		for (int i = 0; i < tech.techs.length; i++)
			sum += evalTech(tech.techs[i]);
		return sum;
	}
	public static int developmentScore(Civilization civ) //Cities, improvements, territory
	{
		float sum = 0;
		for (City city: civ.cities)
		{
			double[] product = new double[4];
			for (Tile t: city.workedLand)
			{
				double[] e = City.staticEval(t);
				for (int j = 0; j < e.length; j++)
					product[j] += e[j];
			}
			sum += product[0]*0.5f + product[1]*0.25f + product[2]*0.5f + product[3];
		}
		for (GameEntity en: civ.units)
		{
			if (en.name.equals("Settler"))
				sum += 10;
		}
		return (int)sum;
	}
	
	private static ArrayList<ArrayList<BaseEntity>> masterList = new ArrayList<ArrayList<BaseEntity>>();
	public static  possible(ArrayList<BaseEntity> list, City c, int turns)
	{
		ArrayList<String> units = c.owner.techTree.allowedUnits;
		ArrayList<String> impr = c.owner.techTree.allowedCityImprovements;
		int[] turnsUnits = new int[units.size()];
		int[] turnsImpr = new int[impr.size()];
		for (int i = 0; i < units.size(); i++)
		{
			turnsUnits[i] = MenuSystem.calcQueueTurnsInt(c, units.get(i));
		}
		for (int i = 0; i < impr.size(); i++)
		{
			turnsImpr[i] = MenuSystem.calcQueueTurnsInt(c, impr.get(i));
		}
		if (turns <= 0)
			masterList.add(list);
	}
	
	public static ArrayList<ArrayList<BaseEntity>> 
	
	public static int unitScore(GameEntity en)
	{
		ArrayList<GameEntity> enemies = new ArrayList<GameEntity>();
		ArrayList<GameEntity> rivals = new ArrayList<GameEntity>();
		int[][] allowed = en.owner.revealed;
		for (Civilization civ: en.location.grid.civs)
		{
			if (civ.equals(en.owner)) continue;
			for (GameEntity unit: civ.units)
			{
				if (allowed[unit.location.row][unit.location.col] == 2)
				{
					if (civ.isWar(en.owner))
						enemies.add(unit);
					else
						rivals.add(unit);
				}
			}
		}
		return unitScoreWithUnits(en, enemies, rivals);
	}
	public static int unitScoreWithRival(GameEntity en, ArrayList<GameEntity> enemies)
	{
		return unitScoreWithUnits(en, enemies, new ArrayList<GameEntity>());
	}
	public static int unitScoreWithUnits(GameEntity en, ArrayList<GameEntity> enemies, ArrayList<GameEntity> rivals) //Calculates score of a unit. Adaptive.
	{
		float sum = 0;
		Grid grid = en.location.grid;
		if (en.name.equals("Settler"))
			sum = 10;
		else if (en.mode == 0)
			return 0;
		else if (en.mode == 1)
			sum = en.offensiveStr + en.defensiveStr;
		else if (en.mode == 2)
			sum = en.rangedStr + en.defensiveStr;
		double averageOffDef = 0;
		//Weighted averages of the off-def potential of a unit versus all adversaries
		for (GameEntity enemy: enemies)
		{
			double[] offDef = grid.conflictSystem.calcOffDefMod(en, enemy);
			averageOffDef += offDef[0]*2/offDef[1]; //double the weight
		}
		for (GameEntity rival: rivals)
		{
			double[] offDef = grid.conflictSystem.calcOffDefMod(en, rival);
			averageOffDef += offDef[0]/offDef[1];
		}
		averageOffDef /= enemies.size()*2 + rivals.size();
		sum *= averageOffDef;
		return (int)sum;
	}

}
