package render;

import processing.core.PApplet;

//A modified copy of an actual game

public class Tutorial extends CivGame {

	public Game game;
	public int step = -1;
	
	public Tutorial(Game game, float width, float height)
	{
		super(game, 2, 1, "survival", "terrain10", 870L);
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
	
	public void step()
	{
		step++;
		switch (step)
		{
		case 0:
			menuSystem.message("Entered tutorial level");
		default:
			
		}
	}
	
	public void keyPressed()
	{
		super.keyPressed();
	}
	
	public void keyReleased()
	{
		super.keyReleased();
	}
	
}
