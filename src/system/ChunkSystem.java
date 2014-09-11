package system;

import render.CivGame;

public class ChunkSystem extends BaseSystem {

	public int[][] chunkMap;
	public double[] dist;
	
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
		int width = 16;
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
		for (int r = 0; r < chunkMap.length; r++)
		{
			for (int c = 0; c < chunkMap[0].length; c++)
			{
				System.out.print((int)chunkMap[r][c] + " ");
			}
			System.out.println();
		}
	}
	
	public int chunkFromLocation(double posX, double posY)
	{
		
	}

	public void tick() 
	{
		if (main.frameCount % 250 == 0)
		{
			
		}
	}

}
