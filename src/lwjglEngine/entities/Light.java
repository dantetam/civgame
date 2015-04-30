package lwjglEngine.entities;

import org.lwjgl.util.vector.Vector3f;

//Represent a colored point light

public class Light {

	public Vector3f position, color;

	public Light(Vector3f p, Vector3f c)
	{
		position = p;
		color = c;
	}
	
}
