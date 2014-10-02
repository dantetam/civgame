package game;

import java.util.ArrayList;

public class Tech {

	public String name;
	public Tech requisite;
	public Tech[] techs;
	//public ArrayList<Tech> techs;
	public boolean researched;
	
	public Tech(String name, Tech requisite, Tech... t)
	{
		this.name = name;
		this.requisite = requisite;
		techs = t;
	}
	
}
