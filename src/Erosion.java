
import java.util.ArrayList;

public class Erosion {

	public double[][] terrain;
	public Droplet[][] waterLevel;

	public Erosion(double[][] terrain)
	{
		this.terrain = terrain;
		waterLevel = new Droplet[terrain.length][terrain[0].length];
	}

	public void flood(int r, int c, int water)
	{
		if (waterLevel[r][c] != null)
			waterLevel[r][c].water += water;
		else
		{
			waterLevel[r][c] = new Droplet(water, 0, 30, r, c);
		}
	}

	public void tick()
	{
		/*
		 * Loop through all water droplets
		 * If there is a neighboring lower level, dissolve some soil and move down
		 * If there is not, place all dissolved soil at bottom
		 */
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				if (waterLevel[r][c] != null)
				{
					ArrayList<Location> locs = checkLower(r,c);
					if (locs.size() > 0) 
					{
						double dissolved = (Math.random()*0.5)*waterLevel[r][c].maxSoil;
						waterLevel[r][c].soil += dissolved;
						terrain[r][c] -= dissolved;
						if (waterLevel[r][c].soil > waterLevel[r][c].maxSoil)
						{
							waterLevel[r][c].soil = waterLevel[r][c].maxSoil;
						}
						int random = (int)(locs.size()*Math.random());
					    Location loc = locs.get(random);
					    if (waterLevel[loc.r][loc.c] != null)
					    {
					    	waterLevel[loc.r][loc.c].water += waterLevel[r][c].water;
					    	waterLevel[loc.r][loc.c].soil += waterLevel[r][c].soil;
					    	waterLevel[r][c] = null;
					    }
					    else
					    {
					    	waterLevel[loc.r][loc.c] = new Droplet(waterLevel[r][c], loc.r, loc.c);
					    	waterLevel[r][c] = null;
					    }
					    
					}
					else
					{
						terrain[r][c] += waterLevel[r][c].soil;
						waterLevel[r][c] = null;
					}
				}
			}
		}
	}

	public class Location {public int r; public int c; Location(int x, int y) {r = x; c = y;}}

	public ArrayList<Location> checkLower(int r, int c)
	{
		ArrayList<Location> temp = new ArrayList<Location>();
		temp.add(new Location(r-1,c-1));
		temp.add(new Location(r-1,c));
		temp.add(new Location(r-1,c+1));
		temp.add(new Location(r,c+1));
		temp.add(new Location(r+1,c+1));
		temp.add(new Location(r+1,c));
		temp.add(new Location(r+1,c-1));
		temp.add(new Location(r,c-1));
		for (int i = temp.size() - 1; i >= 0; i--)
		{
			int row = temp.get(i).r;
			int col = temp.get(i).c;
			if (row >= 0 && row < terrain.length && col >= 0 && col < terrain[0].length)
			{
				if (terrain[row][col] < terrain[r][c])
				{
					continue;
				}
			}
			temp.remove(i);
		}
		return temp;
	}

	//Wrapper class
	public class Droplet
	{
		public double water; public double maxSoil; public double soil;
		public int r; public int c;

		public Droplet(double water, double soil, double maxSoil, int r, int c)
		{
			this.water = water;
			this.soil = soil;
			this.maxSoil = maxSoil;
			this.r = r;
			this.c = c;
		}

		public Droplet(Droplet o, int r, int c)
		{
			water = o.water;
			soil = o.soil;
			maxSoil = o.maxSoil;
			this.r = r;
			this.c = c;
		}
	}

}
