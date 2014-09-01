package render;

import java.util.ArrayList;
import processing.core.PApplet;

public class Game extends PApplet {

	public String gameMode = "MainMenu";
	public ArrayList<Menu> menus;
	public Menu activeMenu;

	public void setup()
	{
		size(1600,900);
		menus = new ArrayList<Menu>();

		Menu menu1 = new Menu("MainMenu");
		menus.add(menu1);
		menu1.addButton("newgame", "New Game", 100, 100, 210, 70);
		menu1.addButton("exitgame", "Exit Game", 100, 700, 210, 70);
		//menu1.on();

		Menu menu2 = new Menu("TerrainMenu");
		menus.add(menu2);
		//menu2.addButton("newgame", "New Game", 100, 100, 210, 70);
	}

	public void draw()
	{
		background(255);
		activeMenu = menus.get(0);
		if (gameMode.equals("MainMenu"))
		{
			activeMenu = menus.get(0);
		} 
		else if (gameMode.equals("TerrainMenu"))
		{
			activeMenu = menus.get(1);
		}
		for (int i = 0; i < activeMenu.buttons.size(); i++)
		{
			fill(0);
			Button b = activeMenu.buttons.get(i);
			rect(b.posX, b.posY, b.sizeX, b.sizeY);
			textAlign(CENTER, CENTER);
			fill(255);
			text(b.display, b.posX + b.sizeX/2, b.posY + b.sizeY/2);
		}
	}

	public void mousePressed()
	{
		for (int i = 0; i < menus.size(); i++)
		{
			if (menus.get(i).equals(activeMenu))
			{
				String command = menus.get(i).click(mouseX, mouseY);
				if (command != null)
				{
					println(command);
				}
			}
		}
	}

}
