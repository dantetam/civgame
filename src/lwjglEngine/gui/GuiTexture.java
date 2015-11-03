package lwjglEngine.gui;

import org.lwjgl.util.vector.Vector2f;

import lwjglEngine.render.DisplayManager;

//Completely scrap t

public class GuiTexture {

	public int texture;
	public Vector2f pos, size;
	public Vector2f pixelPos, pixelSize;
	public boolean active = true;
	
	public GuiTexture(int t, Vector2f p, Vector2f s) {
		texture = t;
		pos = new Vector2f(p.x/DisplayManager.width, p.y/DisplayManager.height); size = new Vector2f(s.x/DisplayManager.width, s.y/DisplayManager.height);
		pixelPos = p; pixelSize = s;
		active = true;
	}
	
	public boolean within(float x, float y)
	{
		return x > pixelPos.x && x < pixelPos.x + pixelSize.x && y > pixelPos.y && y < pixelPos.y + pixelSize.y;
	}


}
