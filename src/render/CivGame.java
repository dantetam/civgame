package render;

import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;

import terrain.*;
import system.*;
import entity.Player;
import game.Grid;
import game.Tile;

public class CivGame extends PApplet {

	public Game game;

	public BaseTerrain map;
	public String challengeType;
	public String terrainType;
	public int numCivs;
	public double[][] terrain;
	public Erosion erosion;

	public Grid grid;

	public Player player = new Player();

	public ArrayList<BaseSystem> systems;
	private RenderSystem renderSystem = new RenderSystem(this);
	public PGraphics pg;
	private MenuSystem menuSystem = new MenuSystem(this);
	
	private InputSystem inputSystem = new InputSystem(this);
	public CivilizationSystem civilizationSystem = new CivilizationSystem(this);
	public ChunkSystem chunkSystem;

	public CivGame(Game game, int numCivs, String challengeType, String terrainType)
	{
		this.game = game;
		this.numCivs = numCivs;
		this.challengeType = challengeType;
		this.terrainType = terrainType;

		systems = new ArrayList<BaseSystem>();

		systems.add(renderSystem);
		systems.add(inputSystem);
		systems.add(civilizationSystem);
		systems.add(menuSystem);
	}

	public void setup()
	{
		size(1500,900,P3D); //TODO: Processing will not take variables for size(); use a JFrame/PFrame w/ embedded applet to work around this
		pg = createGraphics(1500,900);
		background(0,225,255);
		camera(500,500,500,0,0,0,0,-1,0);
		box(100,100,100);
		redraw();
		generate(terrainType);
		/*for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				print((int)terrain[r][c] + " ");
			}
			println();
		}*/
		chunkSystem = new ChunkSystem(this);
		systems.add(chunkSystem);
		erosion = new Erosion(terrain,1);
		erode();
		chunkSystem.tick();
	}

	public void draw()
	{
		background(255);
		for (int i = 0; i < systems.size(); i++)
		{
			systems.get(i).tick();
		}
	}

	public void keyPressed()
	{
		inputSystem.queueKey(key);
		//inputSystem.test();
	}

	public void keyReleased()
	{
		inputSystem.keyReleased(key);
	}

	public void stop()
	{
		println("hi");
		game.exit();
		//super.stop();
	}

	//Use the appropriate terrain to make a table and then render it by making some entities
	//Then make a grid out of it
	public void generate(String terrainType)
	{
		float con; float cutoff;
		if (terrainType.equals("terrain1"))
		{
			map = new PerlinNoise(870L);
			terrain = map.generate(new double[]{32,32,150,8,1,0.8,6,256});
			con = 1F;
			cutoff = 55;
		}
		else if (terrainType.equals("terrain2"))
		{
			map = new RecursiveBlock(87069200L);
			terrain = map.generate(new double[]{10,0});
			con = 3F;
			cutoff = 1;
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
			con = 1F;
			cutoff = 40;
		}
		else if (terrainType.equals("terrain4"))
		{
			map = new RecursiveBlock(87069200L);
			terrain = map.generate(new double[]{10,1});
			con = 3F;
			cutoff = 1;
		}
		else
		{
			System.err.println("No map!");
			int[] err = new int[5]; err[10] = 0;
			con = 1F;
			cutoff = 0;
		}
		grid = new Grid(terrain, numCivs, (int)cutoff);
		//grid.setupTiles(terrain);
		//grid.setupCivs();
		renderSystem.addTerrain(terrain, con, cutoff);
	}

	public void erode()
	{
		for (int i = 0; i < 100; i++)
		{
			int r = 0; int c = 0; 
			do
			{
				r = (int)(terrain.length*Math.random());
				c = (int)(terrain.length*Math.random());
			} while (terrain[r][c] < erosion.cutoff);
			erosion.flood(r,c,15);
		}
		boolean done = false;
		while (!done)
		{
			done = !erosion.tick();
		}
	}

	public float widthBlock() {return renderSystem.widthBlock;}
	public void setUpdateFrame(int frames) {chunkSystem.updateFrame = frames;}
	
}
