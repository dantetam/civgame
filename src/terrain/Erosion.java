package terrain;

import java.util.ArrayList;

import processing.core.PApplet;

public class Erosion {

	public double[][] terrain;
	public double max;
	public double cutoff;
	public Droplet[][] waterLevel;
	
	public Erosion(double[][] terrain, double cutoff)
	{
		this.terrain = terrain;
		this.cutoff = cutoff;
		waterLevel = new Droplet[terrain.length][terrain[0].length];
		double max = 0;
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				if (terrain[r][c] > max) max = terrain[r][c];
			}
		}
		this.max = max;
	}

	public void flood(int r, int c, int water)
	{
		if (waterLevel[r][c] != null)
			waterLevel[r][c].water += water;
		else
		{
			waterLevel[r][c] = new Droplet(water, 0, 60, r, c);
		}
	}

	public boolean tick()
	{
		/*
		 * Loop through all water droplets
		 * If there is a neighboring lower level, dissolve some soil and move down
		 * If there is not, place all dissolved soil at bottom
		 * 
		 * TODO: Dissolve soil from neighboring tiles (less intensive)
		 */
		boolean inProgress = false;
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				if (waterLevel[r][c] != null && !waterLevel[r][c].tick)
				{
					inProgress = true;
					waterLevel[r][c].tick = true;
					ArrayList<Location> locs = checkLower(r,c);
					if (locs.size() > 0) 
					{
						double a = (Math.random()*0.15 + 0.2)*waterLevel[r][c].water;
						double b = Math.max(0,waterLevel[r][c].speed*(Math.random()*0.25 + 0.25));
						double dissolved = a + b;
						//System.out.println(a + " " + b);
						if (terrain[r][c] - dissolved < averageNeighbors(r,c) - 10)
						{
							dissolved = terrain[r][c] - averageNeighbors(r,c) + 10;
							dissolved = Math.max(0, dissolved);
							//System.out.println(dissolved);
						}
						waterLevel[r][c].soil += dissolved;
						terrain[r][c] -= dissolved;
						if (waterLevel[r][c].soil > waterLevel[r][c].maxSoil)
						{
							waterLevel[r][c].soil = waterLevel[r][c].maxSoil;
						}
						int random = (int)(locs.size()*Math.random());
						Location loc = locs.get(random);
						for (int i = 0; i < locs.size(); i++)
						{
							//if (i != random)
							{
								terrain[locs.get(i).r][locs.get(i).c] -= (Math.random()*0.25 + 0.5)*dissolved;
							}
						}
						//If droplet is going to go to water, end the erosion
						//Else, either make a new droplet or combine with the existing one at the location
						if (terrain[loc.r][loc.c] < cutoff) 
						{
							waterLevel[r][c] = null;
						}
						else
						{
							waterLevel[r][c].speed = (terrain[r][c] - terrain[loc.r][loc.c])*(terrain[r][c]/max);
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
						
					}
					else
					{
						//terrain[r][c] += waterLevel[r][c].soil;
						waterLevel[r][c] = null;
					}
				}
			}
		}
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				if (waterLevel[r][c] != null)
				{
					waterLevel[r][c].tick = false;
				}
			}
		}
		return inProgress;
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

	public double averageNeighbors(int r, int c)
	{
		double avg = 0;
		ArrayList<Location> temp = new ArrayList<Location>();
		temp.add(new Location(r-1,c-1));
		temp.add(new Location(r-1,c));
		temp.add(new Location(r-1,c+1));
		temp.add(new Location(r,c+1));
		temp.add(new Location(r+1,c+1));
		temp.add(new Location(r+1,c));
		temp.add(new Location(r+1,c-1));
		temp.add(new Location(r,c-1));
		temp.add(new Location(r,c+2));
		temp.add(new Location(r+2,c));
		temp.add(new Location(r,c-2));
		temp.add(new Location(r-2,c));
		for (int i = 0; i < temp.size(); i++)
		{
			int row = temp.get(i).r;
			int col = temp.get(i).c;
			if (row >= 0 && row < terrain.length && col >= 0 && col < terrain[0].length)
			{
				avg += terrain[row][col];
				continue;
			}
			temp.remove(i);
			i--;
		}
		return avg/(double)temp.size();
	}

	//Wrapper class
	public class Droplet
	{
		public double water; public double maxSoil; public double soil;
		public double speed; public boolean tick = false;
		public int r; public int c;

		public Droplet(double water, double soil, double maxSoil, int r, int c)
		{
			speed = 0;
			this.water = water;
			this.soil = soil;
			this.maxSoil = maxSoil;
			this.r = r;
			this.c = c;
		}

		public Droplet(Droplet o, int r, int c)
		{
			speed = o.speed;
			water = o.water;
			soil = o.soil;
			maxSoil = o.maxSoil;
			this.r = r;
			this.c = c;
		}
	}

}
