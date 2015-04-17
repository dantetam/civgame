package system;

import game.BaseEntity;
import game.GameEntity;
import game.Tile;

import java.util.ArrayList;

import data.EntityData;
import data.Field;
import render.Button;
import render.CivGame;
import render.MouseHelper;
import render.Rune;
import render.Menu;
import render.TextBox;
import units.City;
import units.Settler;

public class NewMenuSystem extends BaseSystem {

	public ArrayList<Menu> menus;
	public Rune selectedRune = null;

	public NewMenuSystem(CivGame civGame) 
	{
		super(civGame);
		menus = new ArrayList<Menu>();

		Menu menu0 = new Menu("UnitMenu");
		Menu menu1 = new Menu("AdvisorMenu");
		
		menus.add(menu0);
		menus.add(menu1);
		/*menus.add(menu0);
		menus.get(0).buttons.add(new Rune("image","tileMove",675,425,50,50).color(0,255,0));

		menus.add(menu1);*/
		/*menus.get(1).buttons.add(new Rune("diplomat","tileDiplomat",600,10,50,50).color(0,0,255));
		menus.get(1).buttons.add(new Rune("war","tileWar",660,10,50,50).color(255,0,0));
		menus.get(1).buttons.add(new Rune("trader","tileTrader",720,10,50,50).color(200,200,0));*/
	}

	public void tick() 
	{
		main.hint(main.DISABLE_DEPTH_TEST);
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
			//if (menus.get(0).buttons.size() > 0)
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

	public boolean notOverlapping(float x, float y)
	{
		for (int i = 0; i < main.menuSystem.noOverlap.size(); i++)
		{
			TextBox t = main.menuSystem.noOverlap.get(i);
			if (t.within(x, y))
				return false;
		}
		return true;
	}

	public void tileIcon(float posX, float posY, int f, int g, int m, int r, boolean bold)
	{
		//Show biome
		//image = EntityData.iconMap.get("Capital");
		//main.image(image, posX - 3*len/2, posY - 30 - len/2, len, len);
		main.noStroke();
		if (!notOverlapping(posX, posY)) return;

		int len = 24, off = 0; //off = -30

		int i = 0;
		//main.image(image, posX - len/2 - len, posY - 30 - i*30 - len/2, len, len);
		float alpha = bold ? 255 : 150;
		//f g
		//m r
		main.tint(0,255,0,alpha);
		main.image(EntityData.iconMap.get("food"), posX - len/2, posY + off - i*30 - len/2, len/2, len/2);
		main.tint(255,150,0,alpha);
		main.image(EntityData.iconMap.get("metal"), posX - len/2, posY + off - i*30, len/2, len/2);
		main.tint(255,255,0,alpha);
		main.image(EntityData.iconMap.get("gold"), posX - len/2 + len, posY + off - i*30 - len/2, len/2, len/2);
		main.tint(150,225,255,alpha);
		main.image(EntityData.iconMap.get("research"), posX - len/2 + len, posY + off - i*30, len/2, len/2);

		alpha = bold ? 255 : 150;

		main.textAlign(main.LEFT, main.TOP);
		if (bold)
		{
			/*main.fill(0,0,0,100);
			main.rect(posX, posY + off - i*30 - len/2, len/2, len/2);
			main.rect(posX, posY + off - i*30, len/2, len/2);
			main.rect(posX + len, posY + off - i*30 - len/2, len/2, len/2);
			main.rect(posX + len, posY + off - i*30, len/2, len/2);*/
			main.textSize(18);
			//main.fill(200,0,0,alpha);
			main.fill(255,255,255,alpha);
		}
		else
		{
			main.textSize(12);
			main.fill(255,255,255,alpha);
		}
		main.pushStyle();
		main.text(f, posX, posY + off - i*10 - len/2);
		main.text(m, posX, posY + off - i*10);
		main.text(g, posX + len, posY + off - i*10 - len/2);
		main.text(r, posX + len, posY + off - i*10);
		main.textSize(12);
		main.popStyle();
	}

	//X position of center, Y position of center, which resource is being shown,
	//Number of the resource yielded from harvest, total number of icons, and the position of showing (i.e. left most is 1)
	/*public void tileIcon(float posX, float posY, int type, int numBlocks, int n, int i)
	{
		float size = 10, space = 0, alpha = 75;
		main.rectMode(main.CENTER);
		main.ellipseMode(main.CENTER);
		if (n == 0)
			return;
		else if (n == 1)
		{
			//do nothing
		}
		else if (n == 2)
		{
			if (i == 1)
				posX -= size + space;
			else
				posX += size + space;
		}
		else if (n == 3)
		{
			if (i == 1)
				posX -= size*2 + space;
			else if (i == 3)
				posX += size*2 + space;
			//else (i == 2) //do nothing;
		}
		else
		{
			if (i == 1)
				posX -= size*2 + space*2;
			else if (i == 2)
				posX -= size + space;
			else if (i == 3)
				posX += size + space;
			else if (i == 4)
				posX += size*2 + space*2;
		}
		if (type == 0)
		{
			main.fill(0,200,0,alpha);
			if (numBlocks == 1)
				main.ellipse(posX, posY, size, size);
			else if (numBlocks == 2)
			{
				main.ellipse(posX - size/2, posY, size, size);
				main.ellipse(posX + size/2, posY, size, size);
			}
			else
			{
				main.ellipse(posX, posY - size/2, size, size);
				main.ellipse(posX - size/2, posY + size/2, size, size);
				main.ellipse(posX + size/2, posY + size/2, size, size);
			}
		}
		else if (type == 1)
		{
			main.fill(255,255,0,alpha);
			if (numBlocks == 1)
				main.ellipse(posX, posY, size, size);
			else if (numBlocks == 2)
			{
				main.ellipse(posX - size/2, posY, size, size);
				main.ellipse(posX + size/2, posY, size, size);
			}
			else
			{
				main.ellipse(posX, posY - size/2, size, size);
				main.ellipse(posX - size/2, posY + size/2, size, size);
				main.ellipse(posX + size/2, posY + size/2, size, size);
			}
		}
		else if (type == 2)
		{
			main.fill(255,140,0,alpha);
			if (numBlocks == 1)
				main.rect(posX, posY, size, size);
			else if (numBlocks == 2)
			{
				main.rect(posX - size/2, posY, size, size);
				main.rect(posX + size/2, posY, size, size);
			}
			else
			{
				main.rect(posX, posY - size/2, size, size);
				main.rect(posX - size/2, posY + size/2, size, size);
				main.rect(posX + size/2, posY + size/2, size, size);
			}
		}
		else //if (type == 3)
		{
			main.fill(0,0,200,alpha);
			main.beginShape(main.TRIANGLES);
			main.vertex(posX - size/2, posY + size/2);
			main.vertex(posX + size/2, posY + size/2);
			main.vertex(posX, posY - size/2);
			main.endShape();
		}
		main.fill(255);
		main.rectMode(main.CORNER);
		main.ellipseMode(main.CORNER);
	}*/

	//Another method that shows GUIs for a tile's fields
	public void fieldIcon(float posX, float posY, Tile t, int n, float len1, float len2)
	{ 
		if (!notOverlapping(posX, posY)) return;
		int space = 0;
		boolean exists = false;
		if (n >= t.fields.size())
		{
			main.fill(150,150,150,50);
			//exists = false;
		}
		else
		{
			Field f = t.fields.get(n);
			if (f.owner == null)
			{
				main.fill(150,150,150,175);
				//exists = true;
			}
			else if (f.status == 0)
			{
				main.fill(t.owner.r, t.owner.g, t.owner.b, 175);
				main.noStroke();
				//exists = true;
			}
			else if (f.status == 1)
			{
				main.strokeWeight(3);
				main.stroke(t.owner.r, t.owner.g, t.owner.b);
				main.fill(150,150,150,175);
				//exists = true;
			}
			else if (f.status == 2)
			{
				main.strokeWeight(3);
				main.stroke(0);
				main.fill(150,150,150,175);
				//exists = true;
			}
			else if (f.status == 3)
			{
				main.noStroke();
				main.fill(0,0,0,175);
				//exists = true;
			}
			exists = true;
		}
		float x, y;
		if (n == 0)
		{
			x = posX - len1/2 - space - len2;
			y = posY - len1/2;
		}
		else if (n == 1)
		{
			x = posX - len1/2 + space + len2;
			y = posY - len1/2;
		}
		else if (n == 2)
		{
			y = posY - len1/2 - space - len2;
			x = posX - len1/2;
		}
		else if (n == 3)
		{
			y = posY - len1/2 + space + len2;
			x = posX - len1/2;
		}
		else {System.out.println("Error: newmenusystem, no tile icon"); x = 0; y = 0;} 
		//Replace with an actual error later?
		//y += len2;

		main.rect(x, y, len1, len1);
		if (!exists)
		{
			main.fill(255,0,0,50);
			main.ellipse(x+len1/2, y+len1/2, len1, len1);
		}
		main.strokeWeight(1);
	}

	public void largeFieldIcon(float posX, float posY, Tile t, float len)
	{
		main.noStroke();
		if (!notOverlapping(posX, posY)) return;
		float[] fill = new float[4];
		if (t.owner == null)
		{
			fill = new float[]{150,150,150,175};
		}
		else
		{
			fill = new float[]{t.owner.r, t.owner.g, t.owner.b, 255};
		}
		Button b = (Button)main.menuSystem.menus.get(14).addButton("fieldMenu" + t.row + "," + t.col, "", "", posX - len/2F, posY - len/2F, len, len);
		b.r = fill[0]; b.g = fill[1]; b.b = fill[2]; b.alpha = fill[3];
		b.tooltip.clear();
		String fieldString = "";
		for (int i = 0; i < t.fields.size(); i++)
			fieldString = t.fields.get(i).name + " ,";
		if (t.fields.size() > 0)
			b.tooltip.add(fieldString + "; " + t.fields.size() + "/" + t.maxFields);
		else
			b.tooltip.add("No fields" + "; " + t.fields.size() + "/" + t.maxFields);
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
