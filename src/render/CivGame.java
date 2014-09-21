package render;

import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.Random;

import data.Color;
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
	//public boolean[][] rivers;
	public boolean[][] verticalRivers;
	public boolean[][] horizontalRivers;
	public Erosion erosion;

	public Grid grid;
	public long seed; 

	public Player player = new Player();

	public ArrayList<BaseSystem> systems;
	private RenderSystem renderSystem = new RenderSystem(this);
	//public PGraphics pg;
	private MenuSystem menuSystem = new MenuSystem(this);

	private InputSystem inputSystem = new InputSystem(this);
	public CivilizationSystem civilizationSystem = new CivilizationSystem(this);
	public ChunkSystem chunkSystem;

	public CivGame(Game game, int numCivs, String challengeType, String terrainType, long seed)
	{
		this.game = game;
		this.numCivs = numCivs;
		this.challengeType = challengeType;
		this.terrainType = terrainType;

		this.seed = seed;

		systems = new ArrayList<BaseSystem>();

		systems.add(inputSystem);
		systems.add(civilizationSystem);
		systems.add(renderSystem);
		systems.add(menuSystem);
	}

	public void setup()
	{
		size(1500,900,P3D); //TODO: Processing will not take variables for size(); use a JFrame/PFrame w/ embedded applet to work around this
		//pg = createGraphics(1500,900,P2D);
		frameRate(25);
		background(0,225,255);
		camera(500,500,500,0,0,0,0,-1,0);
		box(100,100,100);
		redraw();
		generate(terrainType);
		//makeRivers(terrain);
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
		inputSystem.passMouse(mouseX, mouseY);
		for (int i = 0; i < systems.size(); i++)
		{
			systems.get(i).tick();
		}
	}

	public void mousePressed()
	{
		menuSystem.queueClick(mouseX, mouseY);
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

	public void fill(Color c)
	{
		fill((float)c.r*255F,(float)c.g*255F,(float)c.b*255F);
	}

	public void stop()
	{
		println("hi");
		game.exit();
		//super.stop();
	}

	//Use the appropriate terrain to make a table and then render it by making some entities
	//Then make a grid out of it
	public float cutoff;
	public void generate(String terrainType)
	{
		float con;
		if (terrainType.equals("terrain1"))
		{
			map = new PerlinNoise(seed);
			terrain = map.generate(new double[]{32,32,150,8,1,0.8,6,256,55});
			con = 1F;
			cutoff = 55;
		}
		else if (terrainType.equals("terrain2"))
		{
			map = new RecursiveBlock(seed);
			terrain = map.generate(new double[]{10,0});
			con = 1F;
			cutoff = 1;
		}
		else if (terrainType.equals("terrain3"))
		{
			int len = 128;
			double[][] temp = DiamondSquare.makeTable(50,50,50,50,len+1);
			map = new DiamondSquare(temp);
			//ds.diamond(0, 0, 4);
			//displayTables = ds.dS(0, 0, len, 40, 0.7)
			map.seed(seed);
			terrain = map.generate(new double[]{0, 0, len, 40, 0.7});
			print(terrain);
			con = 1F;
			cutoff = 40;
		}
		else if (terrainType.equals("terrain4"))
		{
			map = new RecursiveBlock(seed);
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
		int[][] biomes = assignBiome(terrain);
		grid = new Grid(terrain, biomes, assignResources(biomes), numCivs, (int)cutoff);
		makeRivers(biomes); 
		
		//grid.setupTiles(terrain);
		//grid.setupCivs();
		//renderSystem.addTerrain(terrain, con, cutoff);
	}

	public int[][] assignBiome(double[][] terrain)
	{
		int[][] temp = new int[terrain.length][terrain[0].length];
		double width = Math.max(
				Math.pow(2,Math.floor(Math.log10(terrain.length)/Math.log10(2)) + 1),
				Math.pow(2,Math.floor(Math.log10(terrain[0].length)/Math.log10(2)) + 1)
				);
		double[][] temperature = assignTemperature(width);
		double[][] rain = assignRain(temperature);
		for (int r = 0; r < temp.length; r++)
		{
			for (int c = 0; c < temp[0].length; c++)
			{
				if (terrain[r][c] >= cutoff)
				{
					/*System.out.println("------");
					System.out.println(temp.length + " " + temp[0].length);
					System.out.println(terrain.length + " " + terrain[0].length);
					System.out.println(temperature.length + " " + rain.length);*/
					temp[r][c] = returnBiome(temperature[r][c],rain[r][c]);
				}
				else
					temp[r][c] = -1;
			}
		}
		/*for (int r = 0; r < temp.length; r++)
		{
			for (int c = 0; c < temp[0].length; c++)
			{
				System.out.print(temp[r][c] + " ");
			}
			System.out.println();
		}*/
		return temp;
	}

	public void makeRivers(int[][] biomes)
	{
		verticalRivers = new boolean[biomes.length][biomes.length - 1];
		horizontalRivers = new boolean[biomes.length - 1][biomes.length];
		for (int r = 0; r < verticalRivers.length; r++)
		{
			for (int c = 0; c < verticalRivers[0].length; c++)
			{
				if (biomes[r][c] >= 1 && biomes[r][c+1] >= 1)
				{
					if (Math.random() < 0.02*biomes[r][c])
					{
						verticalRivers[r][c] = true;
					}
				}
			}
		}
		for (int r = 0; r < horizontalRivers.length; r++)
		{
			for (int c = 0; c < horizontalRivers[0].length; c++)
			{
				if (biomes[r][c] >= 1 && biomes[r+1][c] >= 1)
				{
					if (Math.random() < 0.02*biomes[r][c])
					{
						horizontalRivers[r][c] = true;
					}
				}
			}
		}
		grid.verticalRivers = verticalRivers;
		grid.horizontalRivers = horizontalRivers;
	}

	public int[][] assignResources(int[][] biomes)
	{
		int[][] resources = new int[biomes.length][biomes[0].length];
		for (int r = 0; r < biomes.length; r++)
		{
			for (int c = 0; c < biomes[0].length; c++)
			{
				int b = biomes[r][c];
				boolean[] candidates = new boolean[100];
				if (b == -1)
				{
					candidates[10] = true;
					candidates[11] = true;
				}
				else if (b == 0)
				{
					candidates[20] = true;
					candidates[21] = true;
					candidates[22] = true;
					candidates[40] = true;
				}
				else if (b == 1)
				{
					candidates[20] = true;
					candidates[21] = true;
					candidates[22] = true;
					candidates[40] = true;
				}
				else if (b == 2)
				{
					candidates[20] = true;
					candidates[21] = true;
					candidates[22] = true;
					candidates[40] = true;
				}
				else if (b == 3)
				{
					candidates[1] = true;

					candidates[20] = true;
					candidates[22] = true;
					candidates[40] = true;
				}
				else if (b == 4)
				{
					candidates[1] = true;

					candidates[20] = true;
					candidates[22] = true;
					candidates[30] = true;
					candidates[40] = true;
				}
				else if (b == 5)
				{
					candidates[1] = true;

					candidates[22] = true;
					candidates[30] = true;
					candidates[40] = true;
				}
				else if (b == 6)
				{
					candidates[1] = true;
					candidates[2] = true;

					candidates[22] = true;
					candidates[30] = true;
					candidates[40] = true;
				}
				else if (b == 8)
				{

				}
				for (int i = 0; i < candidates.length; i++)
				{
					if (candidates[i])
					{
						if (Math.random() < 0.0125)
						{
							resources[r][c] = i;
						}
					}
				}
			}
		}
		/*for (int r = 0; r < resources.length; r++)
		{
			for (int c = 0; c < resources[0].length; c++)
			{
				System.out.print(resources[r][c] + " ");
			}
			System.out.println();
		}*/
		return resources;
	}

	//The three methods below stolen from "blockgame"
	//Returns an interpolated map which gives each chunk a temperature, 0 to 4 (arctic to tropical)
	public double[][] assignTemperature(double nDiv)
	{
		//int chunkLength = rows; //chunkService.returnChunkLength();
		//double[][] oldSource = new PerlinNoise(870).makePerlinNoise((int)nDiv,(int)nDiv,3,8,3,0.5,2);
		//return PerlinNoise.expand(oldSource,nDiv*2);
		double[][] oldSource = new PerlinNoise(seed).generate(new double[]{32,32,3,16,3,1,3,nDiv});
		return oldSource;
	}

	//Returns an interpolated map which gives each chunk a level of rain, based on temperature
	//Arctic climates do not have rain, tropical climates can have any level
	Random rainRandom = new Random(seed);
	public double[][] assignRain(double[][] temperature)
	{
		double[][] returnThis = new double[temperature.length][temperature[0].length];
		for (int i = 0; i < temperature.length; i++)
		{
			for (int j = 0; j < temperature[0].length; j++)
			{
				returnThis[i][j] = rainRandom.nextDouble()*temperature[i][j] + rainRandom.nextDouble();
			}
		}
		//returnThis = PerlinNoise.recurInter(returnThis,1,returnThis.length/2);
		return returnThis;
	}

	//returns the biome based on temperature, t, and rain, r
	/*
	 * 0 ice
	 * 1 taiga
	 * 2 desert
	 * 3 savannah
	 * 4 dry forest
	 * 5 forest
	 * 6 rainforest
	 * 7 beach (outdated)
	 */
	public int returnBiome(double t, double r)
	{
		if (t > 3)
		{
			if (r > 3.5)
				return 6;
			else if (r > 2.5)
				return 5;
			else if (r > 1.5)
				return 4;
			/*else if (r > 1.25)
				return 3;*/
			else 
				return 2;
		}
		else if (t > 2)
		{
			if (r > 2)
				return 5;
			else if (r > 1.5)
				return 4;
			else if (r > 0.75)
				return 3;
			else 
				return 2;
		}
		else if (t > 1)
		{
			if (r > 1)
				return 4;
			if (r > 0.5)
				return 3;
			else 
				return 2;
		}
		else
		{
			if (r > 0.5)
				return 1;
			else 
			{
				//System.out.println(t + " " + r);
				return 0;
			}
		}
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
