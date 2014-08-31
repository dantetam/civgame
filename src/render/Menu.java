package render;

import java.util.ArrayList;

public class Menu {

	public ArrayList<Button> buttons;

	public Menu()
	{
		buttons = new ArrayList<Button>();
	}

	public void addButton(String command, float a, float b, float c, float d)
	{
		buttons.add(new Button(command,a,b,c,d));
	}

	public String click(float mouseX, float mouseY)
	{
		for (int i = 0; i < buttons.size(); i++)
		{
			Button b = buttons.get(i);
			if (b.enabled)
			{
				if (mouseX > b.posX && mouseX < b.posX+b.sizeX && mouseY > b.posY && mouseY < b.posY+b.sizeY)
				{
					return b.command;
				}
			}
		}
		return null;
	}
	
	public void on()
	{
		for (int i = 0; i < buttons.size(); i++)
		{
			buttons.get(i).enabled = true;
		}
	}
	
	public void off()
	{
		for (int i = 0; i < buttons.size(); i++)
		{
			buttons.get(i).enabled = false;
		}
	}

}
