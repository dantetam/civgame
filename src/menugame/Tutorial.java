package menugame;

import game.Civilization;
import game.Tech;

import java.util.ArrayList;

import processing.core.PApplet;
import render.CivGame;
import render.Game;

//A modified copy of an actual civgame

//Override these methods to make a new tutorial:
/*
 * void initialize() -> Initialize keys or conditions needed to pass to the next step
 *     path.add(list(...) or empty()) -> Add a set of keys needed to be pressed
 *     cond.add("") -> Add a condition string
 * void executeStep(int step) -> Allow tutorial to do something when a step is reached
 *     enable(...) -> Enable a set of keys
 * 	   menuSystem.messageT(...) -> Write a message
 * boolean done(String s) -> Check a string with its conditions (if the string is x, then return if some condition is true)
 * 
 */

public class Tutorial extends CivGame {

	public Game game;
	public int step = -1;
	public ArrayList<ArrayList<Character>> path;
	public ArrayList<String> cond;
	public boolean[] keysAllowed = new boolean[256];

	public Tutorial(Game game, float width, float height)
	{
		super(game, 2, 1, 1, "survival", "terrain11", "Athens", 8700L);
		//keysAllowed[97] = true;
		//super.options(true, false);
		path = new ArrayList<ArrayList<Character>>();
		cond = new ArrayList<String>();
		initialize();
	}
	
	public void step()
	{
		step++;

		//println("Called " + step);
		executeStep(step);
	}
	
	//Unique
	public void initialize()
	{
		//0
		path.add(list(32));
		cond.add("");

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
	
	//Unique
	public void executeStep(int step)
	{
		switch (step)
		{
		case 0:
			menuSystem.messageT( 
					"Use WASD to move the camera around the map",
					"When ready, press SPACE to continue.");
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

	//Unique
	public boolean done(String c)
	{
		Civilization p = grid.civs[0];
		if (c == null || c.equals(""))
		{
			return true;
		}
		else
		{
			//Invalid
			if (c.equals("playerHasOneCity"))
			{
				return p.cities.size() > 0;
			}
			else if (c.equals("cityQueueWarrior"))
			{
				if (p.cities.get(0).queue != null)
					return p.cities.get(0).queue.equals("Warrior");
				return false;
			}
			else if (c.equals("researchingTech"))
			{
				return p.researchTech != null &&
						p.researchTech != "";
			}
			else if (c.equals("unitAndCity"))
			{
				return p.cities.size() > 0 &&
						p.units.size() > 0;
			}
			else if (c.equals("unitOutsideBorders"))
			{
				for (int i = 0; i < p.units.size(); i++)
					if (p.units.get(i).owner == null)
						return true;
				return false;
			}
			else if (c.equals("unitInEnemyTerritory"))
			{
				int n = 0;
				for (int i = 0; i < p.units.size(); i++)
					if (p.units.get(i).location.owner.isWar(p))
						n++;
				return n >= 4;
			}
			else if (c.equals("capturedCity"))
			{
				for (int i = 0; i < p.cities.size(); i++)
					if (p.cities.get(i).takeover > 0)
						return true;
				return false;
			}
			else
			{
				System.out.println("Invalid condition: " + c);
				return false;
			}
		}
	}

	public void check()
	{
		if (step >= cond.size()) return;
		if (done(cond.get(step)))
			if (path.get(step).size() == 0)
				step();
	}

	public void keyPressed()
	{
		if (step < path.size())
		{
			if (path.get(step).size() != 0)
				if (path.get(step).get(0) == key)
				{
					super.keyPressed();
					path.get(step).remove(0);
				}
		}
		if (keysAllowed[(int)key])
			super.keyPressed();
		//else
		//return; //Wrong key pressed. Restrict access to other keys
		//check();
	}

	public void keyReleased()
	{
		super.keyReleased();
	}

	public void setup()
	{
		super.setup();
		step();
	}

	public void draw()
	{
		super.draw();
		check();
	}

	public ArrayList<Character> list(char... keys)
	{
		ArrayList<Character> temp = new ArrayList<Character>();
		for (int i = 0; i < keys.length; i++)
			temp.add(keys[i]);
		return temp;
	}

	public ArrayList<Character> list(int... keys)
	{
		ArrayList<Character> temp = new ArrayList<Character>();
		for (int i = 0; i < keys.length; i++)
			temp.add((char)keys[i]);
		return temp;
	}

	public ArrayList<Character> empty()
	{
		//ArrayList<Character> temp = new ArrayList<Character>();
		return new ArrayList<Character>();
	}

	public void enable(char... keys)
	{
		for (int i = 0; i < keys.length; i++)
			keysAllowed[(int)keys[i]] = true;
	}

	public void disable(char... keys)
	{
		for (int i = 0; i < keys.length; i++)
			keysAllowed[(int)keys[i]] = false;
	}

	public void mousePressed()
	{
		super.mousePressed();
	}

}
