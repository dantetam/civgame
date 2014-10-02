package game;

import java.util.ArrayList;

public class Tech {

	public String name;
	public Tech requisite;
	public Tech[] techs;
	//public ArrayList<Tech> techs;
	public int totalR, requiredR;

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

	public boolean researched()
	{
		return totalR >= requiredR;
	}

}
