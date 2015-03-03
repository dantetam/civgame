package game;

import java.util.ArrayList;

public class Tech {

	public String name;
	public Tech requisite, alternative;
	public Tech[] techs = new Tech[0];
	//public ArrayList<Tech> techs;
	public int totalR, requiredR;

	private String[] unlockUnits = new String[0], unlockTileImprovements = new String[0], unlockCityImprovements = new String[0],
			unlockFieldImprovements = new String[0], unlockUnitImprovements = new String[0], obsoleteUnits = new String[0];
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
			if (!civ.techTree.obsoleteUnits.contains(unlockUnits[i]))
				civ.techTree.allowedUnits.add(unlockUnits[i]);
		for (int i = 0; i < unlockTileImprovements.length; i++)
			civ.techTree.allowedTileImprovements.add(unlockTileImprovements[i]);
		for (int i = 0; i < unlockCityImprovements.length; i++)
			civ.techTree.allowedCityImprovements.add(unlockCityImprovements[i]);
		for (int i = 0; i < unlockFieldImprovements.length; i++)
			civ.techTree.allowedFields.add(unlockFieldImprovements[i]);
		for (int i = 0; i < unlockUnitImprovements.length; i++)
			civ.techTree.allowedUnitImprovements.add(unlockUnitImprovements[i]);
		//Add civics
		if (governmentCivic != null)
		{
			civ.techTree.governmentCivics.add(governmentCivic);
		}
		if (economicCivic != null)
		{
			civ.techTree.economicCivics.add(economicCivic);
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
	public void fImpr(String... t) {unlockFieldImprovements = t;}
	public void uImpr(String... t) {unlockUnitImprovements = t;}
	
	//Getter methods
	public String[] unlockUnits() {return unlockUnits;}
	public String[] unlockTileImprovements() {return unlockTileImprovements;}
	public String[] unlockCityImprovements() {return unlockCityImprovements;}
	public String[] unlockFieldImprovements() {return unlockFieldImprovements;}
	public String[] unlockUnitImprovements() {return unlockUnitImprovements;}

	//Set what the tech makes obsolete
	//i.e. researching warband makes warrior unable to be built
	public void obsUnits(String... t) {obsoleteUnits = t;}

	public Tech addAlt(TechTree tree, String name) {alternative = tree.researched(name); return this; } //System.out.println(tree + " " + name);}

	public String toString() {return name;}

	//Returns everything that this tech unlocks 
	public String unlockString() 
	{
		String temp = "";
		for (int i = 0; i < unlockUnits.length; i++)
			temp += unlockUnits[i] + ", ";
		for (int i = 0; i < unlockCityImprovements.length; i++)
			temp += unlockCityImprovements[i] + ", ";
		for (int i = 0; i < unlockTileImprovements.length; i++)
			temp += unlockTileImprovements[i] + ", ";
		for (int i = 0; i < unlockFieldImprovements.length; i++)
			temp += unlockFieldImprovements[i] + ", ";
		for (int i = 0; i < unlockUnitImprovements.length; i++)
			temp += unlockUnitImprovements[i] + ", ";
		if (temp.equals("")) return temp;
		return temp.substring(0, temp.length() - 2);
	}

}
