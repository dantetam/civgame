package lwjglEngine.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import lwjglEngine.models.RawModel;
import lwjglEngine.render.Loader;
import lwjglEngine.toolbox.Maths;
import render.Menu;
import system.MenuSystem;

public class GuiRenderer {

	private final RawModel quad; //Same model, will be moved and scaled across screen
	private GuiShader shader;
	
	public GuiRenderer(Loader loader) {
		float[] positions = {-1,1,-1,-1,1,1,1,-1};
		quad = loader.loadToVao(positions);
		shader = new GuiShader();
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
			Matrix4f matrix = Maths.createTransformationMatrix(normalize(new Vector2f(gui.pos.x + gui.size.x/2, gui.pos.y + gui.size.y/2)), normalize(gui.size));
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.vertexCount);
		}
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	private static float width = 1500, height = 900;
	private Vector2f normalize(Vector2f v)
	{
		return new Vector2f(v.x*2/width - 1, v.y*2/height - 1);
	}
	
	public void render(MenuSystem menuSystem)
	{
		ArrayList<GuiTexture> guis = new ArrayList<GuiTexture>();
		for (Menu menu: menuSystem.menus)
		{
			for (GuiTexture gui: menu.buttons)
				if (gui.active)
					guis.add(gui);
		}
		for (GuiTexture gui: menuSystem.textboxes)
			if (gui.active)
				guis.add(gui);
		render(guis);
	}

	public void cleanUp()
	{
		shader.cleanUp();
	}
	
}
