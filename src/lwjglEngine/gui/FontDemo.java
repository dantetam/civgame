/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package lwjglEngine.gui;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.libffi.Closure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.opengl.GL;

import java.util.concurrent.CountDownLatch;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import static org.lwjgl.stb.STBEasyFont.*;

/** STB Easy Font demo. */
public class FontDemo {

	protected final String text;
	private final   int    lineCount;

	private GLFWErrorCallback           errorfun;
	private GLFWWindowSizeCallback      windowSizefun;
	private GLFWFramebufferSizeCallback framebufferSizefun;
	private GLFWKeyCallback             keyfun;
	private GLFWScrollCallback          scrollfun;

	public static int width = 1500;
	public static int height = 900;
	public static long window;
	
	private static GLFWErrorCallback errorCallback;
	private static GLFWKeyCallback keyCallback;
	private static GLFWCursorPosCallback cursorPosCallback;
	public static GLFWMouseButtonCallback mouseButtonCallback;

	private boolean ctrlDown;

	private int fontHeight;

	private int   scale;
	private int   lineOffset;
	private float lineHeight;

	private Closure debugProc;
	
	public static void main(String[] args)
	{
		FontDemo demo = new FontDemo(12, "res/gothic.ttf");
		demo.run("FontDemo");
	}

	public FontDemo(int fontHeight, String filePath) {
		this.fontHeight = fontHeight;
		this.lineHeight = fontHeight;

		String t = null;
		int lc = 0;

		try {
			ByteBuffer source = ioResourceToByteBuffer(filePath, 4 * 1024);
			t = memDecodeUTF8(source).replaceAll("\t", "    "); // Replace tabs

			lc = 0;
			Matcher m = Pattern.compile("^.*$", Pattern.MULTILINE).matcher(t);
			while ( m.find() )
				lc++;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		text = t;
		lineCount = lc;

		//errorfun = GLFWErrorCallback.createPrint();

		windowSizefun = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				FontDemo.this.width = width;
				FontDemo.this.height = height;

				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();
				glOrtho(0.0, width, height, 0.0, -1.0, 1.0);
				glMatrixMode(GL_MODELVIEW);

				setLineOffset(lineOffset);
			}
		};

		framebufferSizefun = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				glViewport(0, 0, width, height);
			}
		};

		keyfun = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				ctrlDown = (mods & GLFW_MOD_CONTROL) != 0;
				if ( action == GLFW_RELEASE )
					return;

				switch ( key ) {
					case GLFW_KEY_ESCAPE:
						glfwSetWindowShouldClose(window, GL_TRUE);
						break;
					case GLFW_KEY_PAGE_UP:
						setLineOffset(lineOffset - height / FontDemo.this.lineHeight);
						break;
					case GLFW_KEY_PAGE_DOWN:
						setLineOffset(lineOffset + height / FontDemo.this.lineHeight);
						break;
					case GLFW_KEY_HOME:
						setLineOffset(0);
						break;
					case GLFW_KEY_END:
						setLineOffset(lineCount - height / FontDemo.this.lineHeight);
						break;
					case GLFW_KEY_KP_ADD:
					case GLFW_KEY_EQUAL:
						setScale(scale + 1);
						break;
					case GLFW_KEY_KP_SUBTRACT:
					case GLFW_KEY_MINUS:
						setScale(scale - 1);
						break;
					case GLFW_KEY_0:
					case GLFW_KEY_KP_0:
						if ( ctrlDown )
							setScale(0);
						break;
				}
			}
		};

		scrollfun = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				if ( ctrlDown )
					setScale(scale + (int)yoffset);
				else
					setLineOffset(lineOffset - (int)yoffset * 3);
			}
		};
	}

	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws Exception {
		ByteBuffer buffer;

		File file = new File(resource);
		if ( file.isFile() ) {
			FileInputStream fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();

			buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);

			while ( fc.read(buffer) != -1 ) ;

			fis.close();
			fc.close();
		} else {
			buffer = BufferUtils.createByteBuffer(bufferSize);

			InputStream source = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
			if ( source == null )
				throw new Exception(resource);

			try {
				ReadableByteChannel rbc = Channels.newChannel(source);
				try {
					while ( true ) {
						int bytes = rbc.read(buffer);
						if ( bytes == -1 )
							break;
						if ( buffer.remaining() == 0 )
							buffer = resizeBuffer(buffer, buffer.capacity() * 2);
					}
				} finally {
					rbc.close();
				}
			} finally {
				source.close();
			}
		}

		buffer.flip();
		return buffer;
	}
	public static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity)
	{
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}
	
	public String getText() {
		return text;
	}

	public long getWindow() {
		return window;
	}

	public int getFontHeight() {
		return fontHeight;
	}

	public int getScale() {
		return scale;
	}

	public int getLineOffset() {
		return lineOffset;
	}

	protected void run(String title) {
		try {
			init(title);

			loop();
		} finally {
			try {
				destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void init(String title) {
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

		if(window == 0) {
		    throw new RuntimeException("Failed to create window");
		}

		GL11.glViewport(0, 0, width, height);
		
		glfwSetKeyCallback(window, (keyCallback = new GLFWKeyCallback() {

		    @Override
		    public void invoke(long window, int key, int scancode, int action, int mods) {
		        if(key == GLFW_KEY_SPACE && action == GLFW.GLFW_PRESS) {
		        	System.out.println("space");
		        }
		    }

		}));
		
		GLFW.glfwSetCursorPosCallback(window, (cursorPosCallback = new GLFWCursorPosCallback() {
		    public void invoke(long window, double xpos, double ypos) {
		        Mouse.setMouse((float)xpos, height - (float)ypos);
		    }
		}));
		/*//errorfun.set();
		if ( glfwInit() != GL_TRUE )
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

		this.window = glfwCreateWindow(ww, wh, title, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		windowSizefun.set(window);
		framebufferSizefun.set(window);
		keyfun.set(window);
		scrollfun.set(window);

		// Center window
		GLFWvidmode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwSetWindowPos(
			window,
			(vidmode.getWidth() - ww) / 2,
			(vidmode.getHeight() - wh) / 2
		);

		// Create context
		glfwMakeContextCurrent(window);
		GL.createCapabilities(true);

		glfwSwapInterval(1);
		glfwShowWindow(window);
		glfwInvoke(window, windowSizefun, framebufferSizefun);*/
	}

	private void setScale(int scale) {
		this.scale = max(-3, scale);
		this.lineHeight = fontHeight * (1.0f + this.scale * 0.25f);
		setLineOffset(lineOffset);
	}

	private void setLineOffset(float offset) {
		setLineOffset(round(offset));
	}

	private void setLineOffset(int offset) {
		lineOffset = max(0, min(offset, lineCount - (int)(height / lineHeight)));
	}
	
	protected void loop()
	{
		ByteBuffer charBuffer = BufferUtils.createByteBuffer(text.length() * 270);
		int quads = stb_easy_font_print(0, 0, getText(), null, charBuffer);

		glEnableClientState(GL_VERTEX_ARRAY);
		glVertexPointer(2, GL_FLOAT, 16, charBuffer);

		glClearColor(43f / 255f, 43f / 255f, 43f / 255f, 0f); // BG color
		glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color

		while ( glfwWindowShouldClose(getWindow()) == GL_FALSE ) {
			glfwPollEvents();

			glClear(GL_COLOR_BUFFER_BIT);

			float scaleFactor = 1.0f + getScale() * 0.25f;

			glPushMatrix();
			// Zoom
			glScalef(scaleFactor, scaleFactor, 1f);
			// Scroll
			glTranslatef(4.0f, 4.0f - getLineOffset() * getFontHeight(), 0f);

			glDrawArrays(GL_QUADS, 0, quads * 4);

			glPopMatrix();

			glfwSwapBuffers(getWindow());
		}

		glDisableClientState(GL_VERTEX_ARRAY);

		glfwDestroyWindow(getWindow());
	}

	private void destroy() {
		if ( debugProc != null )
			debugProc.release();
		scrollfun.release();
		keyfun.release();
		framebufferSizefun.release();
		windowSizefun.release();
		glfwTerminate();
		errorfun.release();
	}

}