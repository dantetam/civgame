package game;

import java.util.ArrayList;

public class Tech {

	public String name;
	public Tech requisite, alternative;
	public Tech[] techs;
	//public ArrayList<Tech> techs;
	public int totalR, requiredR;
	
	private String[] unlockUnits = new String[0], unlockTileImprovements = new String[0], unlockCityImprovements = new String[0];
	private String[] obsoleteUnits = new String[0];
	public String governmentCivic, economicCivic = null;
	public Tech(String name, int requiredR, Tech... t)
	{
		this.name = name;
		if (t == null)
		{
			techs = new Tech[0];
		}
		else
		{
			techs = t;
			for (int i = 0; i < techs.length; i++)
			{
				techs[i].requisite = this;
			}
		}
		totalR = 0; this.requiredR = requiredR;
	}

	//When the tech is completed, unlock the units for the civ, allow them to be queued
	public void unlockForCiv(Civilization civ)
	{
		//Add discoveries
		for (int i = 0; i < unlockUnits.length; i++)
		{
			if (!civ.techTree.obsoleteUnits.contains(unlockUnits[i]))
				civ.techTree.allowedUnits.add(unlockUnits[i]);
		}
		for (int i = 0; i < unlockTileImprovements.length; i++)
		{
			civ.techTree.allowedTileImprovements.add(unlockTileImprovements[i]);
		}
		for (int i = 0; i < unlockCityImprovements.length; i++)
		{
			civ.techTree.allowedCityImprovements.add(unlockCityImprovements[i]);
		}
		//Remove obsolete units
		for (int i = 0; i < obsoleteUnits.length; i++)
		{
			civ.techTree.allowedUnits.remove(unlockUnits[i]);
			civ.techTree.obsoleteUnits.add(unlockUnits[i]);
		}
	}
	
	public boolean researched()
	{
		return totalR >= requiredR;
	}
	
	//Set what the tech can unlock for a player
	public void units(String... t) {unlockUnits = t;}
	public void tImpr(String... t) {unlockTileImprovements = t;}
	public void cImpr(String... t) {unlockCityImprovements = t;}
	
	//Set what the tech makes obsolete
	//i.e. researching warband makes warrior unable to be built
	public void obsUnits(String... t) {obsoleteUnits = t;}
	
	public Tech addAlt(TechTree tree, String name) {alternative = tree.researched(name); return this; } //System.out.println(tree + " " + name);}

}
