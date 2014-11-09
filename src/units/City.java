package units;

import java.util.ArrayList;

import data.EntityData;
import data.Improvement;
import game.*;

public class City extends TileEntity {

	public int population;
	public double percentGrowth; 

	public int id;

	public ArrayList<Tile> land;
	public ArrayList<Tile> workedLand;
	public int happiness;
	public int health;
	//public String queue;
	//public int queueTurns;
	public int queueFood, queueMetal;
	public Civilization owner;
	public String focus;
	public ArrayList<Improvement> buildings;
	public int takeover;
	//public int sight = 4;

	public int adm, art, sci; 
	//Specialized workers:
	//Administrator: 25% of tax base per each
	//Artist: 25% of tax base converted into culture
	//Scientist: +2 per each
	public int culture;
	public int expanded; //Stage of expansion: 0, does not exist; 1, 3 by 3; 2, 5 by 5; 3, large cross;
	public boolean raze;

	//Store how many of a copy of a resource (improved) that the city holds
	public int[] resources = new int[41]; //as of 9/28/2014 resources go up to 40 so 40+1 spaces

	//0: cannot sortie, has no sortie
	//1: can sortie, has no sortie
	//2: sortie has been deployed
	public int sortie = 0;

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
		buildings = new ArrayList<Improvement>();
		health = 20;
		offensiveStr = 0; rangedStr = 3; defensiveStr = 6;
		takeover = 0;
		sight = 4;
		art = 0; sci = 0; adm = 0;
		culture = 0; expanded = 0;
		raze = false;
		buildings = new ArrayList<Improvement>();
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

	//Returns true if an enemy is in the city's land
	public boolean enemiesInTerritory()
	{
		for (int i = 0; i < land.size(); i++)
		{
			Tile t = land.get(i);
			for (int j = 0; j < t.occupants.size(); j++)
			{
				if (owner.isWar(t.occupants.get(j).owner))//; <- this damn semicolon
				{
					return true;
				}
			}
		}
		return false;
	}

	//Return all the tiles that are not completely surrounded by the civ's own land
	public ArrayList<Tile> returnFrontier()
	{
		ArrayList<Tile> temp = new ArrayList<Tile>();
		for (int i = 0; i < land.size(); i++)
		{
			Tile t = land.get(i);
			ArrayList<Tile> adjacent = t.grid.adjacent(t.row, t.col);
			for (int j = 0; j < adjacent.size(); j++)
			{
				if (!owner.equals(adjacent.get(j).owner))
				{
					temp.add(t);
					break;
				}
			}
		}
		return temp;
	}


	public int tilesBorderingCiv(Civilization other)
	{
		int temp = 0;
		for (int i = 0; i < land.size(); i++)
		{
			Tile t = land.get(i);
			ArrayList<Tile> adjacent = t.grid.adjacent(t.row, t.col);
			for (int j = 0; j < adjacent.size(); j++)
			{
				//System.out.println(owner + " " + other);
				if (other.equals(adjacent.get(j).owner))
				{
					temp++;
					break;
				}
			}
		}
		return temp;
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

	public static double[] staticEval(Tile t)
	{
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
					m += 2;
		}
		//Record tiles with harvested resources as extra yield and record the number of these special tiles
		if (t.improvement != null)
		{
			if (t.improvement.name.equals("Farm"))
			{
				if (t.resource == 1)
				{
					f += 3;
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
		}
		if (t.resource == 40)
		{
			f += 2;
			g += 1;
			r += 2;
		}
		return new double[]{f,g,m,r};
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
					m += 2;
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
					f++;
					m += Math.min(resources[1],3);
					//resources[1] -= f;
				}
				else if (resources[2] > 0)
				{
					f++;
					m += Math.min(resources[2],3);
					//resources[2] -= f;
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

	//Expands the city to a square of size 2*n + 1
	public void expand(int n)
	{
		expanded++;
		for (int i = location.row - n; i <= location.row + n; i++)
		{
			for (int j = location.col - n; j <= location.col + n; j++)
			{
				if (i >= 0 && i < location.grid.rows && j >= 0 && j < location.grid.cols)
				{
					Tile t = location.grid.getTile(i,j);
					if (t != null)
					{
						if (t.owner == null)
						{
							t.city = this;
							land.add(t);
							location.grid.addTile(owner, t);
						}
						else if (t.owner == owner && t.city == null)
						{
							t.city = this;
							land.add(t);
						}
					}
				}
			}
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

	public void sortie()
	{
		sortie = 2;
		for (int i = 0; i < 3; i++)
		{
			BaseEntity en = EntityData.get("Warrior");
			//Check if it's an actual unit or a building
			if (en != null)
			{
				Tile t = null;
				while (t == null)
				{
					t = land.get((int)(Math.random()*land.size()));
					//Check if the tile is empty to deploy sortie units
					if (t.occupants.size() == 0)
					{
						break;
					}
					else
					{
						t = null;
					}
				}
				//Add a unit and denote it as a sortie
				location.grid.addUnit(en,owner,t.row,t.col).sortie = this;
				//en.unitImprovement = owner.unitImprovements.get(c.queue);
				//en.improve();
			}
		}
	}

	public void endSortie()
	{
		sortie = enemiesInTerritory() ? 1 : 0;
		for (int i = 0; i < owner.units.size(); i++)
		{
			GameEntity unit = owner.units.get(i);
			if (unit.sortie.equals(this))
			{
				location.grid.removeUnit(unit);
			}
		}
	}

	public String getName() {return "City";}

	public boolean equals(City other)
	{
		return location.equals(other.location);
	}

	public boolean hasImprovement(String impr) {
		for (int i = 0; i < buildings.size(); i++)
			if (buildings.get(i).equals(impr))
				return true;
		return false;
	}

}
