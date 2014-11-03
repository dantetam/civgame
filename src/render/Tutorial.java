package render;

import java.util.ArrayList;

import processing.core.PApplet;

//A modified copy of an actual game

public class Tutorial extends CivGame {

	public Game game;
	public int step = -1;
	public ArrayList<ArrayList<Character>> path;
	
	public Tutorial(Game game, float width, float height)
	{
		super(game, 2, 1, "survival", "terrain11", 8700L);
		path = new ArrayList<ArrayList<Character>>();
		path.add(list(32));
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
	
	public void step()
	{
		step++;
		println("Called " + step);
		switch (step)
		{
		case 0:
			menuSystem.message("Entered tutorial level");
			menuSystem.message("Press SPACE to continue");
			break;
		case 1:
			menuSystem.message("End test");
			println("Step1");
			break;
		default:
			break;
		}
	}
	
	public void keyPressed()
	{
		if (step >= path.size()) return; //Tutorial is over
		if (path.get(step).get(0) == key)
		{
			path.get(step).remove(0);
			if (path.get(step).size() == 0)
				step();
		}
		else
			return; //Wrong key pressed. Restrict access to other keys
	}
	
	public void keyReleased()
	{
		super.keyReleased();
	}
	
}
