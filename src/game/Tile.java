package game;

import java.util.ArrayList;

import units.City;

public class Tile {

	public Grid grid;
	public TileEntity improvement;
	public ArrayList<GameEntity> occupants;
	public Civilization owner;
	public City city;
	public boolean harvest; //For rendering purposes
	
	public String type;
	public int height;
	public int row, col;
	public int biome;
	public int shape;
	public int resource;
	public boolean forest, freshWater, road;
	
	public Tile(Grid grid, String type, int height, int biome, int shape, int resource, boolean forest, int row, int col)
	{
		this.grid = grid;
		occupants = new ArrayList<GameEntity>();
		harvest = false;
		this.type = type;
		this.height = height;
		this.biome = biome;
		this.shape = shape;
		this.resource = resource;
		//this.sea = sea;
		this.row = row;
		this.col = col;
		this.forest = forest; freshWater = false; road = false;
	}
	
	public void addUnit(BaseEntity en)
	{
		if (en instanceof GameEntity)
		{
			occupants.add((GameEntity)en);
		}
		else if (en instanceof TileEntity)
		{
			improvement = (TileEntity)en;
		}
		else
		{
			System.err.println("Not a game entity or tile entity");
		}
		en.location = this;
		
	}
	
	public boolean equals(Tile t)
	{
		return row == t.row && col == t.col;
	}
	
}
