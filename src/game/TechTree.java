package game;

//the following may or may not actually be a "tree"

public class TechTree {

	public Tech first;
	
	public TechTree()
	{
		first = new Tech("Civilization", 0,
					new Tech("Agriculture", 30,
						new Tech("Forestry", 60, 
							new Tech("Architecture", 100, null),
							new Tech("Silviculture", 100, 
								new Tech("Terraforming", 200, null),
								new Tech("Animal Husbandry", 200, null)
							)
						),
						new Tech("Milling", 60, 
							new Tech("Fletching", 100, null))
					),
					new Tech("Mining", 30,
						new Tech("Metal Working", 120, 
							new Tech("Currency", 100, null),
							new Tech("Casting", 100, null)),
						new Tech("Stone Working", 60,
							new Tech("Pottery", 30, null))
					)
				);
	}
	
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
		if (start.name.equals(name))
		{
			return start;
		}
		for (int i = 0; i < start.techs.length; i++)
		{
			if (start.techs[i].name.equals(name))
			{
				return start.techs[i];
			}
			else
			{
				return researched(start.techs[i], name);
			}
		}
		return null;
	}
	
}
