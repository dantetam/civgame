package lwjglEngine.shaders;

import org.lwjgl.util.vector.Matrix4f;

import lwjglEngine.toolbox.Maths;
import lwjglEngine.entities.Camera;
import lwjglEngine.entities.Light;

public class TerrainShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/shaders/terrainVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/terrainFragmentShader.txt";
	
	private int locationTransformMatrix, locationProjectionMatrix, locationViewMatrix;
	private int locationLightPosition, locationLightColor;
	private int locationShineDamper, locationReflectiveness;
	private int locationBackTexture, locationRTexture, locationGTexture, locationBTexture;
	private int locationBlendMap;
	
	public TerrainShader() 
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	protected void bindAttributes() 
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	protected void getAllUniformLocations() 
	{
		//Find the location of the transformMatrix uniform variable
		locationTransformMatrix = super.getUniformLocation("transformMatrix");
		locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
		locationViewMatrix = super.getUniformLocation("viewMatrix");
		locationLightPosition = super.getUniformLocation("lightPosition");
		locationLightColor = super.getUniformLocation("lightColor");
		locationShineDamper = super.getUniformLocation("shineDamper");
		locationReflectiveness = super.getUniformLocation("reflectiveness");
		locationBackTexture = super.getUniformLocation("backgroundTexture");
		locationRTexture = super.getUniformLocation("rTexture");
		locationGTexture = super.getUniformLocation("gTexture");
		locationBTexture = super.getUniformLocation("bTexture");
		locationBlendMap = super.getUniformLocation("blendMap");
	}

	public void connectTextures()
	{
		super.loadInt(locationBackTexture, 0);
		super.loadInt(locationRTexture, 1);
		super.loadInt(locationGTexture, 2);
		super.loadInt(locationBTexture, 3);
		super.loadInt(locationBlendMap, 4);

	}
	public void loadTransformMatrix(Matrix4f matrix) {super.loadMatrix(locationTransformMatrix, matrix);}
	public void loadProjectionMatrix(Matrix4f matrix) {super.loadMatrix(locationProjectionMatrix, matrix);}
	public void loadLight(Light light) 
	{
		super.loadVector(locationLightPosition, light.position);
		super.loadVector(locationLightColor, light.color);
	}
	public void loadShineVariables(float d, float r)
	{
		super.loadFloat(locationShineDamper, d);
		super.loadFloat(locationReflectiveness, r);
	}
	
	//Create a new view matrix based on the properties of the camera
	public void loadViewMatrix(Camera camera) 
	{
		Matrix4f matrix = Maths.createViewMatrix(camera);
		super.loadMatrix(locationViewMatrix, matrix);
	}

}
