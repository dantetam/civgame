package render;

import java.util.ArrayList;

public class Menu {

	public ArrayList<TextBox> buttons;
	public String name;
	private boolean active;
	public boolean noShortcuts = false;

	public Menu(String name)
	{
		this.name = name;
		buttons = new ArrayList<TextBox>();
		active = false;
	}

	public TextBox addButton(String command, String display, String tooltip, float a, float b, float c, float d)
	{
		Button temp = new Button(command,display,tooltip,a,b,c,d);
		temp.menu = this;
		buttons.add(temp);
		return temp;
	}
	
	public TextBox addButton(TextBox temp)
	{
		temp.menu = this;
		buttons.add(temp);
		return temp;
	}


	/*public void addButton(String command, ArrayList<String> display, String tooltip, float a, float b, float c, float d, int... n)
	{
		buttons.add(new TextBox(command,display,tooltip,a,b,c,d));
	}*/

	public TextBox findButtonByCommand(String name)
	{
		for (int i = 0; i < buttons.size(); i++)
		{
			TextBox b = buttons.get(i);
			if (b instanceof Button)
				if (((Button)b).command.equals(name))
					return buttons.get(i);
		}
		return null;
	}
	
	public TextBox findButtonByName(String name)
	{
		for (int i = 0; i < buttons.size(); i++)
			if (buttons.get(i).name.equals(name))
				return buttons.get(i);
		return null;
	}
	
	public String click(float mouseX, float mouseY)
	{
		for (int i = 0; i < buttons.size(); i++)
		{
			TextBox b = buttons.get(i);
			if (b instanceof Button)
				if (mouseX > b.posX && mouseX < b.posX+b.sizeX && mouseY > b.posY && mouseY < b.posY+b.sizeY)
				{
					return ((Button)b).command;
				}
		}
		return null;
	}
	
	public TextBox within(float mouseX, float mouseY)
	{
		for (int i = 0; i < buttons.size(); i++)
		{
			TextBox b = buttons.get(i);
			if (mouseX > b.posX && mouseX < b.posX+b.sizeX && mouseY > b.posY && mouseY < b.posY+b.sizeY && b.active)
			{
				return b;
			}
		}
		return null;
	}

	public void pass(boolean[] activeMenus, float mouseX, float mouseY)
	{
		for (int i = 0; i < buttons.size(); i++)
		{
			TextBox b = buttons.get(i);
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
					b.expand(b.origSizeX*2, b.origSizeY, 10);
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
			TextBox b = buttons.get(i);
			if (b.orders.size() == 0)
			{
				buttons.get(i).orderOriginal(false);
				//buttons.get(i).setOriginal();
			}
		}
	}
	
	public boolean requestUpdate = false;
	public void activate(boolean yn) {active = yn; if (yn) requestUpdate = true;}
	public boolean active() {return active;}

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
