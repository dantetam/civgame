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
	public Civilization owner;
	public String focus;
	public ArrayList<String> buildings;

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
		queueTurns = 0;
		//owner = null;
		focus = "Growth";
		buildings = new ArrayList<String>();
	}

	public City(TileEntity other) {
		super(other);
		population = 1;
		land = new ArrayList<Tile>();
		workedLand = new ArrayList<Tile>();
		//queue = null;
		queueTurns = 0;
		//owner = null;
		focus = "Growth";
	}

	public void tick()
	{

	}

	public void workTiles(int num)
	{
		if (num > land.size()) num = land.size();
		ArrayList<Tile> temp = new ArrayList<Tile>();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		for (int k = 0; k < land.size(); k++)
		{
			Tile t = land.get(k);
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
			if (t.shape == 1)
			{
				f--;
				m++;
			}
			if (location.equals(t))
			{
				f = 1; g = 2; m = 1; r = 2;
			}
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
			temp.add(t);
			scores.add(score);
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

}
