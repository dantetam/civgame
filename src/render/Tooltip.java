package render;

import java.util.ArrayList;

import game.BaseEntity;
import game.GameEntity;

//TODO: Fix backwards GUI inheritances

public class Tooltip {

	public float posX, posY;
	public float sizeX, sizeY;
	public ArrayList<String> display;
	public boolean active = false;

	public Tooltip(String s, float a, float b, float c, float d)
	{
		display = new ArrayList<String>();
		display.add(s);
		posX = a;
		posY = b;
		sizeX = c;
		sizeY = d;
	}

	public void dimTooltip(ArrayList<GameEntity> occupants, BaseEntity improvement)
	{
		int max = 0;
		for (int i = 0; i < occupants.size(); i++)
		{
			int len = (occupants.get(i).name + " (" + occupants.get(i).owner + ")").length();
			if (len > max) max = len;
		}
		if (improvement != null)
		{
			int len = (improvement.name + " (" + improvement.owner + ")").length();
			if (len > max) max = len;
		}
		sizeX = 7*max;
		if (occupants.size() == 1)
			sizeY = 20;
		else
		{
			sizeY = 14*occupants.size();
			if (improvement != null)
				sizeY += 14;
		}
	}

}
