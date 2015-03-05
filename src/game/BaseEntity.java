package game;

import java.util.ArrayList;

import units.City;
import units.Worker;
import data.Improvement;

//Base class for all objects in the world, living or not living

public abstract class BaseEntity {

	public Tile location;
	public String name;
	public String id;
	public Civilization owner;

	public String queue;
	public int queueTurns;

	public int health, maxHealth;
	public float offensiveStr, defensiveStr, rangedStr;
	public double action = 1, maxAction = 1;
	public boolean sleep = false;
	public int range = 0;

	public City sortie = null; //If this unit was raised by a sortie order, this will hold the city it "belongs" to

	public Improvement unitImprovement;

	public BaseEntity(String name, float o, float d, float r)
	{
		this.name = name;
		id = Double.toString(Math.random()*System.currentTimeMillis());
		offensiveStr = o;
		defensiveStr = d;
		rangedStr = r;
	}

	public BaseEntity(BaseEntity other)
	{
		name = other.name; 
		id = Double.toString(Math.random()*System.currentTimeMillis());
		offensiveStr = other.offensiveStr;
		defensiveStr = other.defensiveStr;
		rangedStr = other.rangedStr;
	}

	public int sight = 2;
	public void reveal()
	{
		for (int i = location.row - sight; i <= location.row + sight; i++)
		{
			for (int j = location.col - sight; j <= location.col + sight; j++)
			{
				if (location.grid.getTile(i, j) != null)
				{
					owner.revealed[i][j] = 2;
				}
			}
		}
		//System.out.println(name + " reveal");
	}

	public void tick() {}
	public void playerTick() {};

	//Do not send the improvement to the function
	//It must be stored at queue time
	public void improve()
	{
		if (unitImprovement != null)
		{
			if (unitImprovement.equals("Neutral"))
			{
				if (unitImprovement.offensivePercent != 0)
					offensiveStr *= unitImprovement.offensivePercent;
				if (unitImprovement.defensivePercent != 0)
					defensiveStr *= unitImprovement.defensivePercent;
				if (unitImprovement.rangedPercent != 0)
					rangedStr *= unitImprovement.rangedPercent;
				offensiveStr += unitImprovement.offensiveFlat;
				defensiveStr += unitImprovement.defensiveFlat;
				rangedStr += unitImprovement.rangedFlat;
				if (this instanceof Worker)
				{
					((Worker)this).workTime = unitImprovement.workerImprovementTime;
				}
			}
		}
	}
	
	public ArrayList<GameEntity> fireAtTargets()
	{
		ArrayList<GameEntity> temp = new ArrayList<GameEntity>();
		for (int r = location.row - range; r <= location.row + range; r++)
		{
			for (int c = location.col - range; c <= location.col + range; c++)
			{
				Tile t = location.grid.getTile(r, c);
				if (t.occupants.size() > 0)
				{
					for (int i = 0; i < t.occupants.size(); i++)
					{
						GameEntity en = t.occupants.get(i);
						if (owner.isWar(en.owner))
						{
							temp.add(en);
						}
					}
				}
			}
		}
		return temp;
	}
	
	public abstract String getName();
	
	public boolean is(String name) {return this.name.equals(name);}
	//public boolean melee() {return offensiveStr > 0 && rangedStr <= 0;}
	//public boolean ranged() {return rangedStr > 0;}
	
}
