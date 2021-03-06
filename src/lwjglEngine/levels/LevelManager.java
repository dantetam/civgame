package lwjglEngine.levels;

import game.Grid;
import game.LwjglGrid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import lwjglEngine.models.RawModel;
import lwjglEngine.models.TexturedModel;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import lwjglEngine.render.Loader;
import lwjglEngine.render.OBJLoader;
import lwjglEngine.textures.ModelTexture;
import lwjglEngine.entities.Entity;
import lwjglEngine.entities.Group;

public class LevelManager {

	//public ArrayList<Group> groups;

	private static Loader loader = new Loader();
	public ModelManager modelManager;

	public LevelManager(LwjglGrid grid, double[][] heightMap) {
		modelManager = new ModelManager(this, grid, heightMap);
	}

	public static Group loadFromXML(String fileName)
	{
		ArrayList<Entity> entities = new ArrayList<Entity>();
		FileReader fr = null;
		try {
			fr = new FileReader(new File("res/"+fileName));
		} catch (FileNotFoundException e) 
		{

		}
		if (fr == null)
		{
			try {
				fr = new FileReader(new File("res/parsedObj/"+fileName));
			} catch (Exception ex) {System.out.println("Unsuccessful" + fileName); return null;}
		}
		BufferedReader reader = new BufferedReader(fr);
		String line;
		try
		{
			while ((line = reader.readLine()) != null)
			{	
				String[] currentLine = line.split(",");

				if (currentLine.length < 10) continue;
				
				/*int off = 1; //Depends on format of converted XML file
				//First data line only gives relative position of model in world 

				for (int i = 0; i < currentLine.length - off; i++)
					currentLine[i] = currentLine[i+off];*/
				
				float[] data = new float[currentLine.length];
				for (int i = 1; i < currentLine.length; i++)
					data[i] = Float.parseFloat(currentLine[i]);

				Vector3f pos = new Vector3f(data[1], data[2], data[3]);
				Vector3f rot = new Vector3f(
						(float)Math.toDegrees(data[4]), 
						(float)Math.toDegrees(data[5]), 
						(float)Math.toDegrees(data[6])
						);
				Vector3f size = new Vector3f(data[7], data[8], data[9]);
				Entity en = newBox(pos, rot, size, "partTexture");
				entities.add(en);

				/*String output = "";
				for (int i = 0; i < data.length; i++)
				{
					output += data[i] + ",";
				}
				System.out.println(output);*/
			}
			reader.close();
		} catch (Exception e) {e.printStackTrace(); return null;}
		//System.out.println("Successful" + fileName);
		return new Group(entities);
	}
	
	public static Group loadFromXML(String fileName, String partTexture, String colorTexture) //For monochromatic colored models
	{
		ArrayList<Entity> entities = new ArrayList<Entity>();
		FileReader fr = null;
		try {fr = new FileReader(new File("res/"+fileName));} catch (FileNotFoundException e) {}
		if (fr == null)
		{
			try {fr = new FileReader(new File("res/parsedObj/"+fileName));} catch (Exception ex) {System.out.println("Unsuccessful" + fileName); return null;}
		}
		BufferedReader reader = new BufferedReader(fr);
		String line;
		try
		{
			while ((line = reader.readLine()) != null)
			{	
				String[] currentLine = line.split(",");
				if (currentLine.length < 10) continue;
				
				float[] data = new float[currentLine.length];
				for (int i = 1; i < currentLine.length; i++)
					data[i] = Float.parseFloat(currentLine[i]);

				Vector3f pos = new Vector3f(data[1], data[2], data[3]);
				Vector3f rot = new Vector3f(
						(float)Math.toDegrees(data[4]), 
						(float)Math.toDegrees(data[5]), 
						(float)Math.toDegrees(data[6])
						);
				Vector3f size = new Vector3f(data[7], data[8], data[9]);
				Entity en;
				if (currentLine[0].equals("Color"))
					en = newBox(pos, rot, size, colorTexture);
				else
					en = newBox(pos, rot, size, partTexture);
				entities.add(en);
			}
			reader.close();
		} catch (Exception e) {e.printStackTrace(); return null;}
		return new Group(entities);
	}

	public static Entity newObjectFromModel(Vector3f position, Vector3f rotation, Vector3f size, float scale, String objFile, String textureName)
	{
		RawModel model = OBJLoader.loadObjModel(objFile, loader);
		ModelTexture texture = new ModelTexture(loader.loadTexture(textureName));
		TexturedModel texturedModel = new TexturedModel(model, texture);
		Entity entity = new Entity(texturedModel,position,rotation.x,rotation.y,rotation.z,1);
		entity.scale = scale;
		return entity;
	}

	public static Entity newBox(Vector3f position, Vector3f rotation, Vector3f size, String textureName)
	{
		float x = size.x/2F, y = size.y/2F, z = size.z/2F;
		float[] vertices = {			
				-x,y,-z,	
				-x,-y,-z,	
				x,-y,-z,	
				x,y,-z,		

				-x,y,z,	
				-x,-y,z,	
				x,-y,z,	
				x,y,z,

				x,y,-z,	
				x,-y,-z,	
				x,-y,z,	
				x,y,z,

				-x,y,-z,	
				-x,-y,-z,	
				-x,-y,z,	
				-x,y,z,

				-x,y,z,
				-x,y,-z,
				x,y,-z,
				x,y,z,

				-x,-y,z,
				-x,-y,-z,
				x,-y,-z,
				x,-y,z
		};

		/*float[] normals = {
				0,0,-1,
				0,0,-1,
				0,0,-1,
				0,0,-1,

				0,0,1,
				0,0,1,
				0,0,1,
				0,0,1,

				1,0,0,
				1,0,0,
				1,0,0,
				1,0,0,

				-1,0,0,
				-1,0,0,
				-1,0,0,
				-1,0,0,

				0,1,0,
				0,1,0,
				0,1,0,
				0,1,0,

				0,-1,0,
				0,-1,0,
				0,-1,0,
				0,-1,0,

				//1,0,0,
				//1,0,0,
				//-1,0,0,
				//-1,0,0,

				//0,1,0,
				//0,1,0,
				//0,-1,0,
				//0,-1,0
			};*/

		float[] normals = {
				0,0,1,
				0,0,1,
				0,0,1,
				0,0,1,

				0,0,-1,
				0,0,-1,
				0,0,-1,
				0,0,-1,

				-1,0,0,
				-1,0,0,
				-1,0,0,
				-1,0,0,

				1,0,0,
				1,0,0,
				1,0,0,
				1,0,0,

				0,-1,0,
				0,-1,0,
				0,-1,0,
				0,-1,0,

				0,1,0,
				0,1,0,
				0,1,0,
				0,1,0,
		};

		int[] indices = {
				0,1,3,	
				3,1,2,	
				4,5,7,
				7,5,6,
				8,9,11,
				11,9,10,
				12,13,15,
				15,13,14,	
				16,17,19,
				19,17,18,
				20,21,23,
				23,21,22
		};


		float[] textureCoords = {
				0,0,
				0,1,
				1,1,
				1,0,			
				0,0,
				0,1,
				1,1,
				1,0,			
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0
		};

		RawModel model = loader.loadToVAO(vertices, textureCoords, normals, indices);
		ModelTexture texture = new ModelTexture(loader.loadTexture(textureName));

		//TODO: texture.transparent = true;
		//TODO: texture.fastLighting = true;
		texture.shineDamper = 10;
		texture.reflectiveness = 1;

		TexturedModel texturedModel = new TexturedModel(model, texture);
		Entity entity = new Entity(texturedModel,position,rotation.x,rotation.y,rotation.z,1);
		entity.scale = 1;

		return entity;
	}

}
