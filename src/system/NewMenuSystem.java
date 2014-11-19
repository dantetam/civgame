package system;

import java.util.ArrayList;

import render.CivGame;
import render.Rune;

public class NewMenuSystem extends BaseSystem {

	public ArrayList<Rune> runes;
	public Rune selectedRune = null;

	public NewMenuSystem(CivGame civGame) {
		super(civGame);
		runes = new ArrayList<Rune>();
		runes.add(new Rune("","",200,200,100,100));
	}

	public void tick() 
	{
		for (int i = 0; i < runes.size(); i++)
		{
			Rune rune = runes.get(i);
			main.fill(0);
			main.rect(rune.posX, rune.posY, rune.sizeX, rune.sizeY);
		}
		main.hint(main.ENABLE_DEPTH_TEST);
	}

	public float lastMouseX, lastMouseY;
	public void mouseDragged(float mouseX, float mouseY)
	{
		if (selectedRune == null)
		{
			Rune rune = within(mouseX, mouseY);
			if (rune != null)
			{
				selectedRune = rune;
			}
		}
		if (selectedRune != null)
		{
			//Seems like a redundant calculation
			float dX = mouseX - lastMouseX, dY = mouseY - lastMouseY;
			//rune.posX = 500;
			selectedRune.posX += dX; selectedRune.posY += dY;
			//rune.moveTo(mouseX - dX, mouseY - dY, 5);
			lastMouseX = mouseX; lastMouseY = mouseY;
		}
	}

	public void mouseReleased(float mouseX, float mouseY)
	{
		selectedRune = null;
	}

	public Rune within(float mouseX, float mouseY)
	{
		for (int i = 0; i < runes.size(); i++)
		{
			Rune r = runes.get(i);
			if (mouseX > r.posX && mouseX < r.posX + r.sizeX && mouseY > r.posY && mouseY < r.posY + r.sizeY)
				return r;
		}
		return null;
	}

}
