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

}
