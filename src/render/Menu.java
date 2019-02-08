package render;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import lwjglEngine.fontMeshCreator.FontType;

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

	/*public TextBox addButton(String command, String display, String tooltip, float a, float b, float c, float d)
	{
		Button temp = new Button(12,null,display.length(),true,-1,command,display,tooltip,a,b,c,d);
		temp.menu = this;
		buttons.add(temp);
		return temp;
	}*/

	public TextBox addButton(int texture, String command, String display, String tooltip, float a, float b, float c, float d)
	{
		Button temp = new Button(12,null,display.length(),true,texture,command,display,tooltip,a,b,c,d);
		temp.menu = this;
		buttons.add(temp);
		return temp;
	}

	public TextBox addButton(int w, FontType x, int y, boolean z, int texture, String command, String display, String tooltip, float a, float b, float c, float d)
	{
		Button temp = new Button(w,x,y,z,texture,command,display,tooltip,a,b,c,d);
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
		if (this.name.equals("UnitMenu"))
			System.out.println(buttons.size());
		for (int i = 0; i < buttons.size(); i++)
		{
			TextBox b = buttons.get(i);
			if (this.name.equals("UnitMenu"))
			{
				System.out.println(b.getDisplay().get(0) + "; Pos: " + b.pixelPos + "; Size: " + b.pixelSize + "; Bounding Box Edge: " + new Vector2f(b.pixelPos.x + b.pixelSize.x, b.pixelPos.y + b.pixelSize.y) + "; Mouse: " + mouseX + "," + mouseY);
			}
			if (b instanceof Button)
				if (b.within(mouseX, mouseY)) //mouseX > b.pos.x && mouseX < b.pos.x+b.size.x && mouseY > b.pos.y && mouseY < b.pos.y+b.size.y
					return ((Button)b).command;
		}
		return null;
	}

	public TextBox within(float mouseX, float mouseY)
	{
		for (int i = 0; i < buttons.size(); i++)
		{
			TextBox b = buttons.get(i);
			if (b.within(mouseX, mouseY))
				return b;
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
		}
	}

	public boolean equals(Menu other)
	{
		return name.equals(other.name);
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
