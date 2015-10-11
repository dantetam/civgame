package lwjglEngine.gui;

import org.lwjgl.util.vector.Vector2f;

public class GuiTexture {

	public int texture;
	public Vector2f pos, size;
	
	public GuiTexture(int t, Vector2f p, Vector2f s) {
		texture = t;
		pos = p; size = s;
	}

}
