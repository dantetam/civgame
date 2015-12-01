package game_ai;

import java.util.ArrayList;

import game.BaseEntity;
import game.Civilization;
import game.ConflictSystem;
import game.GameEntity;
import game.Grid;
import game.Tech;
import game.Tile;
import system.MenuSystem;
import units.City;

//All around wrapper class for advanced AI concepts used in the game

public class Intelligence {

	public static int[] civScores = null;

	//Provide some utility methods below for scoring purposes to compare moves

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
		if (civ.cities.size() == 0 && civ.units.size() == 0) return 0;
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
	private static int developmentScore(Civilization civ) //Cities, improvements, territory
	{
		float sum = 0;
		for (City city: civ.cities)
		{
			sum += cityDevScore(city);
		}
		for (GameEntity en: civ.units)
		{
			if (en.name.equals("Settler"))
				sum += 10;
		}
		return (int)sum;
	}

	public static int cityDevScore(City city)
	{
		double[] product = new double[4];
		for (Tile t: city.workedLand)
		{
			double[] e = City.staticEval(t);
			for (int j = 0; j < e.length; j++)
				product[j] += e[j];
		}
		return (int)(product[0]*0.5f + product[1]*0.25f + product[2]*0.5f + product[3]);
	}

	public static City cityMaxDevScore(Civilization civ)
	{
		if (civ.cities.size() == 0) return null;
		if (civ.cities.size() == 1) return civ.cities.get(0);
		int maxIndex = 0, maxScore = cityDevScore(civ.cities.get(0));
		for (int i = 1; i < civ.cities.size(); i++)
		{
			int score = cityDevScore(civ.cities.get(i));
			if (score > maxScore)
			{
				maxScore = score;
				maxIndex = i;
			}
		}
		return civ.cities.get(maxIndex);
	}

	//Store these lists manually since doing it recursively is too much of a hassle
	private static ArrayList<ArrayList<String>> masterList = new ArrayList<ArrayList<String>>();
	//Generate a list of queue beelines that can be possibly carried out by City c in given turns
	private static void possible(ArrayList<String> list, City c, int turns, int times)
	{
		System.out.println("Called with " + turns + " number of turns, " + times + " iterations, and contents (" + list.size() + "): ");
		for (int i = 0; i < list.size(); i++)
			System.out.print(list.get(i) + " ");
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
		for (int i = 0; i < turnsUnits.length; i++)
		{
			if (turns - turnsUnits[i] < 0) 
			{
				masterList.add(list);
				return;
			}
			else
			{
				list.add(units.get(i));
				possible(list, c, turns - turnsUnits[i], times+1);
			}
		}
		for (int i = 0; i < turnsImpr.length; i++)
		{
			if (turns - turnsImpr[i] < 0) 
			{
				masterList.add(list);
				return;
			}
			else
			{
				possible(list, c, turns - turnsImpr[i], times+1);
			}
		}
	}

	//Clear out old results, generate new results, and return them
	public static ArrayList<ArrayList<String>> genQueuePermutations(City c, int turns)
	{
		masterList.clear();
		if (c == null) //No cities established
			return masterList;
		possible(new ArrayList<String>(), c, turns, 0);
		return masterList;
	}

	//Generate score based on available units known, or given units
	public static int unitScore(GameEntity en)
	{
		ArrayList<BaseEntity> enemies = new ArrayList<BaseEntity>();
		ArrayList<BaseEntity> rivals = new ArrayList<BaseEntity>();
		int[][] allowed = en.owner.revealed;
		for (Civilization civ: en.location.grid.civs)
		{
			if (civ.equals(en.owner)) continue;
			for (BaseEntity unit: civ.units)
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
	public static int unitScoreWithRival(GameEntity en, ArrayList<BaseEntity> enemies)
	{
		return unitScoreWithUnits(en, enemies, new ArrayList<BaseEntity>());
	}
	public static int unitScoreWithUnits(GameEntity en, ArrayList<BaseEntity> enemies, ArrayList<BaseEntity> rivals) //Calculates score of a unit. Adaptive.
	{
		float sum = 0;
		if (en.name.equals("Settler"))
			sum = 10;
		else if (en.mode == 0)
			return 0;
		else if (en.mode == 1)
			sum = en.offensiveStr + en.defensiveStr;
		else if (en.mode == 2)
			sum = en.rangedStr + en.defensiveStr;
		double averageOffDef = 0; int n = 0;
		//Weighted averages of the off-def potential of a unit versus all adversaries
		for (BaseEntity enemy: enemies)
		{
			if (enemy instanceof GameEntity)
			{
				double[] offDef = ConflictSystem.calcOffDefMod(en, (GameEntity)enemy);
				averageOffDef += offDef[0]*2/offDef[1]; 
				n += 2; //double the weight
			}
		}
		for (BaseEntity rival: rivals)
		{
			if (rival instanceof GameEntity)
			{
				double[] offDef = ConflictSystem.calcOffDefMod(en, (GameEntity)rival);
				averageOffDef += offDef[0]/offDef[1]; 
				n += 1;
			}
		}
		if (n == 0)
			return (int)sum;
		averageOffDef /= n;
		sum *= averageOffDef;
		return (int)sum;
	}

}
