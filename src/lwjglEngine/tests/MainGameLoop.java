package lwjglEngine.tests;

import lwjglEngine.levels.LevelManager;
import lwjglEngine.models.RawModel;
import lwjglEngine.models.TexturedModel;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.opengl.GL11;

import render.CivGame;
import system.BaseSystem;
import terrain.BicubicInterpolator;
import lwjglEngine.entities.Camera;
import lwjglEngine.entities.Entity;
import lwjglEngine.entities.Light;
import lwjglEngine.gui.GuiTexture;
import lwjglEngine.render.*;
import lwjglEngine.shaders.StaticShader;
import lwjglEngine.terrain.GeneratedTerrain;
import lwjglEngine.terrain.Terrain;
import lwjglEngine.textures.ModelTexture;
import lwjglEngine.textures.TerrainTexture;
import lwjglEngine.textures.TerrainTexturePack;
import lwjglEngine.textures.WhiteTerrainTexturePack;
import lwjglEngine.toolbox.MousePicker;

import java.util.ArrayList;

import terrain.DiamondSquare;
import vector.Point;
import game.GameEntity;
import game.Tile;

public class MainGameLoop {

	public CivGame main;

	public static int multiply = 9;

	public int frameCount = 0;
	public boolean stop = false;

	//Public for loading textures
	public Loader loader;

	public LevelManager levelManager;

	//TerrainTexture backTexture, rTexture, gTexture, bTexture;
	TerrainTexture t1,t2,t3,t4,t5,t6,t7;
	TerrainTexturePack texturePack;
	TerrainTexture blendMap, blendMap2;

	/*//counter clockwise vertices
	float[] vertices = {
			//Left bottom and top right, resp.
		-0.5f, 0.5f, 0f,	
		-0.5f, -0.5f, 0f,
		0.5f, -0.5f, 0f,
		0.5f, 0.5f, 0f
	};

	//order in which to transverse the vertices
	int[] indices = {0,1,3,3,1,2};

	//respective u,v vertex of texture to map to
	float[] textureCoords = {0,0,0,1,1,1,1,0};*/

	Terrain terrain1, terrain2, terrain3, terrain4;
	public GeneratedTerrain terrain0; 

	Light light; public Camera camera;
	public MousePicker mousePicker;
	public int widthBlock = 21;
	public MasterRenderer renderer;

	public MainGameLoop(CivGame game)
	{
		try
		{
			main = game;
			main.lwjglSystem = this;

			loader = new Loader();
			main.menuSystem.setupLoader(loader);
			main.menuSystem.setupMenus(); //Set up menus once loader is not null

			//rTexture = new TerrainTexture(loader.loadTexture("dirt"));
			//gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
			//bTexture = new TerrainTexture(loader.loadTexture("path"));
			TerrainTexture backTexture = new TerrainTexture(loader.loadTexture("seaTexture"));
			TerrainTexture t1 = new TerrainTexture(loader.loadTexture("iceTexture"));
			TerrainTexture t2 = new TerrainTexture(loader.loadTexture("taigaTexture"));
			TerrainTexture t3 = new TerrainTexture(loader.loadTexture("desertTexture"));
			TerrainTexture t4 = new TerrainTexture(loader.loadTexture("steppeTexture"));
			TerrainTexture t5 = new TerrainTexture(loader.loadTexture("dryforestTexture"));
			TerrainTexture t6 = new TerrainTexture(loader.loadTexture("forestTexture"));
			TerrainTexture t7 = new TerrainTexture(loader.loadTexture("rainforestTexture"));

			texturePack = new WhiteTerrainTexturePack(
					backTexture,
					t1,
					t2,
					t3,
					t4,
					t5,
					t6,
					t7
					);

			blendMap = new TerrainTexture(loader.loadTexture("generatedBlendMap"));
			blendMap2 = new TerrainTexture(loader.loadTexture("generatedHighlightMap"));

			/*//counter clockwise vertices
		float[] vertices = {
				//Left bottom and top right, resp.
			-0.5f, 0.5f, 0f,	
			-0.5f, -0.5f, 0f,
			0.5f, -0.5f, 0f,
			0.5f, 0.5f, 0f
		};

		//order in which to transverse the vertices
		int[] indices = {0,1,3,3,1,2};

		//respective u,v vertex of texture to map to
		float[] textureCoords = {0,0,0,1,1,1,1,0};*/

			//terrain1 = new Terrain(0,0,loader,texturePack,blendMap,"heightmap");0
			//terrain2 = new Terrain(-1,0,loader,texturePack,blendMap,"heightmap");
			//terrain3 = new Terrain(0,-1,loader,texturePack,blendMap,"heightmap");
			//terrain4 = new Terrain(-1,-1,loader,texturePack,blendMap,"heightmap");
			double[][] temp2 = DiamondSquare.makeTable(0, 0, 0, 0, 33);
			DiamondSquare ds = new DiamondSquare();
			ds = new DiamondSquare(temp2);
			ds.seed(870L);
			//double[][] heightMap = ds.generate(new double[]{0, 0, 2, 7, 0.7, 1});
			double[][] heightMap = average(generateRoughTerrain(main.terrain, multiply));
			terrain0 = new GeneratedTerrain(0, 0, loader, texturePack, blendMap, blendMap2, heightMap);
			//HeightMap is a more detailed version of the terrain map

			levelManager = new LevelManager(game.grid, heightMap);
			main.grid.setManager(levelManager); //Manually assign this since the levelmanager is created after the grid

			light = new Light(new Vector3f(500,500,500), new Vector3f(1,1,1));
			camera = new Camera();
			main.camera = camera; //manually assign this since the while loop belows halts all processes (i.e. no stopping) in civgame

			//GuiTexture test = new GuiTexture(loader.loadTexture("partTexture"), new Vector2f(0.5f,0.5f), new Vector2f(0.2f,0.2f));
			//main.guis.add(test);

			//Keep updating the display until the user exits
			renderer = new MasterRenderer(loader);
			mousePicker = renderer.setupMousePicker(camera);
			main.renderSystem.mousePicker = mousePicker;

			tick();

			stop();

		} catch (Exception e) {e.printStackTrace();} //LWJGL seems to not catch errors for some reason
		//Probably has to do with the fact that's as close to C++ as possible
	}

	public void stop()
	{
		//TODO: Remember to stop
		//Do some clean up of all data
		renderer.cleanUp();
		loader.cleanData();
		DisplayManager.closeDisplay();
	}

	public void tick() 
	{
		while (true)
		{
			if (DisplayManager.requestClose())
			{
				stop = true;
				break;
			}
			if (stop) break;

			for (int i = 0; i < main.systems.size(); i++)
			{
				main.systems.get(i).tick();
			}

			//entity.rotate(0,0.3F,0);
			camera.move();
			//camera.yaw += 0.1;

			/*if (main.grid.civs.length > 1)
				for (int i = 0; i < 1; i++)
				{
					ArrayList<GameEntity> list = main.grid.civs[i].units;
					if (list.size() == 0) continue;
					GameEntity random = list.get(0);
					Tile t = main.menuSystem.getMouseHighlighted();
					if (t != null)
					{
						main.grid.moveTo(random, t.row, t.col);
						System.out.println(random.location.row + " " + random.location.col);
					}
				}
			 */


			/*if (frameCount % 50 == 0)
			{
				if (main.grid.civs.length > 1)
					for (int i = 0; i < 1; i++)
					{
						ArrayList<GameEntity> list = main.grid.civs[i].units;
						if (list.size() == 0) continue;
						GameEntity random = list.get(0);
						Tile t = main.grid.findIsolated(); //Testing unit 'animations'
						main.grid.moveTo(random, random.location.row + 1, random.location.col + 1);
						System.out.println(random.location.row + " " + random.location.col);
					}
			}*/

			renderer.processTerrain(terrain0);
			//renderer.processTerrain(terrain1);
			//renderer.processTerrain(terrain2);
			//renderer.processTerrain(terrain3);
			//renderer.processTerrain(terrain4);
			//renderer.processEntity(levelManager.entities);
			renderer.processGroups(levelManager);
			//levelManager.groups.get(0).move(0,80+(float)(40*Math.sin((float)frameCount/250F)),0);
			/*for (Entity en: levelManager.entities)
			{
				en.rotate(0,1F,0);
			}*/

			Tile sel = main.menuSystem.getSelected() != null ? main.menuSystem.getSelected().location : null;
			renderer.render(light, camera, sel, main.menuSystem.getMouseHighlighted(), main.grid.rows, main.grid.cols, mousePicker);

			renderer.guiRenderer.render(main.menuSystem);

			DisplayManager.updateDisplay();
			frameCount++;
		}
	}

	private double[][] average(double[][] t)
	{
		double[][] temp = new double[t.length][t[0].length];
		for (int r = 0; r < t.length; r++)
		{
			for (int c = 0; c < t[0].length; c++)
			{
				double sum = 0, n = 0;
				if (r - 1 >= 0) {sum += t[r-1][c]; n++;}
				if (c - 1 >= 0) {sum += t[r][c-1]; n++;}
				if (r + 1 < t.length) {sum += t[r+1][c]; n++;}
				if (c + 1 < t[0].length) {sum += t[r][c+1]; n++;}
				temp[r][c] = sum/n;
			}
		}
		return temp;
	}

	public double[][] generateRoughTerrain(double[][] terrain, int multiply)
	{
		double[][] vertices = new double[terrain.length*multiply + 1][terrain.length*multiply + 1];
		double[][] temp1 = DiamondSquare.makeTable(2,2,2,2,multiply);
		temp1[temp1.length/2][temp1.length/2] = 20;
		double[][] temp2 = DiamondSquare.makeTable(2,2,2,2,multiply);
		temp2[temp1.length/2][temp1.length/2] = 40;
		DiamondSquare map;
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				Tile t = main.grid.getTile(r,c);
				if (t.biome == -1)
				{
					for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
							vertices[nr][nc] = 0;
					/*for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
							vertices[nr][nc] = (c + (float)(nc%multiply)/(float)multiply)*widthBlock;*/
				}
				//Check to see if there is a land and sea split
				ArrayList<Tile> sea = main.grid.coastal(r, c);
				if (sea.size() > 0)
				{
					//Diagonal
					//damn these variables
					for (int i = 0; i < sea.size(); i++)
					{
						int dr = sea.get(i).row - r, dc = sea.get(i).col - c;
						int pr = 0, pc = 0;
						if (dr != 0 && dc != 0)
						{

						}
						else if (dr != 0) // && dc == 0
						{
							if (dr == 1)
							{
								//for (int j = 0; j < )
							}
							else //dr == -1
							{

							}
						}
						else if (dc != 0) // && dr == 0
						{
							if (dc == 1)
							{

							}
							else //dc == -1
							{

							}
						}
						else
						{
							System.out.println("impossible");
						}
					}
				}
			}
		}
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				map = new DiamondSquare(temp2);
				map.seed(870L);
				map.random.setSeed((long)(System.currentTimeMillis()*Math.random()*100F));
				Tile t = main.grid.getTile(r,c);
				//map = null;
				if (t.biome == -1) continue;
				for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
				{
					for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
					{
						//vertices[nr][nc] = 0;
						vertices[nr][nc] = (terrain[r][c] - 100F)/3F;
						//System.out.print(terrain[r][c] + " ");
					}
					//System.out.println();
				}
				/*if (t.shape == 2)
				{
					double[][] renderHill = map.generate(DiamondSquare.makeTable(5, 5, 5, 5, multiply), new double[]{0, 0, 2, 7, 0.7, 1});
					renderHill = DiamondSquare.max(renderHill, 40);
					for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
						{
							float rough = (float)renderHill[nr - r*multiply][nc - c*multiply];
							vertices[nr][nc] += rough*2D*Math.random() - rough;
						}
				}
				else if (t.shape == 1)
				{
					map = new DiamondSquare(temp1);
					long seed = (long)(System.currentTimeMillis()*Math.random());
					map.seed(seed);
					double[][] renderHill = map.generate(DiamondSquare.makeTable(0, 0, 0, 0, multiply), new double[]{0, 0, 2, 4, 0.5, 1});
					renderHill = DiamondSquare.max(renderHill, 13);
					for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
						{
							float rough = (float)renderHill[nr - r*multiply][nc - c*multiply];
							vertices[nr][nc] += rough*2D*Math.random() - rough;
						}
				}*/
			}
		}
		//Make the top & left border zero
		/*for (int i = 0; i < vertices.length; i++)
		{
			vertices[i][0].y = 0;
			vertices[0][i].y = 0;
		}
		for (int r = 0; r < vertices.length; r++)
		{
			for (int c = 0; c < vertices[0].length; c++)
			{
				System.out.print((int)vertices[r][c] + " ");
			}
			System.out.println();
		}
		this.multiply = multiply;
		for (int nr = 0; nr < vertices.length; nr++)
		{
			for (int nc = 0; nc < vertices[0].length; nc++)
			{
				Tile t = main.grid.getTile(nr / multiply, nc / multiply);
				if (t != null && t.biome == -1 && main.grid.adjacentLand(t.row, t.col).size() == 0) continue;
				//if (nr % multiply != 0 || nc % multiply != 0) continue;
				Point p = vertices[nr][nc];
				if (p != null)
				{
					vertices[nr][nc] = new Point(p.x + Math.random()*4D - 2, p.y, p.z + Math.random()*4D - 2);
				}
			}
		}*/
		return vertices;
	}

}
