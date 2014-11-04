package menugame;

import java.util.Random;

import system.CivilizationSystem;
import terrain.*;
import game.*;

public class MenuGame {

	public Grid grid;
	public long seed;
	public CivilizationSystem civSystem;
	
	public MenuGame(long seed)
	{
		this.seed = seed;
		BaseTerrain map = new PerlinNoise(seed);
		double[][] terrain = map.generate(new double[]{32,32,150,8,1,0.8,6,64,-100});
		//float con = 1F;
		float cutoff = -100;
		int[][] biomes = assignBiome(terrain, (int)cutoff);
		grid = new Grid(terrain, biomes, assignResources(biomes), 8, 16, (int)cutoff, seed);
		civSystem = new CivilizationSystem(this);
	}
	
	public void tick()
	{
		civSystem.tick();
	}

	public int[][] assignResources(int[][] biomes)
	{
		int[][] resources = new int[biomes.length][biomes[0].length];
		for (int r = 0; r < biomes.length; r++)
			for (int c = 0; c < biomes[0].length; c++)
			{
				int b = biomes[r][c];
				boolean[] candidates = new boolean[100];
				if (b == -1)
				{
					candidates[10] = true;
					candidates[11] = true;
				}
				else if (b == 0)
				{
					candidates[20] = true;
					candidates[21] = true;
					candidates[22] = true;
					candidates[40] = true;
				}
				else if (b == 1)
				{
					candidates[20] = true;
					candidates[21] = true;
					candidates[22] = true;
					candidates[40] = true;
				}
				else if (b == 2)
				{
					candidates[20] = true;
					candidates[21] = true;
					candidates[22] = true;
					candidates[40] = true;
				}
				else if (b == 3)
				{
					candidates[1] = true;

					candidates[20] = true;
					candidates[22] = true;
					candidates[40] = true;
				}
				else if (b == 4)
				{
					candidates[1] = true;

					candidates[20] = true;
					candidates[22] = true;
					candidates[30] = true;
					candidates[40] = true;
				}
				else if (b == 5)
				{
					candidates[1] = true;

					candidates[22] = true;
					candidates[30] = true;
					candidates[40] = true;
				}
				else if (b == 6)
				{
					candidates[1] = true;
					candidates[2] = true;

					candidates[22] = true;
					candidates[30] = true;
					candidates[40] = true;
				}
				else if (b == 8)
				{

				}
				for (int i = 0; i < candidates.length; i++)
					if (candidates[i])
						if (Math.random() < 0.0125)
							resources[r][c] = i;
			}
		return resources;
	}

	public int[][] assignBiome(double[][] terrain, int cutoff)
	{
		int[][] temp = new int[terrain.length][terrain[0].length];
		double width = Math.max(
				Math.pow(2,Math.floor(Math.log10(terrain.length)/Math.log10(2)) + 1),
				Math.pow(2,Math.floor(Math.log10(terrain[0].length)/Math.log10(2)) + 1)
				);
		double[][] temperature = assignTemperature(width);
		double[][] rain = assignRain(temperature);
		for (int r = 0; r < temp.length; r++)
			for (int c = 0; c < temp[0].length; c++)
			{
				if (terrain[r][c] >= cutoff)
					temp[r][c] = returnBiome(temperature[r][c],rain[r][c]);
				else
					temp[r][c] = -1;
			}
		return temp;
	}

	public double[][] assignTemperature(double nDiv)
	{
		//int chunkLength = rows; //chunkService.returnChunkLength();
		//double[][] oldSource = new PerlinNoise(870).makePerlinNoise((int)nDiv,(int)nDiv,3,8,3,0.5,2);
		//return PerlinNoise.expand(oldSource,nDiv*2);
		double[][] oldSource = new PerlinNoise(seed).generate(new double[]{32,32,3,16,3,1,3,nDiv});
		return oldSource;
	}

	//Returns an interpolated map which gives each chunk a level of rain, based on temperature
	//Arctic climates do not have rain, tropical climates can have any level
	Random rainRandom = new Random(870L);
	public double[][] assignRain(double[][] temperature)
	{
		double[][] returnThis = new double[temperature.length][temperature[0].length];
		for (int i = 0; i < temperature.length; i++)
		{
			for (int j = 0; j < temperature[0].length; j++)
			{
				returnThis[i][j] = rainRandom.nextDouble()*temperature[i][j] + rainRandom.nextDouble();
			}
		}
		//returnThis = PerlinNoise.recurInter(returnThis,1,returnThis.length/2);
		return returnThis;
	}

	//returns the biome based on temperature, t, and rain, r
	/*
	 * 0 ice
	 * 1 taiga
	 * 2 desert
	 * 3 savannah
	 * 4 dry forest
	 * 5 forest
	 * 6 rainforest
	 * 7 beach (outdated)
	 */
	public int returnBiome(double t, double r)
	{
		if (t > 3)
		{
			if (r > 3.5)
				return 6;
			else if (r > 2.5)
				return 5;
			else if (r > 1.5)
				return 4;
			/*else if (r > 1.25)
				return 3;*/
			else 
				return 2;
		}
		else if (t > 2)
		{
			if (r > 2)
				return 5;
			else if (r > 1.5)
				return 4;
			else if (r > 0.75)
				return 3;
			else 
				return 2;
		}
		else if (t > 1)
		{
			if (r > 1)
				return 4;
			if (r > 0.5)
				return 3;
			else 
				return 2;
		}
		else
		{
			if (r > 0.5)
				return 1;
			else 
			{
				//System.out.println(t + " " + r);
				return 0;
			}
		}
	}

}
