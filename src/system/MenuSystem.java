package system;

import java.util.ArrayList;

import processing.core.PApplet;
import render.Button;
import render.CivGame;
import render.Menu;

public class MenuSystem extends BaseSystem {

	public ArrayList<Menu> menus;
	public Menu activeMenu;
	
	public MenuSystem(CivGame civGame) {
		super(civGame);
		menus = new ArrayList<Menu>();
		
		Menu menu0 = new Menu("MainMenu");
		menus.add(menu0);
		menu0.addButton("exitgame", "Exit Game", 0, 0, 200, 50);
		
		activeMenu = menu0;
	}

	public void tick()
	{
		main.hint(main.DISABLE_DEPTH_TEST);
		main.textSize(20);
		//main.background(255,255,255,0);
		main.camera();
		main.noLights();
		for (int i = 0; i < activeMenu.buttons.size(); i++)
		{
			main.fill(0);
			Button b = activeMenu.buttons.get(i);
			main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
			main.textAlign(main.pg.CENTER, main.pg.CENTER);
			main.fill(255);
			main.text(b.display, b.posX + b.sizeX/2, b.posY + b.sizeY/2);
		}
		main.hint(main.ENABLE_DEPTH_TEST);
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
	}
	
}
