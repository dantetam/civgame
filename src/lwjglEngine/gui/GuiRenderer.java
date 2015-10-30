package lwjglEngine.gui;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryUtil.*;


import lwjglEngine.models.RawModel;
import lwjglEngine.render.DisplayManager;
import lwjglEngine.render.Loader;
import lwjglEngine.toolbox.Maths;
import render.Menu;
import render.TextBox;
import system.MenuSystem;

public class GuiRenderer {

	private final RawModel quad; //Same model, will be moved and scaled across screen
	private GuiShader shader;
	//private UnicodeFont unicodeFont;

	public GuiRenderer(Loader loader) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		float[] positions = {-1,1,-1,-1,1,1,1,-1};
		quad = loader.loadToVao(positions);
		shader = new GuiShader();

		/*try {
			unicodeFont = new UnicodeFont("res/gothic.ttf", 20, false, false);
			unicodeFont.getEffects().add(new ColorEffect());
			unicodeFont.addAsciiGlyphs();
			unicodeFont.loadGlyphs();
		} catch (SlickException e) {
			unicodeFont = null;
			e.printStackTrace();
		}*/
	}

	public void render(ArrayList<GuiTexture> guis)
	{
		shader.start();
		GL30.glBindVertexArray(quad.vaoID);
		GL20.glEnableVertexAttribArray(0);
		for (GuiTexture gui: guis)
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.texture);
			Matrix4f matrix = Maths.createTransformationMatrix(normalize(new Vector2f(gui.pos.x + gui.size.x/2, gui.pos.y + gui.size.y/2)), normalizeSize(gui.size));
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.vertexCount);
			if (gui instanceof TextBox)
				showText((TextBox)gui);
		}
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
		/*for (GuiTexture gui: guis)
		{
			fpsFont.drawString(280.0F, 300.0F, "", Color.red);
		}*/
	}
	private Vector2f normalize(Vector2f v)
	{
		return new Vector2f(v.x*2/DisplayManager.width - 1, v.y*2/DisplayManager.height - 1);
	}
	private Vector2f normalizeSize(Vector2f v)
	{
		return new Vector2f(v.x/DisplayManager.width, v.y/DisplayManager.height);
	}
	private ByteBuffer cdata;
	private void showText(TextBox gui)
	{
		int BITMAP_W = 512;
		int BITMAP_H = 512;

		int texID = GL11.glGenTextures();
		//STBTTBakedChar.
		//STBTTBakedChar..Buffer cdata = STBTTBakedChar.mallocBuffer(96);
		STBTTBakedChar.malloc(96, 0, 0, 0, 0, 0, texID);

		try {
			ByteBuffer ttf = ioResourceToByteBuffer("res/gothic.ttf", 160 * 1024);

			ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
			stbtt_BakeFontBitmap(ttf, getFontHeight(), bitmap, BITMAP_W, BITMAP_H, 32, cdata);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE, bitmap);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		GL11.glClearColor(43f / 255f, 43f / 255f, 43f / 255f, 0f); // BG color
		GL11.glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		FloatBuffer x = BufferUtils.createFloatBuffer(1);
		FloatBuffer y = BufferUtils.createFloatBuffer(1);
		ByteBuffer q = STBTTAlignedQuad.malloc();

		//float scaleFactor = 1.0f + getScale() * 0.25f;
		float scaleFactor = 1.0f;

		GL11.glPushMatrix();
		// Zoom
		GL11.glScalef(scaleFactor, scaleFactor, 1f);
		// Scroll
		glTranslatef(4.0f, getFontHeight() * 0.5f + 4.0f - getLineOffset() * getFontHeight(), 0f);

		x.put(0, 0.0f);
		y.put(0, 0.0f);
		GL11.glBegin(GL11.GL_QUADS);
		for ( int i = 0; i < gui.display.get(0).length(); i++ ) {
			char c = gui.display.get(0).charAt(i);
			if ( c == '\n' ) {
				y.put(0, y.get(0) + getFontHeight());
				x.put(0, 0.0f);
				continue;
			} else if ( c < 32 || 128 <= c )
				continue;

			stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, c - 32, x, y, q, 1);

			/*GL11.glTexCoord2f(q.getShort(0), q.getShort(0));
			GL11.glVertex2f(q.getX0(), q.getY0());

			GL11.glTexCoord2f(q.getS1(), q.getT0());
			GL11.glVertex2f(q.getX1(), q.getY0());

			GL11.glTexCoord2f(q.getS1(), q.getT1());
			GL11.glVertex2f(q.getX1(), q.getY1());

			GL11.glTexCoord2f(q.getS0(), q.getT1());
			GL11.glVertex2f(q.getX0(), q.getY1());*/
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(0,0);

			GL11.glTexCoord2f(1,0);
			GL11.glVertex2f(1,0);

			GL11.glTexCoord2f(1,1);
			GL11.glVertex2f(1,1);

			GL11.glTexCoord2f(0,1);
			GL11.glVertex2f(0,1);
		}
		GL11.glEnd();

		GL11.glPopMatrix();
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

	public void render(MenuSystem menuSystem)
	{
		ArrayList<GuiTexture> guis = new ArrayList<GuiTexture>();
		for (Menu menu: menuSystem.menus)
		{
			if (menu.active())
				for (GuiTexture gui: menu.buttons)
					if (gui.active)
						guis.add(gui);
		}
		for (GuiTexture gui: menuSystem.textboxes)
			if (gui.active)
				guis.add(gui);
		render(guis);
	}
	
	public int getFontHeight()
	{
		return 12;
	}
	public int getLineOffset()
	{
		return 0;
	}

	public void cleanUp()
	{
		//memFree(cdata);
		shader.cleanUp();
	}

}
