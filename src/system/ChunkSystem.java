package system;

import processing.core.PApplet;
import render.CivGame;

public class ChunkSystem extends BaseSystem {

	public int[][] chunkMap;
	public float[] dist;
	public double[] angle;
	public double playerAngle;

	public ChunkSystem(CivGame civGame) 
	{
		super(civGame);
		chunkMap = new int[civGame.grid.getTiles().length][civGame.grid.getTiles()[0].length];
		/*for (int r = 0; r < chunkMap.length; r++)
		{
			for (int c = 0; c < chunkMap[0].length; c++)
			{

		 * 0,0 0
		 * 0,1 0
		 * 0,4 1
		 * 4,0 2
		 * 
		 * 3 1
		 * 4 1
		 * 5 2

				chunkMap[r][c] = (r%4)*((c%4)+Math.signum(c%4)) + (c - c%4);
				System.out.print((int)chunkMap[r][c] + " ");
			}
			System.out.println();
		}*/
		int width = 8;
		int chunkNum = 0;
		for (int r = 0; r < chunkMap.length + width; r += width)
		{
			for (int c = 0; c < chunkMap[0].length + width; c += width)
			{
				for (int i = 0; i < width; i++)
				{
					for (int j = 0; j < width; j++)
					{
						if (r+i < chunkMap.length && c+j < chunkMap[0].length)
						{
							chunkMap[r+i][c+j] = chunkNum;
						}
					}
				}
				chunkNum++;
			}
		}
		dist = new float[chunkNum];
		angle = new double[chunkNum];
		/*int last = 1000;
		for (int r = 0; r < chunkMap.length; r++)
		{
			for (int c = 0; c < chunkMap[0].length; c++)
			{
				if ((int)chunkMap[r][c] != last)
					System.out.print((int)chunkMap[r][c] + " ");
				last = (int)chunkMap[r][c];
			}
			System.out.println();
		}*/
	}

	public int chunkFromLocation(int posX, int posY)
	{
		int w = main.widthBlock();
		return chunkMap[(posX - posX%w)/w][(posY - posY%w)/w];
	}

	public int[] locationFromChunk(int chunk)
	{
		for (int r = 0; r < chunkMap.length; r++)
		{
			for (int c = 0; c < chunkMap[0].length; c++)
			{
				if (chunkMap[r][c] == chunk)
				{
					int w = main.widthBlock();
					return new int[]{r*w,c*w};
				}
			}
		}
		//System.err.println("Chunk not found: " + chunk);
		return null;
	}

	public void tick() 
	{
		if (main.frameCount % 50 == 0)
		{
			for (int i = 0; i < dist.length; i++)
			{
				int[] dists = locationFromChunk(i);
				if (dists != null)
					dist[i] = PApplet.dist(dists[0], dists[1], main.player.posX, main.player.posZ);
				else
					dist[i] = -1;
			}
			for (int i = 0; i < angle.length; i++)
			{
				int[] dists = locationFromChunk(i);
				if (dists != null)
				{
					angle[i] = Math.atan2(dists[1] - main.player.posZ, dists[0] - main.player.posX);
				}
				else
					angle[i] = -10;
				playerAngle = Math.atan2(main.player.tarZ - main.player.posZ, main.player.posX - main.player.posX);
			}
		}
	}

}
