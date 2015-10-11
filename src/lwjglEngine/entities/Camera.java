package lwjglEngine.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

//There is no real camera in OpenGL
//Every object in the world must be moved in the opposite direction of the camera's movement

public class Camera {

	public Vector3f position = new Vector3f(500,10,500);
	public float pitch, yaw, roll; //High-low, left-right, tilt
	private float jerkPitch, jerkYaw; private int turnsPitch, turnsYaw;

	public Camera() {}
	//public Camera(Vector3f p, float a, float b, float c) {}

	public void focusCamera(float x, float z)
	{
		position = new Vector3f(x,10,z);
		turnsPitch = 20; turnsYaw = 20;
		jerkPitch = -pitch/(float)turnsPitch; 
		jerkYaw = -yaw/(float)turnsYaw;
		//pitch = 0; yaw = 0; roll = 0;
	}

	public void move()
	{
		if (turnsPitch > 0 || turnsYaw > 0) 
		{
			if (turnsPitch > 0)
			{
				pitch += jerkPitch;
				turnsPitch--;
			}
			if (turnsYaw > 0)
			{
				yaw += jerkYaw;
				turnsYaw--;
			}
			return; //Override keyboard when camera is being shifted
		}
		float step = 2.5f, tilt = 0.5f;
		if (Keyboard.isKeyDown(Keyboard.KEY_I))
			position.y -= step;
		if (Keyboard.isKeyDown(Keyboard.KEY_O))
			position.y += step;
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			position.x -= step*Math.cos(Math.toRadians(yaw));
			position.z -= step*Math.sin(Math.toRadians(yaw));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			position.x += step*Math.cos(Math.toRadians(yaw));
			position.z += step*Math.sin(Math.toRadians(yaw));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			//laziness, oops
			position.x -= step*Math.cos(Math.toRadians(yaw-90));
			position.z -= step*Math.sin(Math.toRadians(yaw-90));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			//+90 is clockwise, this is a right turn from pointing left (the 'a' command)
			position.x -= step*Math.cos(Math.toRadians(yaw+90));
			position.z -= step*Math.sin(Math.toRadians(yaw+90));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Q))
			yaw -= tilt;
		if (Keyboard.isKeyDown(Keyboard.KEY_E))
			yaw += tilt;
		if (Keyboard.isKeyDown(Keyboard.KEY_H))
			yaw -= tilt;
		if (Keyboard.isKeyDown(Keyboard.KEY_K))
			yaw += tilt;
		if (Keyboard.isKeyDown(Keyboard.KEY_U))
			pitch -= tilt;
		if (Keyboard.isKeyDown(Keyboard.KEY_J))
			pitch += tilt;
	}

}
