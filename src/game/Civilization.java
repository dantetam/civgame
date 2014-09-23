package game;

import java.util.ArrayList;

import units.City;

public class Civilization {

	public String name;
	public float r,g,b;
	
	public ArrayList<City> cities;
	public City capital;
	public ArrayList<GameEntity> units;
	public ArrayList<TileEntity> improvements;
	public ArrayList<Civilization> enemies;
	//public ArrayList<Tile> tiles;
	
	public int food, gold, metal, research;
	
	public Civilization(String name)
	{
		cities = new ArrayList<City>();
		//capital = null;
		units = new ArrayList<GameEntity>();
		improvements = new ArrayList<TileEntity>();
		enemies = new ArrayList<Civilization>();
		//tiles = new ArrayList<Tile>();
		this.name = name;
		food = 10; gold = 0; metal = 0; research = 0;
	}
	
	public boolean equals(Civilization other)
	{
		if (other == null)
		{
			return true;
		}
		return name.equals(other.name);
	}
	
	public boolean war(Civilization other)
	{
		return enemies.contains(other);
	}
	
}
