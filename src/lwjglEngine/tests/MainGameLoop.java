package lwjglEngine.tests;

import lwjglEngine.levels.LevelManager;
import lwjglEngine.models.RawModel;
import lwjglEngine.models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

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
	
	public int frameCount = 0;
	
	
	public static void main(String[] args)
	{
		new MainGameLoop();
	}
	
	public MainGameLoop()
	{
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		LevelManager levelManager = new LevelManager();
		
		TerrainTexture backTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(
				backTexture,
				rTexture,
				gTexture,
				bTexture
				);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
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
		
		Terrain terrain1 = new Terrain(0,0,loader,texturePack,blendMap,"heightmap");
		Terrain terrain2 = new Terrain(-1,0,loader,texturePack,blendMap,"heightmap");
		Terrain terrain3 = new Terrain(0,-1,loader,texturePack,blendMap,"heightmap");
		Terrain terrain4 = new Terrain(-1,-1,loader,texturePack,blendMap,"heightmap");
		
		Light light = new Light(new Vector3f(0,50,0), new Vector3f(1,1,1));
		Camera camera = new Camera();
		
		//Keep updating the display until the user exits
		MasterRenderer renderer = new MasterRenderer();
		while (!Display.isCloseRequested())
		{
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

		//Do some clean up of all data
		renderer.cleanUp();
		loader.cleanData();
		DisplayManager.closeDisplay();
	}

}
