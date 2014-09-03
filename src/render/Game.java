package render;

import java.util.ArrayList;

import javax.swing.JFrame;

import processing.core.PApplet;

public class Game extends PApplet {

	public String gameMode = "MainMenu";
	public String challengeType = "";
	public ArrayList<Menu> menus;
	public Menu activeMenu;

	public static void main(String[] args)
	{
		PApplet.main(new String[] { Game.class.getName() });
	}
	
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
		menu2.addButton("terrain1", "Archipelago", 100, 100, 210, 70);
		menu2.addButton("terrain2", "Island Chain", 100, 200, 210, 70);
		//menu2.addButton("terrain3", "Rolling Hills", 100, 300, 210, 70);
		//menu2.addButton("newgame", "New Game", 100, 100, 210, 70);
		
		//Main main = new Main();
		//PApplet.main(new String[] { Main.class.getName(),"Test" });
	}

	public void draw()
	{
		background(255);
		activeMenu = menus.get(0);
		if (gameMode.equals("mainMenu"))
		{
			activeMenu = menus.get(0);
		} 
		else if (gameMode.equals("challengeTypeMenu"))
		{
			activeMenu = menus.get(1);
		}
		else if (gameMode.equals("terrainMenu"))
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
	
	private CivGame renderer;
	//Taken from stack overflow
	public class PFrame extends JFrame {
		public PFrame(Game game, int width, int height, String challengeType, String terrainType) {
			setBounds(0, 0, width, height);
			renderer = new CivGame(game, challengeType, terrainType);
			add(renderer);
			renderer.init();
			show();
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
						gameMode = "challengeTypeMenu";
					}
					else if (command.equals("exitgame"))
					{
						System.exit(0);
					}
					else if (command.equals("conquestgame"))
					{
						challengeType = "conquest";
						gameMode = "terrainMenu";
						redraw();
					}
					else if (command.equals("survivalgame"))
					{
						challengeType = "survival";
						gameMode = "terrainMenu";
						redraw();
					}
					else if (command.equals("terrain1"))
					{
						PFrame f = new PFrame(this,1500,900,challengeType,"terrain1");
						f.setTitle("");
						setVisible(false);
						noLoop();
					}
					else if (command.equals("terrain2"))
					{
						PFrame f = new PFrame(this,1500,900,challengeType,"terrain2");
						f.setTitle("");
						setVisible(false);
						noLoop();
					}
					else if (command.equals("terrain3"))
					{
						PFrame f = new PFrame(this,1500,900,challengeType,"terrain3");
						f.setTitle("");
						setVisible(false);
						noLoop();
					}
				}
			}
		}
	}

}
