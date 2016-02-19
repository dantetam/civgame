package lwjglEngine.render;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import lwjglEngine.fontRendering.TextMaster;
import lwjglEngine.gui.Keyboard;
import lwjglEngine.gui.Mouse;
import render.CivGame;

public class DisplayManager {

	public static final int width = 1500, height = 900;
	public static long window;
	public static CivGame main;
	
	//Store the callbacks in memory
	private static GLFWErrorCallback errorCallback;
	public static GLFWKeyCallback keyCallback;
	private static GLFWCursorPosCallback cursorPosCallback;
	public static GLFWMouseButtonCallback mouseButtonCallback;
	
	public DisplayManager(CivGame m)
	{
		main = m;
	}
	
	public static void createDisplay()
	{
		glfwInit();
		glfwSetErrorCallback(errorCallback = Callbacks.errorCallbackPrint(System.err));
		
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); 
		window = glfwCreateWindow(width, height, "", 0, 0);
		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();
		glfwShowWindow(window);

		setCursorPosCallback();
		setMouseCallback();
		setKeyCallback();
		
		if (window == 0) {
		    throw new RuntimeException("Failed to create window");
		}

		GL11.glViewport(0, 0, width, height);
	}
	
	public static void setCursorPosCallback()
	{
		GLFW.glfwSetCursorPosCallback(window, (cursorPosCallback = new GLFWCursorPosCallback() {
		    public void invoke(long window, double xpos, double ypos) {
		        Mouse.setMouse((float)xpos, (float)ypos);
		    }
		}));
	}
	
	public static void setMouseCallback()
	{
		GLFW.glfwSetMouseButtonCallback(DisplayManager.window, (DisplayManager.mouseButtonCallback = new GLFWMouseButtonCallback() {
			public void invoke(long window, int button, int action, int mods) {
				if (action == GLFW.GLFW_PRESS) {
					main.menuSystem.queueMousePass(Mouse.getX(), Mouse.getY()); //includes button == 2 i.e. scroll wheel
					if (button == 0) {
						main.inputSystem.passLeftMouseClick(Mouse.getX(), Mouse.getY());
					} 
					else if (button == 1) {
						main.inputSystem.passRightMouseClick(Mouse.getX(), Mouse.getY());
					}
					TextMaster.update(main.menuSystem);
					main.lwjglSystem.renderer.guiRenderer.update(main.menuSystem);
					//menuSystem.closeMenus();
				} 

			}
		}));
	}
	
	public static void setKeyCallback()
	{
		GLFW.glfwSetKeyCallback(DisplayManager.window, (DisplayManager.keyCallback = new GLFWKeyCallback() {
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (action == GLFW.GLFW_PRESS) {
					main.inputSystem.keyPressed(key);
					main.menuSystem.forceUpdate();
					//menuSystem.closeMenus();
				}
			}
		}));
	}
	
	public static void updateDisplay()
	{
		for (int i = GLFW.GLFW_KEY_0; i <= GLFW.GLFW_KEY_Z; i++)
		{
			Keyboard.keys[i] = GLFW.glfwGetKey(window, i) == GLFW.GLFW_PRESS;
		}
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
