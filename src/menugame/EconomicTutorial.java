package menugame;

import game.Civilization;
import game.GameEntity;
import game.Tech;
import game.Tile;
import render.Game;
import units.City;
import units.Worker;

public class EconomicTutorial extends Tutorial {
	
	public EconomicTutorial(Game game, float width, float height) {
		super(game, width, height);
		enabled = true;
	}
	
	public void initialize()
	{
		//if step == 0 then check the following conditions to advance to step 1
		//0 -> 1
		path.add(empty());
		cond.add("playerHasOneCity");

		path.add(list(32));
		cond.add("mouseOverHighestFood");

		path.add(list(32));
		cond.add("");
		
		path.add(list(32));
		cond.add("");
		
		path.add(empty());
		cond.add("queueBuilding");
		
		//5 -> 6
		path.add(empty());
		cond.add("researchAgriculture");
		
		path.add(empty());
		cond.add("firstCityFourPop");

		path.add(empty());
		cond.add("workerOnTile");
		
		path.add(empty());
		cond.add("buildImprovement");
		
		//last -> end tutorial
		path.add(list(200)); //not a "key"
	}
	
	public void executeStep(int step)
	{
		switch (step)
		{
		case 0:
			menuSystem.messageT(
					"This tutorial will teach you about the economics", 
					"of this world. Found a city first.");
			grid.revealPlayer();
			for (int i = 0; i < grid.civs[0].units.size(); i++)
				grid.civs[0].units.get(i).reveal();
			menuSystem.rbox = grid.civs[0].revealedBox(); //Force update
			//Tech tech = grid.civs[0].techTree.researched("Civilization");
			grid.civs[0].techTree.allowedUnits.clear();
			grid.civs[0].techTree.allowedCityImprovements.clear();
			grid.civs[0].techTree.allowedUnits.add("Warrior");
			enable('w','a','s','d');
			//enable('1','2','3','4','5');
			enable((char)32);
			break;
		case 1:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Your city has a number of tiles under its rule.",
					"These tiles produce food, gold, metal, and science",
					"Mouse over the tile with the highest food (green apple) in your city,",
					"and press SPACE at the same time.");
			enabled = false;
			break;
		case 2:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Certain tiles are worked, meaning that the city gains",
					"the yield, which is used on units and buildings.",
					"Press SPACE to continue.");
			break;
		case 3:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"The number of tiles worked depends on the population",
					"and the number of free workers. Others specialize.",
					"A city's population affects health and happiness,",
					"which in turn, affect production efficiency.",
					"Press SPACE to continue.");
			break;
		case 4:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Units, such as Warriors, can move and perform actions.",
					"Buildings improve city yields or enable certain city actions.",
					"Queue a building.");
			enabled = true;
			break;
		case 5: 
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Agriculture is an early game technology that enables growth.",
					"Research the tech Agriculture.");
			break;
		case 6: 
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Improve the economy of your empire.",
					"Reach population 4 on your first city by advancing turns.");
			break;
		case 7:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Another way to improve yield is to build improvements on tiles.",
					"To do so, train a worker, and move it to a tile outside the city.");
			break;
		case 8:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Tile improvements increase the yield of individual tiles.",
					"Choose improvements carefully; every improvement has an appropriate tile to be built on.",
					"For example, a fresh water forest would be suited for a farm.",
					"Build any improvement with a worker.");
			break;
		case 9:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"End of tutorial");
			break;
		default:
			break;
		}
	}

	public boolean done(String c)
	{
		if (c == null || c.equals(""))
		{
			return true;
		}
		else
		{
			Civilization p = grid.civs[0];
			if (c.equals("mouseOverHighestFood"))
			{
				if (highestFood == 0) //Calculate if not known
				{
					City city = p.cities.get(0);
					if (city == null) return false;
					for (int i = 0; i < city.land.size(); i++)
					{
						double[] yield = City.staticEval(city.land.get(i));
						if (yield[0] > highestFood)
							highestFood = (int)yield[0];
					}
				}
				if (menuSystem.mouseHighlighted != null)
				{
					double[] yield = City.staticEval(menuSystem.mouseHighlighted);
					System.out.println(yield[0] + " " + highestFood);
					if ((int)yield[0] >= highestFood)
						return true;
				}
				return false;
			}
			else if (c.equals("queueBuilding"))
			{
				City city = p.cities.get(0);
				if (city == null) return false;
				return p.techTree.allowedCityImprovements.contains(city.queue);
			}
			else if (c.equals("firstCityFourPop"))
			{
				City city = p.cities.get(0);
				if (city == null) return false;
				return city.population >= 4;
			}
			else if (c.equals("researchAgriculture"))
			{
				return p.researchTech.equals("Agriculture");
			}
			else if (c.equals("workerOnTile"))
			{
				for (int i = 0; i < p.units.size(); i++)
				{
					GameEntity en = p.units.get(i);
					if (en instanceof Worker)
						if (!(en.location.improvement instanceof City))
							return true;
				}
				return false;
			}
			else if (c.equals("buildImprovement"))
			{
				for (int i = 0; i < p.units.size(); i++)
				{
					GameEntity en = p.units.get(i);
					if (en instanceof Worker)
						if (en.queue != null)
							if (!en.queue.isEmpty())
								return true;
				}
				return false;
			}
			else
			{
				//System.out.println("Invalid condition: " + c);
				//return false;
				return super.done(c);
			}
		}
	}
	
	private int highestFood = 0;

}
