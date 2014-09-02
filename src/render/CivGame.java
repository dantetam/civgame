package render;

import processing.core.PApplet;
import terrain.*;

public class CivGame extends PApplet {

	public Game game;
	public BaseTerrain map;
	public String terrainType;
	public double[][] terrain;
	
	public CivGame(Game game, String terrainType)
	{
		this.game = game;
		this.terrainType = terrainType;
	}
	
	public void setup()
	{
		size(1500,900); //TODO: Processing will not take variables for size(); use a JFrame/PFrame w/ embedded applet to work around this
		generate(terrainType);
	}
	
	public void draw()
	{
		background(255);
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				print((int)terrain[r][c] + " ");
			}
			println();
		}
		noLoop();
	}
	
	public void stop()
	{
		println("hi");
		game.exit();
		//super.stop();
	}
	
	public void generate(String terrainType)
	{
		if (terrainType.equals("terrain1"))
		{
			map = new PerlinNoise(870L);
			terrain = map.generate(new double[]{64,64,150,8,1,0.8,6,256});
		}
		else if (terrainType.equals("terrain2"))
		{
			map = new RecursiveBlock(87069200L);
			terrain = map.generate(new double[]{10});
		}
		else if (terrainType.equals("terrain3"))
		{
			int len = 128;
			double[][] temp = DiamondSquare.makeTable(50,50,50,50,len+1);
			map = new DiamondSquare(temp);
			//ds.diamond(0, 0, 4);
			//displayTables = ds.dS(0, 0, len, 40, 0.7)
			map.seed(870);
			terrain = map.generate(new double[]{0, 0, len, 40, 0.7});
			print(terrain);
		}
		else
		{
			println("No map!");
		}
	}
	
}
