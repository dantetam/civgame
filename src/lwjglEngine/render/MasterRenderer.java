package lwjglEngine.render;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import lwjglEngine.entities.*;
import lwjglEngine.models.TexturedModel;
import lwjglEngine.shaders.StaticShader;
import lwjglEngine.shaders.TerrainShader;
import lwjglEngine.terrain.Terrain;

public class MasterRenderer {

	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	//Specific objects for rendering terrain only
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	private Matrix4f projectionMatrix;
	
	private HashMap<TexturedModel,ArrayList<Entity>> entities = 
			new HashMap<TexturedModel, ArrayList<Entity>>();
	private ArrayList<Terrain> terrains = new ArrayList<Terrain>();
	
	public MasterRenderer()
	{	
		//Back culling; do not render faces that are hidden from camera
		enableCulling();
		
		//Create the transformation matrix only once and parse it to other renderers
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
	}
	
	public static void enableCulling()
	{
		//Perhaps checking the culling would be a nice way to find the correct normals
		/*GL11.glEnable(GL11.GL_CULL_FACE); 
		GL11.glCullFace(GL11.GL_BACK);*/
	}
	
	public static void disableCulling()
	{
		//GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void render(Light light, Camera camera)
	{
		prepare();
		
		shader.start();
		shader.loadLight(light);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		
		terrainShader.start();
		terrainShader.loadLight(light);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		terrains.clear();
		entities.clear();
	}
	
	//Copy of the method below, but since there are only a few terrains, there's only one list
	public void processTerrain(Terrain terrain)
	{
		terrains.add(terrain);
	}
	
	//Group entities into certain lists of their own kind
	//i.e. a blue box goes into an arraylist of blue boxes
	public void processEntity(Entity entity)
	{
		TexturedModel entityModel = entity.getModel();
		ArrayList<Entity> allOfType = entities.get(entityModel);
		if (allOfType != null)
			allOfType.add(entity);
		else
		{
			ArrayList<Entity> temp = new ArrayList<Entity>();
			temp.add(entity);
			entities.put(entityModel, temp);
		}
	}
	
	public void processEntities(ArrayList<Entity> entities)
	{
		for (Entity en: entities)
			processEntity(en);
	}
	
	public void processGroups(ArrayList<Group> groups)
	{
		for (Group group: groups)
			processEntities(group.entities);
	}
	
	public void prepare()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(150F/255F,225F/255F,255F/255F,0);
	}
	
	public void cleanUp()
	{
		shader.cleanUp();
		terrainShader.cleanUp();
	}
	
	private static final float FOV = 70, NEAR_PLANE = 0.1f, FAR_PLANE = 3000f;
	private void createProjectionMatrix()
	{
		float ar = (float)Display.getWidth()/(float)Display.getHeight();
		float yScale = (float)(1f/Math.tan(Math.toRadians(FOV/2f)))*ar;
		float xScale = yScale/ar;
		float frustumLength = FAR_PLANE - NEAR_PLANE;
		
		//Set up the projection matrix by declaring discrete values
		//These values are calculated by matrix math
		projectionMatrix = new Matrix4f(); //Initialized to zeroes, not identity
		projectionMatrix.m00 = xScale;
		projectionMatrix.m11 = yScale;
		projectionMatrix.m22 = (FAR_PLANE + NEAR_PLANE)/-frustumLength;
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -(2*FAR_PLANE*NEAR_PLANE / frustumLength);
		projectionMatrix.m33 = 0;
	}

}
