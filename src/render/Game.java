package render;

import java.util.ArrayList;
import processing.core.PApplet;

public class Game extends PApplet {

	public String gameMode = "Menu";
	public ArrayList<Menu> menus;
	
	public void setup()
	{
		size(1600,900);
		menus = new ArrayList<Menu>();
		
		Menu menu1 = new Menu();
		menus.add(menu1);
		menu1.addButton("Test", 100, 100, 210, 70);
		menu1.on();
	}
	
	public void draw()
	{
		background(255);
		Menu activeMenu = menus.get(0);
		if (gameMode.equals("Menu"))
		{
			activeMenu = menus.get(0);
		}
		for (int i = 0; i < activeMenu.buttons.size(); i++)
		{
			fill(0);
			Button b = activeMenu.buttons.get(i);
			rect(b.posX, b.posY, b.sizeX, b.sizeY);
		}
	}
	
	public void mousePressed()
	{
		for (int i = 0; i < menus.size(); i++)
		{
			String command = menus.get(i).click(mouseX, mouseY);
			if (command != null)
			{
				println(command);
			}
		}
	}
	
}
