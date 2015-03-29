package game;

import java.util.ArrayList;
import java.util.HashMap;

import data.EntityData;
import data.Improvement;
import units.City;

public class Civilization {

	public String name;
	public int id;
	public float r,g,b; //primary colors
	public float sR,sG,sB; //secondary colors

	public ArrayList<City> cities;
	public City capital;
	public ArrayList<GameEntity> units;
	public ArrayList<TileEntity> improvements;
	public HashMap<String, Improvement> unitImprovements; //one chosen improvement for each future unit of a certain name
	private ArrayList<Civilization> enemies, openBorders, allies;

	//Use a scale of 0 to 1
	//: war, willingness to declare war for economic gain; 
	//peace, willingness to peace out of a war when progress is made on either side;
	//tallwide, civlization's style of settling - 
	//play 0 tall (build a few large cities with many tiles) or 
	//1 wide (build many small cities close to each other)
	public float war, peace, tallwide;
	public String governmentCivic = "Decentralization", economicCivic = "Tribal Economy";
	public String primaryTrait = "", secondaryTrait = "";
	
	public TechTree techTree;
	public String researchTech;
	public ArrayList<String> bonuses;
	//public ArrayList<Tile> tiles;

	public int gold, research;
	//public int food, gold, research; //,metal; Scrap this system 
	public int health = 0;

	public int[][] revealed;
	public int[] opinions;

	public boolean observe = false;

	public Civilization(String name, ArrayList<String> bonuses, 
			float r, float g, float b,
			double w, double p, double t)
	{
		cities = new ArrayList<City>();
		//capital = null;
		units = new ArrayList<GameEntity>();
		improvements = new ArrayList<TileEntity>();
		unitImprovements = new HashMap<String, Improvement>();
		String[] names = EntityData.allUnitNames();
		for (int i = 0; i < names.length; i++)
		{
			unitImprovements.put(names[i], null);
		}
		enemies = new ArrayList<Civilization>();
		openBorders = new ArrayList<Civilization>();
		allies = new ArrayList<Civilization>();
		//tiles = new ArrayList<Tile>();
		this.name = name;
		gold = 50; research = 0;
		//food = 17; research = 0; //metal = 0;
		techTree = new TechTree(this);
		beeline = new ArrayList<String>();
		/*if (!name.equals("Player"))
		{
			beelineTo("Metal Working");
			beelineTo("Fletching");
		}*/
		//System.out.println(techTree.researched("Agriculture"));
		this.r = r; this.g = g; this.b = b;
		this.bonuses = bonuses;
		war = (float)w; peace = (float)p; tallwide = (float)t;
		EntityData.queueTechAi(this);
	}

	public Civilization(Civilization c)
	{
		cities = new ArrayList<City>();
		//capital = null;
		units = new ArrayList<GameEntity>();
		improvements = new ArrayList<TileEntity>();
		unitImprovements = new HashMap<String, Improvement>();
		String[] names = EntityData.allUnitNames();
		for (int i = 0; i < names.length; i++)
		{
			unitImprovements.put(names[i], null);
		}
		enemies = new ArrayList<Civilization>();
		openBorders = new ArrayList<Civilization>();
		allies = new ArrayList<Civilization>();
		//tiles = new ArrayList<Tile>();
		name = c.name;
		gold = 50; research = 0;
		//food = 17; gold = 0; research = 0; //metal = 0; 
		techTree = new TechTree(this);
		beeline = new ArrayList<String>();
		/*if (!name.equals("Player"))
		{
			beelineTo("Metal Working");
			beelineTo("Fletching");
		}*/
		war = c.war; peace = c.peace; tallwide = c.tallwide;
		traits(c.primaryTrait, c.secondaryTrait);
		r = c.r; g = c.g; b = c.b;
		EntityData.queueTechAi(this);
	}
	
	public int[] revealedBox()
	{
		int minR = 1000, minC = 1000, maxR = 0, maxC = 0;
		for (int r = 0; r < revealed.length; r++)
		{
			for (int c = 0; c < revealed[0].length; c++)
			{
				if (revealed[r][c] != 0)
				{
					if (r < minR) minR = r;
					else if (r > maxR) maxR = r;
					if (c < minC) minC = c;
					else if (c > maxC) maxC = c; 
				}
			}
		}
		return new int[]{minR, minC, maxR-minR, maxC-minC};
	}

	public int count(String... unitName)
	{
		//TODO Fix method//
		/*for (int i = 0; i < unitName.length; i++)
			System.out.print(unitName[i]);*/
		int n = 0;
		for (int index = 0; index < unitName.length; index++)
		{
			for (int i = 0; i < units.size(); i++)
			{
				//System.out.println(unitName[index] + " = " + units.get(i).name);
				if (units.get(i).name.equals(unitName[index]))
					n++;
			}
			for (int i = 0; i < cities.size(); i++)
				if (cities.get(i).queue != null)
					if (cities.get(i).queue.equals(unitName[index]))
						n++;
		}
		//System.out.println();
		//System.out.println(n);
		//System.out.println("-----");
		return n;
	}

	public void cancelDeals(Civilization other)
	{
		if (openBorders.contains(other))
		{
			openBorders.remove(other);
		}
	}

	public int bordering(Civilization other)
	{
		int temp = 0;
		for (int i = 0; i < cities.size(); i++)
			temp += cities.get(i).tilesBorderingCiv(other);
		return temp;
	}

	public boolean equals(Civilization other)
	{
		if (other == null)
		{
			return false;
		}
		return name.equals(other.name);
	}

	public void war(Civilization c)
	{
		if (!isWar(c) && !equals(c))
		{
			enemies.add(c);
			c.enemies.add(this);
		}
	}

	public ArrayList<Civilization> enemies() {return enemies;}
	public ArrayList<Civilization> allies() {return allies;}

	public void peace(Civilization c)
	{
		if (isWar(c) && !equals(c))
		{
			enemies.remove(c);
			c.enemies.remove(this);
		}
	}

	public void ally(Civilization c)
	{
		if (!isAlly(c) && !equals(c))
		{
			allies.add(c);
			c.allies.add(this);
		}
	}

	public void openBorder(Civilization c)
	{
		if (!isOpenBorder(c) && !equals(c))
		{
			openBorders.add(c);
			c.openBorders.add(this);
		}
	}

	/*public boolean war(Civilization c)
	{
		if (!isWar(c))
		{
			enemies.add(c);
			c.enemies.add(this);
			return true;
		}
		else
			return false;
	}

	public boolean ally(Civilization c)
	{
		if (!isAlly(c))
		{
			allies.add(c);
			c.allies.add(this);
			return true;
		}
		else
			return false;
	}

	public boolean openBorder(Civilization c)
	{
		if (!isOpenBorder(c))
		{
			openBorders.add(c);
			c.openBorders.add(this);
			return true;
		}
		else
			return false;
	}*/

	public boolean isWar(Civilization other)
	{
		if (other == null) return false;
		return enemies.contains(other);
	}

	public boolean isAlly(Civilization other)
	{
		if (other == null) return false;
		return allies.contains(other);
	}

	public boolean isOpenBorder(Civilization other)
	{
		if (other == null) return true;
		return openBorders.contains(other);
	}

	//Unlock techs; return the civ for easy chaining
	//Store in memory for reference
	public String firstTech = null, secondTech = null;
	public double tech1 = 0, tech2 = 0;
	public Civilization tech(String tech, double research)
	{
		Tech t = techTree.researched(tech);
		//if (t != null)
		t.totalR += t.requiredR*research;
		if (t.researched()) t.unlockForCiv(this);
		if (firstTech == null)
		{
			firstTech = tech;
			tech1 = research;
		}
		else if (secondTech == null)
		{
			secondTech = tech;
			tech2 = research;
		}
		return this;
	}
	
	public void traits(String p, String s)
	{
		primaryTrait = p; 
		secondaryTrait = s;
	}
	
	public boolean trait(String t)
	{
		return primaryTrait.equals(t) || secondaryTrait.equals(t);
	}
	
	public ArrayList<Tile> land()
	{
		ArrayList<Tile> temp = new ArrayList<Tile>();
		for (int i = 0; i < cities.size(); i++)
		{
			for (int j = 0; j < cities.get(i).land.size(); j++)
			{
				temp.add(cities.get(i).land.get(j));
			}
		}
		return temp;
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
		//beeline.clear();
		Tech tech = techTree.researched(techName);
		int dis = beeline.size();
		//System.out.println("Tech: " + tech); 
		//System.out.println(tech.requisite);
		Tech parent = tech.requisite;
		while (true)
		{
			if (parent.researched()) {break;}
			beeline.add(dis,parent.name);
			parent = parent.requisite;
		}
		beeline.add(tech.name);
		/*for (int i = 0; i < beeline.size(); i++)
		{
			System.out.println(beeline.get(i));
		}
		System.out.println("-----");*/
	}

	public float researchProgress()
	{
		Tech t = techTree.researched(researchTech);
		return (float)t.totalR/(float)t.requiredR;
	}

}
