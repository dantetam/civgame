package game;

import java.util.ArrayList;
import java.util.HashMap;

import units.City;

//the following may or may not actually be a "tree" data structure

public class TechTree {

	public Civilization civ;
	public Tech first;
	//All the valid things that the player can queue
	public ArrayList<String> allowedUnits, allowedTileImprovements, allowedCityImprovements, allowedFields, allowedUnitImprovements;
	public ArrayList<String> obsoleteUnits;
	public ArrayList<String> governmentCivics, economicCivics;
	//public HashMap<String, String> unlockUnits, unlockTileImprovements, unlockCityImprovements;

	public TechTree(Civilization civ)
	{
		this.civ = civ;
		allowedUnits = new ArrayList<String>();
		allowedTileImprovements = new ArrayList<String>();
		allowedCityImprovements = new ArrayList<String>();
		allowedUnitImprovements = new ArrayList<String>();
		allowedFields = new ArrayList<String>();
		obsoleteUnits = new ArrayList<String>();
		governmentCivics = new ArrayList<String>();
		governmentCivics.add("Decentralization");
		economicCivics = new ArrayList<String>();
		economicCivics.add("Tribalism");
		//unlockUnits = new HashMap<String, String>();
		//unlockTileImprovements = new HashMap<String, String>();
		//unlockCityImprovements = new HashMap<String, String>();
		first = 
				new Tech("Civilization", 0,
					new Tech("Agriculture", 30,
						new Tech("Fishing", 60, null),
						new Tech("Forestry", 60,
							new Tech("Sailing", 100, null),
							new Tech("Architecture", 100, null),
								new Tech("Silviculture", 100, 
									new Tech("Terraforming", 200, null)
								)
						),
						new Tech("Hunting", 60, null),
							new Tech("Milling", 60, 
								new Tech("Fletching", 100, null)
							)
					),
					new Tech("Mining", 30,
						new Tech("Pottery", 30, null),
						new Tech("Metal Working", 120, 
							new Tech("Currency", 100, null),
							new Tech("Casting", 100, null)
						),
						new Tech("Stone Working", 60,
							new Tech("Monument Building", 100, null)
						)
					),
					new Tech("Animal Husbandry", 60,
						new Tech("Equestrian Practice", 100, null)
					),
					new Tech("Monotheism", 30,
						new Tech("Organized Religion", 60, null),
						new Tech("Writing", 100, null)
					),
					new Tech("Polytheism", 30,
						new Tech("Sacrificial Tradition", 60, null)
					)
				);
		setupTechs();
	}

	private void setupTechs()
	{
		Tech t; 
		
		t = researched("Civilization");
			t.units("Settler", "Warrior", "Worker", "Slinger");
			t.cImpr("Obelisk");
			t.fImpr("TestField");
			t.uImpr("Neutral");
			t.governmentCivic = "Decentralization";
			t.economicCivic = "Tribalism";

		t = researched("Agriculture");
			t.units("Warband");
			t.tImpr("Farm");
			t.cImpr("Granary");
			t.governmentCivic = "Collective Rule";

		t = researched("Fishing");
			t.units("Work Boat");
			t.cImpr("Port");
			
		t = researched("Forestry");	
			//t.cImpr("Hut");
		
		t = researched("Hunting");
			//t.tImpr("Trading Post");
			t.units("Scout");
			//t.cImpr("Butcher");
			t.obsUnits("Warrior");
			
		t = researched("Milling");
			t.tImpr("Windmill");
			
		t = researched("Sailing");
			t.units("Galley");
			
		t = researched("Architecture");
			t.tImpr("Fort");
			//t.cImpr("Shrine");
			
		t = researched("Silviculture");
			t.tImpr("Forest Yard");
			
		t = researched("Fletching");
			t.units("Archer", "Horse Archer");
			t.obsUnits("Slinger");
			
		t = researched("Terraforming");
			//t.tImpr("Quarry");
			
		t = researched("Mining");
			t.tImpr("Mine");
			t.units("Axeman");
			t.cImpr("Warehouse");

		t = researched("Pottery");

		t = researched("Metal Working");
			t.units("Swordsman", "Spearman");
			t.cImpr("Metalworks");
			t.uImpr("CopperTools", "CopperWeapons", "IronWeapons");
			t.obsUnits("Warrior");
			t.economicCivic = "Slavery";
			
		t = researched("Stone Working");
			t.tImpr("Light Fortifications");
			t.cImpr("Walls", "Palace");
			
		t = researched("Currency");
			t.cImpr("Market");
			t.economicCivic = "Controlled Economy";
		
		t = researched("Casting");
			t.tImpr("Forge");
			t.uImpr("CopperArrows", "IronArrows");
			t.units("Axe Thrower");
		
		t = researched("Currency");
			t.cImpr("Ziggurat");
		
		t = researched("Animal Husbandry");
			t.tImpr("Pasture");
			t.units("Chariot");
			
		t = researched("Equestrian Practice");
			t.units("Horseman");
			t.cImpr("Stables");
			t.governmentCivic = "Tribal Rule";
			
		t = researched("Monotheism");	
			t.cImpr("Temple");
			
		t = researched("Polytheism");
			t.cImpr("Temple");
		
		t = researched("Writing");
			t.addAlt(this,"Polytheism");
			t.cImpr("Library", "Trade Depot");
			t.governmentCivic = "Theocracy";
	}

	//Find potential unlocked items
	public ArrayList<String> findUnlockables()
	{
		ArrayList<String> temp = new ArrayList<String>();
		ArrayList<Tech> techs = findCandidates(first);
		for (int i = 0; i < techs.size(); i++)
		{
			Tech t = techs.get(i);
			String[][] data = {t.unlockUnits(), t.unlockTileImprovements(), t.unlockCityImprovements(), t.unlockFieldImprovements()};
			for (int j = 0; j < data.length; j++)
				for (int k = 0; k < data[j].length; k++)
					if (!temp.contains(data[j][k]))
						temp.add(data[j][k]); //jk?
		}
		return temp;
	}
	
	//Syntax shortcut for later, no need to pass first argument
	public ArrayList<String> findCandidates()
	{
		ArrayList<String> temp = new ArrayList<String>();
		this.temp.clear();
		ArrayList<Tech> techs = findCandidates(first);
		for (int i = 0; i < techs.size(); i++)
		{
			temp.add(techs.get(i).name);
		}
		return temp;
	}

	//Find techs to research
	//It's better practice to pass around the table
	ArrayList<Tech> temp = new ArrayList<Tech>();
	private ArrayList<Tech> findCandidates(Tech tech)
	{
		for (int i = 0; i < tech.techs.length; i++)
		{
			Tech t = tech.techs[i];
			if (t.alternative != null)
				if (t.alternative.researched() && !t.researched() && !temp.contains(t))
				{
					temp.add(t);
					continue;
				}
			if (t.researched())
			{
				findCandidates(t);
			}
			else
			{
				temp.add(t);
			}
		}
		return temp;
	}

	//Check if a city has already built the buildings
	public ArrayList<String> allowedCityImprovements(City c)
	{		
		if (c.buildings.size() == 0) return allowedCityImprovements;
		ArrayList<String> temp = new ArrayList<String>();
		//System.out.println(allowedCityImprovements.size());
		for (int i = 0; i < allowedCityImprovements.size(); i++)
		{
			String s = allowedCityImprovements.get(i);
			for (int j = 0; j < c.buildings.size(); j++)
			{
				if (c.buildings.get(j).name.equals(s))
					break;
				if (j == c.buildings.size() - 1) //Add the candidate if the loop does not break
					temp.add(s);
			}
		}
		return temp;
	}
	
	/*public void printOut()
	{

	}*/

	/*public boolean researched(String name)
	{
		if (name.equals("Civilization")) return true;
		boolean temp;

	}*/

	/*public boolean researched(String name)
	{
		return evalOrLower(first, name);
	}*/

	/*private boolean evalOrLowerBoolean(Tech start, String name)
	{
		boolean temp = false;
		if (start.name.equals(name))
		{
			return true;
		}
		for (int i = 0; i < start.techs.length; i++)
		{
			if (start.techs[i].name.equals(name))
			{
				return start.techs[i].researched();
			}
			else
			{
				temp = temp || evalOrLower(start.techs[i], name);
			}
		}
		return temp;
	}*/
	
	public String unlockedBy(String impr)
	{
		return returnIfUnlocksImpr(impr, new Tech[]{first}).name;
	}
	
	private Tech returnIfUnlocksImpr(String impr, Tech[] techs)
	{
		for (int i = 0; i < techs.length; i++)
		{
			if (techs[i].unlockString().contains(impr))
				return techs[i];
		}
		for (int i = 0; i < techs.length; i++)
		{
			Tech t = returnIfUnlocksImpr(impr, techs[i].techs);
			if (t != null)
				return t;
		}
		return null;
	}

	public Tech researched(String name)
	{
		return researched(first, name);
	}

	//Recursive method to find a tech within the tree
	private Tech researched(Tech start, String name)
	{
		//System.out.println("Looking for tech: " + name + "; In directory: " + start.name);
		if (start.name.equals(name))
			return start;
		for (int i = 0; i < start.techs.length; i++)
		{
			//System.out.println(start.techs[i].name);
			if (start.techs[i].name.equals(name))
				return start.techs[i];
			else
			{
				Tech candidate = researched(start.techs[i], name);
				if (candidate != null)
					return candidate;
				//else continue;
			}
		}
		return null;
	}

	/*private Tech researched(Tech start, String name)
	{
		Tech temp = null;
		if (start.name.equals(name))
		{
			return start;
		}
		for (int i = 0; i < start.techs.length; i++)
		{
			if (start.techs[i].name.equals(name))
			{
				temp = start.techs[i];
			}
			else
			{
				researched(start.techs[i], name);
			}
		}
		return temp;
	}*/

}
