package lwjglEngine.tests;

import lwjglEngine.levels.LevelManager;
import lwjglEngine.models.RawModel;
import lwjglEngine.models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import render.CivGame;
import system.BaseSystem;
import terrain.BicubicInterpolator;
import lwjglEngine.entities.Camera;
import lwjglEngine.entities.Entity;
import lwjglEngine.entities.Light;
import lwjglEngine.render.*;
import lwjglEngine.shaders.StaticShader;
import lwjglEngine.terrain.GeneratedTerrain;
import lwjglEngine.terrain.Terrain;
import lwjglEngine.textures.ModelTexture;
import lwjglEngine.textures.TerrainTexture;
import lwjglEngine.textures.TerrainTexturePack;

import java.util.ArrayList;

import terrain.DiamondSquare;
import vector.Point;
import game.Tile;

public class MainGameLoop {

	public CivGame main;
	
	public int frameCount = 0;
	public boolean stop = false;

	Loader loader;

	LevelManager levelManager;

	TerrainTexture backTexture, rTexture, gTexture, bTexture;
	TerrainTexturePack texturePack;
	TerrainTexture blendMap;

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
	GeneratedTerrain terrain0; 

	Light light; public Camera camera;
	public int widthBlock = 21;
	MasterRenderer renderer;

	public MainGameLoop(CivGame game)
	{
		main = game;
		
		loader = new Loader();

		levelManager = new LevelManager();

		backTexture = new TerrainTexture(loader.loadTexture("grassy"));
		rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		bTexture = new TerrainTexture(loader.loadTexture("path"));

		texturePack = new TerrainTexturePack(
				backTexture,
				rTexture,
				gTexture,
				bTexture
				);
		blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

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

		//terrain1 = new Terrain(0,0,loader,texturePack,blendMap,"heightmap");
		//terrain2 = new Terrain(-1,0,loader,texturePack,blendMap,"heightmap");
		//terrain3 = new Terrain(0,-1,loader,texturePack,blendMap,"heightmap");
		//terrain4 = new Terrain(-1,-1,loader,texturePack,blendMap,"heightmap");
		double[][] temp2 = DiamondSquare.makeTable(0, 0, 0, 0, 33);
		DiamondSquare ds = new DiamondSquare();
		ds = new DiamondSquare(temp2);
		ds.seed(870L);
		//double[][] heightMap = ds.generate(new double[]{0, 0, 2, 7, 0.7, 1});
		System.out.println("wo");
		double[][] heightMap = generateRoughTerrain(main.terrain, 3);
		System.out.println("xo");
		terrain0 = new GeneratedTerrain(0, 0, loader, texturePack, bTexture, heightMap);
		System.out.println("zo");
		light = new Light(new Vector3f(0,50,0), new Vector3f(1,1,1));
		camera = new Camera();

		//Keep updating the display until the user exits
		renderer = new MasterRenderer();
		
		tick();

		stop();
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
			if (Display.isCloseRequested())
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
			
			renderer.processTerrain(terrain0);
			//renderer.processTerrain(terrain1);
			//renderer.processTerrain(terrain2);
			//renderer.processTerrain(terrain3);
			//renderer.processTerrain(terrain4);
			//renderer.processEntity(levelManager.entities);
			renderer.processGroups(levelManager.groups);
			//levelManager.groups.get(0).move(0,80+(float)(40*Math.sin((float)frameCount/250F)),0);
			/*for (Entity en: levelManager.entities)
			{
				en.rotate(0,1F,0);
			}*/
			
			renderer.render(light, camera);
			
			DisplayManager.updateDisplay();
			frameCount++;
		}
	}

	public double[][] generateRoughTerrain(double[][] terrain, int multiply)
	{
		double[][] vertices = new double[terrain.length*multiply + 1][terrain.length*multiply + 1];
		double[][] temp1 = DiamondSquare.makeTable(2,2,2,2,multiply);
		temp1[temp1.length/2][temp1.length/2] = 8;
		double[][] temp2 = DiamondSquare.makeTable(2,2,2,2,multiply);
		temp2[temp1.length/2][temp1.length/2] = 24;
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
					for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
							vertices[nr][nc] = (c + (float)(nc%multiply)/(float)multiply)*widthBlock;
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
				if (t.shape == 2)
				{
					double[][] renderHill = map.generate(DiamondSquare.makeTable(5, 5, 5, 5, multiply), new double[]{0, 0, 2, 7, 0.7, 1});
					renderHill = DiamondSquare.max(renderHill, 20);
					//DiamondSquare.printTable(renderHill);
					for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
					{
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
						{
							vertices[nr][nc] = (float)renderHill[nr - r*multiply][nc - c*multiply];
							//System.out.print(renderHill[nr - r*multiply][nc - c*multiply] + " ");
						}
						//System.out.println();
					}
				}
				else if (t.shape == 1)
				{
					map = new DiamondSquare(temp1);
					long seed = (long)(System.currentTimeMillis()*Math.random());
					map.seed(seed);
					//System.out.println(seed);
					//map.seed(870L);
					//double[][] renderHill = map.generate(new double[]{0, 0, 2, 6, 0.5});
					double[][] renderHill = map.generate(DiamondSquare.makeTable(0, 0, 0, 0, multiply), new double[]{0, 0, 2, 4, 0.5, 1});
					renderHill = DiamondSquare.max(renderHill, 13);
					for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
					{
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
						{
							vertices[nr][nc] = (float)renderHill[nr - r*multiply][nc - c*multiply];
						}
					}
				}
				else
				{
					boolean rough = Math.random() < 0.2;
					for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
					{
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
						{
							//double height = 2;
							//vertices[nr][nc] = terrain[r][c] + Math.random()*height*2 - height;
							if (rough)
								vertices[nr][nc] = (float)(Math.random()*2);
							else
								vertices[nr][nc] = (float)(Math.random()*0.5);
							//vertices[nr][nc] = 1;
						}
					}
				}
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
