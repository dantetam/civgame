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
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBEasyFont.stb_easy_font_print;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryUtil.*;


import lwjglEngine.models.RawModel;
import lwjglEngine.render.DisplayManager;
import lwjglEngine.render.Loader;
import lwjglEngine.toolbox.Maths;
import render.Menu;
import render.TechMenu;
import render.TextBox;
import system.MenuSystem;

public class GuiRenderer {

	private final RawModel quad; //Same model, will be moved and scaled across screen
	private GuiShader shader;
	//private UnicodeFont unicodeFont;

	public GuiRenderer(Loader loader) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		float[] positions = {-1,1,-1,-1,1,1,1,-1};
		quad = loader.loadToVAO(positions);
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
			Matrix4f matrix = Maths.createTransformationMatrix(
					normalize(new Vector2f(gui.pixelPos.x + gui.pixelSize.x/2, DisplayManager.height - (gui.pixelPos.y + gui.pixelSize.y/2))), 
					normalizeSize(gui.pixelSize)
					);
			shader.loadColor(new Vector4f(gui.color.x/255f, gui.color.y/255f, gui.color.z/255f, gui.color.w/255f));
			//if (gui instanceof TextBox)
			//System.out.println(gui.color + " " + ((TextBox)gui).display.get(0));
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.vertexCount);
		}
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		/*for (GuiTexture gui: guis)
		{
			if (gui instanceof TextBox)
				showText((TextBox)gui);
		}*/
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

	/*private void showText(TextBox gui)
	{
		ByteBuffer charBuffer = BufferUtils.createByteBuffer(gui.display.get(0).length() * 270);
		int quads = stb_easy_font_print(0, 0, gui.display.get(0), null, charBuffer);

		//glEnableClientState(GL_VERTEX_ARRAY);
		//glVertexPointer(2, GL_FLOAT, 16, charBuffer);

		//glClearColor(150f / 255f, 225f / 255f, 255f / 255f, 0f); // BG color
		//glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color

		//glPushMatrix();
		// Zoom
		//GL11.glglScalef(scaleFactor, scaleFactor, 1f);
		// Scroll
		//glTranslatef(4.0f, 4.0f - getLineOffset() * getFontHeight(), 0f);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, quads);

		//Matrix4f matrix = Maths.createTransformationMatrix(normalize(new Vector2f(gui.pos.x + gui.size.x/2, gui.pos.y + gui.size.y/2)), normalizeSize(gui.size));

		Matrix4f matrix = Maths.createTransformationMatrix(gui.pos, gui.size);

		shader.loadTransformation(matrix);

		glDrawArrays(GL_QUADS, 0, quads * 4);

		//glTranslatef(-4.0f, -4.0f + getLineOffset() * getFontHeight(), 0f);

		//glPopMatrix();
	}*/

	public void render(MenuSystem menuSystem)
	{
		ArrayList<GuiTexture> guis = new ArrayList<GuiTexture>();
		for (Menu menu: menuSystem.menus)
		{
			if (menu.active())
				for (GuiTexture gui: menu.buttons)
					guis.add(gui);
			if (menu.name.equals("MainMenu"))
			{
				for (GuiTexture gui: menu.buttons)
				{
					String text = ((TextBox)gui).display.size() > 0 ? ((TextBox)gui).display.get(0) : null;
					System.out.println("Moving: " + text + " " + ((TextBox)gui).pos + " " + ((TextBox)gui).size);
				}
				System.out.println(menu.buttons.size());
			}
		}
		//System.out.println("*");
		//ArrayList<GuiTexture> techMenuGuis = renderTechMenu(menuSystem.techMenu);
		/*if (menuSystem.techMenu.active())
			for (GuiTexture gui: menuSystem.techMenu.buttons)
				guis.add(gui);*/
		for (GuiTexture gui: menuSystem.textboxes)
			if (gui.active)
				guis.add(gui);
		render(guis);
	}
	/*public ArrayList<GuiTexture> renderTechMenu(TechMenu techMenu)
	{
		//Display tech tree
		main.pushStyle();
		main.strokeWeight(3);
		//System.out.println("yaaaa");
		main.stroke(255,255,255,50);
		for (int i = 0; i < techMenu.lines.size(); i++)
		{
			main.line(techMenu.lines.get(i).x1, techMenu.lines.get(i).y1, techMenu.lines.get(i).x2, techMenu.lines.get(i).y2);
		}
		main.fill(255,255,255,255); main.noStroke(); //Reset alpha
		for (int i = 0; i < techMenu.buttons.size(); i++)
		{
			TextBox b = techMenu.buttons.get(i);
			if (b.active)
			{
				main.fill(b.r, b.g, b.b);
				//main.stroke(b.borderR, b.borderG, b.borderB);
				strokeTextbox(b.borderR, b.borderG, b.borderB);
				if (b.shape == 0)
					main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
				else if (b.shape == 1)
					main.ellipse(b.posX, b.posY, b.sizeX, b.sizeY);
				else
					System.out.println("Invalid button shape: " + b.shape);
				main.textAlign(PApplet.CENTER, PApplet.CENTER);
				main.fill(255);
				displayText(b);
				main.fill(255,0,0);
				//System.out.println(b.display.get(0) + ": " + b.posX + " " + b.posY + " " + b.sizeX + " " + b.sizeY);
				shortcutText(b);
			}
		}
		main.popStyle();
	}*/

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
