package lwjglEngine.render;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWvidmode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.*;

public class DisplayManager {

	public static final int width = 1500, height = 900;
	private static GLFWErrorCallback errorCallback;
	public static long window;
	
	public static void createDisplay()
	{
		glfwInit();
		glfwSetErrorCallback(errorCallback = Callbacks.errorCallbackPrint(System.err));
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); 
		window = glfwCreateWindow(width, height, "Pong - LWJGL3", 0, 0);
		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();
		glfwShowWindow(window);

		if(window == 0) {
		    throw new RuntimeException("Failed to create window");
		}

		GL11.glViewport(0, 0, width, height);
	}
	
	public static void updateDisplay()
	{
		/*Display.sync(120);
		Display.update();*/
		glfwPollEvents();
		glfwSwapBuffers(window);
	}
	
	public static void closeDisplay() 
	{
		glfwDestroyWindow(window);
	}
	
	public static boolean requestClose()
	{
		return glfwWindowShouldClose(DisplayManager.window) == GL_TRUE;
	}

}
