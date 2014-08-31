package render;

public class Button {

	public float posX, posY;
	public float sizeX, sizeY;
	public String command;
	public boolean enabled;
	
	public Button(String command, float a, float b, float c, float d)
	{
		this.command = command;
		posX = a;
		posY = b;
		sizeX = c;
		sizeY = d;
		enabled = false;
	}
	
}
