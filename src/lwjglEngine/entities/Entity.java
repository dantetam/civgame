package lwjglEngine.entities;

import org.lwjgl.util.vector.Vector3f;

import lwjglEngine.models.TexturedModel;

public class Entity {

	private TexturedModel model;
	public Vector3f position;
	public float rotX, rotY, rotZ;
	public float scale;
	
	public Entity(TexturedModel m, Vector3f p, float a, float b, float c, float s)
	{
		model = m;
		position = p;
		rotX = a; rotY = b; rotZ = c;
		scale = s;
	}
	
	public void move(float dx, float dy, float dz)
	{
		position.x += dx;
		position.y += dy;
		position.z += dz;
	}
	
	public void rotate(float dx, float dy, float dz)
	{
		rotX += dx;
		rotY += dy;
		rotZ += dz;
	}
	
	public TexturedModel getModel()
	{
		return model;
	}

}
