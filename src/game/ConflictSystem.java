package game;

import java.util.ArrayList;

import units.*;

public class ConflictSystem {

	public Grid grid;

	public ConflictSystem(Grid grid)
	{
		this.grid = grid;
	}

	public static void main(String[] args)
	{
		ConflictSystem c = new ConflictSystem(null);
		for (int i = 0; i < 100; i++)
		{
			int[] temp = c.fire(4,4);
			System.out.println(temp[0] + " " + temp[1]);
		}
	}

	public String[] difficultyNames = {"", "Sandbox", "Settler", "Warlord", "Monarch", "Immortal"};
	//Return the damage inflicted by a on d in an attack, and d on a in a defense
	public Object[] attack(GameEntity a, GameEntity d)
	{
		ArrayList<String> reasonsA = new ArrayList<String>();
		ArrayList<String> reasonsD = new ArrayList<String>();
		double off = 1, def = 1;
		double potentialAdv = 0;
		if (a.owner.trait("Aggressive"))
		{
			reasonsA.add("+10% from aggressive trait");
			off += 0.1;
		}
		if (d.owner.trait("Defensive"))
		{
			reasonsD.add("+15% from defensive trait");
			def += 0.15;
		}
		switch (grid.difficultyLevel)
		{
		case 1:
			potentialAdv = 0.15;
			break;
		case 2:
			potentialAdv = 0.05;		
			break;
		case 3:
			//do nothing since neither side gets an advantage
			break;
		case 4:
			potentialAdv = -0.05;
			break;
		case 5:
			potentialAdv = -0.15;
			break;
		default:
			System.out.println("Invalid difficulty level: " + grid.difficultyLevel);
		}
		if (a.owner.id == 0)
		{
			if (potentialAdv >= 0)
			{
				reasonsA.add("+"+(potentialAdv*100)+"% from " + difficultyNames[grid.difficultyLevel] + "difficulty");
				off += potentialAdv;
			}
			else
			{
				reasonsD.add("+"+(potentialAdv*100)+"% from " + difficultyNames[grid.difficultyLevel] + "difficulty");
				def += potentialAdv;
			}
		}
		else if (d.owner.id == 0)
		{
			if (potentialAdv >= 0)
			{
				reasonsD.add("+"+(potentialAdv*100)+"% from " + difficultyNames[grid.difficultyLevel] + "difficulty");
				def += potentialAdv;
			}
			else
			{
				reasonsA.add("+"+(potentialAdv*100)+"% from " + difficultyNames[grid.difficultyLevel] + "difficulty");
				off += potentialAdv;
			}
		}
		//Disadvantage to barbarians
		if (a.owner.id >= grid.barbarians)
		{
			reasonsA.add("-15% from barbarian traditions");
			off -= 0.15;
		}
		if (d.owner.id >= grid.barbarians)
		{
			reasonsA.add("-15% from barbarian traditions");
			def -= 0.15;
		}
		//Offensive bonus
		if (a.is("Swordsman"))
		{
			reasonsA.add("+25% from swordsman ability");
			off += 0.25;
		}
		if (a.is("Axe Thrower"))
		{
			reasonsA.add("+30% from axe thrower ability");
			off += 0.3;
		}
		//Defensive
		if (d.is("Spearman"))
		{
			reasonsD.add("+25% from spearman ability");
			def += 0.25; 
		}
		else if (d.is("Warband"))
		{
			reasonsD.add("+25% from warband ability");
			def += 0.25;
		}
		//City defenses
		if (d.location.improvement != null)
			if (d.location.improvement instanceof City)
			{
				if (d.is("Warrior") || d.is("Archer"))
				{
					reasonsD.add("+25% from early city defense");
					def += 0.25;
				}
				if (((City)d.location.improvement).built("Walls"))
				{
					reasonsD.add("+40% from walls");
					def += 0.4;
				}
			}
		//Specific unit advanages
		if (a.is("Slinger") && d.is("Warrior"))
		{
			reasonsA.add("+25% from slinger against warrior");
			off += 0.25;
		}
		if (a.is("Axeman"))
		{
			if (d.mode == 1)
			{
				reasonsA.add("+25% from axeman against melee");
				off += 0.25;
			}
			else if (d.rangedStr > 0)
			{
				reasonsA.add("-10% from axeman against ranged");
				off -= 0.1;
			}
		}
		if (d.is("Axeman") && d.mode == 1)
		{
			if (d.mode == 1)
			{
				reasonsD.add("+25% from axeman against melee");
				def += 0.25;
			}
			else if (d.mode == 2)
			{
				reasonsD.add("-10% from axeman against ranged");
				def -= 0.1;
			}
		}
		if (a.is("Spearman") && (d.name.contains("Horse") || d.is("Chariot")))
		{
			reasonsA.add("+50% from spearman against mounted");
			off += 0.5;
		}
		if (d.is("Spearman") && (a.name.contains("Horse") || a.is("Chariot")))
		{
			reasonsD.add("+50% from spearman against mounted");
			def += 0.5;
		}
		return new Object[]{attack((int)(a.offensiveStr*off), (int)(d.defensiveStr*def)), reasonsA, reasonsD};
	}

	//Return the damage inflicted by a ranged attack
	//Changed a to BaseEntity to account for city firing on units
	public int[] fire(BaseEntity a, GameEntity d)
	{
		double off = 1, def = 1;
		double potentialAdv = 0;
		if (a.is("City"))
		{
			off = ((City)a).morale; 
		}
		if (a.owner.trait("Aggressive"))
			off += 0.1;
		if (d.owner.trait("Defensive"))
			def += 0.15;
		switch (grid.difficultyLevel)
		{
		case 1:
			potentialAdv = 0.2;
			break;
		case 2:
			potentialAdv = 0.1;		
			break;
		case 3:
			//do nothing since neither side gets an advantage
			break;
		case 4:
			potentialAdv = -0.1;
			break;
		case 5:
			potentialAdv = -0.2;
			break;
		default:
			System.out.println("Invalid difficulty level: " + grid.difficultyLevel);
		}
		if (a.owner.id == 0)
		{
			if (potentialAdv >= 0)
				off += potentialAdv;
			else
				def += potentialAdv;
		}
		else if (d.owner.id == 0)
		{
			if (potentialAdv >= 0)
				def += potentialAdv;
			else
				off += potentialAdv;
		}
		if (a.is("Slinger"))
		{
			off -= 0.35;
			if (d.is("Warrior"))
			{
				off += 0.25;
			}
		}
		return fire((int)(a.rangedStr*off), (int)(d.defensiveStr*def));
	}

	//Attack a city
	public int[] attack(GameEntity a, City c)
	{
		return attack(a.offensiveStr, c.defensiveStr);
	}

	//Fire upon a city
	public int[] fire(GameEntity a, City c)
	{
		return fire(a.rangedStr, c.defensiveStr);
	}

	//This accepts two sets of parameters: offensive str, defensive str, and possibly evasion str later
	//The first index is attacker on defender
	//http://forums.civfanatics.com/showthread.php?t=432238
	public int[] attack(float a, float d)
	{
		float spread = 3F/3F;

		float r = Math.max(a,1)/Math.max(d,1);
		float c1 = 4*r - 1;
		c1 += c1*((float)Math.random()*spread*2 - spread); 
		if (c1 > (16F/3F*r - 4F/3F));
		{
			c1 = (float)(Math.floor(c1-1));
		}

		r = Math.max(d,1)/Math.max(a,1);
		float c2 = 4*r - 1;
		c2 += c2*((float)Math.random()*spread*2 - spread);
		if (c2 > (16F/3F*r - 4F/3F));
		{
			c2 = (float)(Math.floor(c2-1));
		}

		//Not sure why this works
		if (c1 > c2)
		{
			return new int[]{(int)(Math.max(1,c1/2)),(int)(Math.max(1,c2))};
		}
		else
		{
			return new int[]{(int)(Math.max(1,c1)),(int)(Math.max(1,c2/2))};
		}
	}

	public int[] attackNoRandomness(float a, float d)
	{
		float r = Math.max(a,1)/Math.max(d,1);
		float c1 = 4*r - 1; 
		if (c1 > (16F/3F*r - 4F/3F));
		c1 = (float)(Math.floor(c1-1));
		r = Math.max(d,1)/Math.max(a,1);
		float c2 = 4*r - 1;
		if (c2 > (16F/3F*r - 4F/3F));
		c2 = (float)(Math.floor(c2-1));
		if (c1 > c2)
			return new int[]{(int)(Math.max(1,c1/2)),(int)(Math.max(1,c2))};
		return new int[]{(int)(Math.max(1,c1)),(int)(Math.max(1,c2/2))};
	}

	public int[] fire(float a, float d)
	{
		float spread = 3F/3F;

		float r = Math.max(a,1)/Math.max(d,1);
		float c1 = 4*r - 1;
		c1 += c1*((float)Math.random()*spread*2 - spread); 
		if (c1 > (16F/3F*r - 4F/3F));
		{
			c1 = (float)(Math.floor(c1-1));
		}

		return new int[]{(int)(Math.max(1,c1)),0};
	}

}
