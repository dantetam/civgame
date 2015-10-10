package lwjglEngine.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

//There is no real camera in OpenGL
//Every object in the world must be moved in the opposite direction of the camera's movement

public class Camera {

	public Vector3f position = new Vector3f(1000,20,1000);
	public float pitch, yaw, roll; //High-low, left-right, tilt
	
	public Camera() {yaw = (float)Math.PI/4F;}
	//public Camera(Vector3f p, float a, float b, float c) {}
	
	public void move()
	{
		float step = 1f;
		if (Keyboard.isKeyDown(Keyboard.KEY_Q))
			position.y -= step;
		if (Keyboard.isKeyDown(Keyboard.KEY_E))
			position.y += step;
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			position.x -= step;
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			position.x += step;
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
			position.z += step;
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			position.z -= step;
	}
	
}
