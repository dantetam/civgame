package render;

import processing.core.PApplet;
import java.util.ArrayList;

import terrain.*;
import system.*;

public class CivGame extends PApplet {

	public Game game;
	public BaseTerrain map;
	public String challengeType;
	public String terrainType;
	public double[][] terrain;
	
	public ArrayList<BaseSystem> systems;
	private RenderSystem renderSystem = new RenderSystem(this);
	
	public CivGame(Game game, String challengeType, String terrainType)
	{
		this.game = game;
		this.challengeType = challengeType;
		this.terrainType = terrainType;
		systems = new ArrayList<BaseSystem>();
		
		systems.add(renderSystem);
	}
	
	public void setup()
	{
		size(1500,900,P3D); //TODO: Processing will not take variables for size(); use a JFrame/PFrame w/ embedded applet to work around this
		generate(terrainType);
		/*for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				print((int)terrain[r][c] + " ");
			}
			println();
		}*/
		
	}
	
	public void draw()
	{
		background(255);
		for (int i = 0; i < systems.size(); i++)
		{
			systems.get(i).tick();
		}
	}
	
	public void stop()
	{
		println("hi");
		game.exit();
		//super.stop();
	}
	
	//Use the appropriate terrain to make a table and then render it by making some entities
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
			System.err.println("No map!");
			int[] err = new int[5]; err[10] = 0;
		}
		renderSystem.addTerrain(terrain);
	}
	
}
