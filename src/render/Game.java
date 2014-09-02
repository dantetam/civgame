package render;

import java.util.ArrayList;
import processing.core.PApplet;

public class Game extends PApplet {

	public String gameMode = "MainMenu";
	public String challengeType = "";
	public ArrayList<Menu> menus;
	public Menu activeMenu;

	public void setup()
	{
		size(1600,900);
		menus = new ArrayList<Menu>();

		Menu menu0 = new Menu("MainMenu");
		menus.add(menu0);
		menu0.addButton("newgame", "New Game", 100, 100, 210, 70);
		menu0.addButton("exitgame", "Exit Game", 100, 700, 210, 70);
		//menu1.on();

		Menu menu1 = new Menu("ChallengeTypeMenu");
		menus.add(menu1);
		menu1.addButton("conquestgame", "Conquest", 100, 100, 210, 70);
		menu1.addButton("survivalgame", "Survival", 100, 200, 210, 70);
		
		Menu menu2 = new Menu("TerrainMenu");
		menus.add(menu2);
		menu2.addButton("", "Archipelago", 100, 100, 210, 70);
		menu2.addButton("", "Island Chain", 100, 200, 210, 70);
		menu2.addButton("", "Rolling Hills", 100, 300, 210, 70);
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
		else if (gameMode.equals("ChallengeTypeMenu"))
		{
			activeMenu = menus.get(1);
		}
		else if (gameMode.equals("TerrainMenu"))
		{
			activeMenu = menus.get(2);
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
				if (command != null && !command.equals(""))
				{
					if (command.equals("newgame"))
					{
						gameMode = "ChallengeTypeMenu";
					}
					else if (command.equals("exitgame"))
					{
						System.exit(0);
					}
					else if (command.equals("conquestgame"))
					{
						challengeType = "Conquest";
						gameMode = "TerrainMenu";
						redraw();
					}
					else if (command.equals("survivalgame"))
					{
						challengeType = "Survival";
						gameMode = "TerrainMenu";
						redraw();
					}
					
				}
			}
		}
	}

}
