package render;

import java.util.ArrayList;

//TODO: Fix backwards GUI inheritances

public class Tooltip {

	public float posX, posY;
	public float sizeX, sizeY;
	public ArrayList<String> display;
	public boolean active = false;
	
	public Tooltip(String s, float a, float b, float c, float d)
	{
		display = new ArrayList<String>();
		display.add(s);
		posX = a;
		posY = b;
		sizeX = c;
		sizeY = d;
	}
	
}
