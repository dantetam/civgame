package system;

import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Tech;
import game.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import data.EntityData;
import data.Improvement;
import processing.core.PApplet;
import processing.core.PFont;
import render.Button;
import render.CivGame;
import render.Menu;
import render.MouseHelper;
import render.Game.PFrame;
import render.TextBox;
import render.Tooltip;
import render.Tutorial;
import units.Caravan;
import units.City;
import units.Settler;
import units.Warrior;
import units.Worker;

public class MenuSystem extends BaseSystem {

	public ArrayList<Menu> menus;
	public ArrayList<TextBox> textboxes;

	private ArrayList<Click> clicks;

	public boolean minimap, info; //loadout, loadoutDisplay, techMenu, continueMenu; //Access the menu's active property instead
	public int multiplier = 1;

	public Tile target;
	//public ArrayList<String> hintText;
	public Tile highlighted;
	public Tile mouseHighlighted; //Under the player's crosshair versus under the player's mouse
	public Tile lastHighlighted;
	private BaseEntity selected; //Selected by the player with the mouse explicitly
	public Tile[] settlerChoices;
	public String typeOfLastSelected = "";

	public Tooltip tooltip = new Tooltip("",0,0,80,20);
	//public City citySelected;

	//public TextBox hintTextBox;
	//public TextBox selectedTextBox;

	private ArrayList<String> messages;

	public MenuSystem(CivGame civGame) {
		super(civGame);
		menus = new ArrayList<Menu>();
		textboxes = new ArrayList<TextBox>();
		clicks = new ArrayList<Click>();

		//hintText = new ArrayList<String>();
		messages = new ArrayList<String>();
		//highlighted = null;

		//Keep track of the menu's indices in list
		Menu menu0 = new Menu("MainMenu");
		menus.add(menu0);
		menu0.addButton("exitgame", "Exit", "Exit this session of the game.", 0, 0, 100, 30);
		menu0.addButton("close", "Close", "Close all open menus.", 0, 70, 100, 30);
		menu0.addButton("minimap", "Minimap", "Open the minimap of the world.", 0, 100, 100, 30);
		menu0.addButton("info", "Information", "", 0, 130, 100, 30);
		menu0.buttons.add(new Button("loadout", "Loadout", "Change loadouts of certain units.", 0, 160, 100, 30, 3, 4));
		menu0.addButton("stats", "Statistics", "Compare stats of different civilizations.", 0, 190, 100, 30);
		menu0.addButton("techs", "Techs", "Choose technologies to research.", 0, 220, 100, 30);
		menu0.addButton("encyclopedia", "Reference", "A encyclopedia-like list of articles.", 0, 250, 100, 30);
		menu0.addButton("relations", "Relations", "The wars and alliances of this world.", 0, 280, 100, 30);
		menu0.addButton("civic", "Civics", "Change the ideals of your government.", 0, 310, 100, 30);
		menu0.addButton("log", "Messages", "View your messages.", main.width*5/6, main.height*5/6 - 30, main.width*1/6, 30).lock = true;

		Menu menu1 = new Menu("UnitMenu");
		menus.add(menu1);

		Menu menu2 = new Menu("CityMenu");
		menus.add(menu2);

		Menu menu3 = new Menu("LoadoutMenu");
		menus.add(menu3);
		String[] names = EntityData.allUnitNames();
		for (int i = 0; i < names.length; i++)
		{
			menu3.addButton("loadoutDisplay" + names[i], names[i], "", 100, 160 + 30*i, 200, 30);
		}

		Menu menu4 = new Menu("LoadoutDisplay");
		menus.add(menu4);

		Menu menu5 = new Menu("TechMenu");
		menus.add(menu5);

		Menu menu6 = new Menu("ContinueMenu"); //Menu when player loses the game
		menu6.addButton("continue", "You have lost the game. Continue?", "", main.width*2/6, 100, main.width*2/6, 200);
		menus.add(menu6);

		Menu menu7 = new Menu("EncyclopediaMenu");
		TextBox temp = new TextBox(new ArrayList<String>(),"",100,190,700,500); //"EncyclopediaText",
		//System.out.println("Found " + menu7.findButtonByCommand("EncyclopediaText"));
		temp.name = "EncyclopediaText";
		menu7.buttons.add(temp);
		menus.add(menu7);

		Menu menu8 = new Menu("DiplomacyMenu");
		menus.add(menu8);

		Menu menu9 = new Menu("TalkToCivMenu"); //For lack of a better name...
		menus.add(menu9);

		Menu menu10 = new Menu("Logs"); //For lack of a better name...
		menus.add(menu10);

		Menu menu11 = new Menu("RelationsMenu"); 
		menus.add(menu11);

		Menu menu12 = new Menu("CivicMenu");
		menus.add(menu12);

		menu0.active = true;

		TextBox text0 = new TextBox(new ArrayList<String>(),"",main.width - 200,0,200,150); //"HintText"
		textboxes.add(text0);

		TextBox text1 = new TextBox(new ArrayList<String>(),"",main.width - 400,0,200,150); //"SelectedText"
		textboxes.add(text1);

		TextBox text2 = new TextBox(new ArrayList<String>(),"",main.width*4/6,main.height*5/6,main.width*2/6,100); //"Messages"
		textboxes.add(text2);

		TextBox text3 = new TextBox(new ArrayList<String>(),"",main.width/6,0,300,50); //"PlayerStatus"
		textboxes.add(text3);

		TextBox text4 = new TextBox(new ArrayList<String>(),"",100,190,500,250); //"LedgerText"
		textboxes.add(text4);

		text4.active = false;

		updateEncyclopedia();
		//arial = main.loadFont("ArialMT-48.vlw");
	}

	public PFont arial;

	public boolean menuActivated = false;
	public void tick()
	{
		//main.textFont(arial);
		//main.resetShader();
		main.hint(PApplet.DISABLE_DEPTH_TEST);
		//main.textSize(20);
		//main.background(255,255,255,0);
		main.camera();
		main.perspective();
		main.resetShader();
		main.noLights();
		main.noStroke();
		main.textSize(12);

		lastHighlighted = highlighted;

		//Approximate tile lines
		/*main.strokeWeight(5);
		main.stroke(255,0,0);
		//main.line(705,437,698,483);
		//main.line(775,435,781,482);
		//main.line(840,425,854,471);
		main.line(717,424,711,467);
		main.line(785,423,789,467);
		main.line(851,425,864,467);
		main.line(919,425,943,466);
		//main.line(987,428,1023,468);
		main.line(988,425,1019,466);
		main.line(1055,424,1094,471);
		main.line(1120,424,1174,468);
		main.line(649,426,635,469);
		main.line(581,426,556,468);
		main.line(513,424,479,464);
		main.line(445,426,402,469);

		main.line(0,340,main.width,340);
		main.line(0,364,main.width,364);
		main.line(0,392,main.width,392);
		main.line(0,424,main.width,424);
		main.line(0,468,main.width,468);
		main.line(0,527,main.width,527);
		main.line(0,602,main.width,602);
		main.line(0,720,main.width,720);
		main.noStroke();*/

		main.textAlign(main.CENTER);
		main.text("When selecting a unit, hold Q to bring out the quick menu. Drag with right click to the desired tile.", 500, 80);

		if (minimap)
		{
			//main.rect(0, 700, 50, 50);
			int con = 1;
			float sX = 0; float sY = 400; float widthX = 400; float widthY = 400; 
			for (int r = 0; r < main.grid.rows; r += con)
			{
				for (int c = 0; c < main.grid.cols; c += con)
				{
					Tile t = main.grid.getTile(r,c);
					if (t.height >= main.cutoff)
					{
						if (t.owner != null)
						{
							main.fill(t.owner.r,t.owner.g,t.owner.b);
						}
						else if (t.occupants.size() > 0)
						{
							GameEntity en = t.occupants.get(0);
							main.fill(en.owner.r, en.owner.g, en.owner.b);
						}
						else
						{
							main.fill(150);
						}
					}
					else
					{
						main.fill(150,225,255);
					}
					//System.out.println(sX + r/(float)main.grid.rows*widthX);
					main.rect(sX + (main.grid.rows-r)/(float)main.grid.rows*widthX,sY + c/(float)main.grid.cols*widthY,widthX*con/main.grid.rows,widthY*con/main.grid.cols);
				}
			}
		}

		if (info)
		{
			main.fill(0);
			main.rect(100,130,200,100);
			main.fill(255);
			main.textAlign(PApplet.LEFT);
			main.text("Seed: " + main.seed, 115, 150);
		}

		if (textboxes.get(4).active)
		{
			updateCivStats();
		}
		//menus.get(6).active = ledgerMenu;
		//System.out.println(loadout + " " + loadoutDisplay);

		//Render the cursor
		/*if (!menus.get(7).active && !menus.get(9).active)
		{
			int width = 6;
			main.stroke(255);
			main.fill(0);
			main.rect((main.width - width)/2, (main.height - width)/2, width, width);
		}*/

		main.noStroke();

		ArrayList<String> hintText = textboxes.get(0).display;
		//hintText.clear();
		if (mouseHighlighted != null)
		{
			hintText.add(mouseHighlighted.row + " " + mouseHighlighted.col);
			if (mouseHighlighted.owner != null)
				hintText.add("Owner: " + mouseHighlighted.owner.name);
			//else
			//hintText.add("Terra nullius");

			if (mouseHighlighted.biome >= 4 && mouseHighlighted.biome <= 6)
				if (mouseHighlighted.forest)
					hintText.add(EntityData.getBiome(mouseHighlighted.biome) + " (forested)");
				else
					hintText.add(EntityData.getBiome(mouseHighlighted.biome) + " (unforested)");
			else
				hintText.add(EntityData.getBiome(mouseHighlighted.biome));

			if (mouseHighlighted.shape == 1)
			{
				hintText.add("Hill");
			}
			else if (mouseHighlighted.shape == 2)
			{
				hintText.add("Mountain");
			}

			if (mouseHighlighted.improvement != null)
			{
				hintText.add(mouseHighlighted.improvement.name);
				hintText.add(mouseHighlighted.improvement.id);
			}
			else
				hintText.add("Pristine");

			if (mouseHighlighted.city != null)
			{
				if (mouseHighlighted.city.owner != null)
				{
					double[] data = mouseHighlighted.city.evaluate(mouseHighlighted, null);
					hintText.add((int)data[0] + " F, " + (int)data[1] + " G, " + (int)data[2] + " M, " + (int)data[3] + " R");
				}
			}
			//Same check as above, really
			if (mouseHighlighted.owner != null)
			{
				hintText.add("Relations: " + mouseHighlighted.owner.opinions[0]);
			}

			if (main.grid.irrigated(mouseHighlighted.row, mouseHighlighted.col))
				hintText.add("Fresh Water");

			if (mouseHighlighted.occupants.size() > 0)
			{
				String stringy = "";
				for (int i = 0; i < mouseHighlighted.occupants.size(); i++)
				{
					stringy += mouseHighlighted.occupants.get(i).name + "; ";
				}
				if (!stringy.equals(""))
					hintText.add(stringy.substring(0,stringy.length()-2));
			}
		}
		if (selected != null)
		{
			if (selected.owner != null && !(selected instanceof City))
			{
				main.fill(255);
				//main.textSize(14);

				ArrayList<String> temp = textboxes.get(1).display;
				//temp.clear();
				temp.add(selected.name + " " + ((GameEntity)selected).action + "/" + ((GameEntity)selected).maxAction);
				temp.add(selected.health + "/" + selected.maxHealth + " health");
				temp.add(selected.offensiveStr + " offensive, " + selected.rangedStr + " ranged");
				temp.add(selected.defensiveStr + " defensive");

				if (!typeOfLastSelected.equals(selected.name))
				{
					updateUnitMenu((GameEntity)selected);
				}
				menus.get(1).active = true;
				//main.text("Test", main.width*5/6 + 15, main.height*5/6 + 15);
			}
			else
			{
				menus.get(1).active = false;
			}
			Tile h = highlighted;
			if ((getSelected() instanceof City || getSelected() instanceof Settler) && h != null)
			{
				MouseHelper mh = main.inputSystem.mouseHelper;
				for (int r = 0; r < mh.guiPositions.length; r++)
				{
					for (int c = 0; c < mh.guiPositions[0].length; c++)
					{
						float[] pos = mh.positionGui(r,c);
						if (pos != null)
						{
							main.textAlign(main.CENTER);
							main.fill(255,0,0);
							int dC = r - (mh.guiPositions.length-1)/2;
							int dR = c - (mh.guiPositions[0].length-1)/2;
							
							//float[] disp = mh.center();
							//float dX = main.width/2 - disp[0], dY = main.height/2 - disp[1];
							
							Tile t = main.grid.getTile(h.row + dR, h.col - dC);
							if (t != null)
							{
								if (t.biome == -1 && main.grid.adjacentLand(t.row, t.col).size() == 0 || 
										main.grid.civs[0].revealed[t.row][t.col] == 0 && !main.showAll) continue;
								//main.text(t.row + "," + t.col, pos[0], pos[1]);
								double[] y = City.staticEval(t);
								int n = 0;
								for (int i = 0; i < y.length; i++)
									if (y[i] > 0)
										n++;
								int iter = 1;
								for (int i = 0; i < y.length; i++)
									if (y[i] > 0)
									{
										main.newMenuSystem.tileIcon(pos[0],pos[1],i,(int)y[i],n,iter);
										iter++;
									}
							}
						}
					}
				}
			}
		}
		else
		{
			menus.get(1).active = false;
		}

		menus.get(2).active = false;

		if (selected != null)
		{
			if (selected.owner != null)
				if (selected.owner.equals(main.grid.civs[0]) && selected instanceof City)
				{
					City citySelected = (City)selected; //to work with old code
					displayCity(citySelected);
				}
		}
		else if (mouseHighlighted != null)
		{
			if (mouseHighlighted.improvement != null)
				if (mouseHighlighted.improvement instanceof City)
				{
					City citySelected = (City)mouseHighlighted.improvement;
					displayCity(citySelected);
				}
		}

		if (messages.size() > 0)
		{
			int len = Math.min(6,messages.size());
			for (int i = 0; i < len; i++)
			{
				textboxes.get(2).display.add(messages.get(i));
			}
		}

		main.noStroke();
		Civilization c = main.grid.civs[0];
		textboxes.get(3).display.add(c.name + "; Food: " + c.food + "; Gold: " + c.gold + "; Research: " + c.research);
		textboxes.get(3).display.add("Health: " + c.health);
		if (c.researchTech == null)
			textboxes.get(3).display.add("No research");
		else
			textboxes.get(3).display.add("Researching " + c.researchTech + " at " + (int)((c.researchProgress()*1000/1000)*100) + "%");

		updateMessages();
		for (int menu = 0; menu < menus.size(); menu++)
		{
			if (menus.get(menu).active)
			{
				//System.out.println(menu + " " + menus.get(menu).active);
				for (int i = 0; i < menus.get(menu).buttons.size(); i++)
				{
					main.fill(0);
					TextBox b = menus.get(menu).buttons.get(i);
					main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
					main.textAlign(PApplet.CENTER, PApplet.CENTER);
					main.fill(255);
					for (int j = 0; j < b.display.size(); j++)
						main.text(b.display.get(j), b.posX + b.sizeX/2, b.posY + b.sizeY/2 + j*15);
				}
			}
		}
		for (int i = 0; i < textboxes.size(); i++)
		{
			TextBox b = textboxes.get(i);
			if (b.active)
			{
				main.fill(0);
				main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
				main.textAlign(PApplet.LEFT, PApplet.UP);
				main.fill(255);
				for (int j = 0; j < b.display.size(); j++)
				{
					if (b.display.get(j) != null)
					{
						//System.out.println(b.text + " " + b.text.get(j) + " " + b.posX);
						main.text(b.display.get(j), b.posX + 15, b.posY + 15*(j+1));
					}
				}
				b.display.clear(); //Clear them to be refilled next frame
			}
		}

		tooltip.active = false;
		TextBox hover = findButtonWithin(main.mouseX, main.mouseY);
		if (hover != null)
			if (hover.tooltip != null)
				if (!hover.tooltip.equals(""))
				{
					//TODO: Word wrap if the text goes off the screen
					tooltip.active = true;
					int[] d = hover.dimTooltip();
					tooltip.sizeX = d[0];
					tooltip.sizeY = d[1];
					tooltip.posX = main.mouseX;
					tooltip.posY = main.mouseY;
					main.fill(0);
					main.stroke(255);
					main.rect(tooltip.posX, tooltip.posY, tooltip.sizeX, tooltip.sizeY);
					main.fill(255);
					main.noStroke();
					main.textAlign(main.CENTER);
					for (int i = 0; i < hover.tooltip.size(); i++)
						main.text(hover.tooltip.get(i), tooltip.posX + tooltip.sizeX/2, tooltip.posY + tooltip.sizeY/2 + 14*i);
				}

		menuActivated = false;
		for (int menu = 0; menu < menus.size(); menu++)
		{
			if (menus.get(menu).active)
			{
				for (int i = clicks.size() - 1; i >= 0; i--)
				{
					if (clicks.get(i).click)
					{
						String command = menus.get(menu).click(clicks.get(i).mouseX, clicks.get(i).mouseY);
						if (command != null && !command.equals(""))
						{
							menuActivated = true;
							//Replace with function that returns true if the menu resetting should happen
							if (executeAction(command))
							{
								//main.menuSystem.select(null);
								//below was derived from the original expression to calculate rotY & rotVertical
								//main.centerX = main.mouseX/(1 - main.player.rotY/(float)Math.PI);
								//main.centerY = main.mouseY/(1 + 4*main.player.rotVertical/(float)Math.PI);
								main.resetCamera();
							}
						}
					}
					else
					{
						if (menu == 0 || menu == 5)
						{	
							boolean[] activeMenus = new boolean[menus.size()];
							for (int j = 0; j < activeMenus.length; j++)
							{
								activeMenus[j] = menus.get(j).active;
							}
							menus.get(menu).pass(activeMenus, clicks.get(i).mouseX, clicks.get(i).mouseY);
						}
					}
				}
				menus.get(menu).origPosIfNoMouse();
				/*for (int i = 0; i < menus.get(menu).buttons.size(); i++)
				{
					main.fill(0);
					Button b = menus.get(menu).buttons.get(i);
					main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
					main.textAlign(PApplet.CENTER, PApplet.CENTER);
					main.fill(255);
					main.text(b.display, b.posX + b.sizeX/2, b.posY + b.sizeY/2);
				}
				 */
				for (int i = 0; i < menus.get(menu).buttons.size(); i++)
				{
					TextBox b = menus.get(menu).buttons.get(i);
					b.tick();
				}
				for (int i = 0; i < textboxes.size(); i++)
				{
					textboxes.get(i).tick();
				}
			}
		}
		clicks.clear();
	}

	public class Click {float mouseX, mouseY; boolean click; Click(boolean click, float x, float y) {this.click = click; mouseX = x; mouseY = y;}}
	public void queueClick(float mouseX, float mouseY)
	{
		clicks.add(0, new Click(true, mouseX, mouseY));
	}

	public void queueMousePass(float mouseX, float mouseY)
	{
		clicks.add(0, new Click(false, mouseX, mouseY));
	}

	public boolean executeAction(String command)
	{
		if (command.equals("exitgame"))
		{
			System.exit(0);
			return false;
		}
		else if (command.equals("close"))
		{
			//TODO: Replace with a loop later
			closeMenus();
		}
		else if (
				command.equals("info") || 
				command.equals("minimap") || 
				command.equals("loadout") || 
				command.contains("loadoutDisplay") || 
				command.equals("stats") ||
				command.equals("continue") ||
				command.equals("techs") ||
				command.equals("encyclopedia") ||
				command.contains("diplomacy") ||
				command.equals("log") ||
				command.equals("relations") ||
				command.equals("civic")
				)
		{
			closeMenus();
			if (command.equals("info"))
			{
				info = !info;
			}
			else if (command.equals("minimap"))
			{
				minimap = !minimap;
			}
			else if (command.equals("loadout"))
			{
				/*if (menus.get(3).active)
				{
					menus.get(3).active = false;
				}
				menus.get(4).active = !menus.get(4).active;*/
				menus.get(3).active = true;
			}
			else if (command.contains("loadoutDisplay"))
			{
				//loadout = false;
				updateLoadoutDisplay(command.substring(14));
				menus.get(4).active = true;
			}
			else if (command.equals("stats"))
			{
				updateCivStats();
				//ledgerMenu = true;
				textboxes.get(4).active = true;
				menus.get(0).findButtonByCommand("stats").lock = textboxes.get(4).active;
			}
			else if (command.equals("continue"))
			{
				main.grid.civs[0].observe = true;
				menus.get(6).active = false;
			}
			else if (command.equals("techs"))
			{
				displayTechMenu(main.grid.civs[0]);
				menus.get(5).active = !menus.get(5).active;
				//menus.get(5).active = !menus.get(5).active;
			}
			else if (command.equals("encyclopedia"))
			{
				menus.get(7).active = true;
			}
			else if (command.contains("diplomacy"))
			{
				menus.get(8).active = false;
				menus.get(9).active = true;
				Civilization civ = main.grid.civs[Integer.parseInt(command.substring(9))];
				updateDiplomacyMenu(civ);
			}
			else if (command.equals("log"))
			{
				menus.get(10).active = true;
				updateMessages();
			}
			else if (command.equals("relations"))
			{
				menus.get(11).active = true;
				pivot = main.grid.civs[0];
				updateRelations();
			}
			else if (command.equals("civic"))
			{
				menus.get(12).active = true;
				updateCivicsMenu(main.grid.civs[0]);
			}
			resetAllButtons();
			return false;
		}

		else if (command.contains("encyclopedia")) //accessing an encyclopedia entry
		{
			ArrayList<String> text = EntityData.encyclopediaEntries.get(command.substring(12));
			TextBox textBox = (TextBox)menus.get(7).findButtonByName("EncyclopediaText");
			textBox.display.clear();
			for (int j = 0; j < text.size(); j++)
			{
				textBox.display.add(text.get(j));
			}
		}

		else if (command.contains("/")) //if it is a entity-improvement command
		{
			int index = command.indexOf("/");
			String unit = command.substring(0,index);
			for (int j = 0; j < main.grid.civs[0].cities.size(); j++)
			{
				City city = main.grid.civs[0].cities.get(j);
				if (city.queue != null)
				{
					if (city.queue.equals(unit))
					{
						message("Cannot change production method of queued unit");
						return false;
					}
				}
			}
			message("Changed production method of " + unit);
			main.grid.civs[0].unitImprovements.put(unit,EntityData.unitImprovementMap.get(command.substring(index+1)));
			menus.get(4).active = false; //Allow player to stay in menu?
			return false;
		}

		else if (command.equals("buildFarm"))
		{
			//Recycled code
			BaseEntity en = selected;
			if (en.location.resource == 1 || en.location.resource == 2)
			{
				en.queueTurns = 6;
				en.queue = "Farm";
			}
			else if (en.location.biome >= 3 && en.location.biome <= 6 && en.location.grid.irrigated(en.location.row, en.location.col))
			{
				en.queueTurns = 6;
				en.queue = "Farm";
			}
			en.queueTurns = Math.max(1,(int)(en.queueTurns*((Worker)en).workTime));
		}
		else if (command.equals("buildMine"))
		{
			BaseEntity en = selected;
			if (en.location.shape == 2)
			{
				en.queueTurns = 6;
				en.queue = "Mine";
			}
			else if (en.location.resource >= 20 && en.location.resource <= 22)
			{
				en.queueTurns = 6;
				en.queue = "Mine";
			}
			else if (en.location.shape == 1)
			{
				if (en.location.biome >= 0 && en.location.biome <= 3)
				{
					en.queueTurns = 6;
					en.queue = "Mine";
				}
			}
			en.queueTurns = Math.max(1,(int)(en.queueTurns*((Worker)en).workTime));
		}
		else if (command.equals("unitKill"))
		{
			main.grid.removeUnit(selected);
		}
		else if (command.equals("unitMeleeMode"))
		{
			((GameEntity)selected).mode = 1;
			updateUnitMenu((GameEntity)selected);
		}
		else if (command.equals("unitRangedMode"))
		{
			((GameEntity)selected).mode = 2;
			updateUnitMenu((GameEntity)selected);
		}
		else if (command.equals("unitRaze"))
		{
			((Warrior)selected).raze();
			((Warrior)selected).action = 0;
			//selected.playerTick();
		}
		else if (command.equals("unitSettle"))
		{
			((Settler)selected).settle();
		}
		else if (command.contains("unitCaravan"))
		{
			int index = Integer.parseInt(command.substring(7));
			((Caravan)selected).setRoute(selected.owner.cities.get(index));
		}

		else if (command.contains("queueBuilding"))
		{
			City city = ((City)selected);
			String impr = command.substring(13);
			//No need to check if the player's tech is appropriate
			System.out.println(impr);
			if (EntityData.queueCityImprovement(city,impr))
			{
				message("Succesfully queued " + impr);
			}
			else
			{
				message("Could not queue " + impr);
			}
		}
		else if (command.contains("queue"))
		{
			//if (EntityData.queue((City)selected, command.substring(5)))
			if (EntityData.queue((City)selected, command.substring(5)) != null)
			{
				message("Succesfully queued " + command.substring(5));
			}
			else
			{
				message("Cannot queue units in a city being recently captured or razed");
			}
		}
		else if (command.equals("razeCity"))
		{
			((City)selected).raze = true;
		}
		/*else if (command.equals("queueSettler"))
		{
			((City)selected).queue = "Settler";
			((City)selected).queueFood = 35;
		}
		else if (command.equals("queueWarrior"))
		{
			((City)selected).queue = "Warrior";
			((City)selected).queueFood = 5;
			((City)selected).queueMetal = 5;
		}
		else if (command.equals("queueWorker"))
		{
			((City)selected).queue = "Worker";
			((City)selected).queueFood = 25;
		}*/
		//Researching tech commands
		else if (command.contains("research"))
		{
			//Tech t = main.grid.civs[0].techTree.researched(command.substring(8));
			main.grid.civs[0].researchTech = command.substring(8);
			menus.get(5).active = false;
		}
		//Change a government or economic civic
		else if (command.contains("gCivic"))
		{
			String civic = command.substring(6);
			main.grid.civs[0].governmentCivic = civic;
			main.menuSystem.message("Changed form of government to " + civic);
		}
		else if (command.contains("eCivic"))
		{
			String civic = command.substring(6);
			main.grid.civs[0].economicCivic = civic;
			main.menuSystem.message("Changed economy to " + civic);
		}
		//The six commands below check to see if the number of idle people is more than the requested number of specialized workers 					
		else if (command.equals("addAdmin"))
		{
			City s = ((City)selected);
			if (s.adm + s.art + s.sci + 1 <= s.population - 1)
				s.adm++;
		}
		else if (command.equals("addArtist"))
		{
			City s = ((City)selected);
			if (s.adm + s.art + s.sci + 1 <= s.population - 1)
				s.art++;
		}
		else if (command.equals("addSci"))
		{
			City s = ((City)selected);
			if (s.adm + s.art + s.sci + 1 <= s.population - 1)
				s.sci++;
		}
		else if (command.equals("subAdmin"))
		{
			City s = ((City)selected);
			if (s.adm > 0)
				s.adm--;
		}
		else if (command.equals("subArtist"))
		{
			City s = ((City)selected);
			if (s.art > 0)
				s.art--;
		}
		else if (command.equals("subSci"))
		{
			City s = ((City)selected);
			if (s.sci > 0)
				s.sci--;
		}
		else if (command.equals("sortie"))
		{
			City s = ((City)selected);
			s.sortie();
		}
		else if (command.equals("endSortie"))
		{
			City s = ((City)selected);
			s.endSortie();
		}

		//Diplomatic commands
		else if (command.contains("openBorders"))
		{
			Civilization a = main.grid.civs[0];
			Civilization b = main.grid.civs[Integer.parseInt(command.substring(11))];
			if (!a.isOpenBorder(b))
			{
				a.openBorder(b);
				main.menuSystem.message("Requested open borders from " + b.name + ".");
			}
		}
		else if (command.contains("declareWar"))
		{
			Civilization a = main.grid.civs[0];
			Civilization b = main.grid.civs[Integer.parseInt(command.substring(10))];
			a.cancelDeals(b);
			a.war(b);
			main.menuSystem.message("You declared war on " + b.name + "!");
			closeMenus();
		}
		else if (command.contains("declarePeace"))
		{
			Civilization a = main.grid.civs[0];
			Civilization b = main.grid.civs[Integer.parseInt(command.substring(12))];
			a.peace(b);
			main.menuSystem.message("You made peace with " + b.name + "!");
			updateDiplomacyMenu(b);
		}
		else if (command.contains("ally"))
		{
			Civilization a = main.grid.civs[0];
			Civilization b = main.grid.civs[Integer.parseInt(command.substring(4))];
			if (a.opinions[b.id] >= 0 && !a.isWar(b) && !a.isAlly(b))
			{
				a.ally(b);
				main.menuSystem.message("You have allied with " + b.name);
			}
			else
			{
				main.menuSystem.message("Your relations with this nation do not allow for an alliance.");
			}
		}
		else if (command.contains("pivot"))
		{
			pivot = main.grid.civs[Integer.parseInt(command.substring(5))]; 
			updateRelations();
		}
		else
		{
			System.out.println("Invalid or non-functioning command: " + command);
		}
		if (command.contains("build") || command.contains("unit") || command.contains("queue"))
		{
			main.inputSystem.selectAvailableUnit();
		}
		return true;
	}

	public void closeMenus()
	{
		info = false;
		minimap = false;
		menus.get(3).active = false;
		menus.get(4).active = false;
		textboxes.get(4).active = false;
		menus.get(5).active = false;
		menus.get(7).active = false;
		menus.get(8).active = false;
		menus.get(9).active = false;
		menus.get(10).active = false;
		menus.get(11).active = false;
		//Clear all but the main menu and encyclopedia
		//for (int i = 1; i < menus.size(); i++)
	}

	public TextBox findButtonWithin(float mouseX, float mouseY)
	{
		for (int i = 0; i < menus.size(); i++)
		{
			Menu m = menus.get(i);
			if (m.active)
			{
				for (int j = 0; j < m.buttons.size(); j++)
				{
					TextBox b = m.within(mouseX, mouseY);
					if (b != null)
					{
						return b;
					}
				}
			}
		}
		return null;
	}

	//Send a message, checking for repeats
	public void message(String... newMessages)
	{
		if (!(main instanceof Tutorial))
		{
			for (int i = 0; i < newMessages.length; i++)
			{
				String message = newMessages[i];
				/*if (message.length() < 40)
			{
				if (messages.size() == 0) messages.add(message);
				if (!messages.get(0).equals(message))
					messages.add(0,message);
			}
			else
			{
				messages.add(0,message.substring(40));
				messages.add(0,message.substring(0,40));
			}*/
				if (messages.size() == 0) messages.add(message);
				if (!messages.get(0).equals(message))
					messages.add(0,message);
			}
			if (!main.grid.civs[0].observe) //Do not shake the GUI if player is not alive
			{
				textboxes.get(2).moveDis(0,-5,2);
				for (int i = 0; i < 10; i++)
					textboxes.get(2).moveDis(0,(10-i)*(int)Math.pow(-1,i),2);
				textboxes.get(2).moveDis(0,5,2);
				textboxes.get(2).orderOriginal(false);
			}
		}
	}

	//Send a message from tutorial level
	public void messageT(String... newMessages)
	{
		for (int i = 0; i < newMessages.length; i++)
		{
			String message = newMessages[i];
			if (messages.size() == 0) messages.add(message);
			if (!messages.get(0).equals(message))
				messages.add(0,message);
		}
		if (!main.grid.civs[0].observe) //Do not shake the GUI if player is not alive
		{
			textboxes.get(2).moveDis(0,-5,2);
			for (int i = 0; i < 10; i++)
				textboxes.get(2).moveDis(0,(10-i)*(int)Math.pow(-1,i),2);
			textboxes.get(2).moveDis(0,5,2);
			textboxes.get(2).orderOriginal(false);
		}
	}

	//Show all the messages on the menu with index 10
	public void updateMessages()
	{
		menus.get(10).buttons.clear();
		for (int i = 0; i < messages.size(); i++)
		{
			TextBox msg = new TextBox(messages.get(i), "", main.width*5/6, 360 + 20*i, main.width*1/6, 20);
			menus.get(10).buttons.add(msg);
			if (i == 19) break;
		}
	}

	public void resetAllButtons()
	{
		for (int i = 0; i < menus.size(); i++)
		{
			for (int j = 0; j < menus.get(i).buttons.size(); j++)
			{
				menus.get(i).buttons.get(j).orderOriginal(true);
			}
		}
	}

	//Will always refer to the player's tech tree
	public void displayTechMenu(Civilization civ)
	{
		menus.get(5).active = true;
		menus.get(5).buttons.clear();

		ArrayList<String> techNames = civ.techTree.findCandidates();
		for (int i = 0; i < techNames.size(); i++)
		{
			String s = techNames.get(i);
			menus.get(5).addButton("research" + s, s, "", main.width/3F, (float)main.height*2F/6F + 60*i, 200, 50);
		}
	}

	public void displayCity(City citySelected)
	{
		//Selection vs highlight
		if (citySelected.equals(selected))
		{
			menus.get(2).active = true;
		}

		ArrayList<String> temp = textboxes.get(1).display;
		temp.add(citySelected.name + "; Population: " + citySelected.population);
		if (citySelected.takeover > 0)
		{
			main.fill(255,0,0);
			if (citySelected.takeover == 1)
				temp.add("IN RESISTANCE FOR 1 TURN.");
			else
				temp.add("IN RESISTANCE FOR " + citySelected.takeover + " TURNS.");
			main.fill(255);
		}
		temp.add("Health: " + citySelected.health + ", Happiness: " + citySelected.happiness);
		temp.add("Culture: " + citySelected.culture);
		temp.add("Administrators: " + citySelected.adm + ", Artists: " + citySelected.art);
		temp.add("Scientists: " + citySelected.sci);
		String buildingString = "";
		if (citySelected.buildings.size() > 0)
		{
			for (int i = 0; i < citySelected.buildings.size(); i++)
				buildingString += citySelected.buildings.get(i).name + ", ";
		}
		else
		{
			buildingString = "No buildings.";
		}
		buildingString = buildingString.substring(0,buildingString.length()-2); //Remove a trailing comma
		temp.add(buildingString);
		if (citySelected.queueFood > 0 || citySelected.queueMetal > 0)
		{
			int[] t = citySelected.quickEval();
			//Division by zero errors
			if (t[0] == 0 && citySelected.queueFood > 0)
			{
				temp.add("No food production, will not finish.");
			}
			else if (t[2] == 0 && citySelected.queueMetal > 0)
			{
				temp.add("No metal production, will not finish.");
			}
			else if (t[0] == 0 && t[2] == 0)
			{
				temp.add("Neither food nor metal production");
				temp.add("will not finish.");
			}
			else
			{
				//System.out.println(t[0] + " " + t[2]);
				int turns;
				if (t[0] == 0)
				{
					turns = citySelected.queueMetal/(t[2]) + 1;
				}
				else if (t[2] == 0)
				{
					turns = citySelected.queueFood/(t[0]) + 1;
				}
				else
				{
					turns = Math.max(
							citySelected.queueFood/(t[0]) + 1,
							citySelected.queueMetal/(t[2]) + 1
							);
				}
				//English grammar...
				if (turns == 1)
					temp.add("Queued " + citySelected.queue + " for " + turns + " turn.");
				else
					temp.add("Queued " + citySelected.queue + " for " + turns + " turns.");
			}
		}
		else
		{
			temp.add("Nothing queued.");
		}
	}

	//Update the ledger
	public void updateCivStats()
	{
		textboxes.get(4).display.clear();
		textboxes.get(4).display.add("You:");
		Civilization c = main.grid.civs[0];
		String s = c.name + "; Food: " + c.food + "; Gold: " + c.gold + "; Research: " + c.research;
		textboxes.get(4).display.add(s);
		textboxes.get(4).display.add("");

		textboxes.get(4).display.add("Civilizations:");
		menus.get(8).active = false;
		for (int i = 1; i < main.grid.civs.length; i++)
		{
			c = main.grid.civs[i];
			s = c.name + "; Food: " + c.food + "; Gold: " + c.gold + "; Research: " + c.research + "; Relations: " + main.grid.civs[0].opinions[i];
			textboxes.get(4).display.add(s);
			menus.get(8).addButton("diplomacy"+i, "Talk", "Conduct diplomacy with " + c.name + ".", 600, 190+60+15*(i-1), 90, 15);
		}
		textboxes.get(4).sizeY = (main.grid.civs.length - 1 + 4)*15 + 15;
		menus.get(8).active = true;
		//100,190,500,250
	}

	//Choose which buttons to show depending on unit (e.g. only settler can settle)
	public void updateUnitMenu(GameEntity en)
	{
		menus.get(1).buttons.clear();
		menus.get(1).addButton("unitKill", "Destroy", "Destroy this unit.", (float)main.width/3F, (float)main.height*5F/6F, 50, 50);
		if (en.name.equals("Settler"))
		{
			menus.get(1).addButton("unitSettle", "Settle", "Settle a city here.", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
		}
		else if (en.name.equals("Warrior"))
		{
			menus.get(1).addButton("unitRaze", "Attack", "Attack the improvement here.", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
		}
		else if (en.name.equals("Worker"))
		{
			ArrayList<String> units = main.grid.civs[0].techTree.allowedTileImprovements;
			for (int i = 0; i < units.size(); i++)
			{
				menus.get(1).addButton("build"+units.get(i), units.get(i), "Construct " + units.get(i) + " here.", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
			}
			//menus.get(1).addButton("buildfarm", "Farm", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
			//menus.get(1).addButton("buildmine", "Mine", (float)main.width/3F + 120, (float)main.height*5F/6F, 50, 50);
		}
		else if (en.name.equals("Caravan"))
		{
			for (int i = 0; i < en.owner.cities.size(); i++)
			{
				City c = en.owner.cities.get(i);
				if (!c.equals(((Caravan)en).home))
				{
					menus.get(1).addButton("unitCaravan"+i, "Caravan"+c.name, "Establish a trade route.", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
				}
			}
		}

		if (en.mode == 1 && en.rangedStr > 0)
		{
			menus.get(1).addButton("rangedMode", "Ranged", "Allow this unit to use ranged attacks.", (float)main.width/3F, (float)main.height*5F/6F + 60, 50, 50);
		}
		else if (en.mode == 2 && en.offensiveStr > 0)
		{
			menus.get(1).addButton("meleeMode", "Melee", "Allow this unit to use melee attacks.", (float)main.width/3F, (float)main.height*5F/6F + 60, 50, 50);
		}
		//System.out.println(menus.get(1).buttons.size());
	}

	//Choose which builds to allow i.e. which can be queued up in the city (factor in techs later)
	public void updateCity(City c)
	{
		menus.get(2).buttons.clear();

		if (c.takeover > 0)
		{
			menus.get(2).addButton("razeCity", "Raze", "Destroy the city, one citizen at a time.", main.width/3F, (float)main.height*5F/6F + 60, 50, 50);
		}

		ArrayList<String> units = c.owner.techTree.allowedUnits;
		for (int i = 0; i < units.size(); i++)
		{
			menus.get(2).addButton("queue" + units.get(i), units.get(i), "Queue a " + units.get(i) + ".", main.width/3F + 60*i, (float)main.height*5F/6F, 50, 50);
		}

		ArrayList<String> buildings = c.owner.techTree.allowedCityImprovements;
		for (int i = 0; i < buildings.size(); i++)
		{
			menus.get(2).addButton("queueBuilding" + buildings.get(i), buildings.get(i), "Queue a " + buildings.get(i) + ".", main.width/3F + 60*i, (float)main.height*5F/6F + 60, 50, 50);
		}
		//menus.get(2).addButton("queueSettler", "Settler", main.width/3F, (float)main.height*5F/6F, 50, 50);
		//menus.get(2).addButton("queueWorker", "Worker", main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
		//menus.get(2).addButton("queueWarrior", "Warrior", main.width/3F + 120, (float)main.height*5F/6F, 50, 50);

		menus.get(2).addButton("addAdmin", "Admin+", "Convert one citizen to admin.", main.width/6F, (float)main.height*5F/6F, 50, 50);
		menus.get(2).addButton("subAdmin", "Admin-", "Revert one admin to citizen.", main.width/6F, (float)main.height*5F/6F + 60, 50, 50);
		menus.get(2).addButton("addArtist", "Artist+", "Convert one citizen to artist.", main.width/6F + 60, (float)main.height*5F/6F, 50, 50);
		menus.get(2).addButton("subArtist", "Artist-", "Revert one artist to citizen.", main.width/6F + 60, (float)main.height*5F/6F + 60, 50, 50);
		menus.get(2).addButton("addSci", "Sci+", "Convert one citizen to scientist.", main.width/6F + 120, (float)main.height*5F/6F, 50, 50);
		menus.get(2).addButton("subSci", "Sci-", "Revert one scientist to citizen.", main.width/6F + 120, (float)main.height*5F/6F + 60, 50, 50);

		if (c.sortie == 1)
		{
			menus.get(2).addButton("sortie", "Sortie", "Raise a temporary garrison (cannot leave borders).", main.width/6F - 60, (float)main.height*5F/6F, 50, 50);
		}
		else if (c.sortie == 2)
		{
			menus.get(2).addButton("endSortie", "End sortie", "End the sortie and return troops to city.", main.width/6F - 60, (float)main.height*5F/6F, 50, 50);
		}
	}

	public void updateLoadoutDisplay(String name)
	{
		menus.get(4).buttons.clear();
		BaseEntity en = EntityData.get(name);
		ArrayList<Improvement> valid = EntityData.getValidImprovements(main.grid.civs[0], en);
		for (int i = 0; i < valid.size(); i++)
		{
			Improvement temp = valid.get(i);
			menus.get(4).addButton(en.name + "/" + temp.name, temp.name, "", main.width/3F, (float)main.height*2F/6F + 60*i, 200, 50);
		}
	}

	public void updateDiplomacyMenu(Civilization civ)
	{
		Civilization plr = main.grid.civs[0];
		menus.get(9).buttons.clear();

		TextBox text0 = new TextBox(new ArrayList<String>(),"",main.width*2/6,main.height*2/6,main.width*2/6,main.height/12); //"HintText"
		text0.display.add(civ.name);

		menus.get(9).addButton("openBorders"+civ.id, 
				"Request open borders.",
				"Allow unrestricted travel between you and this nation.", 
				main.width*2/6,main.height*2/6 + main.height/12 + 10,main.width*2/6,main.height/24);

		if (!plr.isWar(civ))
		{
			menus.get(9).addButton("declareWar"+civ.id,
					"Declare war.",
					"Declare war on this civilization (and cancel all deals).",
					main.width*2/6,main.height*2/6 + main.height/12 + main.height/24 + 20,main.width*2/6,main.height/24);
		}
		else
		{
			menus.get(9).addButton("declarePeace"+civ.id,
					"Declare peace.",
					"Negotiate peace with this nation.",
					main.width*2/6,main.height*2/6 + main.height/12 + main.height/24 + 20,main.width*2/6,main.height/24);
		}

		if (!plr.isAlly(civ))
			menus.get(9).addButton("ally"+civ.id,
					"Request an alliance.",
					"Request a mutual protection and aggression between you and this nation.",
					main.width*2/6,main.height*2/6 + main.height/12 + 2*main.height/24 + 30,main.width*2/6,main.height/24);

		menus.get(9).buttons.add(text0);
	}

	private Civilization pivot; //The civilization that the relations menu will "focus" on
	public void updateRelations()
	{
		menus.get(11).buttons.clear();

		//Top set
		TextBox text = new TextBox("Relations","Your relations with this nation (-200 to 200).",200,255,100,20);
		menus.get(11).buttons.add(text);
		text = new TextBox("Open Borders","Your ability to access this nation's lands.",300,255,100,20);
		menus.get(11).buttons.add(text);
		text = new TextBox("War","",400,255,100,20);
		menus.get(11).buttons.add(text);
		text = new TextBox("Alliance","The existence of a formal alliance between you and this nation.",500,255,100,20);
		menus.get(11).buttons.add(text);

		for (int i = 0; i < main.grid.civs.length; i++)
		{
			Civilization civ = main.grid.civs[i];

			Button b = new Button("pivot"+i,civ.name,"Select to view " + civ.name + "'s diplomatic situation.",100,280 + 25*(i),100,20);
			menus.get(11).buttons.add(b);

			if (civ.equals(pivot)) continue;

			text = new TextBox("" + pivot.opinions[i],"",200,280 + 25*(i),100,20);
			menus.get(11).buttons.add(text);

			String temp = pivot.isOpenBorder(civ) ? "Yes" : "No";
			text = new TextBox(temp,"",300,280 + 25*(i),100,20);
			menus.get(11).buttons.add(text);

			temp = pivot.isWar(civ) ? "Yes" : "No";
			text = new TextBox(temp,"",400,280 + 25*(i),100,20);
			menus.get(11).buttons.add(text);

			temp = pivot.isAlly(civ) ? "Yes" : "No";
			text = new TextBox(temp,"",500,280 + 25*(i),100,20);
			menus.get(11).buttons.add(text);
		}

		//Bottom set
		/*text = new TextBox("","In war","The list of nations that this nation is currently fighting.",
				200,280 + 25*main.grid.civs.length,200,20);
		menus.get(11).buttons.add(text);

		for (int i = 0; i < main.grid.civs.length; i++)
		{
			Civilization civ = main.grid.civs[i];

			text = new TextBox("",civ.name,"",100,280 + 25*(i+1+main.grid.civs.length),100,20);
			menus.get(11).buttons.add(text);

			String temp = "At Peace";
			if (civ.enemies.size() > 1)
			text = new TextBox("",temp,"",300,280 + 25*(i-1),100,20);
			menus.get(11).buttons.add(text);
		}*/
	}

	public void updateCivicsMenu(Civilization civ)
	{
		menus.get(12).buttons.clear();
		for (int i = 0; i < civ.techTree.governmentCivics.size(); i++)
		{
			String s = civ.techTree.governmentCivics.get(i);
			menus.get(12).addButton("gCivic" + s, s, "", main.width/3F, (float)main.height*2F/6F + 60*i, 200, 50);
		}
		for (int i = 0; i < civ.techTree.governmentCivics.size(); i++)
		{
			String s = civ.techTree.economicCivics.get(i);
			menus.get(12).addButton("eCivic" + s, s, "", main.width/3F + 250, (float)main.height*2F/6F + 60*i, 200, 50);
		}
	}

	//Only done once
	public void updateEncyclopedia()
	{
		int n = 0;
		for (Entry<String, ArrayList<String>> i: EntityData.encyclopediaEntries.entrySet())
		{
			String key = i.getKey();
			menus.get(7).addButton("encyclopedia" + key, key, "", 830, 190 + 30*n, 100, 30);
			n++;
		}
	}

	//Encapsulation for selected
	public BaseEntity getSelected()
	{
		return selected;
	}

	public void select(BaseEntity en)
	{
		selected = en;
		main.newMenuSystem.updateUnitMenu(en);
		if (en != null)
		{
			if (en instanceof Settler)
			{
				settlerChoices = main.grid.returnBestCityScores(en.location.row, en.location.col, 0.25);
			}
			else
			{
				settlerChoices = null;
			}
			if (en instanceof City)
			{
				updateCity((City)en);
			}
			textboxes.get(1).active = true;
			textboxes.get(1).move(main.width - 400,-150);
			textboxes.get(1).moveTo(main.width - 400,0,20);
		}
		else
		{
			textboxes.get(1).active = false;
			textboxes.get(1).move(main.width*4/6,-150);

			menus.get(1).buttons.clear();
		}
	}


}
