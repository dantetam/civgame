package menugame;

import game.Civilization;
import render.Game;
import units.City;

public class AdvancedWarTutorial extends Tutorial {

	public AdvancedWarTutorial(Game game, float width, float height) {
		super(game, width, height);
	}

	public void initialize()
	{
		//0 -> 1
		path.add(empty());
		cond.add("firstCityThreePop");
		
		path.add(empty());
		cond.add("researchedMetalWorking");
		
		path.add(empty());
		cond.add("upgradedUnit");
	}
	
	public void executeStep(int step)
	{
		switch (step)
		{
		case 0:
			menuSystem.messageT(
					"Start up your civilization by reaching population 3.");
			grid.revealPlayer();
			for (int i = 0; i < grid.civs[0].units.size(); i++)
				grid.civs[0].units.get(i).reveal();
			menuSystem.rbox = grid.civs[0].revealedBox();
			enable('w','a','s','d');
			enable('1','2','3','4','5','6','7','8','9','0');
			enable((char)32);
			break;
		case 1:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"While certain technologies unlock new units, some can unlock",
					"new upgrades, which can be used to customize units.",
					"Research Metal Working for copper and iron weapon improvements.",
					"These can increase the offensive and defensive strength of a melee unit.");
			break;
		case 2:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT(
					"Now, in the upgrade, give any melee unit a weapons upgrade.",
					"This makes the unit harder to produce, but it gets a bonus",
					"in offense and defense. It also requires the resource copper",
					"or iron, which are strategic resources.");
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
			if (c.equals("firstCityThreePop"))
			{
				City city = p.cities.get(0);
				if (city == null) return false;
				return city.population >= 3;
			}
			else if (c.equals("researchedMetalWorking"))
			{
				return p.techTree.researched("Metal Working").researched();
			}
			else if (c.equals("upgradedUnit"))
			{
				for (int i = 0; i < )
			}
			else 
			{
				return false;
			}
		}
	}

}
