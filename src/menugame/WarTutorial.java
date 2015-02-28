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
		cond.add("fourUnits");

		path.add(list(32));
		cond.add("");
		
		path.add(empty());
		cond.add("unitInEnemyTerritory");

		//10
		path.add(list(200)); //not a "key"
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
					"Settle a city and train 4 units.");
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
			}
			//enable((char)32);
			break;
		case 2:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Move your units into enemy territory.",
					"(You are at war with all other civilizations.)");
			/*Tech t = grid.civs[0].techTree.researched("Civilization");
			t.units("Settler", "Warrior", "Worker", "Slinger");
			t.cImpr("Obelisk");*/
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
			if (c.equals("fourUnits"))
			{
				int n = 0;
				for (int i = 0; i < p.units.size(); i++)
					if (p.units.get(i).mode == 1)
						n++;
				return n >= 4;
			}
			else
			{
				System.out.println("Invalid condition: " + c);
				return false;
			}
		}
	}

}
