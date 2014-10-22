package render;

import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.JFrame;

import data.EntityData;
import processing.core.PApplet;
import processing.core.PFont;

public class Game extends PApplet {

	public String gameMode = "MainMenu";
	public String challengeType = "";
	public int numCivs = 2, numCityStates = 0;
	public ArrayList<Menu> menus;
	public Menu activeMenu;
	public PFont arial;
	
	public long seed = 87069200L;

	public static void main(String[] args)
	{
		PApplet.main(new String[] { Game.class.getName() });
	}
	
	public void setup()
	{
		size(400,800);
		arial = createFont("ArialMT-48.vlw", 48);
		EntityData.init();
		setModels();
		frame.setTitle("Survival: Civilization");
		menus = new ArrayList<Menu>();

		Menu menu0 = new Menu("MainMenu");
		menus.add(menu0);
		menu0.addButton("newgame", "New Game", 100, 100, 210, 70);
		menu0.addButton("exitgame", "Exit", 100, 630, 210, 70);
		//menu1.on();

		Menu menu1 = new Menu("ChallengeTypeMenu");
		menus.add(menu1);
		menu1.addButton("conquestgame", "Conquest", 100, 100, 210, 70);
		menu1.addButton("survivalgame", "Survival", 100, 200, 210, 70);
		
		Menu menu2 = new Menu("OpponentMenu");
		menus.add(menu2);
		menu2.addButton("civs2", "Duel", 100, 100, 210, 70);
		menu2.addButton("civs3", "Tiny", 100, 200, 210, 70);
		menu2.addButton("civs5", "Small", 100, 300, 210, 70);
		menu2.addButton("civs8", "Standard", 100, 400, 210, 70);
		menu2.addButton("civs12", "Large", 100, 500, 210, 70);
		menu2.addButton("civs16", "Huge", 100, 600, 210, 70);
		menu2.addButton("civs64", "Testing", 100, 700, 210, 70);
		
		Menu menu3 = new Menu("TerrainMenu");
		menus.add(menu3);
		menu3.addButton("terrain1", "Archipelago", 100, 100, 210, 70);
		menu3.addButton("terrain2", "Fractal", 100, 200, 210, 70);
		menu3.addButton("terrain4", "Fractal+", 100, 300, 210, 70);
		
		menu3.addButton("terrain10", "Rolling Hills", 100, 400, 210, 70);
		menu3.addButton("terrain11", "Pangaea", 100, 500, 210, 70);
		
		menu3.addButton("terrain5", "Testing", 100, 600, 210, 70);
		//menu2.addButton("newgame", "New Game", 100, 100, 210, 70);
		
		//Main main = new Main();
		//PApplet.main(new String[] { Main.class.getName(),"Test" });
	}

	public void draw()
	{
		background(255);
		textFont(arial);
		textSize(14);
		activeMenu = menus.get(0);
		if (gameMode.equals("mainMenu"))
		{
			activeMenu = menus.get(0);
		} 
		else if (gameMode.equals("challengeTypeMenu"))
		{
			activeMenu = menus.get(1);
		}
		else if (gameMode.equals("opponentMenu"))
		{
			activeMenu = menus.get(2);
		}
		else if (gameMode.equals("terrainMenu"))
		{
			activeMenu = menus.get(3);
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
		public PFrame(Game game, int width, int height, int numCivs, int numCityStates, String challengeType, String terrainType, long seed) {
			setBounds(0, 0, width, height);
			renderer = new CivGame(game, numCivs, numCityStates, challengeType, terrainType, seed);
			add(renderer);
			setResizable(false);
			renderer.init();
			//setTitle("Survival: Civilization");
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
						gameMode = "opponentMenu";
						redraw();
					}
					else if (command.equals("survivalgame"))
					{
						challengeType = "survival";
						gameMode = "opponentMenu";
						redraw();
					}
					else if (command.contains("civs"))
					{
						numCivs = Integer.parseInt(command.substring(4));
						numCityStates = (int)Math.floor(numCivs*1.5);
						gameMode = "terrainMenu";
						redraw();
					}
					else if (command.contains("terrain"))
					{
						PFrame f = new PFrame(this,1500,900,numCivs,numCityStates,challengeType,command,seed);
						f.setTitle("Survival: Civilization");
						setVisible(false);
						background(255);
						noLoop();
					}
				}
			}
		}
	}
	
	private String[] models = {"City","Farm","Fishing Boats","Forest","Galley","Lumbermill","Mine","Settler","Transport","Warrior","Windmill","Work Boat","Worker"};
	private void setModels()
	{
		for (int i = 0; i < models.length; i++)
		{
			String[] data = loadStrings("/models/"+models[i]);
			EntityData.passModelData(models[i],data);
		}
	}

}
