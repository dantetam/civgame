package lwjglEngine.shaders;

import org.lwjgl.util.vector.Matrix4f;

import lwjglEngine.toolbox.Maths;
import lwjglEngine.entities.Camera;
import lwjglEngine.entities.Light;

public class WhiteTerrainShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/lwjglEngine/shaders/whiteTerrainVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/lwjglEngine/shaders/whiteTerrainFragmentShader.txt";

	private int locationTransformMatrix, locationProjectionMatrix, locationViewMatrix;
	private int locationLightPosition, locationLightColor;
	private int locationShineDamper, locationReflectiveness;
	
	private int locationBackTexture, locationTexture1, locationTexture2, locationTexture3, locationTexture4, locationTexture5, locationTexture6, locationTexture7;
	
	private int locationBlendMap;

	public WhiteTerrainShader() 
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
		locationTexture1 = super.getUniformLocation("texture1");
		locationTexture2 = super.getUniformLocation("texture2");
		locationTexture3 = super.getUniformLocation("texture3");
		locationTexture4 = super.getUniformLocation("texture4");
		locationTexture5 = super.getUniformLocation("texture5");
		locationTexture6 = super.getUniformLocation("texture6");
		locationTexture7 = super.getUniformLocation("texture7");
		locationBlendMap = super.getUniformLocation("blendMap");
	}

	public void connectTextures()
	{
		super.loadInt(locationBackTexture, 0);
		super.loadInt(locationTexture1, 1);
		super.loadInt(locationTexture2, 2);
		super.loadInt(locationTexture3, 3);
		super.loadInt(locationTexture4, 4);
		super.loadInt(locationTexture5, 5);
		super.loadInt(locationTexture6, 6);
		super.loadInt(locationTexture7, 7);
		super.loadInt(locationBlendMap, 8);
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
