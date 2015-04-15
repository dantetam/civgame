package render;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import data.Color;
import data.ColorImage;
import data.EntityData;
import terrain.*;
import system.*;
import entity.Player;
import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Grid;
import game.Pathfinder;
import game.Tile;

public class CivGame extends PApplet {

	public Game game;
	public boolean enabled = true;
	
	public BaseTerrain map;
	public String challengeType, terrainType, civChoice;
	public int numCivs, numCityStates, difficultyLevel;
	public double[][] terrain;
	//public boolean[][] rivers;
	public Erosion erosion;

	public Grid grid;
	public long seed; 

	public Player player = new Player();
	public boolean showAll = false;

	public ArrayList<BaseSystem> systems;
	private RenderSystem renderSystem = new RenderSystem(this);
	public float width = 1500, height = 900;
	public float centerX = width/2, centerY = height/2; //for rendering purposes, to determine how the position of the mouse affects the camera
	public PGraphics pg;
	public MenuSystem menuSystem = new MenuSystem(this);
	public NewMenuSystem newMenuSystem = new NewMenuSystem(this);
	public PShader lightShader, texLightShader;
	public PFont arial, dvs;

	public InputSystem inputSystem = new InputSystem(this);
	public CivilizationSystem civilizationSystem = new CivilizationSystem(this);
	public ChunkSystem chunkSystem;

	public boolean testing = false, tacticalView = false, keyMenu = false; //tacticalView -> display special tile yield and tile ownership GUIs

	public CivGame(Game game, int numCivs, int numCityStates, int difficultyLevel, String challengeType, String terrainType, String civChoice, long seed)
	{
		this.game = game;
		this.numCivs = numCivs;
		this.numCityStates = numCityStates;
		this.difficultyLevel = difficultyLevel;
		this.challengeType = challengeType;
		this.terrainType = terrainType;
		this.civChoice = civChoice;

		this.seed = seed;

		systems = new ArrayList<BaseSystem>();

		systems.add(civilizationSystem);
		systems.add(renderSystem);
		systems.add(menuSystem);
		systems.add(newMenuSystem);
		systems.add(inputSystem);
	}

	public void options(boolean autoSelect, boolean testing)
	{
		inputSystem.autoSelect = autoSelect;
		this.testing = testing;
	}

	public void setup()
	{
		size((int)width,(int)height,P3D);
		//size(1500,900,P3D); //TODO: Processing will not take variables for size(); use a JFrame/PFrame w/ embedded applet to work around this
		//frameRate(60);
		arial = createFont("ArialMT-48.vlw", 48);
		//dvs = createFont("DejaVuSansMono-48.vlw", 48);
		dvs = createFont("ProggyClean.ttf", 48);
		textFont(arial);
		//pg = createGraphics(1500,900,P2D);
		lightShader = loadShader("fragtest.glsl", "verttest.glsl");
		texLightShader = loadShader("fragtest2", "verttest2");
		frameRate(25);
		background(150,225,255);
		camera(500,500,500,0,0,0,0,-1,0);
		box(100,100,100);
		//redraw();
		generate(terrainType);
		//Force update
		menuSystem.rbox = grid.civs[0].revealedBox();
		
		menuSystem.techMenu = new TechMenu(grid.civs[0].techTree, "TechMenu");
		menuSystem.menus.add(menuSystem.techMenu); //Not including it is a violation of OOP principles
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

		//Set it manually
		player.civ = grid.civs[0];
		player.civ.name = "Player";
		player.orient(grid);
		inputSystem.on = false;
		menuSystem.select(null); //Fix the selection menu
		chunkSystem.update(); //Update once
		Tile t = grid.civs[0].units.get(0).location; //First settler
		fixCamera(t.row, t.col); //Center the camera at the appropriate location

		for (int i = 0; i < grid.civs.length; i++)
		{
			Civilization civ = grid.civs[i];
			civ.war = Math.min(1, civ.war*2);
			civ.tallwide = Math.min(0, civ.tallwide/2);
		}
		/*if (testing)
		{
			for (int i = 0; i < grid.civs.length; i++)
			{
				Civilization civ = grid.civs[i];
				civ.war = Math.min(1, civ.war*2);
				civ.tallwide = Math.min(0, civ.tallwide/2);
			}
		}*/
		
		pg = createGraphics((int)width, (int)height, P3D);
	}

	public void draw()
	{
		background(255);
		inputSystem.passMouse(mouseX, mouseY);
		menuSystem.queueMousePass(mouseX, mouseY);
		for (int i = 0; i < systems.size(); i++)
		{
			systems.get(i).tick();
		}
		if (frameCount == 10 && inputSystem.autoSelect)
			inputSystem.queueKey((char)32);
		newMenuSystem.lastMouseX = mouseX; newMenuSystem.lastMouseY = mouseY;
	}

	public boolean newLine = false;
	public float rMouseX = -1, rMouseY = -1;
	public void mousePressed()
	{
		/*if (!newLine)
		{
			print("rHorizontal("+(int)mouseX+","+(int)mouseY);
		}
		else
		{
			println(","+mouseX+","+mouseY+");");
		}
		newLine = !newLine;*/
		//println(player.toString());
		//println((int)mouseX+","+(int)mouseY);
		menuSystem.queueClick(mouseX, mouseY);
		if (newMenuSystem.within(mouseX, mouseY) != null)
			menuSystem.menuActivated = true;
		else
		{
			if (mouseButton == LEFT)
			{
				inputSystem.queueLeftClick(mouseX, mouseY);
			}
			else if (mouseButton == RIGHT)
			{
				//Pass a right click to input system
				rMouseX = mouseX; rMouseY = mouseY;
				BaseEntity en = menuSystem.getSelected();
				if (en != null)
				{
					if (en instanceof GameEntity)
					{
						ArrayList<Tile> temp = new ArrayList<Tile>(); temp.add(en.location);
						menuSystem.movementChoice(temp, true, (int)en.maxAction);
					}
				}
				//inputSystem.queueRightClick(mouseX, mouseY);
			}
		}
	}
	
	public void image(PImage image, float a, float b, float c, float d)
	{
		if (image instanceof ColorImage)
		{
			ColorImage col = (ColorImage)image;
			tint(col.r, col.g, col.b);
			super.image(col.image, a, b, c, d);
		}
		else
			super.image(image, a, b, c, d);
	}

	public void mouseMoved()
	{
		rMouseX = mouseX; rMouseY = mouseY;
		//if (menuSystem.lastMouseHighlighted)
	}

	public void mouseDragged()
	{
		//println("Dragging " + mouseX + "," + mouseY);
		menuSystem.menuActivated = true;
		newMenuSystem.mouseDragged(mouseX, mouseY);
	}

	public void mouseReleased()
	{
		if (mouseButton == RIGHT)
		{
			inputSystem.queueRightClick(rMouseX, rMouseY);
			rMouseX = -1; rMouseY = -1;
			menuSystem.movementChoices.clear();
			menuSystem.pathToHighlighted.clear();
			//menuSystem.menuActivated = false;
			newMenuSystem.mouseReleased(rMouseX, rMouseY);
		}
	}

	/*public void mouseMoved()
	{

	}*/

	public void keyPressed()
	{
		if (keyCode == KeyEvent.VK_F1) inputSystem.queueKey((char)131);
		else if (keyCode == KeyEvent.VK_F2) inputSystem.queueKey((char)132);
		else if (keyCode == KeyEvent.VK_F3) inputSystem.queueKey((char)133);
		else if (keyCode == KeyEvent.VK_F4) inputSystem.queueKey((char)134);
		else if (keyCode == KeyEvent.VK_F5) inputSystem.queueKey((char)135);
		else if (keyCode == KeyEvent.VK_F6) inputSystem.queueKey((char)136);
		else if (keyCode == KeyEvent.VK_F7) inputSystem.queueKey((char)137);
		else if (keyCode == KeyEvent.VK_F8) inputSystem.queueKey((char)138);
		else if (keyCode == KeyEvent.VK_F9) inputSystem.queueKey((char)139);
		else if (keyCode == KeyEvent.VK_F10) inputSystem.queueKey((char)140);
		else
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
		game.exit();
		//super.stop();
	}

	public void fixCamera(int r, int c)
	{
		player.posX = r*renderSystem.widthBlock;
		player.posY = 60;
		player.posZ = (c-2)*renderSystem.widthBlock;
		//player.rotY = 0;
		//player.rotVertical = 0;
		//player.update();
	}

	public void resetCamera()
	{
		centerX = mouseX/(1 - player.rotY/(float)Math.PI);
		centerY = mouseY/(1 + 4*player.rotVertical/(float)Math.PI);
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
		else if (terrainType.equals("terrain10"))
		{
			int len = 128;
			double[][] temp = DiamondSquare.makeTable(50,50,50,50,len+1);
			map = new DiamondSquare(temp);
			map.seed(seed);
			//ds.diamond(0, 0, 4);
			//displayTables = ds.dS(0, 0, len, 40, 0.7)
			//map.seed(seed);
			terrain = map.generate(new double[]{0, 0, len, 40, 0.7});
			//System.out.println(terrain);
			con = 1F;
			cutoff = 60;
		}
		else if (terrainType.equals("terrain11"))
		{
			int len = 128;
			double[][] temp = DiamondSquare.makeTable(50,50,50,50,len+1);
			temp[temp.length/2][temp[0].length/2] = 200;
			temp[0][temp[0].length/2] = 50;
			temp[temp.length-1][temp[0].length/2] = 50;
			temp[temp[0].length/2][0] = 50;
			temp[temp[0].length/2][temp.length-1] = 50;
			map = new DiamondSquare(temp);
			map.seed(seed);
			//ds.diamond(0, 0, 4);
			//displayTables = ds.dS(0, 0, len, 40, 0.7)
			//map.seed(seed);
			terrain = map.generate(new double[]{0, 0, len, 40, 0.7});
			//System.out.println(terrain);
			con = 1F;
			cutoff = 100;
		}
		else if (terrainType.equals("terrain4"))
		{
			map = new RecursiveBlock(seed);
			terrain = map.generate(new double[]{10,1});
			con = 3F;
			cutoff = 1;
		}
		else if (terrainType.equals("terrain5"))
		{
			map = new PerlinNoise(seed);
			terrain = map.generate(new double[]{32,32,150,8,1,0.8,6,64,-150});
			con = 1F;
			cutoff = -150;
		}
		else
		{
			System.err.println("No map!");
			int[] err = new int[5]; err[10] = 0;
			con = 1F;
			cutoff = 0;
		}
		int n = 1;
		if (numCivs <= 4)
		{
			n = 4;
		}
		else if (numCivs <= 7)
		{
			n = 3;
		}
		else if (numCivs <= 10)
		{
			n = 2;
		}
		else
		{
			n = 1;
			//Don't sample and downsize it
		}
		if (!terrainType.equals("terrain11")) n *= 2;
		terrain = downSample(terrain,n);
		menuSystem.multiplier = n;
		menuSystem.markedTiles = new boolean[terrain.length][terrain[0].length];
		int[][] biomes = assignBiome(terrain);
		grid = new Grid(civChoice, terrain, biomes, assignResources(biomes), assignFields(biomes), numCivs, numCityStates, difficultyLevel, 3, (int)cutoff, seed);
		civilizationSystem.theGrid = grid;
		//player = new Player(grid.civs[0]);
		makeRivers(biomes); 

		//Odd numbers only
		renderSystem.generateRoughTerrain(terrain, 3);
		//renderSystem.smoothRoughTerrain(1);
		renderSystem.generateTextures(3);
		//grid.setupTiles(terrain);
		//grid.setupCivs();
		//renderSystem.addTerrain(terrain, con, cutoff);
	}

	public int[][] assignFields(int[][] biomes)
	{
		int[][] temp = new int[biomes.length][biomes[0].length];
		Random rand = new Random(seed + 1000000000);
		for (int r = 0; r < biomes.length; r++)
		{
			for (int c = 0; c < biomes[0].length; c++)
			{
				if (biomes[r][c] == -1)
					temp[r][c] = 0;
				else
				{
					double random = rand.nextDouble();
					if (random < 0.05*biomes[r][c]/3)
					{
						temp[r][c] = 3;
					}
					else if (random < 0.1*biomes[r][c]/3)
					{
						temp[r][c] = 2;
					}
					else if (random < 0.25*biomes[r][c]/3)
					{
						temp[r][c] = 1;
					}
					else
					{
						temp[r][c] = 0;
					}
				}
			}
		}
		return temp;
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
		grid.verticalRivers = new boolean[biomes.length][biomes.length - 1];
		grid.horizontalRivers = new boolean[biomes.length - 1][biomes.length];
		for (int r = 0; r < grid.verticalRivers.length; r++)
		{
			for (int c = 0; c < grid.verticalRivers[0].length; c++)
			{
				if (biomes[r][c] >= 1 && biomes[r][c+1] >= 1)
				{
					if (Math.random() < 0.02*biomes[r][c])
					{
						grid.verticalRivers[r][c] = true;
					}
				}
			}
		}
		for (int r = 0; r < grid.horizontalRivers.length; r++)
		{
			for (int c = 0; c < grid.horizontalRivers[0].length; c++)
			{
				if (biomes[r][c] >= 1 && biomes[r+1][c] >= 1)
				{
					if (Math.random() < 0.02*biomes[r][c])
					{
						grid.horizontalRivers[r][c] = true;
					}
				}
			}
		}
		//^ Set them directly from here
		//grid.verticalRivers = verticalRivers;
		//grid.horizontalRivers = horizontalRivers;
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
						if (Math.random() < EntityData.resourceLevels[i])
						{
							resources[r][c] = i;
						}
						/*else if (Math.random() < 0.06)
						{
							resources[r][c] = 1;
						}*/
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
	public double[][] assignRain(double[][] temperature)
	{
		Random rainRandom = new Random(seed);
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

	public double[][] downSample(double[][] terrain, int num)
	{
		double[][] temp = new double[terrain.length/num + 1][terrain.length/num + 1];
		for (int r = 0; r < terrain.length; r += num)
			for (int c = 0; c < terrain[0].length; c += num)
				temp[r/num][c/num] = terrain[r][c];
		return temp;
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
	public void requestUpdate() {renderSystem.requestUpdate = true;}

}
