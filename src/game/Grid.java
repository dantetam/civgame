package game;

import java.util.ArrayList;

public class Grid {

	public Tile[][] tiles;
	public Civilization[] civs;
	//public Civilization playerCiv;
	//Player's civilization will always be the first
	
	public Grid(int row, int col, int numCivs)
	{
		civs = new Civilization[numCivs];
		tiles = new Tile[row][col];
		
	}
	
	public void setupTiles(double[][] terrain)
	{
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				tiles[r][c] = new Tile((int)terrain[r][c],r,c);
			}
		}
	}
	
	public void setupCivs()
	{
		for (int i = 0; i < civs.length; i++)
		{
			Civilization civ = new Civilization("Civilization " + Long.toString((long)(System.currentTimeMillis()*Math.random())));
			civ.r = i*50;
			civ.g = i*50;
			civ.b = i*50;
			civs[i] = civ;
		}
	}
	
}
