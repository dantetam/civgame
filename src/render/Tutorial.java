package render;

import java.util.ArrayList;

import processing.core.PApplet;

//A modified copy of an actual game

public class Tutorial extends CivGame {

	public Game game;
	public int step = -1;
	public ArrayList<ArrayList<Character>> path;
	public ArrayList<String> cond;
	public boolean[] keysAllowed = new boolean[256];

	public Tutorial(Game game, float width, float height)
	{
		super(game, 2, 1, "survival", "terrain11", "Athens", 8700L);
		//keysAllowed[97] = true;
		path = new ArrayList<ArrayList<Character>>();
		cond = new ArrayList<String>();

		path.add(list(32));
		cond.add("");
		
		path.add(list(32));
		cond.add("");
		
		path.add(empty());
		cond.add("playerHasOneCity");
		
		path.add(list(32));
		cond.add("");
		
		path.add(list(200)); //not a "key"
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
		ArrayList<Character> temp = new ArrayList<Character>();
		return temp;
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

	public void step()
	{
		step++;
		println("Called " + step);
		switch (step)
		{
		case 0:
			menuSystem.messageT("When ready, press SPACE to control a unit.");
			menuSystem.messageT("Use WASD to move the camera around the map");
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
			break;
		case 3:
			menuSystem.messageT("------------------------------------------");
			menuSystem.messageT("This will allow you to give orders to those who need them.");
			menuSystem.messageT("Press SPACE again to cycle through your units.");
			break;
		default:
			break;
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
		{
			super.keyPressed();
		}
		//else
			//return; //Wrong key pressed. Restrict access to other keys
		//check();
	}

	public void keyReleased()
	{
		super.keyReleased();
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
			else
			{
				System.out.println("Invalid condition");
				return false;
			}
		}
	}

	public void mousePressed()
	{
		super.mousePressed();
	}
	
}
