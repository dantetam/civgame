package system;

import game.Tile;

import java.util.ArrayList;

import processing.core.PApplet;
import render.Button;
import render.CivGame;
import render.Menu;
import render.Game.PFrame;

public class MenuSystem extends BaseSystem {

	public ArrayList<Menu> menus;
	private ArrayList<Click> clicks;
	public Menu activeMenu;

	public MenuSystem(CivGame civGame) {
		super(civGame);
		menus = new ArrayList<Menu>();
		clicks = new ArrayList<Click>();

		Menu menu0 = new Menu("MainMenu");
		menus.add(menu0);
		menu0.addButton("exitgame", "Exit", 0, 0, 200, 50);

		activeMenu = menu0;
	}

	public void tick()
	{
		main.hint(PApplet.DISABLE_DEPTH_TEST);
		main.textSize(20);
		//main.background(255,255,255,0);
		main.camera();
		main.perspective();
		//main.noLights();
		for (int i = 0; i < activeMenu.buttons.size(); i++)
		{
			main.fill(0);
			Button b = activeMenu.buttons.get(i);
			main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
			main.textAlign(PApplet.CENTER, PApplet.CENTER);
			main.fill(255);
			main.text(b.display, b.posX + b.sizeX/2, b.posY + b.sizeY/2);
		}

		main.noStroke();
		//main.rect(0, 700, 50, 50);
		float sX = 0; float sY = 500; float widthX = 600; float widthY = 400; 
		int con = 3;
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
				main.rect(sX + (main.grid.rows-r)/(float)main.grid.rows*widthX,sY + c/(float)main.grid.cols*widthY,con+4,con+2);
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
		for (int i = clicks.size() - 1; i >= 0; i--)
		{
			String command = activeMenu.click(clicks.get(i).mouseX, clicks.get(i).mouseY);
			if (command != null && !command.equals(""))
			{
				if (command.equals("exitgame"))
				{
					System.exit(0);
				}
			}

		}
	}

	public class Click {float mouseX, mouseY; Click(float x, float y) {mouseX = x; mouseY = y;}}
	public void queueClick(float mouseX, float mouseY)
	{
		clicks.add(0, new Click(mouseX, mouseY));
	}


}
