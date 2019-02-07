package lwjglEngine.entities;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import lwjglEngine.gui.Keyboard;

//There is no real camera in OpenGL
//Every object in the world must be moved in the opposite direction of the camera's movement

public class Camera {

	public Vector3f position = new Vector3f(500,100,500);
	public float pitch = -10, yaw = 0, roll = 0; //High-low, left-right, tilt
	private float jerkPitch, jerkYaw; private int turnsPitch, turnsYaw;
	private static final float LOWER_BOUND_Y = 40, UPPER_BOUND_Y = 400;
	
	public Camera() {}
	//public Camera(Vector3f p, float a, float b, float c) {}

	//Allow the user to focus on the point (x, z) from a top-view perspective
	public void focusCamera(float x, float z, float angPitch)
	{
		float distanceBack = Math.abs(position.y / (float)Math.tan(angPitch));
		position = new Vector3f(x,position.y,z + distanceBack);
		turnsPitch = 20; turnsYaw = 20;
		//jerkPitch = -(pitch-(float)Math.toDegrees(Math.atan(position.y/20F)))/(float)turnsPitch; 
		jerkPitch = -(pitch+angPitch)/(float)turnsPitch; 
		jerkYaw = -(yaw)/(float)turnsYaw;
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
		float step = 5f, tilt = 1f;
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_I)) {
			position.y -= step;
			if (position.y < LOWER_BOUND_Y) {
				position.y = LOWER_BOUND_Y;
			}
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_O)) {
			position.y += step;
			if (position.y > UPPER_BOUND_Y) {
				position.y = UPPER_BOUND_Y;
			}
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_A))
		{
			position.x -= step*Math.cos(Math.toRadians(yaw));
			position.z -= step*Math.sin(Math.toRadians(yaw));
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_D))
		{
			position.x += step*Math.cos(Math.toRadians(yaw));
			position.z += step*Math.sin(Math.toRadians(yaw));
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_S))
		{
			//laziness, oops
			position.x -= step*Math.cos(Math.toRadians(yaw-90));
			position.z -= step*Math.sin(Math.toRadians(yaw-90));
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_W))
		{
			//+90 is clockwise, this is a right turn from pointing left (the 'a' command)
			position.x -= step*Math.cos(Math.toRadians(yaw+90));
			position.z -= step*Math.sin(Math.toRadians(yaw+90));
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_Q))
			yaw -= tilt*2f;
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_E))
			yaw += tilt*2f;
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_H))
			yaw -= tilt*2f;
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_K))
			yaw += tilt*2f;
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_U))
			pitch -= tilt;
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_J))
			pitch += tilt;
	}

}
