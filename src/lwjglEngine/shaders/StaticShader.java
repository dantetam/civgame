package lwjglEngine.shaders;

import org.lwjgl.util.vector.Matrix4f;

import lwjglEngine.entities.Camera;
import lwjglEngine.entities.Light;
import lwjglEngine.toolbox.MatrixMathUtil;

public class StaticShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/lwjglEngine/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/lwjglEngine/shaders/fragmentShader.txt";
	
	private int locationTransformMatrix, locationProjectionMatrix, locationViewMatrix;
	private int locationLightPosition, locationLightColor;
	private int locationShineDamper, locationReflectiveness;
	private int locationFastLighting;
	
	public StaticShader() 
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
		locationFastLighting = super.getUniformLocation("fastLighting");
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
	public void loadFastLighting(boolean fastLighting) {super.loadBoolean(locationFastLighting, fastLighting);}
	
	//Create a new view matrix based on the properties of the camera
	public void loadViewMatrix(Camera camera) 
	{
		Matrix4f matrix = MatrixMathUtil.createViewMatrix(camera);
		super.loadMatrix(locationViewMatrix, matrix);
	}
	
}
