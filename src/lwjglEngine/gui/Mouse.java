package lwjglEngine.gui;

public class Mouse {

	private static float x,y; 
	
	public static void setMouse(float a, float b) {x = a; y = b;} //Only to be used by DisplayManager/GLFW abstract
	
	public static float getX() {return x;}
	public static float getY() {return y;}
	
}
