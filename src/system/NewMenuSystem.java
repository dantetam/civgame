package system;

import game.BaseEntity;
import game.GameEntity;
import game.Tile;

import java.util.ArrayList;

import render.CivGame;
import render.MouseHelper;
import render.Rune;
import render.Menu;
import units.City;
import units.Settler;

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
		Tile h = main.menuSystem.highlighted;
		if ((main.menuSystem.getSelected() instanceof City || main.menuSystem.getSelected() instanceof Settler) && h != null)
		{
			MouseHelper mh = main.inputSystem.mouseHelper;
			/*for (int r = h.row - (mh.rHorizonLines.size()-1)/2; r <= h.row + (mh.rHorizonLines.size()-1)/2; r++)
			{
				for (int c = h.col - (mh.rVertLines.size()-1)/2; r <= h.col + (mh.rVertLines.size()-1)/2; c++)
				{
					float[] pos = mh.positionGui(r - h.row + (mh.rVertLines.size()-1)/2, c - h.col + (mh.rVertLines.size()-1)/2);
					if (pos != null)
					{
						main.textAlign(main.CENTER);
						main.fill(255,0,0);
						main.text(pos[0] + "," + pos[1], pos[0], pos[1]);
					}
				}
			}*/
			for (int r = 0; r < mh.guiPositions.length; r++)
			{
				for (int c = 0; c < mh.guiPositions[0].length; c++)
				{
					float[] pos = mh.positionGui(r,c);
					if (pos != null)
					{
						main.textAlign(main.CENTER);
						main.fill(255,0,0);
						main.text((h.row - (mh.guiPositions.length-1)/2 + c) + "," + (h.col - (mh.guiPositions[0].length-1)/2 - r), pos[0], pos[1]);
					}
				}
			}
		}

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
			showMenu(0);
		}
		else
		{
			for (int i = 0; i < menus.get(0).buttons.size(); i++)
			{
				Rune rune = (Rune)menus.get(0).buttons.get(i);
				if (!rune.equals(selectedRune))
				{
					menus.get(0).buttons.get(i).active = false;
					//menus.get(0).buttons.get(i).orderOriginal(false);
				}
			}
		}
	}

	public void showMenu(int n)
	{
		for (int i = 0; i < menus.get(n).buttons.size(); i++)
			menus.get(n).buttons.get(i).active = true;
	}

	public void hideMenu(int n)
	{
		for (int i = 0; i < menus.get(n).buttons.size(); i++)
			menus.get(n).buttons.get(i).active = false;
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
			for (int i = 0; i < menus.size(); i++)
			{
				for (int j = 0; j < menus.get(i).buttons.size(); j++)
				{
					menus.get(i).buttons.get(j).setOriginal();
				}
			}
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
				hideMenu(0);
				main.menuSystem.select(null);
			}
			else if (selectedRune.command.equals("tileDiplomat"))
			{
				if (main.menuSystem.mouseHighlighted != null)
				{
					if (main.menuSystem.mouseHighlighted.owner != null)
					{
						main.menuSystem.executeAction("diplomacy"+main.menuSystem.mouseHighlighted.owner.id);
					}
				}
			}
			else if (selectedRune.command.equals("tileWar"))
			{

			}
			else if (selectedRune.command.equals("tileDiplomat"))
			{

			}
			else
			{
				System.out.println("Invalid or non-functioning command in newMenuSystem: " + selectedRune.command);
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
				if (r.active && mouseX > r.posX && mouseX < r.posX + r.sizeX && mouseY > r.posY && mouseY < r.posY + r.sizeY)
					return r;
			}
		}
		return null;
	}

}
