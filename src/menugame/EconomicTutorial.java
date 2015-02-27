package menugame;

import game.Tech;
import game.Tile;
import render.Game;
import units.City;

public class EconomicTutorial extends Tutorial {
	
	public EconomicTutorial(Game game, float width, float height) {
		super(game, width, height);
		enabled = false;
	}
	
	public void initialize()
	{
		//if step == 0 then check the following conditions to advance to step 1
		//0
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
		
		//5
		path.add(list(32));
		cond.add("firstCityFourPop");

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
			//grid.civs[0].techTree.allowedUnits.clear();
			//grid.civs[0].techTree.allowedCityImprovements.clear();
			//grid.civs[0].techTree.allowedUnits.add("Warrior");
			enable('w','a','s','d');
			enable('1','2','3','4','5');
			enable((char)32);
			break;
		case 1:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Your city has a number of tiles under its rule.",
					"These tiles produce food, gold, metal, and science",
					"Mouse over the tile with the highest food (green apple) in your city,",
					"and press SPACE at the same time.");
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
			break;
		case 5: 
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Improve the economy of your empire.",
					"Reach population 4 on your first city.");
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
			//Invalid
			if (c.equals("mouseOverHighestFood"))
			{
				if (highestFood == 0) //Calculate if not known
				{
					City city = grid.civs[0].cities.get(0);
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
				City city = grid.civs[0].cities.get(0);
				if (city == null) return false;
				return grid.civs[0].techTree.allowedCityImprovements.contains(city.queue);
			}
			else if (c.equals("firstCityFourPop"))
			{
				City city = grid.civs[0].cities.get(0);
				if (city == null) return false;
				return city.population >= 4;
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
