package menugame;

import game.Civilization;
import game.Tech;
import render.Game;

public class WarTutorial extends Tutorial {

	public WarTutorial(Game game, float width, float height) {
		super(game, width, height);
	}

	public void initialize()
	{
		//0 -> 1
		path.add(empty());
		cond.add("threeUnits");

		path.add(list(32));
		cond.add("");
		
		path.add(empty());
		cond.add("unitInEnemyTerritory");
		
		path.add(empty());
		cond.add("capturedCity");
		
		path.add(list(32));
		cond.add("");
		
		//5 -> 6
		path.add(empty());
		cond.add("queuedAxeman");

		//last
		path.add(list(200)); //not a "key"
		cond.add("");
	}
	
	public void executeStep(int step)
	{
		switch (step)
		{
		case 0:
			menuSystem.messageT( 
					"Constant war is not a necessary strategy in Tamora",
					"yet it is important to consider. Economic expansion,",
					"especially at higher difficulties, must be acquired through",
					"war.",
					"Settle a city and train 3 units.");
			grid.revealPlayer();
			for (int i = 0; i < grid.civs[0].units.size(); i++)
				grid.civs[0].units.get(i).reveal();
			menuSystem.rbox = grid.civs[0].revealedBox(); //Force update
			//Tech tech = grid.civs[0].techTree.researched("Civilization");
			grid.civs[0].techTree.allowedUnits.clear();
			grid.civs[0].techTree.allowedCityImprovements.clear();
			grid.civs[0].techTree.allowedUnits.add("Warrior");
			enable('w','a','s','d');
			enable('1','2','3','4','5');
			enable((char)32);
			//Make the civs passive
			for (int i = 1; i < grid.civs.length; i++)
			{
				grid.civs[i].war = 0;
				grid.civs[i].peace = 1;
				grid.civs[i].tallwide = 0;
			}
			break;
		case 1:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT( 
					"Certain technologies can be unlocked that allow for powerful units.",
					"These units have different strengths, weaknesses, and costs.",
					"Press SPACE to continue.");
			for (int i = 1; i < grid.civs.length; i++)
			{
				grid.civs[0].war(grid.civs[i]);
				for (int j = 0; j < grid.civs[i].cities.size(); j++)
				{
					grid.civs[i].cities.get(j).health = 5;
					grid.civs[i].cities.get(j).maxHealth = 5;
				}
			}
			//enable((char)32);
			break;
		case 2:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Move your 3 units into enemy territory.",
					"(You are at war with all other civilizations.)");
			/*Tech t = grid.civs[0].techTree.researched("Civilization");
			t.units("Settler", "Warrior", "Worker", "Slinger");
			t.cImpr("Obelisk");*/
			break;
		case 3:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Your units are ready to attack.",
					"Capture the city.");
			break;
		case 4:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"You have a new addition to your empire.",
					"The city will be hostile for a period and will not queue new units.",
					"In reality, capturing cities will not be as easy.",
					"Press SPACE to continue.");
			break;
		case 5:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Some technologies unlock new powerful units.",
					"Research Mining and queue an axeman.");
		case 6:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"This concludes the tutorial on basic warfare.",
					"The next tutorial covers advanced warfare.");
			break;
		default:
			break;
		}
	}

	public boolean done(String c)
	{
		Civilization p = grid.civs[0];
		if (c == null || c.equals(""))
		{
			return true;
		}
		else
		{ 
			if (c.equals("threeUnits"))
			{
				int n = 0;
				for (int i = 0; i < p.units.size(); i++)
					if (p.units.get(i).mode == 1)
						n++;
				return n >= 3;
			}
			else if (c.equals("queuedAxeman"))
			{
				for (int i = 0; i < p.cities.size(); i++)
					if (p.cities.get(i).queue.equals("Axeman"))
						return true;
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

}
