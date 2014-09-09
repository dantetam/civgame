package game;

import java.util.ArrayList;

public class Civilization {

	public String name;
	public float r,g,b;
	
	public ArrayList<GameEntity> units;
	public ArrayList<Tile> tiles;
	
	public Civilization(String name)
	{
		units = new ArrayList<GameEntity>();
		tiles = new ArrayList<Tile>();
		this.name = name;
	}
	
}
