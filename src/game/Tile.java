package game;

import java.util.ArrayList;

public class Tile {

	public TileEntity improvement;
	public ArrayList<GameEntity> occupants;
	public Civilization owner;
	
	public String type;
	public int height;
	public int rows, cols;
	
	public Tile(String type, int height, int rows, int cols)
	{
		occupants = new ArrayList<GameEntity>();
		this.type = type;
		this.height = height;
		this.rows = rows;
		this.cols = cols;
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
	
}
