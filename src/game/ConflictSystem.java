package game;

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

	//Return the damage inflicted by a on d in an attack, and d on a in a defense
	public int[] attack(GameEntity a, GameEntity d)
	{
		double off = 1, def = 1;
		double potentialAdv = 0;
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
		//Disadvantage to barbarians
		if (a.owner.id >= grid.barbarians)
			off -= 0.3;
		if (d.owner.id >= grid.barbarians)
			def -= 0.3;
		//Offensive bonus
		if (a.is("Swordsman"))
			off += 0.25;
		if (a.is("Axe Thrower"))
			off += 0.35;
		//Defensive
		if (d.is("Spearman") || d.is("Warband"))
			def += 0.25; 
		//City defenses
		if (d.location.improvement != null)
			if (d.location.improvement instanceof City)
			{
				if (d.is("Warrior") || d.is("Archer"))
					def += 0.25;
				if (((City)d.location.improvement).built("Walls"))
					def += 0.4;
			}
		//Specific unit advanages
		if (a.is("Slinger") && d.is("Warrior"))
		{
			off += 0.25;
		}
		if (a.is("Axeman"))
		{
			if (d.mode == 1)
				off += 0.25;
			else if (d.rangedStr > 0)
				off -= 0.25;
		}
		if (d.is("Axeman") && d.mode == 1)
		{
			if (d.mode == 1)
				def += 0.25;
			else if (d.mode == 2)
				def -= 0.25;
		}
		if (a.is("Spearman") && (d.name.contains("Horse") || d.is("Chariot")))
		{
			off += 0.5;
		}
		if (d.is("Spearman") && (a.name.contains("Horse") || a.is("Chariot")))
		{
			def += 0.5;
		}
		return attack((int)(a.offensiveStr*off), (int)(d.defensiveStr*def));
	}

	//Return the damage inflicted by a ranged attack
	public int[] fire(GameEntity a, GameEntity d)
	{
		double off = 1, def = 1;
		double potentialAdv = 0;
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
