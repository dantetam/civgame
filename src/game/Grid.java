package game;

import java.util.ArrayList;

import data.EntityData;

public class Grid {

	public Tile[][] tiles;
	public Civilization[] civs;
	//public Civilization playerCiv;
	//Player's civilization will always be the first

	public Grid(double[][] terrain, int numCivs, int cutoff)
	{
		civs = new Civilization[numCivs];
		tiles = new Tile[terrain.length][terrain[0].length];
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				if (terrain[r][c] >= cutoff)
					tiles[r][c] = new Tile("Land",(int)terrain[r][c],r,c);
				else
					tiles[r][c] = new Tile("Sea",(int)terrain[r][c],r,c);
			}
		}
		for (int i = 0; i < civs.length; i++)
		{
			Civilization civ = new Civilization("Civilization " + Long.toString((long)(System.currentTimeMillis()*Math.random())));
			civ.r = (float)(Math.random()*255);
			civ.g = (float)(Math.random()*255);
			civ.b = (float)(Math.random()*255);
			civs[i] = civ;

			int r,c;
			do
			{
				r = (int)(Math.random()*tiles.length);
				c = (int)(Math.random()*tiles[0].length);
			} while (tiles[r][c].type.equals("Sea"));
			//Test out giving a civilization land and a unit
			tiles[r][c].owner = civs[i];
			
			BaseEntity en = EntityData.get("Settler");
			en.owner = civs[i];
			tiles[r][c].addUnit(en);
		}
	}

	public void setupCivs()
	{

	}

}
