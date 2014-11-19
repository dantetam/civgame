package render;

import java.util.ArrayList;

public class Button extends TextBox {

	//public float posX, posY;
	//public float sizeX, sizeY;
	
	public String command;
	
	public Button(String command, ArrayList<String> display, String tooltip, float a, float b, float c, float d) {
		super(display, tooltip, a, b, c, d);
		this.command = command;
	}
	
	public Button(String command, String display, String tooltip, float a, float b, float c, float d) {
		super(display, tooltip, a, b, c, d);
		this.command = command;
	}
	
	public Button(String command, String display, String tooltip, float a, float b, float c, float d, int... n) {
		super(display, tooltip, a, b, c, d, n);
		this.command = command;
	}
	
	/*public Button(String name, String display, String tooltip, float a, float b, float c, float d) {
		super(name, dtooltip, a, b, c, d);
		this.display.add(display);
		this.name = name;
	}*/
	
	/*public TextBox(String name, ArrayList<String> text, float x, float y, float sX, float sY)
	{
		super("", text, x, y, sX, sY);
		this.name = name;
		text = new ArrayList<String>();
		posX = x; posY = y;
		sizeX = sX; sizeY = sY;
	}*/
	
}
