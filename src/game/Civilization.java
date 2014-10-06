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
	
	public TechTree techTree;
	public String researchTech;
	//public ArrayList<Tile> tiles;
	
	public int food, gold, metal, research;
	
	public boolean[][] revealed;
	
	public Civilization(String name)
	{
		cities = new ArrayList<City>();
		//capital = null;
		units = new ArrayList<GameEntity>();
		improvements = new ArrayList<TileEntity>();
		enemies = new ArrayList<Civilization>();
		//tiles = new ArrayList<Tile>();
		this.name = name;
		food = 17; gold = 0; metal = 0; research = 0;
		techTree = new TechTree();
		beeline = new ArrayList<String>();
		beelineTo("Fletching");
		//System.out.println(techTree.researched("Agriculture"));
	}
	
	public boolean equals(Civilization other)
	{
		if (other == null)
		{
			return false;
		}
		return name.equals(other.name);
	}
	
	public boolean war(Civilization other)
	{
		return enemies.contains(other);
	}
	
	public String toString()
	{
		return name;
	}
	
	//Find the list of techs leading up to and including a certain target tech
	//Queue up techs to research below, zero is first
	public ArrayList<String> beeline;
	public void beelineTo(String techName)
	{
		//System.out.println(techTree.researched("Animal Husbandry").name);
		//System.out.println(techName);
		beeline.clear();
		Tech tech = techTree.researched(techName);
		//System.out.println("Tech: " + tech); 
		//System.out.println(tech.requisite);
		Tech parent = tech.requisite;
		while (true)
		{
			if (parent.researched()) {break;}
			beeline.add(0,parent.name);
			parent = parent.requisite;
		}
		beeline.add(tech.name);
		for (int i = 0; i < beeline.size(); i++)
		{
			System.out.println(beeline.get(i));
		}
		System.out.println("-----");
	}
	
}
