package system;

import game.BaseEntity;
import game.GameEntity;
import game.Tile;

import java.util.ArrayList;

import data.EntityData;
import processing.core.PApplet;
import render.Button;
import render.CivGame;
import render.Menu;
import render.Game.PFrame;
import units.City;
import units.Settler;

public class MenuSystem extends BaseSystem {

	public ArrayList<Menu> menus;
	private ArrayList<Click> clicks;

	public boolean minimap = false;
	public int multiplier = 1;

	public Tile target;
	public ArrayList<String> hintText;
	public Tile highlighted; //Under the player's crosshair
	public GameEntity selected; //Selected by the player with the mouse explicitly
	public String typeOfLastSelected = "";
	public City citySelected;

	public MenuSystem(CivGame civGame) {
		super(civGame);
		menus = new ArrayList<Menu>();
		clicks = new ArrayList<Click>();

		hintText = new ArrayList<String>();
		//highlighted = null;

		Menu menu0 = new Menu("MainMenu");
		menus.add(menu0);
		menu0.addButton("exitgame", "Exit", 0, 0, 100, 30);
		menu0.addButton("minimap", "Minimap", 0, 800, 100, 50);

		Menu menu1 = new Menu("UnitMenu");
		menus.add(menu1);
		
		Menu menu2 = new Menu("CityMenu");
		menus.add(menu2);

		menu0.active = true;
	}

	public boolean menuActivated = false;
	public void tick()
	{
		main.hint(PApplet.DISABLE_DEPTH_TEST);
		main.textSize(20);
		//main.background(255,255,255,0);
		main.camera();
		main.perspective();
		//main.noLights();
		main.noStroke();
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
			int con = 2;
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

		int width = 6;
		main.stroke(255);
		main.fill(0);
		main.rect((main.width - width)/2, (main.height - width)/2, width, width);

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
				hintText.add(target.improvement.name);
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
			if (selected.owner != null)
			{
				main.stroke(255);
				main.fill(0);
				main.rect(main.width*4/6,main.height*5/6,200,150);
				main.fill(255);
				main.textSize(12);

				ArrayList<String> temp = new ArrayList<String>();
				temp.add(selected.name + " " + selected.action + "/" + selected.maxAction);
				temp.add(selected.offensiveStr + " offensive / " + selected.rangedStr + " ranged");
				temp.add(selected.defensiveStr + " defensive");

				for (int i = 0; i < temp.size(); i++)
				{
					main.textAlign(PApplet.LEFT);
					main.text(temp.get(i), main.width*4/6 + 15, main.height*5/6 + 15*(i+1));
				}

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
		if (citySelected != null)
		{
			if (citySelected.owner.equals(main.grid.civs[0]))
			{
				menus.get(2).active = true;
				
				main.stroke(255);
				main.fill(0);
				main.rect(main.width*3/6,main.height*5/6,200,150);
				main.fill(255);
				main.textSize(12);

				ArrayList<String> temp = new ArrayList<String>();
				temp.add(citySelected.name + "; Population: " + citySelected.population);
				temp.add("Health: " + citySelected.health + ", Happiness: " + citySelected.happiness);
				if (citySelected.queueTurns > 0)
				{
					temp.add("Queued " + citySelected.queue + ", ready in " + citySelected.queueTurns + " turns.");
				}
				else
				{
					temp.add("Nothing queued.");
				}

				for (int i = 0; i < temp.size(); i++)
				{
					main.textAlign(PApplet.LEFT);
					main.text(temp.get(i), main.width*3/6 + 15, main.height*5/6 + 15*(i+1));
				}
			}
		}

		if (hintText.size() > 0)
		{
			main.stroke(255);
			main.fill(0);
			main.rect(main.width*5/6,main.height*5/6,200,150);
			main.fill(255);
			main.textSize(12);
			for (int i = 0; i < hintText.size(); i++)
			{
				main.textAlign(main.LEFT);
				main.text(hintText.get(i), main.width*5/6 + 15, main.height*5/6 + 15*(i+1));
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
						}
						else if (command.equals("minimap"))
						{
							minimap = !minimap;
						}
						
						else if (command.equals("kill"))
						{
							main.grid.removeUnit(selected);
						}
						else if (command.equals("settle"))
						{
							((Settler)selected).settle();
						}
						
						else if (command.equals("queueSettler"))
						{
							System.out.println("Queued");
							citySelected.queue = "Settler";
							citySelected.queueFood = 35;
						}
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

	//Choose which buttons to show depending on unit (e.g. only settler can settle)
	public void updateUnitMenu(String name)
	{
		menus.get(1).buttons.clear();
		menus.get(1).addButton("kill", "Destroy", (float)main.width/3F, (float)main.height*5F/6F, 50, 50);
		if (name.equals("Settler"))
		{
			menus.get(1).addButton("settle", "Settle", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
		}
		//System.out.println(menus.get(1).buttons.size());
	}
	
	//Choose which builds to allow i.e. which can be queued up in the city (factor in techs later)
	public void updateCity(City c)
	{
		menus.get(2).buttons.clear();
		menus.get(2).addButton("queueSettler", "Settler", 500, 500, 100, 100);
	}


}
