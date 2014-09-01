package render;

public class Button {

	public float posX, posY;
	public float sizeX, sizeY;
	public String command;
	public String display;
	//public boolean enabled;
	
	public Button(String command, String display, float a, float b, float c, float d)
	{
		this.command = command;
		this.display = display;
		posX = a;
		posY = b;
		sizeX = c;
		sizeY = d;
		//enabled = false;
	}
	
}
