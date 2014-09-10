package game;

import java.util.ArrayList;

public class Tile {

	public TileEntity improvement;
	public ArrayList<GameEntity> occupants;
	public Civilization owner;
	
	public String type;
	public int height;
	public int row, col;
	
	public Tile(String type, int height, int row, int col)
	{
		occupants = new ArrayList<GameEntity>();
		this.type = type;
		this.height = height;
		this.row = row;
		this.col = col;
	}
	
	public void addUnit(BaseEntity en)
	{
		if (en instanceof GameEntity)
		{
			
		}
		else if (en instanceof TileEntity)
		{
			
		}
		else
		{
			System.err.println("Not a game entity or tile entity");
		}
	}
	
}
