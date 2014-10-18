package system;

import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Tile;

import java.util.ArrayList;

import data.EntityData;
import processing.core.PApplet;
import render.Button;
import render.CivGame;
import render.Menu;
import render.Game.PFrame;
import render.TextBox;
import units.City;
import units.Settler;
import units.Warrior;

public class MenuSystem extends BaseSystem {

	public ArrayList<Menu> menus;
	public ArrayList<TextBox> textboxes;
	
	private ArrayList<Click> clicks;

	public boolean minimap, info = false;
	public int multiplier = 1;

	public Tile target;
	public ArrayList<String> hintText;
	public Tile highlighted; //Under the player's crosshair
	private BaseEntity selected; //Selected by the player with the mouse explicitly
	public Tile[] settlerChoices;
	public String typeOfLastSelected = "";
	//public City citySelected;
	
	private ArrayList<String> messages;

	public MenuSystem(CivGame civGame) {
		super(civGame);
		menus = new ArrayList<Menu>();
		textboxes = new ArrayList<TextBox>();
		clicks = new ArrayList<Click>();

		hintText = new ArrayList<String>();
		messages = new ArrayList<String>();
		//highlighted = null;

		Menu menu0 = new Menu("MainMenu");
		menus.add(menu0);
		menu0.addButton("exitgame", "Exit", 0, 0, 100, 30);
		menu0.addButton("minimap", "Minimap", 0, 100, 100, 30);
		menu0.addButton("info", "Information", 0, 130, 100, 30);
		
		Menu menu1 = new Menu("UnitMenu");
		menus.add(menu1);

		Menu menu2 = new Menu("CityMenu");
		menus.add(menu2);

		menu0.active = true;
	}

	public boolean menuActivated = false;
	public void tick()
	{
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
		for (int menu = 0; menu < menus.size(); menu++)
		{
			if (menus.get(menu).active)
			{
				for (int i = 0; i < menus.get(menu).buttons.size(); i++)
				{
					main.fill(0);
					Button b = menus.get(menu).buttons.get(i);
					main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
					main.textAlign(PApplet.CENTER, PApplet.CENTER);
					main.fill(255);
					main.text(b.display, b.posX + b.sizeX/2, b.posY + b.sizeY/2);
				}
			}
		}

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
			main.text("Work in progress", 115, 150);
		}

		//Render the cursor
		int width = 6;
		main.stroke(255);
		main.fill(0);
		main.rect((main.width - width)/2, (main.height - width)/2, width, width);

		main.noStroke();
		
		hintText.clear();
		if (target != null)
		{
			hintText.add(target.row + " " + target.col);
			if (target.owner != null)
				hintText.add("Owner: " + target.owner.name);
			else
				hintText.add("Terra nullius");

			if (target.biome >= 4 && target.biome <= 6)
				if (target.forest)
					hintText.add(EntityData.getBiome(target.biome) + " (forested)");
				else
					hintText.add(EntityData.getBiome(target.biome) + " (unforested)");
			else
				hintText.add(EntityData.getBiome(target.biome));

			if (target.improvement != null)
			{
				hintText.add(target.improvement.name);
				hintText.add(target.improvement.id);
			}
			else
				hintText.add("Pristine");

			if (target.city != null)
			{
				double[] data = target.city.evaluate(target, null);
				hintText.add((int)data[0] + " F, " + (int)data[1] + " G, " + (int)data[2] + " M, " + (int)data[3] + " R");
			}

			if (target.freshWater)
				hintText.add("Fresh Water");

			if (highlighted != null)
				if (highlighted.occupants.size() > 0)
				{
					String stringy = "";
					for (int i = 0; i < highlighted.occupants.size(); i++)
					{
						stringy += highlighted.occupants.get(i).name + "; ";
						hintText.add(stringy);
					}
				}
		}
		if (selected != null)
		{
			if (selected.owner != null && !(selected instanceof City))
			{
				//main.stroke(255);
				main.fill(0);
				main.rect(main.width*4/6,0,200,150);
				main.fill(255);
				main.textSize(12);

				ArrayList<String> temp = new ArrayList<String>();
				temp.add(selected.name + " " + ((GameEntity)selected).action + "/" + ((GameEntity)selected).maxAction);
				temp.add(selected.offensiveStr + " offensive / " + selected.rangedStr + " ranged");
				temp.add(selected.defensiveStr + " defensive");

				for (int i = 0; i < temp.size(); i++)
				{
					main.textAlign(PApplet.LEFT);
					main.text(temp.get(i), main.width*4/6 + 15, 15*(i+1));
				}
				
				/*main.fill(0);
				main.rect(main.width*3/6 - 75,470 - 30,150,60);
				main.fill(255);
				main.textSize(12);
				
				main.textAlign(PApplet.LEFT);
				main.text(selected.name,main.width*3/6 - 75,470 - 30);*/

				if (!typeOfLastSelected.equals(selected.name))
				{
					updateUnitMenu(selected.name);
				}
				menus.get(1).active = true;
				//main.text("Test", main.width*5/6 + 15, main.height*5/6 + 15);
			}
			else
			{
				menus.get(1).active = false;
			}
		}
		else
		{
			menus.get(1).active = false;
		}

		menus.get(2).active = false;
		if (selected != null)
			if (selected.owner != null)
				if (selected.owner.equals(main.grid.civs[0]) && selected instanceof City)
				{
					City citySelected = (City)selected; //to work with old code
					menus.get(2).active = true;

					//main.stroke(255);
					main.fill(0);
					main.rect(main.width*4/6,0,200,150);
					main.fill(255);
					main.textSize(12);

					ArrayList<String> temp = new ArrayList<String>();
					temp.add(citySelected.name + "; Population: " + citySelected.population);
					temp.add("Health: " + citySelected.health + ", Happiness: " + citySelected.happiness);
					if (citySelected.queueFood > 0 || citySelected.queueMetal > 0)
					{
						int[] t = citySelected.quickEval();
						//Division by zero errors
						if (t[0] == 0 && t[2] != 0 && citySelected.queueFood > 0)
						{
							temp.add("No food production, will not finish.");
						}
						else if (t[0] != 0 && t[2] == 0 && citySelected.queueMetal > 0)
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
							int turns = Math.max(
									citySelected.queueFood/(t[0]) + 1,
									citySelected.queueMetal/(t[2]) + 1
									);
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

					for (int i = 0; i < temp.size(); i++)
					{
						main.textAlign(PApplet.LEFT);
						main.text(temp.get(i), main.width*4/6 + 15, 15*(i+1));
					}
				}

		main.fill(0);
		main.rect(main.width*5/6,200,main.width*1/6,100);
		main.fill(255);
		main.textSize(12);
		main.textAlign(PApplet.LEFT);
		if (messages.size() > 0)
		{
			for (int i = 0; i < 4; i++)
			{
				if (i >= messages.size()) break;
				main.text(messages.get(messages.size() - i - 1), main.width*5/6, 200 + 15*(i+1));
			}
			/*for (int i = messages.size() - 1; i >= 0; i--)
			{
				main.text(messages.get(i), main.width*5/6, 200 + 15*(i+1));
				if (messages.size() - i >= 4)
				{
					break;
				}
			}*/
		}
		
		if (hintText.size() > 0)
		{
			//main.stroke(255);
			main.fill(0);
			main.rect(main.width*5/6,0,200,150);
			main.fill(255);
			main.textSize(12);
			for (int i = 0; i < hintText.size(); i++)
			{
				main.textAlign(main.LEFT);
				if (hintText.get(i) != null)
					main.text(hintText.get(i), main.width*5/6 + 15, 15*(i+1));
			}
		}

		main.hint(PApplet.ENABLE_DEPTH_TEST);
		/*main.pg.beginDraw();
		//main.perspective();
		main.pg.textSize(20);
		main.pg.background(255,255,255,0);
		main.hint(PApplet.DISABLE_DEPTH_TEST);
		main.pg.hint(PApplet.DISABLE_DEPTH_TEST);
		main.camera();
		main.noLights();
		for (int i = 0; i < activeMenu.buttons.size(); i++)
		{
			main.pg.fill(0);
			Button b = activeMenu.buttons.get(i);
			main.pg.rect(b.posX, b.posY, b.sizeX, b.sizeY);
			main.pg.textAlign(main.pg.CENTER, main.pg.CENTER);
			main.pg.fill(255);
			main.pg.text(b.display, b.posX + b.sizeX/2, b.posY + b.sizeY/2);
		}
		main.hint(PApplet.ENABLE_DEPTH_TEST);
		main.pg.hint(PApplet.ENABLE_DEPTH_TEST);
		main.lights();
		main.pg.endDraw();
		main.image(main.pg, 1500, 900);*/
		
		main.noStroke();
		main.fill(0);
		main.rect(main.width/6,0,300,50);
		main.fill(255);
		Civilization c = main.grid.civs[0];
		main.textAlign(main.LEFT);
		main.text(c.name + "; Food: " + c.food + "; Gold: " + c.gold + "; Metal: " + c.metal + "; Research: " + c.research, main.width/6 + 15, 15);
		
		menuActivated = false;
		for (int menu = 0; menu < menus.size(); menu++)
		{
			if (menus.get(menu).active)
			{
				for (int i = clicks.size() - 1; i >= 0; i--)
				{
					String command = menus.get(menu).click(clicks.get(i).mouseX, clicks.get(i).mouseY);
					if (command != null && !command.equals(""))
					{
						menuActivated = true;
						if (command.equals("exitgame"))
						{
							System.exit(0);
							continue;
						}
						else if (command.equals("info"))
						{
							info = !info;
							continue;
						}
						else if (command.equals("minimap"))
						{
							minimap = !minimap;
							continue;
						}

						else if (command.equals("buildfarm"))
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
						}
						else if (command.equals("buildmine"))
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
						}
						else if (command.equals("kill"))
						{
							main.grid.removeUnit(selected);
						}
						else if (command.equals("raze"))
						{
							((Warrior)selected).raze();
							((Warrior)selected).action = 0;
						}
						else if (command.equals("settle"))
						{
							((Settler)selected).settle();
						}

						else if (command.equals("queueSettler"))
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
						}
						main.menuSystem.selected = null;
						//below was derived from the original expression to calculate rotY & rotVertical
						//main.centerX = main.mouseX/(1 - main.player.rotY/(float)Math.PI);
						//main.centerY = main.mouseY/(1 + 4*main.player.rotVertical/(float)Math.PI);
						main.resetCamera();
					}
				}
			}
		}
		clicks.clear();
	}

	public class Click {float mouseX, mouseY; Click(float x, float y) {mouseX = x; mouseY = y;}}
	public void queueClick(float mouseX, float mouseY)
	{
		clicks.add(0, new Click(mouseX, mouseY));
	}
	
	//Send a message, checking for repeats
	public void message(String message)
	{
		if (messages.size() == 0) messages.add(message);
			if (!messages.get(messages.size()-1).equals(message))
				messages.add(message);
	}

	//Choose which buttons to show depending on unit (e.g. only settler can settle)
	public void updateUnitMenu(String name)
	{
		menus.get(1).buttons.clear();
		menus.get(1).addButton("kill", "Destroy", (float)main.width/3F, (float)main.height*5F/6F, 50, 50);
		if (name.equals("Settler"))
		{
			menus.get(1).addButton("settle", "Settle", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
		}
		else if (name.equals("Warrior"))
		{
			menus.get(1).addButton("raze", "Raze", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
		}
		else if (name.equals("Worker"))
		{
			menus.get(1).addButton("buildfarm", "Farm", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
			menus.get(1).addButton("buildmine", "Mine", (float)main.width/3F + 120, (float)main.height*5F/6F, 50, 50);
		}
		//System.out.println(menus.get(1).buttons.size());
	}

	//Choose which builds to allow i.e. which can be queued up in the city (factor in techs later)
	public void updateCity(City c)
	{
		menus.get(2).buttons.clear();
		menus.get(2).addButton("queueSettler", "Settler", main.width/3F, (float)main.height*5F/6F, 100, 100);
		menus.get(2).addButton("queueWorker", "Worker", main.width/3F + 110, (float)main.height*5F/6F, 100, 100);
		menus.get(2).addButton("queueWarrior", "Warrior", main.width/3F + 220, (float)main.height*5F/6F, 100, 100);
	}
	
	//Encapsulation for selected
	public BaseEntity getSelected()
	{
		return selected;
	}
	
	public void select(BaseEntity en)
	{
		selected = en;
		if (en instanceof Settler)
		{
			settlerChoices = main.grid.returnBestCityScores(en.location.row, en.location.col);
		}
		else
		{
			settlerChoices = null;
		}
		if (en instanceof City)
		{
			updateCity((City)en);
		}
	}


}
