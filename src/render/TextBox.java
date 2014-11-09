package render;

import java.util.ArrayList;

public class TextBox extends Button {

	//public float posX, posY;
	//public float sizeX, sizeY;
	
	public TextBox(String name, ArrayList<String> display, String tooltip, float a, float b, float c, float d) {
		super("", display, tooltip, a, b, c, d);
		this.name = name;
	}
	
	public TextBox(String name, String display, String tooltip, float a, float b, float c, float d) {
		super("", new ArrayList<String>(), tooltip, a, b, c, d);
		this.display.add(display);
		this.name = name;
	}
	
	/*public TextBox(String name, ArrayList<String> text, float x, float y, float sX, float sY)
	{
		super("", text, x, y, sX, sY);
		this.name = name;
		text = new ArrayList<String>();
		posX = x; posY = y;
		sizeX = sX; sizeY = sY;
	}*/
	
}
