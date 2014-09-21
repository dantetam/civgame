package game;

import java.util.ArrayList;

import data.EntityData;

public class Grid {

	private Tile[][] tiles;
	public int rows, cols;
	public Civilization[] civs;
	public boolean[][] verticalRivers;
	public boolean[][] horizontalRivers;
	//public Civilization playerCiv;
	//Player's civilization will always be the first

	public Grid(double[][] terrain, int[][] biomes, int[][] resources, int numCivs, int cutoff)
	{
		civs = new Civilization[numCivs];
		tiles = new Tile[terrain.length][terrain[0].length];
		rows = tiles.length; cols = tiles[0].length;
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				int hill = 0;
				double random = Math.random();
				//Assign a shape
				if (random < 0.025)
				{
					hill = 2;
				}
				else if (random < 0.3)
				{
					hill = 1;
				}
				boolean forest = false;
				if (biomes[r][c] == 3)
				{
					forest = Math.random() < 0.02;
				}
				else if (biomes[r][c] >= 4 && biomes[r][c] <= 6)
				{
					forest = Math.random() < 0.15;
				}
				if (terrain[r][c] >= cutoff)
					tiles[r][c] = new Tile(this,"Land",(int)terrain[r][c],biomes[r][c],hill,resources[r][c],forest,r,c);
				else
					tiles[r][c] = new Tile(this,"Sea",(int)terrain[r][c],-1,0,resources[r][c],forest,r,c);
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
		//makeRivers(terrain);
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
		if (en instanceof GameEntity)
		{
			tiles[en.location.row][en.location.col].occupants.remove(en);
			en.owner.units.remove((GameEntity)en);
		}
		else if (en instanceof TileEntity)
		{
			tiles[en.location.row][en.location.col].improvement = null;
			en.owner.improvements.remove((TileEntity)en);
		}
		en.location = null;
		en = null;
	}

	public void addTile(Civilization civ, Tile tile)
	{
		tile.owner = civ;
		//civ.tiles.add(tile);
	}

	public GameEntity hasEnemy(GameEntity attacker, int r, int c)
	{
		for (int i = 0; i < tiles[r][c].occupants.size(); i++)
		{
			GameEntity occupant = tiles[r][c].occupants.get(i);
			if (!occupant.owner.equals(attacker.owner))
			{
				return occupant;
			}
		}
		return null;
	}
	
	public void setupCivs()
	{

	}

	//Check if a tile is bordered by a river
	public boolean irrigated(int r, int c)
	{
		boolean temp = false;
		if (c > 0)
		{
			temp = temp || verticalRivers[r][c-1];
		}
		if (c < cols - 1)
		{
			temp = temp || verticalRivers[r][c];
		}
		if (r > 0)
		{
			temp = temp || horizontalRivers[r-1][c];
		}
		if (r < rows - 1)
		{
			temp = temp || horizontalRivers[r][c];
		}
		return temp;
	}
	
	//Check if a tile borders the sea
	public boolean coastal(int r, int c)
	{
		boolean temp = false;
		if (getTile(r+1,c) != null) {temp = temp || getTile(r+1,c).biome == -1;} 
		if (getTile(r-1,c) != null) {temp = temp || getTile(r-1,c).biome == -1;} 
		if (getTile(r,c-1) != null) {temp = temp || getTile(r,c-1).biome == -1;} 
		if (getTile(r,c+1) != null) {temp = temp || getTile(r,c+1).biome == -1;} 
		if (getTile(r+1,c+1) != null) {temp = temp || getTile(r+1,c+1).biome == -1;} 
		if (getTile(r+1,c-1) != null) {temp = temp || getTile(r+1,c-1).biome == -1;} 
		if (getTile(r-1,c+1) != null) {temp = temp || getTile(r-1,c+1).biome == -1;} 
		if (getTile(r-1,c-1) != null) {temp = temp || getTile(r-1,c-1).biome == -1;} 
		return temp;
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
