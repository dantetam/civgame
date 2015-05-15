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
import lwjglEngine.textures.WhiteTerrainTexturePack;

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

	//TerrainTexture backTexture, rTexture, gTexture, bTexture;
	TerrainTexture t1,t2,t3,t4,t5,t6,t7;
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
		try
		{
		main = game;
		
		loader = new Loader();

		levelManager = new LevelManager();

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
		double[][] heightMap = average(generateRoughTerrain(main.terrain, 9));
		terrain0 = new GeneratedTerrain(0, 0, loader, texturePack, blendMap, heightMap);

		light = new Light(new Vector3f(0,50,0), new Vector3f(1,1,1));
		camera = new Camera();

		//Keep updating the display until the user exits
		renderer = new MasterRenderer();
		
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
				if (t.shape == 2)
				{
					double[][] renderHill = map.generate(DiamondSquare.makeTable(5, 5, 5, 5, multiply), new double[]{0, 0, 2, 7, 0.7, 1});
					renderHill = DiamondSquare.max(renderHill, 40);
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
								vertices[nr][nc] = (float)(Math.random()*5);
							else
								vertices[nr][nc] = (float)(Math.random()*2.5F);
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
