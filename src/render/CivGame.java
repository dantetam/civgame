package render;

import processing.core.PApplet;

import terrain.*;

public class CivGame extends PApplet {

	public Game game;
	public BaseTerrain map;
	
	public CivGame(Game game, String terrainType)
	{
		this.game = game;
	}
	
	public void setup()
	{
		size(1500,900); //Processing will not take variables for size(); use a JFrame/PFrame
	}
	
	public void draw()
	{
		background(255);
	}
	
}
