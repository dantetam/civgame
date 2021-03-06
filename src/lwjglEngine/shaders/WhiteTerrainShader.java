package lwjglEngine.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import game.Tile;
import lwjglEngine.toolbox.MatrixMathUtil;
import lwjglEngine.toolbox.MousePicker;
import lwjglEngine.entities.Camera;
import lwjglEngine.entities.Light;

public class WhiteTerrainShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/lwjglEngine/shaders/whiteTerrainVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/lwjglEngine/shaders/whiteTerrainFragmentShader.txt";

	private int locationTransformMatrix, locationProjectionMatrix, locationViewMatrix;
	private int locationSelectedCoord, locationMouseHighlightedCoord, locationMouseOverCoord;
	private int locationLightPosition, locationLightColor;
	private int locationShineDamper, locationReflectiveness;

	private int locationBackTexture, locationTexture1, locationTexture2, locationTexture3, locationTexture4, locationTexture5, locationTexture6, locationTexture7;

	private int locationBlendMap; //, locationBlendMap2; //First blend map represents biome, second represents highlighting

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
		locationSelectedCoord = super.getUniformLocation("selectedCoord");
		locationMouseHighlightedCoord = super.getUniformLocation("mouseHighlightedCoord");
		locationMouseOverCoord = super.getUniformLocation("mouseOverCoord");
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
		//locationBlendMap2 = super.getUniformLocation("blendMap2");
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
		//super.loadInt(locationBlendMap2, 9);
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
		Matrix4f matrix = MatrixMathUtil.createViewMatrix(camera);
		super.loadMatrix(locationViewMatrix, matrix);
	}

	//Send the selected and mouse highlighted coordinates
	public void loadCoords(Tile sel, Tile hi, float rows, float cols, MousePicker mousePicker)
	{
		if (sel == null)
			super.loadVector2f(locationSelectedCoord, new Vector2f(-1f, -1f));
		else
		{
			//super.loadVector2f(locationSelectedCoord, new Vector2f(sel.row/(rows*0.9f), sel.col/(cols*0.9f)));
			//super.loadVector2f(locationSelectedCoord, new Vector2f(sel.row/rows, sel.col/cols));
			super.loadVector2f(locationSelectedCoord, new Vector2f(
					((float)sel.row+0.5F)/(float)rows*0.9F, 
					((float)sel.col+0.5F)/(float)cols*0.9F
					));
		}
		if (hi == null)
		{
			super.loadVector2f(locationMouseHighlightedCoord, new Vector2f(-1f, -1f));
			super.loadVector2f(locationMouseOverCoord, new Vector2f(-1f, -1f));
		}
		else
		{
			super.loadVector2f(locationMouseHighlightedCoord, new Vector2f(mousePicker.rayCastHit.x/1600f, mousePicker.rayCastHit.z/1600f));
			//super.loadVector2f(locationMouseHighlightedCoord, new Vector2f(hi.row/rows, hi.col/cols));
			super.loadVector2f(locationMouseOverCoord, new Vector2f(
					//Figure out floor correctly
					(float)(Math.floor(mousePicker.rayCastHit.x/(1600f*0.9f)*rows)+0.5f)/rows*0.9f,
					(float)(Math.floor(mousePicker.rayCastHit.z/(1600f*0.9f)*cols)+0.5f)/cols*0.9f
					));
		}
	}

}
