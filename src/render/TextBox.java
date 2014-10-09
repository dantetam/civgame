package render;

import java.util.ArrayList;

public class TextBox {

	public float posX, posY;
	public float sizeX, sizeY;
	public ArrayList<String> strings;
	
	public TextBox(float x, float y, float sX, float sY)
	{
		strings = new ArrayList<String>();
		posX = x; posY = y;
		sizeX = sX; sizeY = sY;
	}
	
}
