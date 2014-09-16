package units;

import java.util.ArrayList;

import game.*;

public class City extends TileEntity {

	public int population;
	public ArrayList<Tile> land;
	public ArrayList<Tile> workedLand;
	public String queue;
	public int queueTurns;
	public Civilization owner;
	public String focus;
	
	public City(String name)
	{
		super(name);
		population = 1;
		land = new ArrayList<Tile>();
		workedLand = new ArrayList<Tile>();
		//queue = null;
		queueTurns = 0;
		//owner = null;
		focus = "Growth";
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
		ArrayList<Tile> temp = new ArrayList<Tile>();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		for (int k = 0; k < land.size(); k++)
		{
			Tile t = land.get(k);
			int f, g, m;
			switch (t.biome)
			{
			case 0:
				f = 0; g = 1; m = 2;
			case 1:
				f = 1; g = 1; m = 1;
			case 2:
				f = 0; g = 0; m = 2;
			case 3:
				f = 2; g = 0; m = 1;
			case 4:
				f = 2; g = 1; m = 1;
			case 5:
				f = 3; g = 0; m = 1;
			case 6:
				f = 3; g = 1; m = 0;
			default:
				System.err.println("Invalid biome");
				f = 0; g = 0; m = 0;
			}
			if (t.shape == 1)
			{
				f--;
				m++;
			}
			if (location.equals(t))
			{
				f = 2; g = 2; m = 2;
			}
			int score = 0;
			if (focus.equals("Growth"))
			{
				score = f*2 + g + m;
			}
			else if (focus.equals("Production"))
			{
				score = f + g + m*2;
			}
			else if (focus.equals("Wealth"))
			{
				score = f + g*2 + m;
			}
			else if (focus.equals("Balanced"))
			{
				score = f + g + m;
			}
			else
			{
				System.err.println("Invalid city focus");
			}
			temp.add(t);
			scores.add(score);
		}
		workedLand.clear();
		for (int i = 0; i < num; i++)
		{
			int index = indexOfBest(scores);
			workedLand.add(temp.get(index));
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
		return index;
	}
	
}
