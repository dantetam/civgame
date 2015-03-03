package data;

import game.Civilization;

public class Field extends Improvement {

	public Civilization owner;
	public int status = 0; 
	/*
	 * 0 -> directly owned
	 * 1 -> tributary
	 * 2 -> savage
	 * 3 -> barbarian
	 */
	public double autonomy = 0;
	
	public Field(Civilization civ, String name, String tooltip) {
		super(name, tooltip);
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
