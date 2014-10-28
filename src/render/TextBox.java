package render;

import java.util.ArrayList;

public class TextBox {

	public String name;
	public float posX, posY;
	public float sizeX, sizeY;
	public ArrayList<String> text;
	public boolean active = true;
	
	public TextBox(String name, float x, float y, float sX, float sY)
	{
		this.name = name;
		text = new ArrayList<String>();
		posX = x; posY = y;
		sizeX = sX; sizeY = sY;
	}
	
}
