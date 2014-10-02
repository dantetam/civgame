package game;

//the following may or may not actually be a "tree"

public class TechTree {

	public Tech first;
	
	public TechTree()
	{
		first = new Tech("Civilization", 0,
					new Tech("Agriculture", 30,
						null),
					new Tech("Mining", 30,
						null)
				);
	}
	
	/*public boolean researched(String name)
	{
		if (name.equals("Civilization")) return true;
		boolean temp;
		
	}*/
	
	public boolean researched(String name)
	{
		return evalOrLower(first, name);
	}
	
	private boolean evalOrLower(Tech start, String name)
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
	}
	
}
