package terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class TerrainData {

	public double[][] terrain;
	public double cutoff;

	public TerrainData(double[][] terrain, double cutoff)
	{
		this.terrain = terrain;
		this.cutoff = cutoff;
	}

	public IslandHelper islandHelper()
	{
		return new IslandHelper(terrain,cutoff);
	}

	public double divIndex(ArrayList<IslandHelper.Location> locations)
	{
		//TODO:
		HashMap<Integer,Integer> count = new HashMap<Integer,Integer>();
		//Find maximum height in terrain t
		int max = 0;
		for (int i = 0; i < locations.size(); i++)
		{
			IslandHelper.Location loc = locations.get(i);
			if (terrain[loc.r][loc.c] > max) max = (int)terrain[loc.r][loc.c];
		}
		//Stratify heights into groups
		for (int i = 0; i <= max; i += 5)
		{
			count.put(i, 0);
		}
		for (int i = 0; i < locations.size(); i++)
		{
			IslandHelper.Location loc = locations.get(i);
			int adj = (int)terrain[loc.r][loc.c] - ((int)terrain[loc.r][loc.c] % 5);
			try {
				count.put(adj, count.get(adj) + 1);
			} catch (Exception e)
			{
				e.printStackTrace();
				//System.out.println((int)terrain[r][c]/5);
			}
		}
		//Measure "diversity" of the stratified groups of heights
		double n = locations.size();
		double divIndex = 0;
		for (Entry en : count.entrySet())
		{
			int temp = (Integer) en.getValue();
			System.out.println(temp);
			if (temp != 0)
				divIndex -= ((double)temp/n)*Math.log((double)temp/n);
		}
		System.out.println(divIndex);
		return divIndex;
	}

	public double divIndex(int sX, int sY, int width)
	{
		//TODO:
		HashMap<Integer,Integer> count = new HashMap<Integer,Integer>();
		//Find maximum height in terrain t
		int max = 0;
		for (int r = sX; r < sX + width; r++)
		{
			for (int c = sY; c < sY + width; c++)
			{
				if (terrain[r][c] > max) max = (int)terrain[r][c];
			}
		}
		//Stratify heights into groups
		for (int i = 0; i <= max; i += 5)
		{
			count.put(i, 0);
		}
		for (int r = sX; r < sX + width; r++)
		{
			for (int c = sY; c < sY + width; c++)
			{
				int adj = (int)terrain[r][c] - ((int)terrain[r][c] % 5);
				try {
					count.put(adj, count.get(adj) + 1);
				} catch (Exception e)
				{
					e.printStackTrace();
					//System.out.println((int)terrain[r][c]/5);
				}
			}
		}
		//Measure "diversity" of the stratified groups of heights
		double n = Math.pow(width, 2);
		double divIndex = 0;
		for (Entry en : count.entrySet())
		{
			int temp = (Integer) en.getValue();
			System.out.println(temp);
			if (temp != 0)
				divIndex -= ((double)temp/n)*Math.log((double)temp/n);
		}
		System.out.println(divIndex);
		return divIndex;
	}

	public void recurDivIndex(int sX, int sY, int width)
	{
		int orig = width;
		DiamondSquare dS = new DiamondSquare(terrain);
		while (width > 4)
		{
			//Invert axis?
			for (int r = sX; r < sX + orig; r += width)
			{
				for (int c = sY; c < sY + orig; c += width)
				{
					double divIndex = divIndex(r, c, width);
					//System.out.println(divIndex);
					if (divIndex < 0 && r + width < terrain.length && c + width < terrain[0].length)
					{
						try
						{
							//TODO:
							//dS.dS(r, c, width+1, terrain[r][c]*0.5, 0.5);
							//System.out.println("DS: " + r + " " + c + " " + width);
						} catch (Exception e) {/*do nothing*/}
					}
				}
			}
			width /= 2;
		}
	}

	//Helper class. Returns arraylist of islands. An island is an arraylist of locations.
	public static class IslandHelper
	{
		//Wrapper class
		public class Location {public int r; public int c; public Location(int x, int y) {r = x; c = y;}};

		//Tiles that have been assigned islands
		public boolean[][] accounted;

		//An arraylist containing lists of islands (an island is a list, so this is a list of all those lists)
		public ArrayList<ArrayList<Location>> listIslands = new ArrayList<ArrayList<Location>>();

		public IslandHelper(double[][] terrain, double cutoff)
		{
			accounted = new boolean[terrain.length][terrain[0].length];
			getListIslands(terrain, cutoff);
			for (int i = 0; i < listIslands.size(); i++)
			{
				ArrayList<Location> island = listIslands.get(i);
				System.out.println("Island " + i);
				for (int j = 0; j < island.size(); j++)
				{
					System.out.println(">>>>>" + island.get(j).r + "," + island.get(j).c);
				}
			}
		}

		public void getListIslands(double[][] terrain, double cutoff)
		{
			for (int r = 0; r < terrain.length; r++)
			{
				for (int c = 0; c < terrain[0].length; c++)
				{
					//Do not look at islands below cutoff e.g. sea
					accounted[r][c] = terrain[r][c] < cutoff;
					//System.out.println(accounted[r][c]);
				}
			}
			for (int r = 0; r < terrain.length; r++)
			{
				for (int c = 0; c < terrain[0].length; c++)
				{
					if (!accounted[r][c])
					{
						loc = new ArrayList<Location>(); //Reset it.
						startIsland(r,c); //Change it.
						if (loc.size() > 0)
							listIslands.add(loc); //Record it.
					}
				}
			}
		}

		//Recursive method that returns a list for one island containing r,c
		ArrayList<Location> loc; // = new ArrayList<Location>();
		//A temporary global. Lazy fix.
		public void startIsland(int r, int c)
		{
			if (r <= 0 || c <= 0 || r >= accounted.length || c >= accounted[0].length) return;
			if (!accounted[r][c])
			{
				accounted[r][c] = true;
				loc.add(new Location(r,c));
				//if (r > 0 && c > 0 && r < accounted.length - 1 && c < accounted[0].length - 1)
				{
					startIsland(r+1,c);
					startIsland(r-1,c);
					startIsland(r,c+1);
					startIsland(r,c-1);
				}
			}
			else
			{
				return;
				//return new ArrayList<Location>(); //add nothing
			}
		}

	}

}
