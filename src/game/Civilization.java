package game;

import java.util.ArrayList;

import units.City;

public class Civilization {

	public String name;
	public float r,g,b;
	
	public ArrayList<City> cities;
	public ArrayList<GameEntity> units;
	public ArrayList<TileEntity> improvements;
	public ArrayList<Tile> tiles;
	
	public int food, gold, metal;
	
	public Civilization(String name)
	{
		cities = new ArrayList<City>();
		units = new ArrayList<GameEntity>();
		improvements = new ArrayList<TileEntity>();
		tiles = new ArrayList<Tile>();
		this.name = name;
		food = 0; gold = 0; metal = 0;
	}
	
}
