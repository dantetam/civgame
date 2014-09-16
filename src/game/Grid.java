package game;

import java.util.ArrayList;

import data.EntityData;

public class Grid {

	private Tile[][] tiles;
	public int rows, cols;
	public Civilization[] civs;
	//public Civilization playerCiv;
	//Player's civilization will always be the first

	public Grid(double[][] terrain, int[][] biomes, int numCivs, int cutoff)
	{
		civs = new Civilization[numCivs];
		tiles = new Tile[terrain.length][terrain[0].length];
		rows = tiles.length; cols = tiles[0].length;
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				int hill = Math.random() < 0.25 ? 1 : 0;
				if (terrain[r][c] >= cutoff)
					tiles[r][c] = new Tile("Land",(int)terrain[r][c],biomes[r][c],hill,r,c);
				else
					tiles[r][c] = new Tile("Sea",(int)terrain[r][c],-1,0,r,c);
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
			//with proper encapsulation
			//addTile(civs[i], tiles[r][c]);

			BaseEntity en = EntityData.get("Settler");
			addUnit(en,civs[i],r,c);
		}
	}

	public void move(BaseEntity en, int rDis, int cDis)
	{
		int r = en.location.row; int c = en.location.col;
		if (r+rDis >= 0 && r+rDis < tiles.length && c+cDis >= 0 && c+cDis < tiles.length)
		{
			tiles[r][c].occupants.remove(en);
			en.location = tiles[r+rDis][c+cDis];
			en.location.addUnit(en);
		}
	}

	public void moveTo(BaseEntity en, int r, int c)
	{
		tiles[en.location.row][en.location.col].occupants.remove(en);
		en.location = tiles[r][c];
		en.location.addUnit(en);
	}

	public void addUnit(BaseEntity en, Civilization civ, int r, int c)
	{
		en.owner = civ;
		if (en instanceof GameEntity)
			civ.units.add((GameEntity)en);
		else if (en instanceof TileEntity)
			civ.improvements.add((TileEntity)en);
		tiles[r][c].addUnit(en);
	}

	public void removeUnit(BaseEntity en)
	{
		tiles[en.location.row][en.location.col].occupants.remove(en);
		if (en instanceof GameEntity)
			en.owner.units.remove((GameEntity)en);
		else if (en instanceof TileEntity)
			en.owner.improvements.remove((TileEntity)en);
		en.location = null;
		en = null;
	}

	public void addTile(Civilization civ, Tile tile)
	{
		tile.owner = civ;
		civ.tiles.add(tile);
	}

	public void setupCivs()
	{

	}

	public Tile getTile(int r, int c)
	{
		if (r >= 0 && r < tiles.length && c >= 0 && c < tiles[0].length)
		{
			return tiles[r][c];
		}
		return null;
	}
	
	//public Tile[][] getTiles() {return tiles;}

}
