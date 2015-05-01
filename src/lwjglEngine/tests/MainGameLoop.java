package lwjglEngine.tests;

import lwjglEngine.levels.LevelManager;
import lwjglEngine.models.RawModel;
import lwjglEngine.models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import render.CivGame;
import system.BaseSystem;
import lwjglEngine.entities.Camera;
import lwjglEngine.entities.Entity;
import lwjglEngine.entities.Light;
import lwjglEngine.render.*;
import lwjglEngine.shaders.StaticShader;
import lwjglEngine.terrain.Terrain;
import lwjglEngine.textures.ModelTexture;
import lwjglEngine.textures.TerrainTexture;
import lwjglEngine.textures.TerrainTexturePack;

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

		terrain1 = new Terrain(0,0,loader,texturePack,blendMap,"heightmap");
		terrain2 = new Terrain(-1,0,loader,texturePack,blendMap,"heightmap");
		terrain3 = new Terrain(0,-1,loader,texturePack,blendMap,"heightmap");
		terrain4 = new Terrain(-1,-1,loader,texturePack,blendMap,"heightmap");

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
			
			renderer.processTerrain(terrain1);
			renderer.processTerrain(terrain2);
			renderer.processTerrain(terrain3);
			renderer.processTerrain(terrain4);
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

}
