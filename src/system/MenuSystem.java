package system;

import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Tech;
import game.Tile;
import lwjglEngine.gui.GuiTexture;
import lwjglEngine.render.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import menugame.Tutorial;
import data.ColorImage;
import data.EntityData;
import data.Field;
import data.Improvement;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import render.*;
import render.MouseHelper.Point;
import render.MouseHelper.Shape;
import units.Caravan;
import units.City;
import units.Settler;
import units.Warrior;
import units.Worker;
import vector.Line;

public class MenuSystem extends BaseSystem {

	public ArrayList<Menu> menus;
	public TechMenu techMenu;
	public ArrayList<TextBox> textboxes;

	private ArrayList<Click> clicks;

	public int minimapMode = 0; //0 -> off, 1 -> local, 2 -> global
	public boolean info; //loadout, loadoutDisplay, techMenu, continueMenu; //Access the menu's active property instead
	public int multiplier = 1;
	public float highlightDispX = main.width/2, highlightDispY = main.width/2;
	public int sight = 7; 

	public Tile target;
	//public ArrayList<String> hintText;
	public Tile highlighted;
	public Tile mouseHighlighted; //Under the player's crosshair versus under the player's mouse
	public Tile lastMouseHighlighted;
	public Tile lastHighlighted;
	private BaseEntity selected; //Selected by the player with the mouse explicitly
	public Tile[] settlerChoices; public ArrayList<Tile> movementChoices = new ArrayList<Tile>(), pathToHighlighted = new ArrayList<Tile>();
	public String typeOfLastSelected = "";

	//2 click type interactions
	public String candidateField = ""; public City candidateCityField = null;
	public ArrayList<GameEntity> stack = new ArrayList<GameEntity>(); //Select a stack of units to do an action

	//if non-null, console is on
	public String console = null;

	public int[] rbox;

	public Tooltip tooltip = new Tooltip("",0,0,80,20);
	public boolean[][] markedTiles;

	public Button[] shortcuts = new Button[10];
	public ArrayList<TextBox> noOverlap = new ArrayList<TextBox>();

	public boolean requestFieldsUpdate = false;

	public float height = 100;
	
	private Loader loader;
	
	//public City citySelected;

	//public TextBox hintTextBox;
	//public TextBox selectedTextBox;

	private ArrayList<String> messages;

	public MenuSystem(CivGame civGame) {
		super(civGame);
		loader = main.lwjglSystem.loader;
		menus = new ArrayList<Menu>();
		textboxes = new ArrayList<TextBox>();
		clicks = new ArrayList<Click>();

		//hintText = new ArrayList<String>();
		messages = new ArrayList<String>();
		//highlighted = null;

		//Keep track of the menu's indices in list
		Menu menu0 = new Menu("MainMenu");
		menus.add(menu0);
		int height = 30;
		//menu0.addButton("exitgame", "Exit", "Exit this session of the game.", main.width - 100, 0, 100, height).lock = true;
		menu0.addButton("close", "Close All", "Close all open menus.", main.width - 100, 70, 100, height).lock = true;
		menu0.addButton("minimap", "Minimap", "Open the minimap of the world.", main.width - 100, 100, 100, height).lock = true;
		menu0.addButton("info", "Information", "", main.width - 100, 130, 100, height).lock = true;
		//menu0.buttons.add(new Button("loadout", "Loadout", "Change loadouts of certain units.", main.width - 100, 160, 100, height, 3, 4));
		menu0.addButton("loadout", "Loadout", "Change loadouts of certain units.", main.width - 100, 160, 100, height).lock = true;
		menu0.addButton("stats", "Statistics", "Compare stats of different civilizations.", main.width - 100, 190, 100, height).lock = true;
		//menu0.addButton("techs", "Techs", "Choose technologies to research.", main.width - 100, 220, 100, height).lock = true;
		menu0.addButton("techsweb", "Techs", "Choose technologies to research.", main.width - 100, 220, 100, height).lock = true;
		menu0.addButton("encyclopedia", "Reference", "A encyclopedia-like list of articles.", main.width - 100, 250, 100, height).lock = true;
		menu0.addButton("relations", "Relations", "The wars and alliances of this world.", main.width - 100, 280, 100, height).lock = true;
		menu0.addButton("civic", "Civics", "Change the ideals of your government.", main.width - 100, 310, 100, height).lock = true;
		menu0.addButton("log", "Messages", "", main.width*3/4, 0, main.width*1/4, height).lock = true;
		//menu0.addButton("log", "Messages", "View your messages.", main.width*3/4, 0, main.width*1/4, height).lock = true;

		int pivot = menu0.buttons.size()*height;
		for (int i = 0; i < menu0.buttons.size() - 1; i++)
		{
			TextBox b = menu0.buttons.get(i);
			b.move(0, 70 + (i)*height);
		}

		TextBox b = menu0.addButton("markTile", "MarkTile", "Mark this tile", main.width - 100, 70, 100, height);
		b.lock = true; b.activate(false); b.autoClear = false;

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
		TextBox temp = new TextBox(loader.loadTexture("partTexture"),"",100,190,700,500); //"EncyclopediaText",
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

		Menu menu13 = new KeyMenu(main.inputSystem, "KeyMenu");
		menus.add(menu13);

		Menu menu14 = new Menu("TacticalMenu"); //rendered and added to in NewMenuSystem
		menu14.noShortcuts = true;
		menus.add(menu14);

		Menu menu15 = new Menu("FieldMenu"); //for displaying fields
		menu15.noShortcuts = true;
		menus.add(menu15);

		Menu menu16 = new Menu("CreateFieldMenu");
		menus.add(menu16);

		menu0.activate(true);

		TextBox text0 = new TextBox(loader.loadTexture("partTexture"),"",main.width - 200,main.height - 250,200,100); //"HintText"
		text0.alpha = 0;
		textboxes.add(text0);

		TextBox text1 = new TextBox(loader.loadTexture("partTexture"),"",main.width - 400,main.height - 150,200,150); //"SelectedText"
		textboxes.add(text1);

		TextBox text2 = new TextBox(loader.loadTexture("partTexture"),"",main.width*3/4,30,main.width/4,100); //"Messages"
		textboxes.add(text2);

		TextBox text3 = new TextBox(loader.loadTexture("partTexture"),"",main.width/6,0,300,50); //"PlayerStatus"
		textboxes.add(text3);

		TextBox text4 = new TextBox(loader.loadTexture("partTexture"),"",100,190,500,250); //"LedgerText"
		textboxes.add(text4);

		TextBox text5 = new TextBox(loader.loadTexture("partTexture"),"...",main.width - 400,main.height - 200 + 15,200,35); //"ConditionText"
		//ArrayList<String> stringy = new ArrayList<String>(); stringy.add("..."); text5.display = stringy;
		text5.autoClear = false;
		textboxes.add(text5);

		TextBox text6 = new TextBox(loader.loadTexture("partTexture"),"",main.width - 200,main.height - 150,100,150); //"Detail1Text" (goes with HintText)
		textboxes.add(text6);

		TextBox text7 = new TextBox(loader.loadTexture("partTexture"),"",main.width - 100,main.height - 150,100,150); //"Detail2Text" (goes with HintText)
		text7.monospace = true;
		textboxes.add(text7);

		TextBox text8 = new TextBox(loader.loadTexture("partTexture"),"",main.width/6,50,300,30);
		textboxes.add(text8);

		text4.activate(false);

		updateEncyclopedia();
		
		for (int i = 0; i < textboxes.size(); i++)
		{
			TextBox t = textboxes.get(i);
			t.alpha = 100;
			t.noOverlap = true;
		}
		for (int i = 0; i < menus.size(); i++)
		{
			for (int j = 0; j < menus.get(i).buttons.size(); j++)
			{
				TextBox t = menus.get(i).buttons.get(j);
				if (i != 0)
					t.alpha = 100;
				t.noOverlap = true;
			}
		}
		//arial = main.loadFont("ArialMT-48.vlw");
	}

	public PFont arial;

	public boolean menuActivated = false, menuHighlighted = false;
	public void tick()
	{
		/*//main.textFont(arial);
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
		lastMouseHighlighted = mouseHighlighted;

		//System.out.println(menus.get(0).findButtonByCommand("markTile").posX);
		//System.out.println(menus.get(0).findButtonByCommand("markTile").posY);
		//System.out.println("======");

		main.textAlign(main.CENTER);
		//main.text("When selecting a unit, hold Q to bring out the quick menu. Drag with right click to the desired tile.", 500, 80);
		//main.image(EntityData.iconMap.get("CopperWeapons"),200,200,200,200);

		if (minimapMode == 1 || minimapMode == 2)
		{
			//main.rect(0, 700, 50, 50);
			int con = 1;
			main.noFill();
			float sX = main.width - 300; float sY = main.height - 600; float widthX = 250; float widthY = 250;
			main.rect(sX, sY, widthX, widthY);
			//System.out.println(rbox[0] + " " + rbox[1] + " " + rbox[2] + " " + rbox[3]);
			if (minimapMode == 2)
			{
				for (int r = rbox[0]; r <= rbox[0] + rbox[2]; r += con)
				{
					for (int c = rbox[1]; c <= rbox[1] + rbox[3]; c += con)
					{
						Tile t = main.grid.getTile(r,c);
						if (t == null) continue;
						minimapFill(t);
						//main.rect(sX + (main.grid.rows-r)/(float)main.grid.rows*widthX,sY + c/(float)main.grid.cols*widthY,widthX*con/main.grid.rows,widthY*con/main.grid.cols);
						//System.out.println(sX + r/(float)main.grid.rows*widthX);
						main.rect(sX + (r-rbox[0])/(float)rbox[2]*widthX, sY + (1 - (c-rbox[1])/(float)(rbox[3]))*widthY, widthX*con/(float)rbox[2], widthY*con/(float)rbox[3]);
					}
				}
			}
			else //if (minimapMode == 1) by disjunctive syllogism
			{
				int rr = 0, cc = 0;
				if (highlighted != null)
				{
					for (int r = highlighted.row - sight; r <= highlighted.row + sight; r++)
					{
						for (int c = highlighted.col - sight; c <= highlighted.col + sight; c++)
						{
							Tile t = main.grid.getTile(r,c);
							if (t == null) {cc++; continue;}
							float wX = widthX/(sight*2 + 1), wY = widthY/(sight*2 + 1);
							//minimapFill(t);
							if (main.grid.civs[0].revealed[r][c] == 0 && !main.showAll)
							{
								main.fill(0);
								main.rect(sX + rr*wX,sY + (sight*2 + 1 - cc)*wY,wX,wY);
								cc++;
								continue;
							}
							if (t.biome == -1)
								main.fill(150,225,255,150);
							else if (t.owner == null) 
								main.fill(150,255);
							else
								main.fill(t.owner.r, t.owner.g, t.owner.b, 255);
							main.stroke(0);
							main.rect(sX + rr*wX,sY + (sight*2 + 1 - cc)*wY,wX,wY);
							ArrayList<PImage> images = icon(t);
							if (images.size() > 0)
								for (int i = images.size() - 1; i >= 0; i--)
									if (images.get(i) != null)
									{
										if (t.occupants.get(i).owner != null)
											main.tint(t.occupants.get(i).owner.r, t.occupants.get(i).owner.g, t.occupants.get(i).owner.b);
										else
											main.tint(150);
										main.image(images.get(i),sX + (rr+0.25F)*wX,sY + (sight*2 + 1 - cc + 0.25F - 0.25F*i)*wY,wX/2F,wY/2F); //Flip cols
									}
							cc++;
						}
						rr++;
						cc = 0;
					}
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
		if (console != null)
		{
			textboxes.get(8).active = true;
			textboxes.get(8).display.clear();
			textboxes.get(8).display.add(console);
		}
		else
		{
			textboxes.get(8).active = false;
		}
		//menus.get(6).active = ledgerMenu;
		//System.out.println(loadout + " " + loadoutDisplay);

		//Render the cursor
		if (!menus.get(7).active && !menus.get(9).active)
		{
			int width = 6;
			main.stroke(255);
			main.fill(0);
			main.rect((main.width - width)/2, (main.height - width)/2, width, width);
		}

		main.noStroke();

		ArrayList<String> hintText = textboxes.get(0).display;
		//hintText.clear();
		if (mouseHighlighted != null)
		{
			String stringy = (main.grid.cols - mouseHighlighted.col) + " " + mouseHighlighted.row;
			if (mouseHighlighted.owner != null)
				stringy += " (" + mouseHighlighted.owner.name + ")";
			hintText.add(stringy);
			//else
			//hintText.add("Terra nullius");

			String biomeText = "";

			if (mouseHighlighted.biome >= 4 && mouseHighlighted.biome <= 6)
				if (mouseHighlighted.forest)
					biomeText += EntityData.getBiomeName(mouseHighlighted.biome) + " (fertile)";
				else
					biomeText += EntityData.getBiomeName(mouseHighlighted.biome);
			else
				biomeText += EntityData.getBiomeName(mouseHighlighted.biome);

			if (mouseHighlighted.shape == 1)
				biomeText += ", Hill";
			else if (mouseHighlighted.shape == 2)
				biomeText += ", Mountain";

			hintText.add(biomeText);

			if (mouseHighlighted.improvement != null)
			{
				hintText.add(mouseHighlighted.improvement.name + ", " + mouseHighlighted.improvement.id);
			}
			else
				hintText.add("Pristine");

			if (mouseHighlighted.city != null)
			{
				if (mouseHighlighted.city.owner != null)
				{
					double[] data = mouseHighlighted.city.evaluate(mouseHighlighted, -1);
					hintText.add((int)data[0] + " F, " + (int)data[1] + " G, " + (int)data[2] + " M, " + (int)data[3] + " R");
				}
			}
			else //A rough estimate that does not take the city into account
			{
				double[] data = City.staticEval(mouseHighlighted);
				hintText.add((int)data[0] + " F, " + (int)data[1] + " G, " + (int)data[2] + " M, " + (int)data[3] + " R");
			}
			//Same check as above, really
			if (mouseHighlighted.owner != null)
				hintText.add("Relations: " + mouseHighlighted.owner.opinions[0]);

			if (main.grid.irrigated(mouseHighlighted.row, mouseHighlighted.col))
				hintText.add("Fresh Water");

			if (mouseHighlighted.occupants.size() > 0)
			{
				//String stringy;
				stringy = "";
				if (mouseHighlighted.occupants.size() > 3)
				{
					stringy += "Stack (" + mouseHighlighted.occupants.size() + ")";
				}
				else
				{
					for (int i = 0; i < mouseHighlighted.occupants.size(); i++)
					{
						GameEntity en = mouseHighlighted.occupants.get(i);
						stringy += en.name + " (" + en.owner + "); ";
					}
					//if (!stringy.equals(""))
					hintText.add(stringy.substring(0,stringy.length()-2));
				}
			}

			String resource = EntityData.getResourceName(mouseHighlighted.resource);
			if (resource != null)
				hintText.add(resource);

			hintText.add(" ");

			if (mouseHighlighted != null)
			{
				ArrayList<String> conditions;
				if (mouseHighlighted.improvement != null)
					conditions = City.staticEvalReasons(mouseHighlighted, mouseHighlighted.improvement.name);
				else
					conditions = City.staticEvalReasons(mouseHighlighted, null);
				ArrayList<String> text6 = textboxes.get(6).display;
				ArrayList<String> text7 = textboxes.get(7).display;
				int[] bYield = {0,0,0,0};
				for (int i = 0; i < conditions.size(); i++)
				{
					text6.add(conditions.get(i));
					int[] yield = EntityData.yield.get(conditions.get(i));
					String s = "";
					for (int j = 0; j < yield.length; j++)
					{
						bYield[j] += yield[j]; 
						if (yield[j] == 0) s += "  ";
						else if (yield[j] > 0) s += "+" + yield[j];
						else s += yield[j];
						s += " ";
					}
					text7.add(s);
				}
				String s = "";
				for (int j = 0; j < bYield.length; j++)
				{
					if (bYield[j] == 0) s += "  ";
					else if (bYield[j] > 0) s += "+" + bYield[j];
					else s += bYield[j];
					s += " ";
				}
				text6.add("TOTAL YIELD");
				text7.add(s);

				s = "";
				if (mouseHighlighted.improvement == null)
				{
					String impr = EntityData.optimalImpr(mouseHighlighted);
					if (impr != null)
						text6.add("With " + impr.toLowerCase());
					double[] aYield = City.staticEval(mouseHighlighted, impr);
					for (int j = 0; j < aYield.length; j++)
					{
						if (aYield[j] - bYield[j] == 0) s += "  ";
						else if (aYield[j] - bYield[j] > 0) s += "+" + (int)(aYield[j] - bYield[j]);
						else s += (int)(aYield[j] - bYield[j]);
						s += " ";
					}
					text7.add(s);
				}
			}
		}
		Tile h = highlighted;
		MouseHelper mh = main.inputSystem.mouseHelper;
		main.noStroke();
		if (selected != null) //Allow GUI to show elements specific to selected unit
		{
			if (selected.owner != null && !(selected instanceof City))
			{
				main.fill(255);
				//main.textSize(14);

				ArrayList<String> temp = textboxes.get(1).display;
				//temp.clear();
				temp.add(selected.name + " " + ((GameEntity)selected).action*2 + "/" + ((GameEntity)selected).maxAction*2);
				//temp.add(selected.health + "<!health!>/" + selected.maxHealth + " health");
				temp.add(selected.health + "/" + selected.maxHealth + " health");
				temp.add(selected.offensiveStr + " offensive, " + selected.rangedStr + " ranged,");
				temp.add(selected.defensiveStr + " defensive");

				if (!typeOfLastSelected.equals(selected.name))
				{
					updateUnitMenu((GameEntity)selected);
				}
				if (!menus.get(1).active())
					menus.get(1).activate(true);
				//main.text("Test", main.width*5/6 + 15, main.height*5/6 + 15);
			}
			else
			{
				if (menus.get(1).active())
					menus.get(1).activate(false);
			}
			if ((getSelected() instanceof City || getSelected() instanceof Settler || getSelected() instanceof Worker) && h != null && 
					(main.grid.civs[0].revealed[h.row][h.col] != 0 || main.showAll) && main.player.posY < height)
			{
				for (int r = 0; r < mh.guiPositions.length; r++)
				{
					for (int c = 0; c < mh.guiPositions[0].length; c++)
					{
						float[] pos = mh.positionGui(r,c);
						if (pos != null)
						{
							if (!main.newMenuSystem.notOverlapping(pos[0], pos[1])) continue;
							main.textAlign(main.CENTER);
							main.textSize(18);
							main.fill(255,0,0);
							int dC = r - (mh.guiPositions.length-1)/2;
							int dR = c - (mh.guiPositions[0].length-1)/2;

							//float[] disp = mh.center();
							float dX = main.width/2 - highlightDispX, dY = main.height/2 - highlightDispY;
							//float dX = 0, dY = 0;
							//System.out.println(dX + " " + dY + " " + highlightDispX + " " + highlightDispY);

							Tile t = main.grid.getTile(h.row + dR, h.col - dC);
							if (t != null)
							{
								if (t.biome == -1 && main.grid.adjacentLand(t.row, t.col).size() == 0 || 
										main.grid.civs[0].revealed[t.row][t.col] == 0 && !main.showAll) 
									continue;
								//if (movementChoices.contains(t))
								//main.text(">", pos[0] - dX,pos[1] - dY + 10);
								if (!(getSelected() instanceof City) && pathToHighlighted != null)
								{
									int index = pathToHighlighted.indexOf(t);
									if (index != -1)
										main.text(pathToHighlighted.size() - index, pos[0] - dX,pos[1] - dY + 20);
								}
								if (movementChoices.size() > 0) continue; 
								//main.text(t.row + "," + t.col, pos[0], pos[1]);
								if (!main.tacticalView)
								{
									double[] y = City.staticEval(t);
									//float height = pos[1]-dY+(5-dC)*8;
									float height = pos[1]-dY+25;
									//if (dC >= 1)
									//height += 20*dC; //height = pos[1]-dY;
									main.newMenuSystem.tileIcon(pos[0]-dX, height, (int)y[0], (int)y[1], (int)y[2], (int)y[3], t.harvest);
									//main.rect(pos[0]-dX-5, height-5+25, 10, 10);
									PImage img = EntityData.iconMap.get(EntityData.getBiomeName((t.biome)));
									int len = 20;
									float iX = pos[0]-dX-len/2, iY = pos[1]-dY+10;										
									main.tint(255,255,255,100);
									//Make way for the appropriate GUIs
									if (t.forest || t.freshWater)
									{
										if (img != null)
										{
											main.image(img, iX, iY, len, len);
											iX = pos[0]-dX+len/2; //iY = pos[1]-dY+20-len/2;
										}
									}
									else
									{
										iX = pos[0]-dX;
									}
									img = EntityData.iconMap.get(EntityData.getResourceName(t.resource));
									if (img != null)
									{
										main.tint(255,255,255,255);
										main.pushStyle();
										if (t.owner != null)
										{
											main.strokeWeight(3);
											main.noFill();
											main.stroke(t.owner.r, t.owner.g, t.owner.b);
											main.rect(iX, iY, len, len);
										}
										main.image(img, iX, iY + 2*len, len, len);
										main.popStyle();
										//iX = pos[0]-dX-len/2; iY = pos[1]-dY+20-len/2;
									}
									main.tint(255,255,255,255);
									//main.rect(pos[0]-dX, pos[1]-dY, 10, 10);
									int n = 0;
									for (int i = 0; i < y.length; i++)
										if (y[i] > 0)
											n++;
									int iter = 1;
									for (int i = 0; i < y.length; i++)
										if (y[i] > 0)
										{
											main.newMenuSystem.tileIcon(pos[0] - dX,pos[1] - dY,i,(int)y[i],n,iter);
											iter++;
										}
								}
							}
						}
					}
				}
			}
		}
		else
			menus.get(1).activate(false);

		menus.get(14).activate(main.tacticalView);
		if (requestFieldsUpdate)
		{
			menus.get(14).buttons.clear();
		}

		main.strokeWeight(1);
		if (h != null && main.player.posY < height)
		{
			for (int r = 0; r < mh.guiPositions.length; r++)
			{
				for (int c = 0; c < mh.guiPositions[0].length; c++)
				{
					float[] pos = mh.positionGui(r,c);
					if (pos != null)
					{
						if (!main.newMenuSystem.notOverlapping(pos[0], pos[1])) continue;
						main.textAlign(main.CENTER);
						main.textSize(18);
						main.fill(255,0,0);
						int dC = r - (mh.guiPositions.length-1)/2;
						int dR = c - (mh.guiPositions[0].length-1)/2;
						float dX = main.width/2 - highlightDispX, dY = main.height/2 - highlightDispY;

						Tile t = main.grid.getTile(h.row + dR, h.col - dC);
						if (t != null)
						{
							if (t.biome == -1 && main.grid.adjacentLand(t.row, t.col).size() == 0 && t.resource == 0 || 
									main.grid.civs[0].revealed[t.row][t.col] == 0 && !main.showAll) 
								continue;
							if (markedTiles[h.row + dR][h.col - dC])
								main.text("X", pos[0] - dX,pos[1] - dY + 20);
							if (t.improvement != null)
							{
								if (t.improvement instanceof City)
								{
									//main.rectMode(main.LEFT);
									City city = (City)t.improvement;
									//Growth GUI
									main.fill(0);
									//main.rectMode(main.CENTER);
									main.rect(pos[0] - dX - 50, pos[1] - dY - 10, 100, 20);
									main.fill(0,255,0);
									//System.out.println((float)city.percentGrowth*100);
									main.rect(pos[0] - dX - 50, pos[1] - dY - 10, 100F*(float)city.percentGrowth, 20);
									main.fill(255);
									main.textAlign(main.CENTER);
									main.text(city.name + " <" + ((int)(city.percentGrowth*10000F)/100) + "% >", pos[0] - dX, pos[1] - dY + 3);
									//Queue GUI
									if (city.queue != null)
									{
										main.fill(0, 100);
										main.rect(pos[0] - dX - 50, pos[1] - dY + 10, 100, 20);
										float[] cost = EntityData.getCost(city.queue);
										main.fill(0,255,0,255);
										main.rect(pos[0] - dX - 50, pos[1] - dY + 10, (float)Math.min(100F, 100F*(1 - city.queueFood/cost[0])), 10);
										main.fill(255,150,0,255);
										main.rect(pos[0] - dX - 50, pos[1] - dY + 20, (float)Math.min(100F, 100F*(1 - city.queueMetal/cost[2])), 10);
										main.fill(255,255);
										main.textAlign(main.CENTER);
										//main.text(city.name + " <" + ((int)(city.percentGrowth*10000F)/100) + "% >", pos[0] - dX, pos[1] - dY + 3);
									}
									main.textAlign(main.LEFT);
									//main.rectMode(main.LEFT);
								}
							}
							//if (!main.tacticalView)
							if (true)
							{
								float len = 30; boolean cityGui = false;
								if (t.improvement != null)
								{
									if (t.improvement instanceof City)
									{
										City city = (City)t.improvement;
										main.tint(t.improvement.owner.r, t.improvement.owner.g, t.improvement.owner.b);
										PImage image = city.owner.id >= main.grid.barbarians ? EntityData.iconMap.get("CityIcon") : EntityData.iconMap.get("Barbarian");
										if (t.improvement.owner.capital != null)
											if (t.improvement.owner.capital.equals(t.improvement))
												image = EntityData.iconMap.get("Capital");
										main.image(image, pos[0] - dX - 3*len/2, pos[1] - dY - 30 - len/2, len, len);

										int i = 0;
										main.image(image, pos[0] - dX - len/2 - len, pos[1] - dY - 30 - i*30 - len/2, len, len);
										main.image(EntityData.iconMap.get("population"), pos[0] - dX - len/2, pos[1] - dY - 30 - i*30 - len/2, len/2, len/2);
										main.image(EntityData.iconMap.get("defense"), pos[0] - dX - len/2, pos[1] - dY - 30 - i*30, len/2, len/2);
										main.image(EntityData.iconMap.get("ranged"), pos[0] - dX - len/2 + len, pos[1] - dY - 30 - i*30 - len/2, len/2, len/2);
										main.image(EntityData.iconMap.get("cityhealth"), pos[0] - dX - len/2 + len, pos[1] - dY - 30 - i*30, len/2, len/2);

										main.fill(0);
										main.rect(pos[0] - dX, pos[1] - dY - 30 - i*30 - len/2, len/2, len/2);
										main.rect(pos[0] - dX, pos[1] - dY - 30 - i*30, len/2, len/2);
										main.rect(pos[0] - dX + len, pos[1] - dY - 30 - i*30 - len/2, len/2, len/2);
										main.rect(pos[0] - dX + len, pos[1] - dY - 30 - i*30, len/2, len/2);
										main.textAlign(main.LEFT, main.TOP);
										main.fill(255);
										main.text((int)city.population, pos[0] - dX, pos[1] - dY - 30 - i*30 - len/2);
										main.text((int)city.defensiveStr, pos[0] - dX, pos[1] - dY - 30 - i*30);
										main.text((int)city.rangedStr, pos[0] - dX + len, pos[1] - dY - 30 - i*30 - len/2);
										main.text((int)city.health, pos[0] - dX + len, pos[1] - dY - 30 - i*30);
										
										cityGui = true;
									}
								}
								if (t.occupants.size() > 0)
								{
									//for (int i = 0; i < t.occupants.size(); i++)
									if (!(selected instanceof City) && !(getSelected() instanceof Settler))
									{
										for (int i = t.occupants.size() - 1; i >= 0; i--)
										{
											GameEntity en = t.occupants.get(i);
											main.fill(en.owner.r, en.owner.g, en.owner.b);
											//main.rectMode(main.CENTER);
											//main.rect(pos[0] - dX - len/2, pos[1] - dY - 60 - i*10 - len/2, len, len);
											PImage image = EntityData.iconMap.get(en.name);
											float iX, iY;
											//ArrayList<PImage> images = icon(t);
											if (image != null)
											{
												main.pushStyle();
												main.tint(en.owner.r, en.owner.g, en.owner.b);
												//main.rectMode(main.LEFT);
												if (en.mode != 0)
												{
													//TODO
													//Redefine this type of unit stat rendering
													//as a function which accepts a unit and some of its stat types as input
													//and renders in correct order at the appropriate location

													//4*len to compensate for unit strength GUI
													iX = pos[0] - dX - len/2 - len; iY = pos[1] - dY - (i+1)*len - len/2;
													if (cityGui)
														iY -= len;
													unitStats(en, iX, iY, len);
													main.image(image, iX, iY, len, len);
													main.image(EntityData.iconMap.get("attack"), pos[0] - dX - len/2, pos[1] - dY - 30 - i*30 - len/2, len/2, len/2);
													main.image(EntityData.iconMap.get("defense"), pos[0] - dX - len/2, pos[1] - dY - 30 - i*30, len/2, len/2);
													main.image(EntityData.iconMap.get("ranged"), pos[0] - dX - len/2 + len, pos[1] - dY - 30 - i*30 - len/2, len/2, len/2);
													main.image(EntityData.iconMap.get("health"), pos[0] - dX - len/2 + len, pos[1] - dY - 30 - i*30, len/2, len/2);

													main.fill(0);
													main.rect(pos[0] - dX, pos[1] - dY - 30 - i*30 - len/2, len/2, len/2);
													main.rect(pos[0] - dX, pos[1] - dY - 30 - i*30, len/2, len/2);
													main.rect(pos[0] - dX + len, pos[1] - dY - 30 - i*30 - len/2, len/2, len/2);
													main.rect(pos[0] - dX + len, pos[1] - dY - 30 - i*30, len/2, len/2);
													main.textAlign(main.LEFT, main.TOP);
													main.fill(255);
													main.text((int)en.offensiveStr, pos[0] - dX, pos[1] - dY - 30 - i*30 - len/2);
													main.text((int)en.defensiveStr, pos[0] - dX, pos[1] - dY - 30 - i*30);
													main.text((int)en.rangedStr, pos[0] - dX + len, pos[1] - dY - 30 - i*30 - len/2);
													main.text((int)en.health, pos[0] - dX + len, pos[1] - dY - 30 - i*30);

													//image = EntityData.iconMap.get("CopperWeapons");
													//main.image(image, iX + len*0.6F, iY + len*0.6F, len*0.4F, len*0.4F);
												}
												else
												{
													iX = pos[0] - dX - len/2; iY = pos[1] - dY - 30 - i*30 - len/2;
													main.image(image, iX, iY, len, len);
												}
												main.popStyle();
												if (en.unitImprovement != null)
												{
													image = null; image = EntityData.iconMap.get(en.unitImprovement.name);
													if (image != null)
														main.image(image, iX + len*0.6F, iY, len*0.4F, len*0.4F);
												}
											}
										}
									}
									else
									{
										for (int i = t.occupants.size() - 1; i >= 0; i--)
										{
											GameEntity en = t.occupants.get(i);
											if (main.grid.civs[0].isWar(en.owner))
												main.fill(255,0,0);
											else
												main.fill(150,225,255);
											float iX = pos[0] - dX - len/2, iY = pos[1] - dY - 60 - i*10 - len/2;
											main.stroke(en.owner.r, en.owner.g, en.owner.b);
											main.rect(iX + len/4, iY + len/4, len/2, len/2);
											main.noStroke();
										}
									}
								}
							}
							if (main.tacticalView)
							{
								int len = 8;
								//main.fill(t.owner.r, t.owner.g, t.owner.b);
								//main.rect(pos[0] - dX - len/2, pos[1] - dY - len/2, len, len);
								//Replace with for loop //done
								if (requestFieldsUpdate)
								{
									//requestFieldsUpdate = false;
									main.newMenuSystem.largeFieldIcon(pos[0]-dX,pos[1]-dY + len*1.5F,t,(int)(len*1.5));
									if (Math.random() < 0.01)
										System.out.println(t.maxFields);
								}
								for (int i = 0; i <= 3; i++)
								{
									if (t.maxFields > i)
									{
										main.newMenuSystem.fieldIcon(pos[0]-dX,pos[1]-dY + len*1.5F,t,i,len,(int)(len*1.5F));
									}
								}
							}
						}
					}
				}
			}
		}
		if (requestFieldsUpdate) requestFieldsUpdate = false;

		main.textSize(12);

		if (mouseHighlighted != null && selected instanceof GameEntity)
		{
			GameEntity atk = (GameEntity)selected;
			if (mouseHighlighted.occupants.size() > 0)
			{
				BaseEntity def = mouseHighlighted.occupants.get(0);
				if (def == null && mouseHighlighted.improvement instanceof City)
					def = mouseHighlighted.improvement;
				if (atk.owner.isWar(def.owner))
				{
					if (atk.mode != 0)
					{
						//Preview of calculated damage
						int[] offensiveDamage = main.grid.conflictSystem.attackNoRandomness(atk.offensiveStr, def.defensiveStr);
						int[] defensiveDamage = main.grid.conflictSystem.attackNoRandomness(def.offensiveStr, atk.defensiveStr);

						//All numbers, great
						float iX = 150, iY = main.height - 150, len = 40;

						main.pushStyle();
						main.textAlign(main.CENTER, main.TOP);
						main.fill(0);
						main.rect(iX, iY - 30, 340, 30);
						main.fill(255);
						main.text(atk.name + " (" + atk.owner.name + ") vs " + def.name + " (" + def.name + ")", iX + 170, iY - 30);

						main.tint(atk.owner.r, atk.owner.g, atk.owner.b);
						unitStats(atk, iX, iY, len);

						main.fill(0);
						main.rect(iX, iY + len, 160, 150 - len);
						main.rect(iX + 180, iY + len, 160, 150 - len);

						main.fill(255);
						ArrayList<String> text1 = new ArrayList<String>();
						text1.add("If I attacked...");
						text1.add("Damage inflicted: " + offensiveDamage[0]);
						text1.add("Damage taken: " + offensiveDamage[1]);
						for (int i = 0; i < text1.size(); i++)
							main.text(text1.get(i), iX, iY + len + 14*i);

						if (def instanceof GameEntity)
						{
							main.tint(def.owner.r, def.owner.g, def.owner.b);
							unitStats((GameEntity)def, iX + 220, iY, len);

							ArrayList<String> text2 = new ArrayList<String>();
							text2.add("If I attacked...");
							text2.add("Damage inflicted: " + defensiveDamage[0]);
							text2.add("Damage taken: " + defensiveDamage[1]);
							for (int i = 0; i < text2.size(); i++)
								main.text(text2.get(i), iX + 180, iY + len + 14*i);
						}

						main.popStyle();

					}
				}
			}
		}
		//Show the possible tiles that a unit can move to
		//Make this a function to stop code repeats
		//System.out.println(movementChoices.size());
		for (int i = 0; i < movementChoices.size(); i++)
		{
			Tile t = movementChoices.get(i);
			//System.out.println((t.row - h.row - (mh.guiPositions.length-1)/2) + " " + (t.col - h.col + (mh.guiPositions[0].length-1)/2));
			float[] pos = mh.positionGui(t.col - h.col + (mh.guiPositions[0].length-1)/2, t.row - h.row + (mh.guiPositions.length-1)/2);
			if (pos != null && t != null)
			{
				if (t.biome == -1 && main.grid.adjacentLand(t.row, t.col).size() == 0 || 
						main.grid.civs[0].revealed[t.row][t.col] == 0 && !main.showAll) 
					continue;
				main.textAlign(main.CENTER);
				main.fill(255,0,0);
				float dX = main.width/2 - highlightDispX, dY = main.height/2 - highlightDispY;
				main.text("1", pos[0] - dX,pos[1] - dY);
			}
		}

		//Show the city queue food/metal menu and associated UI
		//More repeating code
		if (h != null)
		{
			for (int r = 0; r < mh.guiPositions.length; r++)
			{
				for (int c = 0; c < mh.guiPositions[0].length; c++)
				{
					float[] pos = mh.positionGui(r,c);
					if (pos != null)
					{
						int dC = r - (mh.guiPositions.length-1)/2;
						int dR = c - (mh.guiPositions[0].length-1)/2;
						float dX = main.width/2 - highlightDispX, dY = main.height/2 - highlightDispY;
						Tile t = main.grid.getTile(h.row + dR, h.col - dC);
						if (t != null)
						{
							if (t.improvement != null)
							{
								if (t.improvement instanceof City)
								{
									//TODO Show the city GUI/label
								}
							}
						}
					}
				}
			}
		}

		if (menus.get(2).active())
			menus.get(2).activate(false);

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
		textboxes.get(3).display.add(c.name + "; Health: " + c.health + "; Gold: " + c.gold + "; Research: " + c.research);
		//textboxes.get(3).display.add("Health: " + c.health);
		if (c.researchTech == null)
			textboxes.get(3).display.add("No research");
		else
			textboxes.get(3).display.add("Researching " + c.researchTech + " at " + (int)((c.researchProgress()*1000/1000)*100) + "%");

		//Manual textbox; does not use class
		main.fill(0);
		main.rect(0,0,main.width/6,50);
		main.textSize(20);
		main.fill(main.grid.civs[0].r, main.grid.civs[0].g, main.grid.civs[0].b);
		main.text(main.civChoice,main.width/6/2,25);
		int s = 5;
		main.rect(s, s, 50 - 2*s, 50 - 2*s);
		main.rect(main.width/6 - 50 + s, s, 50 - 2*s, 50 - 2*s);
		main.textSize(12);

		updateMessages();
		for (int menu = 0; menu < menus.size(); menu++)
		{
			displayMenu(menu);
		}

		for (int i = 0; i < textboxes.size(); i++)
		{
			TextBox b = textboxes.get(i);
			if (b.active)
			{
				main.fill(b.r, b.g, b.b, b.alpha);
				strokeTextbox(b.borderR, b.borderG, b.borderB);
				if (b.shape == 0)
					main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
				else if (b.shape == 1)
					main.ellipse(b.posX, b.posY, b.sizeX, b.sizeY);
				else
					System.out.println("Invalid button shape: " + b.shape);
				//main.textAlign(PApplet.LEFT, PApplet.UP);
				main.fill(255);
				if (i == 7) //Monospaced GUI?
				{
					main.pushStyle();
					main.textFont(main.dvs, 18);
					main.textAlign(main.LEFT, main.TOP);
					for (int j = 0; j < textboxes.get(7).display.size(); j++)
					{
						String stringy = textboxes.get(7).display.get(j);
						main.text(stringy, b.posX, b.posY + 15*j + 5);
					}
					main.popStyle();
				}
				else
				{
					main.textAlign(main.CENTER, main.TOP);
					displayText(b);
				}
				if (b.autoClear)
					b.display.clear(); //Clear them to be refilled next frame
			}
		}

		tooltip.active = false;
		TextBox hover = findButtonWithin(main.mouseX, main.mouseY);
		tooltip.active = false;
		if (hover != null)
		{
			if (hover.tooltip != null)
				if (hover.tooltip.size() > 0)
					if (!hover.tooltip.get(0).isEmpty())
					{
						//TODO: Word wrap if the text goes off the screen
						tooltip.active = true;
						int[] d = hover.dimTooltip();
						tooltip.sizeX = d[0];
						tooltip.sizeY = d[1];
						tooltip.posX = main.mouseX;
						tooltip.posY = main.mouseY;
						if (hover instanceof Button)
						{
							Button b = (Button)hover;
							if (b.menu != null)
								if (b.menu.name.equals("MainMenu") || 
										b.menu.name.equals("UnitMenu") || 
										b.menu.name.equals("TechMenu") || 
										b.menu.name.equals("CityMenu") ||
										b.menu.name.equals("CreateFieldMenu") ||
										b.menu instanceof TechMenu)
								{
									tooltip.posX = hover.posX + hover.sizeX;
									tooltip.posY = hover.posY;
									//System.out.println("---_>");
								}
						}
						main.fill(0);
						main.stroke(255);
						main.rect(tooltip.posX, tooltip.posY, tooltip.sizeX, tooltip.sizeY);
						main.fill(255);
						main.noStroke();
						main.textAlign(main.CENTER);
						//if (hover.tooltip.size() == 0)
						for (int i = 0; i < hover.tooltip.size(); i++)
							main.text(hover.tooltip.get(i), tooltip.posX + tooltip.sizeX/2, tooltip.posY + 10 + 14*i);
					}
		}
		else //Show the tooltip for a unit being hovered over
		{
			if (mouseHighlighted != null)
			{
				if (mouseHighlighted.occupants.size() > 0)
				{
					//if (tooltip.display.size() != 0) 
					{
						//if (!tooltip.display.get(0).equals(""))
						{
							tooltip.active = true;
							tooltip.posX = main.mouseX;
							tooltip.posY = main.mouseY;
							tooltip.dimTooltip(mouseHighlighted.occupants, mouseHighlighted.improvement);
							main.fill(0);
							main.stroke(255);
							main.rect(tooltip.posX, tooltip.posY, tooltip.sizeX, tooltip.sizeY);
							main.fill(255);
							main.noStroke();
							main.textAlign(main.CENTER);
							BaseEntity impr = mouseHighlighted.improvement;
							if (impr != null)
								main.text(impr.name + " (" + impr.owner + ")", tooltip.posX + tooltip.sizeX/2, tooltip.posY + 10);
							for (int i = 0; i < mouseHighlighted.occupants.size(); i++)
							{
								GameEntity en = mouseHighlighted.occupants.get(i);
								if (i != mouseHighlighted.occupants.size() - 1)
								main.text(en.name + "(" + en.owner + ")", tooltip.posX + tooltip.sizeX/2, tooltip.posY + tooltip.sizeY/2 + 14*i);
								else
								if (impr != null)
									main.text(en.name + " (" + en.owner + ")", tooltip.posX + tooltip.sizeX/2, tooltip.posY + 10 + 14*(i+1));
								else
									main.text(en.name + " (" + en.owner + ")", tooltip.posX + tooltip.sizeX/2, tooltip.posY + 10 + 14*i);
							}
						}
					}
				}
			}
		}
		
		//Update the rectangular regions where there will be no extra elements shown
		noOverlap.clear();
		for (int i = 0; i < menus.size(); i++)
		{
			if (menus.get(i).active())
			{
				for (int j = 0; j < menus.get(i).buttons.size(); j++)
				{
					TextBox t = menus.get(i).buttons.get(j);
					if (t.active && t.noOverlap)
						noOverlap.add(t);
				}
			}
		}
		for (int i = 0; i < textboxes.size(); i++)
		{
			TextBox t = textboxes.get(i);
			if (t.active && t.noOverlap)
				noOverlap.add(t);
		}

		menuActivated = false;
		for (int menu = 0; menu < menus.size(); menu++)
		{
			if (!main.enabled) break;
			if (menus.get(menu).active())
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
								activeMenus[j] = menus.get(j).active();
							}
							menus.get(menu).pass(activeMenus, clicks.get(i).mouseX, clicks.get(i).mouseY);
						}
					}
				}
				menus.get(menu).origPosIfNoMouse();
				for (int i = 0; i < menus.get(menu).buttons.size(); i++)
				{
					main.fill(0);
					Button b = menus.get(menu).buttons.get(i);
					main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
					main.textAlign(PApplet.CENTER, PApplet.CENTER);
					main.fill(255);
					main.text(b.display, b.posX + b.sizeX/2, b.posY + b.sizeY/2);
				}
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
			if (menus.get(menu).requestUpdate && menu != 0 && menu != 14)
			{
				menus.get(menu).requestUpdate = false;
				//System.out.println("Clear shortcuts");
				shortcuts = new Button[10];
				//System.out.println(menu);
				if (menus.get(menu).active() && !menus.get(menu).noShortcuts)
					makeShortcut(menus.get(menu));
			}
		}
		if (techMenu.active())
			makeShortcut(techMenu);
		clicks.clear();

		menuHighlighted = false;
		for (int index = 0; index < menus.size(); index++)
			if (menus.get(index).active())
				if (menus.get(index).within(main.mouseX, main.mouseY) != null)
				{
					menuHighlighted = true;
					break;
				}

		if (main.testing)
		{
			MouseHelper mouseh = main.inputSystem.mouseHelper;
			for (int i = 0; i < mouseh.horizonLines.size(); i++)
			{
				main.strokeWeight(5);
				main.stroke(255,0,0);
				MouseHelper.Line l = mouseh.horizonLines.get(i);
				main.line((float)l.xPoint - 1000, (float)l.yPoint - 1000*l.slope, (float)l.xPoint + 1000, (float)l.yPoint + 1000*l.slope);
			}
			for (int i = 0; i < mouseh.vertLines.size(); i++)
			{
				main.strokeWeight(5);
				main.stroke(255,0,0);
				MouseHelper.Line l = mouseh.vertLines.get(i);
				main.line((float)l.xPoint - 200, (float)l.yPoint - 200*l.slope, (float)l.xPoint + 200, (float)l.yPoint + 200*l.slope);
			}
			for (int i = 0; i < mouseh.intersections.length; i++)
			{
				for (int j = 0; j < mouseh.intersections[0].length; j++)
				{
					Point p = mouseh.intersections[i][j];
					main.fill(0);
					main.rect(p.x, p.y, 10, 10);
				}
			}
		}
		MouseHelper mouseHelper = main.inputSystem.mouseHelper;
		for (int row = 0; row < mouseHelper.shapes.length; row++)
		{
			for (int col = 0; col < mouseHelper.shapes[0].length; col++)
			{
				//if (r != activeX || c != activeY) continue;
				Shape shape = mouseHelper.shapes[row][col];
				//if (s == null) continue;
				main.fill(150*col/15,225*row/15,255*row/15);
				main.beginShape(main.QUADS);
				for (int i = 0; i < shape.x.length; i++)
				{
					main.vertex(shape.x[i],shape.y[i]);
				}
				main.vertex(shape.x[0],shape.y[0]);
				main.endShape();
			}
		}
		MouseHelper mouseh = main.inputSystem.mouseHelper;
		for (int i = 0; i < mouseh.rHorizonLines.size(); i++)
		{
			main.strokeWeight(5);
			main.stroke(255,0,0);
			MouseHelper.Line l = mouseh.rHorizonLines.get(i);
			main.line(0F, (float)l.yPoint - 200*l.slope, 1000F, (float)l.yPoint + 200*l.slope);
		}
		for (int i = 0; i < mouseh.rVertLines.size(); i++)
		{
			main.strokeWeight(5);
			main.stroke(255,0,0);
			MouseHelper.Line l = mouseh.rVertLines.get(i);
			main.line((float)l.xPoint - 200, (float)l.yPoint - 200*l.slope, (float)l.xPoint + 200, (float)l.yPoint + 200*l.slope);
		}
		for (int i = 0; i < mouseh.guiPositions.length; i++)
		{
			for (int j = 0; j < mouseh.guiPositions[0].length; j++)
			{
				Point p = mouseh.guiPositions[i][j];
				main.fill(0);
				main.rect(p.x, p.y, 10, 10);
			}
		}

		main.strokeWeight(1);*/
	}

	//TODO: Possibly sort shortcuts; higher buttons get lower numbers for shortcuts
	public void makeShortcut(Menu menu)
	{
		int iter = 1;
		for (int i = 0; i < menu.buttons.size(); i++)
			//for (int i = menus.get(menu).buttons.size() - 1; i >= 0; i--)
		{
			//if (i >= menus.get(menu).buttons.size()) break;
			TextBox b = menu.buttons.get(i);
			if (b instanceof Button && b.shortcut)
			{
				shortcuts[iter] = (Button)b;
				if (iter == 9) //Loop from 1 to 9 to 0 for shortcut keys
					iter = 0;
				else if (iter == 0)
					break;
				else
					iter++;
			}
			//System.out.println("Assign shortcut " + iter);
		}
	}

	public void displayMenu(int menu)
	{
		/*if (menus.get(menu).active())
		{
			if (menus.get(menu) instanceof TechMenu)
			{
				//Display tech tree
				main.pushStyle();
				main.strokeWeight(3);
				//System.out.println("yaaaa");
				main.stroke(255,255,255,50);
				for (int i = 0; i < techMenu.lines.size(); i++)
				{
					main.line(techMenu.lines.get(i).x1, techMenu.lines.get(i).y1, techMenu.lines.get(i).x2, techMenu.lines.get(i).y2);
				}
				main.fill(255,255,255,255); main.noStroke(); //Reset alpha
				for (int i = 0; i < techMenu.buttons.size(); i++)
				{
					TextBox b = techMenu.buttons.get(i);
					if (b.active)
					{
						main.fill(b.r, b.g, b.b);
						//main.stroke(b.borderR, b.borderG, b.borderB);
						strokeTextbox(b.borderR, b.borderG, b.borderB);
						if (b.shape == 0)
							main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
						else if (b.shape == 1)
							main.ellipse(b.posX, b.posY, b.sizeX, b.sizeY);
						else
							System.out.println("Invalid button shape: " + b.shape);
						main.textAlign(PApplet.CENTER, PApplet.CENTER);
						main.fill(255);
						displayText(b);
						main.fill(255,0,0);
						//System.out.println(b.display.get(0) + ": " + b.posX + " " + b.posY + " " + b.sizeX + " " + b.sizeY);
						shortcutText(b);
					}
				}
				main.popStyle();
			}
			else
			{
				main.strokeWeight(1);
				//System.out.println(menu + " " + menus.get(menu).active);
				for (int i = 0; i < menus.get(menu).buttons.size(); i++)
				{
					TextBox b = menus.get(menu).buttons.get(i);
					if (b.active)
					{
						if (b instanceof ImageBox)
						{
							ImageBox img = (ImageBox)b;
							main.tint(img.tintR, img.tintG, img.tintB);
							if (img.image != null)
								main.image(img.image, img.posX, img.posY, img.sizeX, img.sizeY);
							else 
								System.out.println("Invalid image: " + img.imageString);
						}
						else
						{
							main.fill(b.r, b.g, b.b, b.alpha);
							//main.stroke(b.borderR, b.borderG, b.borderB);
							if (b.shape == 0)
								main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
							else if (b.shape == 1)
								main.ellipse(b.posX, b.posY, b.sizeX, b.sizeY);
							else
								System.out.println("Invalid button shape: " + b.shape);
							main.textAlign(PApplet.CENTER, PApplet.CENTER);
							main.fill(255);
							displayText(b);
							main.fill(255,0,0);
							if (menu == 2) //If queuing
							{
								if (b instanceof Button)
								{
									Button button = (Button)b;
									if (button.command.contains("queue"))
									{
										//System.out.println(button.command.substring(5));
										PImage image = EntityData.iconMap.get(button.command.substring(5));
										if (image != null)
										{
											main.tint(main.grid.civs[0].r, main.grid.civs[0].g, main.grid.civs[0].b, 255);
											main.image(image, b.posX, b.posY, b.sizeY, b.sizeY);
										}
									}
								}
							}
							else if (menu == 4) //Unit improvements
							{
								if (b instanceof Button)
								{
									Button button = (Button)b;
									int index = button.command.indexOf('/');
									if (index != -1)
									{
										//System.out.println(button.command.substring(5));
										PImage image = EntityData.iconMap.get(button.command.substring(index+1));
										if (image != null)
											if (image instanceof ColorImage)
												main.image(image, b.posX, b.posY, b.sizeY, b.sizeY);
									}
								}
							}
							shortcutText(b);
						}
					}
				}
			}
		}
		main.hint(main.DISABLE_DEPTH_TEST);*/
	}
	
	/*private void strokeTextbox(float r, float g, float b)
	{
		if (r == -1 || g == -1 || b == -1)
			main.noStroke();
		else
			main.stroke(r,g,b);
	}
	
	private void shortcutText(TextBox b)
	{
		for (int j = 0; j < shortcuts.length; j++)
		{
			//System.out.println(shortcuts[j]); 
			if (shortcuts[j] != null)
				if (shortcuts[j].equals(b))
				{
					main.text("[" + j + "]", b.posX + b.sizeX*0.9F, b.posY + b.sizeY/2);
					//System.out.println("Text");
				}
		}
	}

	public void unitStats(GameEntity en, float iX, float iY, float len)
	{
		main.image(EntityData.iconMap.get(en.name), iX, iY, len, len);
		main.image(EntityData.iconMap.get("attack"), iX + len, iY, len/2, len/2);
		main.image(EntityData.iconMap.get("defense"), iX + len, iY + len/2, len/2, len/2);
		main.image(EntityData.iconMap.get("ranged"), iX + len*2, iY, len/2, len/2);
		main.image(EntityData.iconMap.get("health"), iX + len*2, iY + len/2, len/2, len/2);

		main.textAlign(main.LEFT, main.TOP);
		main.fill(255);
		main.text((int)en.offensiveStr, iX + len/2, iY + len);
		main.text((int)en.defensiveStr, iX + len/2, iY + len*3/2);
		main.text((int)en.rangedStr, iX + len*3/2, iY + len);
		main.text((int)en.health, iX + len*3/2, iY + len*3/2);

		main.fill(0);
		main.rect(iX + 3*len/2, iY, len/2, len/2);
		main.rect(iX + 3*len/2, iY + len/2, len/2, len/2);
		main.rect(iX + 5*len/2, iY, len/2, len/2);
		main.rect(iX + 5*len/2, iY + len/2, len/2, len/2);
		main.textAlign(main.LEFT, main.TOP);
		main.fill(255);
		main.text((int)en.offensiveStr, iX + 3*len/2, iY);
		main.text((int)en.defensiveStr, iX + 3*len/2, iY + len/2);
		main.text((int)en.rangedStr, iX + 5*len/2, iY);
		main.text((int)en.health, iX + 5*len/2, iY + len/2);
	}

	public ArrayList<PImage> icon(Tile t)
	{
		ArrayList<PImage> images = new ArrayList<PImage>();
		if (t == null) return images;
		for (int i = 0; i < t.occupants.size(); i++)
			images.add(EntityData.iconMap.get(t.occupants.get(i).name));
		return images;
	}

	public void minimapFill(Tile t)
	{
		if (t == null)
			main.fill(0);
		else
		{
			if (main.grid.civs[0].revealed[t.row][t.col] == 0 && !main.showAll)
				main.fill(0);
			else
			{
				if (t.biome != -1)
				{
					if (t.owner != null)
						main.fill(t.owner.r,t.owner.g,t.owner.b);
					else if (t.occupants.size() > 0)
					{
						GameEntity en = t.occupants.get(0);
						main.fill(en.owner.r, en.owner.g, en.owner.b);
					}
					else
						main.fill(150);
				}
				else
					main.fill(150,225,255);
			}
		}
	}

	public void displayText(TextBox b)
	{
		for (int j = 0; j < b.display.size(); j++)
		{
			String text = b.display.get(j);
			if (text == null) continue;
			do
			{
				int index1 = text.indexOf("<!"), index2 = text.indexOf("!>");
				if (index1 != -1 && index2 != -1)
				{
					System.out.println((float)index1/(float)text.length()*b.sizeX);
					System.out.println(index1 + " " + text.length() + " " + b.sizeX + " " + ((index1 - text.length()/2F)*10));
					PImage img = EntityData.iconMap.get(text.substring(index1+2, index2));
					main.imageMode(main.CENTER);
					main.fill(255,0,0);
					main.rect(b.posX + b.sizeX/2 + (index1 - text.length()/2F)*3, b.posY + 10 + j*15, 4, 4);
					main.fill(255);
					main.image(img, b.posX + b.sizeX/2 + (index1 - text.length()/2F)*5, b.posY + 10 + j*15, 10, 10);
					main.imageMode(main.CORNER);
					String temp = text.substring(0,index1) + new String("        ") + text.substring(index2+2);
					text = temp;
				}
				else
					break;
			} while (true);
			main.textAlign(main.CENTER, main.CENTER);
			main.text(text, b.posX + b.sizeX/2, b.posY + 10 + j*15);
		}
	}*/

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
		if (command.equals("")) return true;
		System.out.println(command);
		GameEntity en = null;
		menuActivated = true;
		if (selected != null) 
		{
			if (selected instanceof GameEntity)
				en = (GameEntity)selected;
		}
		if (command.contains("unit")) //Specific unit orders
		{
			if (selected == null)
				return false;
		}
		if (command.equals("exitgame"))
		{
			System.exit(0);
			return false;
		}
		else if (command.equals("close"))
		{
			//Replace with a loop later
			//done
			closeMenus();
			//select(null);
		}
		else if (command.equals("markTile"))
		{
			System.out.println("marked tile");
			if (mouseHighlighted != null)
				markedTiles[mouseHighlighted.row][mouseHighlighted.col] = !markedTiles[mouseHighlighted.row][mouseHighlighted.col];
			menus.get(0).findButtonByCommand("markTile").activate(false);
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
				command.equals("civic") ||
				command.equals("techsweb")
				)
		{
			closeMenus();
			if (command.equals("info"))
			{
				info = !info;
			}
			else if (command.equals("minimap"))
			{
				minimapMode++;
				if (minimapMode > 2) minimapMode = 0;
			}
			else if (command.equals("loadout"))
			{
				/*if (menus.get(3).active)
				{
					menus.get(3).activate(false);
				}
				menus.get(4).active = !menus.get(4).active;*/
				menus.get(3).activate(true);
			}
			else if (command.contains("loadoutDisplay"))
			{
				//loadout = false;
				updateLoadoutDisplay(command.substring(14));
				menus.get(4).activate(true);
			}
			else if (command.equals("stats"))
			{
				updateCivStats();
				//ledgerMenu = true;
				textboxes.get(4).activate(true);
				menus.get(0).findButtonByCommand("stats").lock = textboxes.get(4).active;
			}
			else if (command.equals("continue"))
			{
				main.grid.civs[0].observe = true;
				menus.get(6).activate(false);
			}
			else if (command.equals("techs"))
			{
				/*displayTechMenu(main.grid.civs[0]);
				//menus.get(5).active = !menus.get(5).active;
				menus.get(5).activate(true);
				//menus.get(5).active = !menus.get(5).active;
				 */			
				techMenu.setupButtons();
				techMenu.activate(true);
			}
			else if (command.equals("techsweb"))
			{
				techMenu.setupButtons();
				techMenu.activate(true);
			}
			else if (command.equals("encyclopedia"))
			{
				menus.get(7).activate(true);
			}
			else if (command.contains("diplomacy"))
			{
				menus.get(8).activate(false);
				menus.get(9).activate(true);
				Civilization civ = main.grid.civs[Integer.parseInt(command.substring(9))];
				updateDiplomacyMenu(civ);
			}
			else if (command.equals("log"))
			{
				textboxes.get(2).activate(false);
				menus.get(10).activate(true);
				updateMessages();
			}
			else if (command.equals("relations"))
			{
				menus.get(11).activate(true);
				pivot = main.grid.civs[0];
				updateRelations();
			}
			else if (command.equals("civic"))
			{
				menus.get(12).activate(true);
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
			menus.get(4).activate(false); //Allow player to stay in menu?
			return false;
		}
		else if (command.equals("buildFarm"))
		{
			//Recycled code
			if (en.location.resource == 1 || en.location.resource == 2)
			{
				EntityData.queueTileImprovement(en, "Farm");
			}
			else if (en.location.biome >= 3 && en.location.biome <= 6 && en.location.grid.irrigated(en.location.row, en.location.col))
			{
				EntityData.queueTileImprovement(en, "Farm");
			}
			if (en.queue != null && !en.queue.isEmpty())
				en.queueTurns = Math.max(1,(int)(en.queueTurns*((Worker)en).workTime));
		}
		else if (command.equals("buildMine"))
		{
			if (en.location.shape == 2)
			{
				EntityData.queueTileImprovement(en, "Mine");
			}
			else if (en.location.resource >= 20 && en.location.resource <= 22)
			{
				EntityData.queueTileImprovement(en, "Mine");
			}
			else if (en.location.shape == 1)
			{
				if (en.location.biome >= 0 && en.location.biome <= 3)
				{
					EntityData.queueTileImprovement(en, "Mine");
				}
			}
			if (en.queue != null && !en.queue.isEmpty())
				en.queueTurns = Math.max(1,(int)(en.queueTurns*((Worker)en).workTime));
		}
		else if (command.equals("buildRoad"))
		{
			EntityData.queueTileImprovement(en, "Road");
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
			//((Warrior)selected).action = 0;
			//selected.playerTick();
		}
		else if (command.equals("unitSettle"))
		{
			if (selected != null) 
				if (!((Settler)selected).settle())
					message("Cannot settle here.");
		}
		else if (command.equals("stack"))
		{
			if (stack.size() > 0)
				stack.clear();
			else
			{
				if (selected != null)
					for (int i = 0; i < selected.location.occupants.size(); i++)
					{
						GameEntity entity = selected.location.occupants.get(i);
						entity.sleep = false;
						stack.add(entity);
					}
			}
		}
		else if (command.equals("unitSleep"))
		{
			if (selected != null)
				selected.sleep = true;
			select(null);
		}
		else if (command.contains("unitCaravan"))
		{
			int index = Integer.parseInt(command.substring(7));
			((Caravan)selected).setRoute(selected.owner.cities.get(index));
		}
		else if (command.equals("unitSkipTurn"))
		{
			selected.action = 0;
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
		else if (command.contains("qfield"))
		{
			candidateField = command.substring(6);
			candidateCityField = (City)selected;
		}
		else if (command.equals("razeCity"))
		{
			((City)selected).raze = true;
		}

		else if (command.contains("fieldMenu"))
		{
			int index = command.indexOf(',');
			int r = Integer.parseInt(command.substring(9,index)), c = Integer.parseInt(command.substring(index+1));
			//updateFieldMenu(main.grid.getTile(r,c));
		}
		/*else if (command.contains("editField"))
		{
			int n = Integer.parseInt(command.substring(9));
			updateCreateFieldMenu(editingFields, n);
		}
		else if (command.contains("makeField"))
		{
			int index = command.indexOf(',');
			int n = Integer.parseInt(command.substring(9,index));
			Field f = EntityData.getField(command.substring(index+1));
			editingFields = null;
		}*/
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
			Tech t = main.grid.civs[0].techTree.researched(command.substring(8));
			if (t.requisite != null && t.requisite.researched())
			{
				main.grid.civs[0].researchTech = command.substring(8);
				menus.get(5).activate(false); techMenu.activate(false);
			} 
			else if (t.alternative != null && t.alternative.researched())
			{
				main.grid.civs[0].researchTech = command.substring(8);
				menus.get(5).activate(false); techMenu.activate(false);
			} 
			else
				message(t.name + " is not unlocked.");
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
			main.menuSystem.select(null);
			//main.inputSystem.timeSelection();
		}
		return true;
	}

	public void executeShortcut(int n)
	{
		if (shortcuts[n] != null)
		{
			executeAction(shortcuts[n].command);
		}
	}

	public void closeMenus()
	{
		select(null);
		textboxes.get(2).activate(true);
		info = false;
		minimapMode = 0;
		menus.get(3).activate(false);
		menus.get(4).activate(false);
		textboxes.get(4).activate(false);
		menus.get(5).activate(false);
		for (int i = 7; i <= 13; i++)
			menus.get(i).activate(false);
		menus.get(15).activate(false);
		menus.get(16).activate(false);
		techMenu.activate(false);
		//Clear all but the main menu and encyclopedia
		//for (int i = 1; i < menus.size(); i++)
	}

	public TextBox findButtonWithin(float mouseX, float mouseY)
	{
		for (int i = 0; i < menus.size(); i++)
		{
			Menu m = menus.get(i);
			if (m.active())
			{
				for (int j = 0; j < m.buttons.size(); j++)
				{
					TextBox b = m.within(mouseX, mouseY);
					if (b != null)
						return b;
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
				if (message == null) continue;
				if (messages.size() == 0) messages.add(message);
				if (!messages.get(0).equals(message))
					messages.add(0,message);
			}
			if (!main.grid.civs[0].observe) //Do not shake the GUI if player is not alive
			{
				//TODO: Revise
				/*textboxes.get(2).moveDis(0,-5,2);
				for (int i = 0; i < 10; i++)
					textboxes.get(2).moveDis(0,(10-i)*(int)Math.pow(-1,i),2);
				textboxes.get(2).moveDis(0,5,2);
				textboxes.get(2).orderOriginal(false);*/
			}
		}
	}

	//Send a message from tutorial level
	public void messageT(String... newMessages)
	{
		if (newMessages.length == 0) return;
		for (int i = newMessages.length - 1; i >= 0; i--)
		{
			String message = newMessages[i];
			if (messages.size() == 0) messages.add(message);
			if (!messages.get(0).equals(message))
				messages.add(0,message);
		}
		if (!main.grid.civs[0].observe) //Do not shake the GUI if player is not alive
		{
			//TODO: Revise
			/*textboxes.get(2).moveDis(0,-5,2);
			for (int i = 0; i < 10; i++)
				textboxes.get(2).moveDis(0,(10-i)*(int)Math.pow(-1,i),2);
			textboxes.get(2).moveDis(0,5,2);
			textboxes.get(2).orderOriginal(false);*/
		}
	}

	//Show all the messages on the menu with index 10
	public void updateMessages()
	{
		menus.get(10).buttons.clear();
		for (int i = 0; i < messages.size(); i++)
		{
			TextBox msg = new TextBox(loader.loadTexture("partTexture"), messages.get(i), main.width*4.5F/6, 30 + 14*i, main.width*1.5F/6, 14);
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
				TextBox b = menus.get(i).buttons.get(j);
				if (b.autoClear)
				{
					//b.orderOriginal(true);
				}
			}
		}
	}

	//Will always refer to the player's tech tree
	public void displayTechMenu(Civilization civ)
	{
		menus.get(5).activate(true);
		menus.get(5).buttons.clear();

		ArrayList<String> techNames = civ.techTree.findCandidates();
		float disp = techNames.size()*30;
		for (int i = 0; i < techNames.size(); i++)
		{
			String s = techNames.get(i);
			Tech t = civ.techTree.researched(s);
			int turns = calcQueueTurnsTech(civ, t);
			String name = turns != -1 ? s + " <" + turns + ">" : s + " <N/A>";
			Button b = (Button)menus.get(5).addButton("research" + s, name, "Research " + s + ".", 0, main.height*5/6 - disp + 30*i, main.width*1/6, 30);
			b.lock = true;
			b.tooltip.clear();
			if (turns != -1)
				b.tooltip.add("Estimated research time: " + turns + " turns");
			else
				b.tooltip.add("Estimated research time: N/A");
			b.tooltip.add(t.totalR + " research out of " + t.requiredR + "; " + (int)((float)t.totalR/(float)t.requiredR*100) + "%");
			b.tooltip.add("Requires " + t.requisite.name);
			String techString = "";
			for (int j = 0; j < t.techs.length; j++)
				techString += t.techs[j].name + ", ";
			if (t.techs.length != 0)
				b.tooltip.add("Leads to " + techString.substring(0, techString.length()-2));
			b.tooltip.add("Unlocks " + t.unlockString());
			//menus.get(5).addButton("research" + s, s, "", main.width/3F, (float)main.height*2F/6F + 60*i, 200, 50);
		}
	}

	/*public void displayCity(City citySelected)
	{
		//Selection vs highlight
		if (citySelected.equals(selected))
		{
			menus.get(2).activate(true);
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
			buildingString = buildingString.substring(0,buildingString.length()-2); //Remove a trailing comma
		}
		else
			buildingString = "No buildings.";
		temp.add(buildingString);
		if (citySelected.queueFood > 0 || citySelected.queueMetal > 0)
		{
			temp.add(calcQueueTurns(citySelected));
		}
		else
		{
			temp.add("Nothing queued.");
		}
	}*/

	public int calcQueueTurnsInt(City citySelected, String name)
	{
		int[] t = citySelected.quickEval();
		float[] cost = EntityData.getCost(name);
		//Division by zero errors
		if ((t[0] == 0 && cost[0] > 0) || (t[2] == 0 && cost[2] > 0) || (t[0] == 0 && t[2] == 0))
			return -1;
		else
		{
			//System.out.println(t[0] + " " + t[2]);
			float turns;
			if (t[0] == 0)
				turns = cost[2]/(t[2]) + 1;
			else if (t[2] == 0)
				turns = cost[0]/(t[0]) + 1;
			else
			{
				turns = Math.max(
						cost[0]/(t[0]) + 1,
						cost[2]/(t[2]) + 1
						);
			}
			return (int)turns;
		}
	}

	public static int calcQueueTurnsTech(Civilization civ, Tech tech)
	{
		int research = 0;
		for (int i = 0; i < civ.cities.size(); i++)
		{
			int[] t = civ.cities.get(i).quickEval();
			research += t[3];
		}
		if (research == 0) return -1;
		return (int)((tech.requiredR - tech.totalR)/(float)research + 1);
	}

	public String calcQueueTurns(City citySelected)
	{
		int[] t = citySelected.quickEval();
		//Division by zero errors
		if (t[0] == 0 && citySelected.queueFood > 0)
		{
			return new String("No food production, will not finish.");
		}
		else if (t[2] == 0 && citySelected.queueMetal > 0)
		{
			return new String("No metal production, will not finish.");
		}
		else if (t[0] == 0 && t[2] == 0)
		{
			//return new String("Neither food nor metal production");
			return new String("Will not finish.");
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
				return new String("Queued " + citySelected.queue + " for " + turns + " turn.");
			else
				return new String("Queued " + citySelected.queue + " for " + turns + " turns.");
		}
	}

	//Update the ledger
	public void updateCivStats()
	{
		textboxes.get(4).display.clear();
		textboxes.get(4).display.add("You:");
		Civilization c = main.grid.civs[0];
		String s = c.name + "; Health: " + c.health + "; Gold: " + c.gold + "; Research: " + c.research;
		textboxes.get(4).display.add(s);
		textboxes.get(4).display.add("");

		textboxes.get(4).display.add("Civilizations:");
		//Menu 8 buttons were moved to menu 11
		//menus.get(8).activate(false);
		for (int i = 1; i < main.grid.civs.length; i++)
		{
			c = main.grid.civs[i];
			s = c.name + "; Health: " + c.health + "; Gold: " + c.gold + "; Research: " + c.research + "; Relations: " + main.grid.civs[0].opinions[i];
			textboxes.get(4).display.add(s);
			//menus.get(8).addButton("diplomacy"+i, "Talk", "Conduct diplomacy with " + c.name + ".", 600, 190+60+15*(i-1), 90, 15);
		}
		textboxes.get(4).size.y = (main.grid.civs.length - 1 + 4)*15 + 15;
		//menus.get(8).activate(true);
		//100,190,500,250
	}

	//TODO: Battlefield perspective
	/*public void updateBattlePerspective()
	{

	}*/

	//Choose which buttons to show depending on unit (e.g. only settler can settle)
	public void updateUnitMenu(GameEntity en)
	{
		float height = 20;

		menus.get(1).buttons.clear();
		//int n = 0;
		menus.get(1).addButton("unitKill", "Destroy", "Destroy this unit.", 0, main.height*5/6 + 30, main.width*1/6, 30);
		menus.get(1).addButton("unitSkipTurn", "Skip Turn", "Do nothing this turn.", 0, main.height*5/6 + 30, main.width*1/6, 30);
		menus.get(1).addButton("unitSleep", "Sleep", "This unit will be inactive until you select it again.", 0, main.height*5/6 + 30, main.width*1/6, 30);
		if (stack.size() == 0)
			menus.get(1).addButton("stack", "Create Stack", "Group a set of units together that can be moved.", 0, main.height*5/6 + 30, main.width*1/6, 30);
		else
			menus.get(1).addButton("stack", "Separate Stack", "Make multiple units out of the stack.", 0, main.height*5/6 + 30, main.width*1/6, 30);

		if (en.name.equals("Settler"))
		{
			menus.get(1).addButton("unitSettle", "Settle", "Settle a city here.", 0, main.height*5/6 + 30, main.width*1/6, 30);

		}
		else if (en.name.equals("Warrior"))
		{
			menus.get(1).addButton("unitRaze", "Attack", "Attack the improvement here.", 0, main.height*5/6 + 30, main.width*1/6, 30);

		}
		else if (en.name.equals("Worker"))
		{
			ArrayList<String> units = main.grid.civs[0].techTree.allowedTileImprovements;
			for (int i = 0; i < units.size(); i++)
			{
				Button b = (Button)menus.get(1).addButton("build"+units.get(i), units.get(i), "Construct " + units.get(i) + " here.", 0, main.height*5/6 + 30, main.width*1/6, 30);
				b.tooltip.clear();
				int turns = EntityData.tileImprovementTime(en, units.get(i));
				if (turns != -1) b.tooltip.add("Estimated build time: " + turns + " turns");
				else b.tooltip.add("Estimated build time: N/A");
				double[] yieldBefore = City.staticEval(en.location), yieldAfter = City.staticEval(en.location, units.get(i));
				double[] temp = new double[]{
						yieldAfter[0] - yieldBefore[0], 
						yieldAfter[1] - yieldBefore[1], 
						yieldAfter[2] - yieldBefore[2],
						yieldAfter[3] - yieldBefore[3]
				};
				String[] names = new String[]{"food", "gold", "metal", "research"};
				for (int j = 0; j < temp.length; j++)
				{
					if (temp[j] != 0)
					{
						if (temp[j] > 0)
							b.tooltip.add("+" + temp[j] + " " + names[j]);
						else
							b.tooltip.add("-" + temp[j] + " " + names[j]);
					}
				}
			}
			if (!en.location.road)
			{
				Button b = (Button)menus.get(1).addButton("buildRoad", "Road", "Construct a road, to expand your civilization's network.", 
						0, main.height*5/6 + 30, main.width*1/6, 30);
				b.tooltip.add("Roads allow for increased movement,");
				b.tooltip.add("and connect resources and cities.");
				b.dimTooltip();
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
					menus.get(1).addButton("unitCaravan"+i, "Caravan"+c.name, "Establish a trade route.", 0, main.height*5/6 + 30, main.width*1/6, 30);

				}
			}
		}

		if (en.mode == 1 && en.rangedStr > 0)
		{
			menus.get(1).addButton("rangedMode", "Ranged", "Allow this unit to use ranged attacks.", 0, main.height*5/6 + 30, main.width*1/6, 30);

		}
		else if (en.mode == 2 && en.offensiveStr > 0)
		{
			menus.get(1).addButton("meleeMode", "Melee", "Allow this unit to use melee attacks.", 0, main.height*5/6 + 30, main.width*1/6, 30);

		}

		for (int i = 0; i < menus.get(1).buttons.size(); i++)
		{
			TextBox b = menus.get(1).buttons.get(i);
			b.move(b.pos.x, main.height*5/6 - (menus.get(1).buttons.size()+2)*height + i*height); //Shift the buttons to their proper place
			b.size.x = 150; b.size.y = height;
			/*b.origSizeX = 150; b.origSizeY = height;
			b.origX = b.posX; b.origY = b.posY;*/
		}

		ImageBox img = new ImageBox(en.name,0,main.height*5/6,main.height/6,main.height/6);
		img.tint(en.owner.r, en.owner.g, en.owner.b);
		menus.get(1).buttons.add(img);
		
		//TODO: Add encyclopedia entries
		//TextBox b = menus.get(1).addButton("encyclopedia"+en.name, en.name, "Encyclopedia entry for "+en.name+" >",0,main.height*5/6-height,main.height/6,height);
		TextBox b = menus.get(1).addButton("", en.name, "Encyclopedia entry for "+en.name+" >",0,main.height*5/6-height,main.height/6,height);
		b.shortcut = false;
		//System.out.println(menus.get(1).buttons.size());
	}

	//Choose which builds to allow i.e. which can be queued up in the city (factor in techs later)
	public void updateCity(City c)
	{
		menus.get(2).buttons.clear();

		TextBox button = menus.get(2).addButton("unitSleep", "Sleep", "Do not queue and produce anything with this city.", 0, 0, 0, 0);
		button.tooltip.add("Not recommended.");
		if (c.takeover > 0)
		{
			menus.get(2).addButton("razeCity", "Raze", "Destroy the city, one citizen at a time.", main.width/3F, (float)main.height*5F/6F + 60, 50, 50);
		}

		float height = 20;
		float disp = c.owner.techTree.allowedUnits.size() + c.owner.techTree.allowedCityImprovements.size() + 1; disp *= height;

		ArrayList<String> units = c.owner.techTree.allowedUnits;
		for (int i = 0; i < units.size(); i++)
		{
			unitButton(c, units.get(i), true);
		}

		ArrayList<String> buildings = c.owner.techTree.allowedCityImprovements(c);
		for (int i = 0; i < buildings.size(); i++)
		{
			buildingButton(c, buildings.get(i), true);
		}

		ArrayList<String> fields = c.owner.techTree.allowedFields;
		for (int i = 0; i < fields.size(); i++)
		{
			fieldButton(c, fields.get(i), true);
		}

		ArrayList<String> potential = c.owner.techTree.findUnlockables();
		for (int i = 0; i < potential.size(); i++)
		{
			BaseEntity example = EntityData.get(potential.get(i));
			Improvement impr = EntityData.cityImprovementMap.get(potential.get(i));
			Improvement impr2 = EntityData.getField(potential.get(i));
			if (example != null && EntityData.tileEntityMap.get(potential.get(i)) == null) //Make sure it's not a farm or something
			{
				unitButton(c, potential.get(i), false);
			}
			else if (impr != null)
			{
				buildingButton(c, potential.get(i), false);
			}
			else if (impr2 != null)
			{
				fieldButton(c, potential.get(i), false);
			}
			else
			{
				//do nothing
			}
		}
		//menus.get(2).addButton("queueSettler", "Settler", main.width/3F, (float)main.height*5F/6F, 50, 50);
		//menus.get(2).addButton("queueWorker", "Worker", main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
		//menus.get(2).addButton("queueWarrior", "Warrior", main.width/3F + 120, (float)main.height*5F/6F, 50, 50);

		menus.get(2).addButton("addAdmin", "Admin+", "Convert one citizen to admin.", main.width/6F, (float)main.height*5F/6F, 50, 50).shortcut = false;
		menus.get(2).addButton("subAdmin", "Admin-", "Revert one admin to citizen.", main.width/6F, (float)main.height*5F/6F + 60, 50, 50).shortcut = false;
		menus.get(2).addButton("addArtist", "Artist+", "Convert one citizen to artist.", main.width/6F + 60, (float)main.height*5F/6F, 50, 50).shortcut = false;
		menus.get(2).addButton("subArtist", "Artist-", "Revert one artist to citizen.", main.width/6F + 60, (float)main.height*5F/6F + 60, 50, 50).shortcut = false;
		menus.get(2).addButton("addSci", "Sci+", "Convert one citizen to scientist.", main.width/6F + 120, (float)main.height*5F/6F, 50, 50).shortcut = false;
		menus.get(2).addButton("subSci", "Sci-", "Revert one scientist to citizen.", main.width/6F + 120, (float)main.height*5F/6F + 60, 50, 50).shortcut = false;

		if (c.sortie == 1)
		{
			menus.get(2).addButton("sortie", "Sortie", "Raise a temporary garrison (cannot leave borders).", main.width/6F - 60, (float)main.height*5F/6F, 50, 50);
		}
		else if (c.sortie == 2)
		{
			menus.get(2).addButton("endSortie", "End sortie", "End the sortie and return troops to city.", main.width/6F - 60, (float)main.height*5F/6F, 50, 50);
		}

		int n = menus.get(2).buttons.size();
		for (int i = 0; i < n; i++)
		{
			TextBox b = menus.get(2).buttons.get(i);
			b.move(0, main.height*5/6 + i*height - (n*1 + 2)*height); //Shift the buttons to their proper place
			b.size.x = 150; b.size.y = height;
			/*b.origX = b.posX; b.origY = b.posY;
			b.origSizeX = b.sizeX; b.origSizeY = b.sizeY;*/
		}
		/*for (int i = 0; i < n; i++)
		{
			TextBox b = menus.get(2).buttons.get(i);
			b.move(150 + 50*i, main.height - 50); //Shift the buttons to their proper place
			b.origX = b.posX; b.origY = b.posY;
			b.sizeX = 50; b.sizeY = 50;
			b.origSizeX = b.sizeX; b.origSizeY = b.sizeY;
		}*/

		menus.get(2).buttons.add(new TextBox(loader.loadTexture("partTexture"),c.name,0,main.height*5/6 - height,150,height));

		double[] data = EntityData.calculateYield(c);
		TextBox t = new TextBox(loader.loadTexture("partTexture"), "Food per turn: " + (int)Math.floor(data[0]),0,main.height*5/6,150,main.height*1/6);
		t.display.add("Gold per turn: " + (int)Math.floor(data[1]));
		t.display.add("Metal per turn: " + (int)Math.floor(data[2]));
		t.display.add("Research per turn: " + (int)Math.floor(data[3]));
		menus.get(2).buttons.add(t);
	}

	private void unitButton(City c, String s, boolean enabled)
	{
		int turns = calcQueueTurnsInt(c, s);
		String name = turns != -1 ? s + " <" + turns + ">" : s + " <N/A>";
		Button b = (Button)menus.get(2).addButton("queue" + s, name, "", 0, 0, 0, 0);
		b.tooltip.clear();
		if (!enabled) {b.command = ""; b.alpha = 100; b.shortcut = false; b.tooltip.add("Unlocked by " + c.owner.techTree.unlockedBy(s));}
		//b.tooltip.add("Estimated build time: " + calcQueueTurnsInt(c, units.get(i)) + " turns");
		b.tooltip.add("Queue a " + s + ".");
		if (turns != -1) b.tooltip.add("Estimated build time: " + turns + " turns");
		else b.tooltip.add("Estimated build time: N/A");

		float[] cost = EntityData.getCost(s);
		b.tooltip.add("Requires " + cost[0] + " food");
		b.tooltip.add("Requires " + cost[2] + " metal");
		//System.out.println(s + ": " + cost[0] + " " + cost[2]);

		GameEntity example = (GameEntity)EntityData.get(s);
		b.tooltip.add("Offensive strength: " + example.offensiveStr);
		b.tooltip.add("Defensive strength: " + example.defensiveStr);
		b.tooltip.add("Ranged strength: " + example.rangedStr);
		//menus.get(2).addButton("queue" + units.get(i), units.get(i), "", 0, main.height*5/6 - disp + 30*i, main.width*1/6, 30);
	}

	private void buildingButton(City c, String s, boolean enabled)
	{
		int turns = calcQueueTurnsInt(c, s);
		String name = turns != -1 ? s + " <" + calcQueueTurnsInt(c, s) + ">" : s + " <N/A>";
		Button b = (Button)menus.get(2).addButton("queueBuilding" + s, name, "", 0, 0, 0, 0);
		b.tooltip.clear();
		if (!enabled) {b.command = ""; b.alpha = 100; b.shortcut = false; b.tooltip.add("Unlocked by " + c.owner.techTree.unlockedBy(s));}
		b.tooltip.add("Queue a " + s + ".");
		//b.tooltip.add(calcQueueTurns(c));
		if (turns != -1) b.tooltip.add("Estimated build time: " + turns + " turns");
		else b.tooltip.add("Estimated build time: N/A");

		Improvement impr = EntityData.cityImprovementMap.get(s);
		float[] cost = EntityData.getCost(s);
		b.tooltip.add(impr.tooltip);
		b.tooltip.add("Requires " + cost[0] + " food");
		b.tooltip.add("Requires " + cost[2] + " metal");
		/*menus.get(2).addButton("queueBuilding" + s, s, "",
				0, main.height*5/6 - disp + 30*(i+c.owner.techTree.allowedUnits.size()), main.width*1/6, 30);*/
	}

	private void fieldButton(City c, String s, boolean enabled)
	{
		int turns = calcQueueTurnsInt(c, s);
		Button b = (Button)menus.get(2).addButton("qfield" + s, "F: " + s + " <" + calcQueueTurnsInt(c,s) + ">", "",
				0, 0, 0, 0);
		b.tooltip.clear();
		//b.tooltip.add(calcQueueTurns(c));
		if (!enabled) {b.command = ""; b.alpha = 100; b.shortcut = false; b.tooltip.add("Unlocked by " + c.owner.techTree.unlockedBy(s));}
		b.tooltip.add("Add a " + s + " field.");
		if (turns != -1) b.tooltip.add("Estimated build time: " + turns + " turns");
		else b.tooltip.add("Estimated build time: N/A");

		Improvement impr = EntityData.getField(s);
		b.tooltip.add(impr.tooltip);
		b.tooltip.add("Requires " + (int)impr.foodFlat + " food");
		b.tooltip.add("Requires " + (int)impr.metalFlat + " metal");
		b.tooltip.add("Requires " + (int)impr.goldFlat + " gold");
		/*menus.get(2).addButton("queueBuilding" + buildings.get(i), buildings.get(i), "",
				0, main.height*5/6 - disp + 30*(i+c.owner.techTree.allowedUnits.size()), main.width*1/6, 30);*/
	}

	/*public void updateCreateFieldMenu(Tile t, int n)
	{
		menus.get(16).buttons.clear();
		closeMenus();
		menus.get(16).activate(true);

		ArrayList<String> fields = main.grid.civs[0].techTree.allowedFields;
		for (int i = 0; i < fields.size(); i++)
		{
			Field f = EntityData.getField(fields.get(i));
			TextBox b = menus.get(16).addButton("makeField"+n+","+f.name, f.name, "", 0, 0, 0, 0);
			//b.tooltip.clear();
			b.tooltip = new ArrayList<String>();
			b.tooltip.add(f.name + "");
			b.tooltip.add(f.tooltip + "");
			b.tooltip.add("Costs " + f.foodFlat + " F, " + f.goldFlat + " G, " + f.metalFlat + " M");
			//System.out.println("Tooltip: " + b.tooltip.get(1));
			//b.dimTooltip();
		}

		for (int i = 0; i < menus.get(16).buttons.size(); i++)
		{
			TextBox b = menus.get(16).buttons.get(i);
			b.move(0, main.height*5/6 + i*30 - (menus.get(16).buttons.size()+1)*30); //Shift the buttons to their proper place
			b.origX = b.posX; b.origY = b.posY;
			b.sizeX = 100; b.sizeY = 30;
			b.origSizeX = b.sizeX; b.origSizeY = b.sizeY;
			//b.dimTooltip();
		}
	}*/

	//private Tile editingFields; //The tile that the player wants to improve
	/*public void updateFieldMenu(Tile t)
	{
		if (t.maxFields == 0) return;
		closeMenus();
		menus.get(15).buttons.clear();
		menus.get(15).activate(true);
		//editingFields = t;
		//Shortcuts are turned off for this menu
		for (int i = 0; i < t.maxFields; i++)
		{
			Field f = null;
			if (i < t.fields.size())
				f = t.fields.get(i);
			if (f == null)
				menus.get(15).addButton("editField"+i, "Add field", "There is no field built here. Add a new one.", i*150, 0, 100, 30);
			else
			TextBox b = new TextBox("", "", i*150, 30, 150, 100);
			b.display = new ArrayList<String>();
			if (f != null)
			{
				if (f.owner != null)
					b.display.add(f.name + " (" + f.owner.name + ")");
				else
					b.display.add(f.name + " (unowned)");
			}
			else
			{
				b.display.add("No field");
			}
			menus.get(15).buttons.add(b);
		}
		for (int i = 0; i < menus.get(15).buttons.size(); i++)
		{
			TextBox b = menus.get(15).buttons.get(i);
			b.move(b.posX + main.mouseX, b.posY + main.mouseY); //Shift the buttons to their proper place with respect to mouse
			b.origX = b.posX; b.origY = b.posY;
			//b.sizeX = 100; b.sizeY = 30;
			//b.origSizeX = b.sizeX; b.origSizeY = b.sizeY;
		}
	}*/

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

		TextBox text0 = new TextBox(loader.loadTexture("partTexture"),"",main.width*2/6,main.height*2/6,main.width*2/6,main.height/12); //"HintText"
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
		int width = 60, width2 = 120;
		TextBox text = new TextBox(loader.loadTexture("partTexture"),"Opinion","Your relations with this nation (-200 to 200).",100+width2,255,width,20);
		menus.get(11).buttons.add(text);
		text = new TextBox(loader.loadTexture("partTexture"),"Border","Your ability to access this nation's lands.",100+width2+width,255,width,20);
		menus.get(11).buttons.add(text);
		text = new TextBox(loader.loadTexture("partTexture"),"War","The formal declaration of hostility between you and this nation.",100+width2+width*2,255,width,20);
		menus.get(11).buttons.add(text);
		text = new TextBox(loader.loadTexture("partTexture"),"Ally","The existence of a formal alliance between you and this nation.",100+width2+width*3,255,width,20);
		menus.get(11).buttons.add(text);

		for (int i = 0; i < main.grid.civs.length; i++)
		{
			Civilization civ = main.grid.civs[i];

			Button b = new Button("pivot"+i,civ.name,"",100,280 + 25*(i),width2,20);
			menus.get(11).buttons.add(b);

			b.tooltip.clear();
			String s = civ.name + "; Health: " + civ.health + "; Gold: " + civ.gold + "; Research: " + civ.research + "; Relations: " + main.grid.civs[0].opinions[i];
			b.tooltip.add(s);
			b.tooltip.add("Select to view the diplomatic situation of " + civ.name + ".");
			b.dimTooltip();

			//Allow player to talk with other civs in this menu
			if (i != 0)
			{
				TextBox textBox = menus.get(11).addButton("diplomacy"+i, "Talk", "Conduct diplomacy with " + civ.name + ".", 100+width2+width*4, 280 + 25*(i), width2, 20);
				textBox.shortcut = false;
			}

			if (civ.equals(pivot)) continue;

			text = new TextBox(loader.loadTexture("partTexture"),"" + pivot.opinions[i],100+width2,280 + 25*(i),width,20);
			menus.get(11).buttons.add(text);

			String temp = pivot.isOpenBorder(civ) ? "Open" : "Closed";
			text = new TextBox(loader.loadTexture("partTexture"),temp,100+width2+width,280 + 25*(i),width,20);
			menus.get(11).buttons.add(text);

			temp = pivot.isWar(civ) ? "WAR" : "";
			text = new TextBox(loader.loadTexture("partTexture"),temp,100+width2+width*2,280 + 25*(i),width,20);
			menus.get(11).buttons.add(text);

			temp = pivot.isAlly(civ) ? "Yes" : "No";
			text = new TextBox(loader.loadTexture("partTexture"),temp,100+width2+width*3,280 + 25*(i),width,20);
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

	//Find the spaces that a selected unit could potentially move to
	ArrayList<Tile> temp = new ArrayList<Tile>();
	public void movementChoice(ArrayList<Tile> initial, boolean first, int action)
	{
		if (first)
			temp = new ArrayList<Tile>();
		//action--;
		if (action <= 0)
		{
			movementChoices = temp; 
			return;
		}
		for (int i = 0; i < initial.size(); i++)
		{
			ArrayList<Tile> adj = main.grid.adjacent(initial.get(i).row, initial.get(i).col);
			for (int j = 0; j < adj.size(); j++)
			{
				if (!temp.contains(adj.get(j)))
					temp.add(adj.get(j));
			}
		}
		//System.out.println(initial.size() + " " + temp.size());
		if (action > 0)
			movementChoice(temp, false, action-1);
	}

	//Draw a path from the selected's entity tile to another
	public void pathTo(Tile t)
	{
		pathToHighlighted = main.grid.pathFinder.findAdjustedPath(
				selected.owner,
				selected.location.row,
				selected.location.col,
				t.row,
				t.col
				);
		if (pathToHighlighted == null) //Handle case that there is no point or invalid tile
			pathToHighlighted = new ArrayList<Tile>();
	}

	//Encapsulation for selected
	public BaseEntity getSelected()
	{
		return selected;
	}

	public void selectAndFocus(BaseEntity en)
	{
		textboxes.get(5).display.clear();
		textboxes.get(5).display.add(0, "A UNIT NEEDS ORDERS");
		textboxes.get(5).display.add("PRESS SPACE");
		textboxes.get(5).tooltip.set(0, "Please order your unit.");
		select(en);
		main.fixCamera(en.location.row, en.location.col);
		//main.chunkSystem.update();
		//main.requestUpdate();
	}

	public void select(BaseEntity en)
	{
		selected = en;
		//main.newMenuSystem.updateUnitMenu(en);
		//main.requestUpdate();
		if (en != null)
		{
			en.sleep = false;
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
			//textboxes.get(1).orders.clear();
			textboxes.get(1).activate(true);
			textboxes.get(1).move(main.width - 400,main.height);
			//textboxes.get(1).moveTo(textboxes.get(1).origX,textboxes.get(1).origY,20);
		}
		else
		{
			stack.clear();
			//textboxes.get(1).orders.clear();
			textboxes.get(1).activate(false);
			textboxes.get(1).move(main.width - 400,main.height-150);

			menus.get(1).buttons.clear();
		}
		/*selected = en;
		main.newMenuSystem.updateUnitMenu(en);
		//main.requestUpdate();
		if (en != null)
		{
			en.sleep = false;
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
			textboxes.get(1).orders.clear();
			textboxes.get(1).activate(true);
			textboxes.get(1).move(main.width - 400,main.height);
			textboxes.get(1).moveTo(textboxes.get(1).origX,textboxes.get(1).origY,20);
		}
		else
		{
			stack.clear();
			textboxes.get(1).orders.clear();
			textboxes.get(1).activate(false);
			textboxes.get(1).move(main.width - 400,main.height-150);

			menus.get(1).buttons.clear();
		}*/
	}


}
