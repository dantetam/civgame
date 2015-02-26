package menugame;

import game.Tech;
import render.Game;

public class EconomicTutorial extends Tutorial {

	public EconomicTutorial(Game game, float width, float height) {
		super(game, width, height);
	}
	
	public void initialize()
	{
		//0
		path.add(empty());
		cond.add("playerHasOneCity");

		path.add(list(32));
		cond.add("");

		path.add(empty());
		cond.add("playerHasOneCity");

		/*path.add(list(32));
		cond.add("");*/

		path.add(empty());
		cond.add("cityQueueWarrior");

		//5
		path.add(list(32));
		cond.add("");

		path.add(empty());
		cond.add("researchingTech");

		path.add(list(32));
		cond.add("");

		path.add(empty());
		cond.add("unitAndCity");

		path.add(empty());
		cond.add("unitOutsideBorders");

		//10
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
			break;
		case 1:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT("Press SPACE to continue.");
			menuSystem.messageT("Cities produce units and improvements for the civilization.");
			menuSystem.messageT("It is a settler, which can found a city.");
			menuSystem.messageT("This is a unit belonging to your civilization.");
			//enable((char)32);
			break;
		case 2:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT("It will be founded at your settler's location.");
			menuSystem.messageT("In the provided menu, press SETTLE to create a new city.");
			enable((char)32);
			enable('1','2','3','4','5');
			break;
		/*case 3:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT("This will allow you to give orders to those who need them.");
			menuSystem.messageT("Press SPACE again to cycle through your units.");
			break;*/
		case 3:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT("which are used for creating new units. Queue a WARRIOR.");
			menuSystem.messageT("It harvests the tiles around it for food and metal,");
			menuSystem.messageT("This is the city menu for your first city.");
			break;
		case 4:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT("if all your units have orders.");
			menuSystem.messageT("press SPACE. This will advance to next turn,");
			menuSystem.messageT("Your unit will be produced soon. To progress the game forward,");
			break;
		case 5: 
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT("Click on any technology to research it.");
			menuSystem.messageT("Before you can go on, your civilization must research a tech.");
			break;
		case 6:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT("Now, you may advance the game with SPACE.");
			break;
		case 7:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT("until your unit is completed.");
			menuSystem.messageT("Keep pressing SPACE to advance the game");
			break;
		case 8:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT("Move it outside of your territory with RMB when selecting it.");
			menuSystem.messageT("Later units may have ranged strength.");
			menuSystem.messageT("It has offensive and defensive values.");
			menuSystem.messageT("Your city has produced its first combat unit.");
			Tech t = grid.civs[0].techTree.researched("Civilization");
			t.units("Settler", "Warrior", "Worker", "Slinger");
			t.cImpr("Obelisk");
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
			if (c.equals("playerHasOneCity"))
			{
				return grid.civs[0].cities.size() > 0;
			}
			else if (c.equals("cityQueueWarrior"))
			{
				if (grid.civs[0].cities.get(0).queue != null)
					return grid.civs[0].cities.get(0).queue.equals("Warrior");
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
