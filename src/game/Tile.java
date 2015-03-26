package game;

import java.util.ArrayList;

import data.Field;
import units.City;

public class Tile {

	public Grid grid;
	public TileEntity improvement;
	public ArrayList<GameEntity> occupants;
	public ArrayList<Field> fields; public int maxFields;
	public Civilization owner;
	public City city;
	public boolean harvest; //For rendering purposes
	
	public String type;
	public int height;
	public int row, col, biome, shape, resource;
	public boolean forest, freshWater, road;
	public int turnsSettled = 0;
	
	//Amount of culture currently in this tile from the owner
	//When it reaches zero, it can be taken
	public int culture; 
	
	public Tile(Grid grid, String type, int height, int biome, int shape, int resource, boolean forest, int row, int col, int maxFields)
	{
		this.grid = grid;
		occupants = new ArrayList<GameEntity>();
		fields = new ArrayList<Field>();
		this.maxFields = maxFields;
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
		if (t == null) return false;
		return row == t.row && col == t.col;
	}
	
	public double dist(Tile t)
	{
		return Math.sqrt(Math.pow(row-t.row,2) + Math.pow(col-t.col,2));
	}
	
	public int manhattan(Tile t)
	{
		return Math.abs(t.row - row) + Math.abs(t.col - col);
	}
	
	public String toString() {return "[" + row + "," + col + "]";}
	
}
