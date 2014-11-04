package render;

import game.Civilization;
import game.Tile;

import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.JFrame;

import data.EntityData;
import processing.core.PApplet;
import processing.core.PFont;
import menugame.MenuGame;

public class Game extends PApplet {

	//public String gameMode = "MainMenu";
	public String challengeType = "";
	public int numCivs = 2, numCityStates = 0;
	public ArrayList<Menu> menus;
	public Tooltip tooltip = new Tooltip("",0,0,80,20);
	public Menu activeMenu;
	public PFont arial;
	
	public MenuGame menuGame;
	public int tickEvery = 5;
	
	//public long seed = 87069200L;
	public String seed = "87069200"; //for easy modification (not by modulo)

	public static void main(String[] args)
	{
		PApplet.main(new String[] { Game.class.getName() });
	}

	public void setup()
	{
		size(800,800);
		arial = createFont("ArialMT-48.vlw", 48);
		EntityData.init();
		setModels();
		frame.setTitle("Survival: Civilization");
		menus = new ArrayList<Menu>();

		Menu menu0 = new Menu("MainMenu");
		menus.add(menu0);
		menu0.addButton("newgame", "New Game", "Start a new game.", 0, 100, 210, 50);
		menu0.addButton("tutorial", "Tutorial", "Start a tutorial level.", 0, 160, 210, 50);
		menu0.addButton("options", "Options", "Change options such as the level seed.", 0, 220, 210, 50);
		menu0.addButton("exitgame", "Exit", "Exit the game.", 0, 630, 210, 70);
		//menu1.on();

		Menu menu1 = new Menu("ChallengeTypeMenu");
		menus.add(menu1);
		menu1.addButton("conquestgame", "Conquest", "Destroy all other nations.", 0, 100, 210, 50);
		menu1.addButton("survivalgame", "Survival", "Survive to be the most powerful.", 0, 160, 210, 50);
		menu1.addButton("backMenu0", "Back", "Back to the main menu.", 0, 630, 210, 70);

		Menu menu2 = new Menu("OpponentMenu");
		menus.add(menu2);
		menu2.addButton("civs2", "Duel", "2 civs, 4 city states", 0, 100, 210, 50);
		menu2.addButton("civs3", "Tiny", "3 civs, 6 city states", 0, 160, 210, 50);
		menu2.addButton("civs5", "Small", "5 civs, 10 city states", 0, 220, 210, 50);
		menu2.addButton("civs8", "Standard", "8 civs, 16 city states", 0, 280, 210, 50);
		menu2.addButton("civs12", "Large", "12 civs, 24 city states", 0, 340, 210, 50);
		//menu2.addButton("civs16", "Huge", 100, 600, 210, 70);
		//menu2.addButton("civs64", "Testing", 100, 700, 210, 70);
		menu2.addButton("backMenu1", "Back", "Back to the game mode menu.", 0, 630, 210, 70);

		Menu menu3 = new Menu("TerrainMenu");
		menus.add(menu3);
		menu3.addButton("terrain1", "Archipelago", "A set of small islands.", 0, 100, 210, 50);
		menu3.addButton("terrain2", "Fractal", "Unpredictable as always.", 0, 160, 210, 50);
		menu3.addButton("terrain4", "Fractal+", "A true fractal.", 0, 220, 210, 50);

		menu3.addButton("terrain10", "Rolling Hills", "A set of large islands.", 0, 280, 210, 50);
		menu3.addButton("terrain11", "Pangaea", "One large landmass and satellite islands.", 0, 340, 210, 50);

		menu3.addButton("terrain5", "Testing", "", 0, 400, 210, 50);
		//menu2.addButton("newgame", "New Game", 100, 100, 210, 70);
		menu3.addButton("backMenu2", "Back", "Back to the size menu.", 0, 630, 210, 70);

		Menu menu4 = new Menu("OptionsMenu");

		menu4.addButton("setSeedAndBack", "Back", "Back to the main menu.", 0, 630, 210, 70);
		menus.add(menu4);

		//Main main = new Main();
		//PApplet.main(new String[] { Main.class.getName(),"Test" });
		activeMenu = menus.get(0);
		
		//Make the "fake" game to be displayed in the menu
		//menuGame = new MenuGame((long)(System.currentTimeMillis()*Math.random()));
		newMenuGame((long)(System.currentTimeMillis()*Math.random()));
	}

	public void draw()
	{
		background(255);
		noStroke();
		textFont(arial);
		textSize(14);
		
		if (frameCount % tickEvery == 0)
		{
			menuGame.civSystem.requestTurn = true;
			menuGame.tick();
		}
		for (int r = 0; r < menuGame.grid.rows; r++)
		{
			for (int c = 0; c < menuGame.grid.cols; c++)
			{
				Tile t = menuGame.grid.getTile(r, c);
				Civilization civ = t.owner;
				if (t.biome == -1)
					fill(150,225,255);
				else if (civ != null)
					fill(civ.r,civ.g,civ.b);
				else
					fill(150);
				int len = 500/menuGame.grid.rows;
				rect(250 + len*r,150 + len*c,len,len);
			}
		}
		
		for (int i = 0; i < activeMenu.buttons.size(); i++)
		{
			fill(0);
			Button b = activeMenu.buttons.get(i);
			rect(b.posX, b.posY, b.sizeX, b.sizeY);
			textAlign(CENTER, CENTER);
			fill(255);
			for (int j = 0; j < b.display.size(); j++)
				text(b.display.get(j), b.posX + b.sizeX/2, b.posY + b.sizeY/2);
		}

		tooltip.active = false;
		Button hover = activeMenu.within(mouseX, mouseY);
		if (hover != null)
			if (hover.tooltip != null)
				if (!hover.tooltip.equals(""))
				{
					//TODO: Word wrap if the text goes off the screen
					tooltip.active = true;
					tooltip.sizeX = 7*hover.tooltip.length();
					tooltip.posX = mouseX;
					tooltip.posY = mouseY;
					fill(0);
					stroke(255);
					rect(tooltip.posX, tooltip.posY, tooltip.sizeX, tooltip.sizeY);
					fill(255);
					noStroke();
					text(hover.tooltip, tooltip.posX + tooltip.sizeX/2, tooltip.posY + tooltip.sizeY/2);
				}

		//Display the seed being typed if in the options menu
		if (menus.get(4).equals(activeMenu))
		{
			fill(0);
			rect(100, 160, 210, 50);
			fill(255);
			text("Seed: " + seed, 205, 185);
		}
	}
	
	public void newMenuGame(long seed)
	{
		menuGame = new MenuGame(seed);
	}

	public void keyPressed()
	{
		if (activeMenu.equals(menus.get(4)))
		{
			if (Character.isDigit(key))
			{
				if (seed.length() <= 18)
					seed += key;
			}
			else if (key == BACKSPACE)
			{
				if (seed.length() > 0)
					seed = seed.substring(0,seed.length()-1);
			}
		}
	}

	private CivGame renderer;
	private Tutorial tutorial;
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
		public PFrame(Game game, int width, int height)
		{
			setBounds(0, 0, width, height);
			tutorial = new Tutorial(game,width,height);
			add(tutorial);
			setResizable(false);
			tutorial.init();
			show();
		}
	}

	public void mousePressed()
	{
		if (mouseButton == LEFT)
		{
			for (int i = 0; i < menus.size(); i++)
			{
				if (menus.get(i).equals(activeMenu))
				{
					String command = menus.get(i).click(mouseX, mouseY);
					if (command != null && !command.equals(""))
					{
						tickEvery = 20;
						if (command.equals("newgame"))
						{
							//gameMode = "challengeTypeMenu";
							activeMenu = menus.get(1);
						}
						else if (command.equals("tutorial"))
						{
							PFrame f = new PFrame(this,1500,900);
							f.setTitle("Tutorial");
							background(255);
							noLoop();
						}
						else if (command.equals("options"))
						{
							activeMenu = menus.get(4);
							redraw();
						}
						else if (command.equals("exitgame"))
						{
							System.exit(0);
						}
						else if (command.equals("conquestgame"))
						{
							challengeType = "conquest";
							activeMenu = menus.get(2);
							redraw();
						}
						else if (command.equals("survivalgame"))
						{
							challengeType = "survival";
							activeMenu = menus.get(2);
							redraw();
						}
						else if (command.contains("civs"))
						{
							numCivs = Integer.parseInt(command.substring(4));
							numCityStates = (int)Math.floor(numCivs*1.5);
							activeMenu = menus.get(3);
							redraw();
						}
						else if (command.contains("terrain"))
						{
							PFrame f = new PFrame(this,1500,900,numCivs,numCityStates,challengeType,command,Long.parseLong(seed));
							f.setTitle("Survival: Civilization");
							setVisible(false);
							background(255);
							noLoop();
						}
						else if (command.contains("backMenu"))
						{
							activeMenu = menus.get(Integer.parseInt(command.substring(8)));
							redraw();
						}
						else if (command.equals("setSeedAndBack"))
						{
							if (seed.length() == 0)
							{
								seed = "87069200";
							}
							activeMenu = menus.get(0);
						}
						//println("Executed " + command);
						return;
					}
				}
			}
		}
		else if (mouseButton == RIGHT)
		{
			newMenuGame((long)(System.currentTimeMillis()*Math.random()));
		}
	}

	private String[] models = {"City","Farm","Fishing Boats","Forest","Galley","Lumbermill","Mine","Ruins","Settler","Transport","Warrior","Windmill","Work Boat","Worker"};
	private void setModels()
	{
		for (int i = 0; i < models.length; i++)
		{
			String[] data = loadStrings("/models/"+models[i]);
			EntityData.passModelData(models[i],data);
		}
	}

}
