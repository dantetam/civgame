package system;

import java.util.ArrayList;

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
		main.pg.beginDraw();
		main.pg.background(255,255,255,0);
		for (int i = 0; i < activeMenu.buttons.size(); i++)
		{
			main.pg.fill(0);
			Button b = activeMenu.buttons.get(i);
			main.pg.rect(b.posX, b.posY, b.sizeX, b.sizeY);
			main.pg.textAlign(main.pg.CENTER, main.pg.CENTER);
			main.pg.fill(255);
			main.pg.text(b.display, b.posX + b.sizeX/2, b.posY + b.sizeY/2);
		}
		main.pg.endDraw();
		main.image(main.pg, 1500, 900);
	}
	
}
