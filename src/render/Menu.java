package render;

import java.util.ArrayList;

public class Menu {

	public ArrayList<Button> buttons;
	public String name;
	public boolean active;

	public Menu(String name)
	{
		this.name = name;
		buttons = new ArrayList<Button>();
		active = false;
	}

	public void addButton(String command, String display, float a, float b, float c, float d)
	{
		buttons.add(new Button(command,display,a,b,c,d));
	}
	
	public void addButton(String command, String display, float a, float b, float c, float d, int... n)
	{
		ArrayList<String> temp = new ArrayList<String>();
		buttons.add(new Button(command,display,a,b,c,d,n));
	}

	public String click(float mouseX, float mouseY)
	{
		for (int i = 0; i < buttons.size(); i++)
		{
			Button b = buttons.get(i);
			if (mouseX > b.posX && mouseX < b.posX+b.sizeX && mouseY > b.posY && mouseY < b.posY+b.sizeY)
			{
				return b.command;
			}
		}
		return null;
	}
	
	public void pass(boolean[] activeMenus, float mouseX, float mouseY)
	{
		for (int i = 0; i < buttons.size(); i++)
		{
			Button b = buttons.get(i);
			boolean skip = false;
			if (b.noOrdersIfMenu != null) //Check if all the menus that can stop the button from acting are not active
				for (int j = 0; j < b.noOrdersIfMenu.length; j++)
					if (activeMenus[b.noOrdersIfMenu[j]])
					{
						skip = true;
						break;
					}
			if (!skip)
				if (mouseX > b.posX && mouseX < b.posX+b.sizeX && mouseY > b.posY && mouseY < b.posY+b.sizeY && !b.orderOfType("expand"))
					b.expand(b.origSizeX*2, b.origSizeY, 20);
		}
	}
	
	public boolean equals(Menu other)
	{
		return name.equals(other.name);
	}
	
	//Returns the buttons to their original positions if there is no tween order
	public void origPosIfNoMouse()
	{
		for (int i = 0; i < buttons.size(); i++)
		{
			Button b = buttons.get(i);
			if (b.orders.size() == 0)
				buttons.get(i).setOriginal();
		}
	}
	
	/*public void on()
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
	}*/

}
