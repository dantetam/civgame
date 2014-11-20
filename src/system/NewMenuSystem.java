package system;

import game.BaseEntity;
import game.GameEntity;
import game.Tile;

import java.util.ArrayList;

import render.CivGame;
import render.Rune;
import render.Menu;

public class NewMenuSystem extends BaseSystem {

	public ArrayList<Menu> menus;
	public Rune selectedRune = null;

	public NewMenuSystem(CivGame civGame) {
		super(civGame);
		menus = new ArrayList<Menu>();

		Menu menu0 = new Menu("UnitMenu");
		menus.add(menu0);
		menus.get(0).buttons.add(new Rune("image","tileMove",675,425,50,50).color(0,255,0));
		
		Menu menu1 = new Menu("AdvisorMenu");
		menus.add(menu1);
		menus.get(1).buttons.add(new Rune("diplomat","tileDiplomat",600,10,50,50).color(0,0,255));
		menus.get(1).buttons.add(new Rune("war","tileWar",660,10,50,50).color(255,0,0));
		menus.get(1).buttons.add(new Rune("trader","tileTrader",720,10,50,50).color(200,200,0));
	}

	public void tick() 
	{
		for (int i = 0; i < menus.size(); i++)
		{
			for (int j = 0; j < menus.get(i).buttons.size(); j++)
			{
				Rune rune = (Rune)menus.get(i).buttons.get(j);
				if (rune.active)
				{
					main.fill(rune.r, rune.g, rune.b);
					main.rect(rune.posX, rune.posY, rune.sizeX, rune.sizeY);
				}
			}
		}
		main.menuSystem.menuActivated = false;
		if (selectedRune != null)
			main.menuSystem.menuActivated = true;
		main.hint(main.ENABLE_DEPTH_TEST);
		if (main.inputSystem.keyHeld[113-97] && main.menuSystem.getSelected() != null)
		{
			for (int i = 0; i < menus.get(0).buttons.size(); i++)
			{
				menus.get(0).buttons.get(i).active = true;
			}
		}
		else
		{
			for (int i = 0; i < menus.get(0).buttons.size(); i++)
			{
				Rune rune = (Rune)menus.get(0).buttons.get(i);
				if (!rune.equals(selectedRune))
				{
					menus.get(0).buttons.get(i).active = false;
					menus.get(0).buttons.get(i).orderOriginal(false);
				}
			}
		}
	}

	public void updateUnitMenu(BaseEntity en)
	{
		//menus.get(0).buttons.clear();
		/*for (int i = 0; i < menus.get(0).buttons.size(); i++)
		{
			menus.get(0).buttons.get(i).orderOriginal(false);
		}*/
	}

	public float lastMouseX, lastMouseY;
	public void mouseDragged(float mouseX, float mouseY)
	{
		if (selectedRune == null)
		{
			Rune rune = within(mouseX, mouseY);
			if (rune != null)
			{
				if (rune.active)
				{
					main.menuSystem.menuActivated = true;
					selectedRune = rune;
				}
			}
		}
		if (selectedRune != null)
		{
			selectedRune.active = true;
			//Seems like a redundant calculation
			float dX = mouseX - lastMouseX, dY = mouseY - lastMouseY;
			//rune.posX = 500;
			selectedRune.posX += dX; selectedRune.posY += dY;
			//rune.moveTo(mouseX - dX, mouseY - dY, 5);
			lastMouseX = mouseX; lastMouseY = mouseY;
			main.menuSystem.menuActivated = true;
		}
	}

	public void mouseReleased(float mouseX, float mouseY)
	{
		if (selectedRune != null)
		{
			main.menuSystem.menuActivated = true;
			if (selectedRune.command.equals("tileMove"))
			{
				GameEntity en = (GameEntity)main.menuSystem.getSelected();
				Tile t = main.menuSystem.mouseHighlighted;
				if (en != null && t != null)
				{
					if (t.biome != -1 && en.owner != null) //Removing does not seem to clear from memory, check if owner is null then
					{
						String msg = en.playerWaddleToExact(t.row, t.col);
						if (msg == null && en.action > 0)
							en.playerTick();
						else
							main.menuSystem.message(msg);
					}
				}
				main.menuSystem.select(null);
			}
			selectedRune = null;
		}
	}

	public Rune within(float mouseX, float mouseY)
	{
		for (int i = 0; i < menus.size(); i++)
		{
			for (int j = 0; j < menus.get(i).buttons.size(); j++)
			{
				Rune r = (Rune)menus.get(i).buttons.get(j);
				if (mouseX > r.posX && mouseX < r.posX + r.sizeX && mouseY > r.posY && mouseY < r.posY + r.sizeY)
					return r;
			}
		}
		return null;
	}

}
