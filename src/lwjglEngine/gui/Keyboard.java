package lwjglEngine.gui;

import org.lwjgl.glfw.GLFW;

public class Keyboard {

	public static boolean[] keys = new boolean[200];
	
	public static boolean isKeyDown(int key)
	{
		return keys[key];
	}
	
}
