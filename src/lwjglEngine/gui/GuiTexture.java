package lwjglEngine.gui;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import lwjglEngine.render.DisplayManager;

//Completely scrap t

public class GuiTexture {

	public int texture;
	public Vector2f pos, size;
	public Vector2f origPos;
	public Vector2f pixelPos, pixelSize;
	public boolean active = false;
	public Vector4f color = new Vector4f(0,0,0,255);
	
	public GuiTexture(int t, Vector2f p, Vector2f s) {
		texture = t;
		pixelPos = p; pixelSize = s;
		origPos = p; 
		pos = new Vector2f(p.x/DisplayManager.width, p.y/DisplayManager.height); size = new Vector2f(s.x/DisplayManager.width, s.y/DisplayManager.height);
		active = true;
	}
	
	public boolean within(float x, float y)
	{
		return x > pixelPos.x && x < pixelPos.x + pixelSize.x && y > pixelPos.y && y < pixelPos.y + pixelSize.y;
	}


}
