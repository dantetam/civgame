package game;

import java.util.ArrayList;

public class Civilization {

	public String name;
	public float r,g,b;
	
	public ArrayList<GameEntity> units;
	public ArrayList<TileEntity> improvements;
	public ArrayList<Tile> tiles;
	
	public Civilization(String name)
	{
		units = new ArrayList<GameEntity>();
		improvements = new ArrayList<TileEntity>();
		tiles = new ArrayList<Tile>();
		this.name = name;
	}
	
}
