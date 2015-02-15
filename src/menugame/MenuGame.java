package menugame;

import java.util.Random;

import system.CivilizationSystem;
import terrain.*;
import game.*;

public class MenuGame {

	public Grid grid;
	public long seed;
	public CivilizationSystem civSystem;
	public String terrainString;
	
	public Civilization[][] civRecord; //A record of the owners of the respective tiles
	public Civilization[][] civUnitRecord; //A record of the units present at those tiles
	
	public boolean testing = true;
	
	public MenuGame(long seed)
	{
		this.seed = seed;
		
		BaseTerrain map;
		double[][] terrain = null; 
		//float con = 1F;
		float cutoff;
		
		/*int len = 64;
		double[][] temp = DiamondSquare.makeTable(50,50,50,50,len+1);
		temp[temp.length/2][temp[0].length/2] = 200;
		temp[0][temp[0].length/2] = 50;
		temp[temp.length-1][temp[0].length/2] = 50;
		temp[temp[0].length/2][0] = 50;
		temp[temp[0].length/2][temp.length-1] = 50;
		BaseTerrain map = new DiamondSquare(temp);
		map.seed(seed);
		double[][] terrain = map.generate(new double[]{0, 0, len, 40, 0.7});
		float cutoff = 100;*/
		
		int[] choices = {1,10,11};
		String terrainType = "terrain" + choices[(int)(Math.random()*choices.length)];
		terrainString = terrainType;
		//System.out.println(terrainType);
		
		if (terrainType.equals("terrain1"))
		{
			map = new PerlinNoise(seed);
			terrain = map.generate(new double[]{32,32,150,8,1,0.8,6,64,55});
			cutoff = 55;
		}
		else if (terrainType.equals("terrain2"))
		{
			map = new RecursiveBlock(seed);
			terrain = map.generate(new double[]{10,0});
			cutoff = 1;
		}
		else if (terrainType.equals("terrain10"))
		{
			int len = 64;
			double[][] temp = DiamondSquare.makeTable(50,50,50,50,len+1);
			map = new DiamondSquare(temp);
			map.seed(seed);
			//ds.diamond(0, 0, 4);
			//displayTables = ds.dS(0, 0, len, 40, 0.7)
			//map.seed(seed);
			terrain = map.generate(new double[]{0, 0, len, 40, 0.7});
			//System.out.println(terrain);
			cutoff = 60;
		}
		else if (terrainType.equals("terrain11"))
		{
			int len = 64;
			double[][] temp = DiamondSquare.makeTable(50,50,50,50,len+1);
			temp[temp.length/2][temp[0].length/2] = 200;
			temp[0][temp[0].length/2] = 50;
			temp[temp.length-1][temp[0].length/2] = 50;
			temp[temp[0].length/2][0] = 50;
			temp[temp[0].length/2][temp.length-1] = 50;
			map = new DiamondSquare(temp);
			map.seed(seed);
			//ds.diamond(0, 0, 4);
			//displayTables = ds.dS(0, 0, len, 40, 0.7)
			//map.seed(seed);
			terrain = map.generate(new double[]{0, 0, len, 40, 0.7});
			//System.out.println(terrain);
			cutoff = 100;
		}
		else if (terrainType.equals("terrain4"))
		{
			map = new RecursiveBlock(seed);
			double[][] master = map.generate(new double[]{10,1});
			terrain = new double[master.length/10 + 1][master.length/10 + 1];
			for (int r = 0; r < master.length; r += 10)
			{
				for (int c = 0; c < master[0].length; c += 10)
				{
					terrain[r/10][c/10] = master[r][c];
				}
			}
			cutoff = 1;
		}
		else if (terrainType.equals("terrain5"))
		{
			map = new PerlinNoise(seed);
			terrain = map.generate(new double[]{32,32,150,8,1,0.8,6,32,-150});
			cutoff = -150;
		}
		else
		{
			System.err.println("No map!");
			int[] err = new int[5]; err[10] = 0;
			cutoff = 0;
		}
		
		int[][] biomes = assignBiome(terrain, (int)cutoff);
		grid = new Grid("Athens", terrain, biomes, assignResources(biomes), new int[biomes.length][biomes[0].length],
				(int)(Math.random()*3) + 7, 
				0,
				3,
				1, 
				(int)cutoff, seed);
		civRecord = new Civilization[terrain.length][terrain[0].length];
		civUnitRecord = new Civilization[terrain.length][terrain[0].length];
		makeRivers(biomes);
		civSystem = new CivilizationSystem(this);
		civSystem.theGrid = grid;
		if (testing)
		{
			for (int i = 0; i < grid.civs.length; i++)
			{
				Civilization civ = grid.civs[i];
				//civ.war = Math.min(1, civ.war*2);
				civ.tallwide = Math.min(1, civ.tallwide*2);
			}
		}
	}
	
	public void tick()
	{
		civSystem.requestTurn = true;
		//Record the current owners of tiles
		for (int r = 0; r < civRecord.length; r++)
		{
			for (int c = 0; c < civRecord[0].length; c++)
			{
				Tile t = grid.getTile(r,c);
				Civilization civ = t.owner;
				//civRecord[r][c] = null;
				civRecord[r][c] = civ;
				if (t.occupants.size() > 0)
				{
					civUnitRecord[r][c] = t.occupants.get(0).owner; //Get the first unit's owner
				}
				else
				{
					civUnitRecord[r][c] = null;
				}
			}
		}
		//Tick the game one turn forward
		civSystem.tick();
	}
	
	public String[] gameParameters()
	{
		return new String[]{terrainString, "4", "8"};
	}

	public void makeRivers(int[][] biomes)
	{
		grid.verticalRivers = new boolean[biomes.length][biomes.length - 1];
		grid.horizontalRivers = new boolean[biomes.length - 1][biomes.length];
		for (int r = 0; r < grid.verticalRivers.length; r++)
		{
			for (int c = 0; c < grid.verticalRivers[0].length; c++)
			{
				if (biomes[r][c] >= 1 && biomes[r][c+1] >= 1)
				{
					if (Math.random() < 0.02*biomes[r][c])
					{
						grid.verticalRivers[r][c] = true;
					}
				}
			}
		}
		for (int r = 0; r < grid.horizontalRivers.length; r++)
		{
			for (int c = 0; c < grid.horizontalRivers[0].length; c++)
			{
				if (biomes[r][c] >= 1 && biomes[r+1][c] >= 1)
				{
					if (Math.random() < 0.02*biomes[r][c])
					{
						grid.horizontalRivers[r][c] = true;
					}
				}
			}
		}
		//^ Set them directly from here
		//grid.verticalRivers = verticalRivers;
		//grid.horizontalRivers = horizontalRivers;
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
