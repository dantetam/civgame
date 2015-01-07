package data;

import game.Civilization;

public class Field extends Improvement {

	public Civilization owner;
	
	public Field(Civilization civ, String name, String tech) {
		super(name, tech);
		owner = civ;
	}

}
