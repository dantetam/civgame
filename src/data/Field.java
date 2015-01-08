package data;

import game.Civilization;

public class Field extends Improvement {

	public Civilization owner;
	
	public Field(Civilization civ, String name, String tech) {
		super(name, tech);
		owner = civ;
	}
	
	public Field(Field f)
	{
		super(f);
	}
	
	/*public Field clone()
	{
		try {
			return (Field)super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("No clone");
			return null;
		}
	}*/

}
