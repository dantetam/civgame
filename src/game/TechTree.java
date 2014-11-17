package game;

import java.util.ArrayList;
import java.util.HashMap;

//the following may or may not actually be a "tree"

public class TechTree {

	public Tech first;
	//All the valid things that the player can queue
	public ArrayList<String> allowedUnits, allowedTileImprovements, allowedCityImprovements;
	//public HashMap<String, String> unlockUnits, unlockTileImprovements, unlockCityImprovements;

	public TechTree()
	{
		allowedUnits = new ArrayList<String>();
		allowedTileImprovements = new ArrayList<String>();
		allowedCityImprovements = new ArrayList<String>();
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
		t.units("Settler", "Warrior", "Worker");

		t = researched("Agriculture");
		t.tImpr("Farm");
		t.cImpr("Granary");

		t = researched("Mining");
		t.tImpr("Mine");

		t = researched("Pottery");

		t = researched("Writing");
		t.addAlt(this,"Polytheism");
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
			{
				if (t.alternative.researched() && !t.researched() && !temp.contains(t))
				{
					temp.add(t);
					continue;
				}
			}
			if (t.researched())
			{
				findCandidates(t);
			}
			else
			{
				temp.add(t);
				//findCandidates(t);
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
