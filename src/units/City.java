package units;

import java.util.ArrayList;

import game.*;

public class City extends TileEntity {

	public int population;
	public double percentGrowth; 

	public ArrayList<Tile> land;
	public ArrayList<Tile> workedLand;
	public int happiness;
	public int health;
	//public String queue;
	//public int queueTurns;
	public int queueFood, queueMetal;
	public Civilization owner;
	public String focus;
	public ArrayList<String> buildings;
	public int takeover;

	//Store how many of a copy of a resource (improved) that the city holds
	public int[] resources = new int[41]; //as of 9/28/2014 resources go up to 40 so 40+1 spaces

	public City(String name)
	{
		super(name);
		population = 1;
		percentGrowth = 0;
		happiness = 0;
		health = 0;
		land = new ArrayList<Tile>();
		workedLand = new ArrayList<Tile>();
		//queue = null;
		queueTurns = 0; queueFood = 0; queueMetal = 0;
		//owner = null;
		focus = "Growth";
		buildings = new ArrayList<String>();
		health = 20;
		offensiveStr = 0; rangedStr = 3; defensiveStr = 6;
		takeover = 0;
	}

	/*public City(TileEntity other) {
		super(other);
		population = 1;
		land = new ArrayList<Tile>();
		workedLand = new ArrayList<Tile>();
		//queue = null;
		queueTurns = 0;
		//owner = null;
		focus = "Growth";
	}*/

	public void tick()
	{
		if (takeover > 0)
		{
			takeover--;
		}
	}

	public void workTiles(int num)
	{
		if (num > land.size()) num = land.size();
		ArrayList<Tile> temp = new ArrayList<Tile>();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		for (int k = 0; k < land.size(); k++)
		{
			Tile t = land.get(k);
			temp.add(t);
			scores.add((int)evaluate(t,focus)[0]);
		}
		workedLand.clear();
		//System.out.println("-----");
		for (int i = 0; i < num; i++)
		{
			int index = indexOfBest(scores);
			workedLand.add(temp.get(index));
			//System.out.println(temp.get(index).row + " " + temp.get(index).col + " " + this.location.row + " " + this.location.col);
			temp.remove(index);
			scores.remove(index);
		}
		//return returnThis;
	}

	public int[] quickEval()
	{
		int p = population;
		int happiness = 4 - p;
		if (happiness < 0)
			workTiles(p - happiness);
		else
			workTiles(population);

		int[] temp = new int[4];
		for (int i = 0; i < workedLand.size(); i++)
		{
			double[] eval = evaluate(workedLand.get(i), null);
			temp[0] += eval[0]; 
			temp[1] += eval[1];
			temp[2] += eval[2];
			temp[3] += eval[3];
		}
		return temp;
	}

	public void findResources()
	{
		for (int i = 0; i < resources.length; i++)
			resources[i] = 0;
		//Record tiles with harvested resources as extra yield and record the number of these special tiles
		for (int i = 0; i < workedLand.size(); i++)
		{
			Tile t = workedLand.get(i);
			if (t.improvement != null)
			{
				if (t.improvement.name.equals("Farm"))
				{
					if (t.resource == 1)
					{
						resources[1]++;
					}
					else if (t.resource == 2)
					{
						resources[2]++;
					}
				}
				else if (t.improvement.name.equals("Fishing Boats"))
				{
					if (t.resource == 10)
					{
						resources[10]++;
					}
					else if (t.resource == 11)
					{
						resources[11]++;
					}
				}
				else if (t.improvement.name.equals("Mine"))
				{
					if (t.resource == 20)
					{
						resources[20]++;
					}
					else if (t.resource == 21)
					{
						resources[21]++;
					}
					else if (t.resource == 22)
					{
						resources[22]++;
					}
				}
				else if (t.improvement.name.equals("Forest Yard"))
				{
					if (t.resource == 30)
					{
						resources[30]++;
					}
				}
			}
		}
	}

	//Returns a score
	public double[] evaluate(Tile t, String focus)
	{
		findResources();
		int f, g, m, r;
		if (t.biome == -1)
		{
			f = 1; g = 1; m = 0; r = 2;
		}
		else if (t.biome == 0)
		{
			f = 0; g = 1; m = 2; r = 1;
		}
		else if (t.biome == 1)
		{
			f = 1; g = 1; m = 1; r = 1;
		}
		else if (t.biome == 2)
		{
			f = 0; g = 0; m = 2; r = 1;
		}
		else if (t.biome == 3)
		{
			f = 2; g = 0; m = 1; r = 2;
		}
		else if (t.biome == 4)
		{
			f = 2; g = 1; m = 1; r = 2;
		}
		else if (t.biome == 5)
		{
			f = 3; g = 0; m = 1; r = 2;
		}
		else if (t.biome == 6)
		{
			f = 3; g = 1; m = 0; r = 3;
		}
		else
		{
			System.err.println("Invalid biomerrr " + t.biome);
			f = 0; g = 0; m = 0; r = 0;
		}
		if (t.biome >= 4 && t.biome <= 6 && !t.forest)
		{
			f--;
			r--;
		}
		if (t.shape == 1)
		{
			f--;
			m++;
			if (t.improvement != null)
				if (t.improvement.name.equals("Mine"))
					m++;
		}
		else if (t.shape == 2)
		{
			f -= 1;
			m += 1;
			if (t.improvement != null)
				if (t.improvement.name.equals("Mine"))
					m+=2;
		}
		//Record tiles with harvested resources as extra yield and record the number of these special tiles
		if (t.improvement != null)
		{
			if (t.improvement.name.equals("Farm"))
			{
				if (t.resource == 1)
				{
					f += 3;
					resources[1]++;
				}
				else if (t.resource == 2)
				{
					f += 4;
				}
				else
				{
					f += 2;
				}
			}
			else if (t.improvement.name.equals("Fishing Boats"))
			{
				if (t.resource == 10)
				{
					f += 3;
				}
				else if (t.resource == 11)
				{
					f += 3;
					g += 2;
					r += 3;
				}
			}
			else if (t.improvement.name.equals("Mine"))
			{
				if (t.resource == 20)
				{
					m += 3;
					g += 1;
					r += 1;
				}
				else if (t.resource == 21)
				{
					m += 4;
					g += 2;
					r += 3;
				}
				else if (t.resource == 22)
				{
					m += 3;
					g += 1;
					r += 1;
				}
			}
			else if (t.improvement.name.equals("Forest Yard"))
			{
				if (t.resource == 30)
				{
					f += 1;
					g += 1;
					m += 3;
					r += 1;
				}
			}
			else if (t.improvement.name.equals("Windmill"))
			{
				if (resources[1] > 0)
				{
					f = Math.min(resources[1],4);
					resources[1] -= f;
				}
				else if (resources[2] > 0)
				{
					f = Math.min(resources[2],4);
					resources[2] -= f;
				}
			}
		}
		if (t.resource == 40)
		{
			f += 2;
			g += 1;
			r += 2;
		}
		if (location.equals(t))
		{
			f = 1; g = 2; m = 1; r = 2;
			if (owner.capital != null)
			{
				if (owner.capital.equals(this))
				{
					f = 3;
					g = 3;
					m = 2;
					r = 3;
				}
			}
		}
		if (focus == null)
		{
			return new double[]{f,g,m,r};
		}
		else
		{
			int score = 0;
			if (focus.equals("Growth"))
			{
				score = f*2 + g + m + r;
			}
			else if (focus.equals("Production"))
			{
				score = f + g + m*2 + r;
			}
			else if (focus.equals("Wealth"))
			{
				score = f + g*2 + m + r*2;
			}
			else if (focus.equals("Balanced"))
			{
				score = f + g + m + r;
			}
			else
			{
				System.err.println("Invalid city focus");
			}
			return new double[]{score,f,g,m,r};
		}
	}

	private int indexOfBest(ArrayList<Integer> scores)
	{
		int index = 0;
		for (int i = 0; i < scores.size(); i++)
		{
			if (scores.get(i) > scores.get(index)) index = i;
		}
		//System.out.println(scores.get(index));
		return index;
	}

	public String getName() {return "City";}

	public boolean equals(City other)
	{
		return location.equals(other.location);
	}

}
