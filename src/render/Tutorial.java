package render;

import java.util.ArrayList;

import processing.core.PApplet;

//A modified copy of an actual game

public class Tutorial extends CivGame {

	public Game game;
	public int step = -1;
	public ArrayList<ArrayList<Character>> path;
	public ArrayList<String> cond;
	public boolean[] keysAllowed = new boolean[200];

	public Tutorial(Game game, float width, float height)
	{
		super(game, 2, 1, "survival", "terrain11", 8700L);
		keysAllowed[97] = true;
		path = new ArrayList<ArrayList<Character>>();
		cond = new ArrayList<String>();

		path.add(list(32));
		cond.add("");
		
		path.add(list(200));
		cond.add("");
	}

	public void setup()
	{
		super.setup();
		step();
	}

	public void draw()
	{
		super.draw();
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
			menuSystem.message("Entered tutorial level");
			menuSystem.message("Press SPACE to cycle through your units");
			break;
		case 1:
			menuSystem.message("End test");
			enable('w','a','s','d');
			break;
		default:
			break;
		}
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
		if (done(cond.get(step)))
			if (path.get(step).size() == 0)
				step();
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
			return true;
		}
	}

}
