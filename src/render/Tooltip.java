package render;

import java.util.ArrayList;

//TODO: Fix backwards GUI inheritances

public class Tooltip {

	public float posX, posY;
	public float sizeX, sizeY;
	public String display;
	public boolean active = false;
	
	public Tooltip(String display, float a, float b, float c, float d)
	{
		this.display = display;
		posX = a;
		posY = b;
		sizeX = c;
		sizeY = d;
	}
	
}
