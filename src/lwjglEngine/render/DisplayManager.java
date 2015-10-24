package lwjglEngine.render;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {

	public static final int width = 1500, height = 900;
	
	public static void createDisplay()
	{
		//Always declare this line in this inline style
		ContextAttribs attribs = new ContextAttribs(3,2).withForwardCompatible(true).withProfileCore(true); //version 3.2;

		//attribs.withProfileCore(true);
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create(new PixelFormat(), attribs);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		GL11.glViewport(0, 0, width, height);
	}
	
	public static void updateDisplay()
	{
		Display.sync(120);
		Display.update();
	}
	
	public static void closeDisplay() 
	{
		Display.destroy();
	}

}
