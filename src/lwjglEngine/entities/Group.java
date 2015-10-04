package lwjglEngine.entities;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

public class Group {

	public ArrayList<Entity> entities;
	public Vector3f position = new Vector3f(0,0,0);
	
	public Group() {
		entities = new ArrayList<Entity>();
	}
	
	public Group(ArrayList<Entity> entities) {
		this.entities = entities;
	}
	
	public void move(float x, float y, float z)
	{
		//Undo the previous translation
		for (Entity en: entities)
		{
			en.position.x -= position.x;
			en.position.y -= position.y;
			en.position.z -= position.z;
		}
		//Record the new translation
		position.x = x;
		position.y = y;
		position.z = z;
		//Use the new translation
		for (Entity en: entities)
		{
			en.position.x += position.x;
			en.position.y += position.y;
			en.position.z += position.z;
		}
	}
	
	public float[] boundingBox()
	{
		if (entities.size() == 0) return null;
		float minX = entities.get(0).position.x, maxX = entities.get(0).position.x;
		float minY = entities.get(0).position.y, maxY = entities.get(0).position.y;
		float minZ = entities.get(0).position.z, maxZ = entities.get(0).position.z;
		if (entities.size() == 1) return new float[]{minX, minY, minZ, maxX-minX, maxY-minY, maxZ-minZ};
		for (int i = 1; i < entities.size(); i++)
		{
			float x = entities.get(i).position.x;
			float y = entities.get(i).position.y;
			float z = entities.get(i).position.z;
			if (x < minX) minX = x; else if (x > maxX) maxX = x; 
			if (y < minY) minY = y; else if (y > maxY) maxY = y; 
			if (z < minZ) minZ = z; else if (z > maxZ) maxZ = z; 
		}
		return new float[]{minX, minY, minZ, maxX-minX, maxY-minY, maxZ-minZ};
	}

}
