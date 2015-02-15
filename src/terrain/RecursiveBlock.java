package terrain;

import java.util.ArrayList;
import java.util.Random;

public class RecursiveBlock extends BaseTerrain {

	public ArrayList<Entity> entities;
	public boolean[][] zeroMap;	
	public long seed;
	public int expandRatio = 2;
	
	public RecursiveBlock(long seed)
	{
		random = new Random(seed);
		this.seed = seed;
	}
	
	public void rough()
	{
		//int first = (int)Math.floor(Math.log(terrain.length)/Math.log(2D));
		int first = 3;
		if (Math.pow(2,first) == terrain.length) first--;
		while (true)
		{
			int width = (int)Math.pow(2, first);
			//println("Yoooo " + width);
			for (int i = 0; i < terrain.length - width; i += width)
			{
				for (int j = 0; j < terrain.length - width; j += width)
				{
					if (i + width >= terrain.length || j + width >= terrain.length) continue;
					//println(i + " " + j + " " + width);
					//if (devZero(i,j,width))
					{
						//print("-----------");
						dS(i,j,width,10,0.5);
					}
					//println(dS(i,j,width,10,0.5).size());
				}
			}
			if (width <= 8) break;
			width /= 2;
		}
	}

	//Returns true if there are fewer than 3 zeroes and the data is flat (low std dev)
	public boolean devZero(int i, int j, int width)
	{
		int zero = 0;
		int terms = 0; int avg = 0;
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for (int r = i; r < i + width; r++)
		{
			for (int c = j; c < j + width; c++)
			{
				int num = (int)terrain[r][c];
				//print(num + " ");
				if (num == 0)
				{
					zero++;
				}
				else
				{
					terms++;
					avg += num;
					numbers.add(num);
				}
			}
			//println();
		}
		if (terms == 0 || zero >= 2) return false;
		avg /= terms;
		int sum = 0;
		for (int iter = 0; iter < numbers.size(); iter++)
		{
			sum += Math.pow(numbers.get(iter) - avg,2);
		}
		//println("STDDEV: " + (int) Math.pow(sum/terms, 0.5) + " " + i + " " + j);
		return (int) Math.pow(sum/terms, 0.5) < 3;
	}

	public double[][] expandData(double[][] a, double nDiv)
	{
		BicubicInterpolator bi = new BicubicInterpolator();
		double[][] returnThis = new double[(int)nDiv][(int)nDiv];
		for (int i = 0; i < nDiv; i++)
		{
			for (int j = 0; j < nDiv; j++)
			{
				double idx = (double)(a.length*i)/nDiv;
				double idy = (double)(a[0].length*j)/nDiv;
				//System.out.println("L: " + idx + "," + idy + ": " + bi.getValue(source,idx,idx));
				double zeroCheck = bi.getValue(a,idx,idy);
				returnThis[i][j] = zeroCheck >= 0 ? zeroCheck : 0;
			}
		}
		return returnThis;
	}

	//Returns a more familiar 2d array of heights
	public double[][] heightMap(int widthBlock)
	{
		int minX = 0; int maxX = 0; int minZ = 0; int maxZ = 0;
		int minY = 10000;
		int width = 4;
		for (int i = 0; i < entities.size(); i++)
		{
			Entity en = entities.get(i);
			if (en.posX < minX) minX = (int)en.posX;
			if (en.posZ < minZ) minZ = (int)en.posZ;
			if (en.posX > maxX) maxX = (int)en.posX;
			if (en.posZ > maxZ) maxZ = (int)en.posZ;
			if (en.posY < minY) minY = (int)en.posY;
		}
		minY--;
		double[][] temp = new double[(int)(maxX-minX)/width + 1][(int)(maxZ-minZ)/width + 1];
		//println(temp.length + " " + temp[0].length);
		int row = 0; int col = 0; //Keep track of position in table
		for (int r = minX; r <= maxX; r += width)
		{
			for (int c = minZ; c <= maxZ; c += width)
			{
				ArrayList<Entity> candidates = getNear(r,c,widthBlock);
				int max = 0;
				for (int i = 0; i < candidates.size(); i++)
				{
					Integer height = candidates.get(i).intersectRay(r, c);
					if (height != null)
					{
						if (height > max) max = height;
					}
				}
				temp[row][col] = max > 0 ? max - minY : 0;
				/*Entity en = getNearest(candidates,r,c);
				if (en == null)
					temp[row][col] = 0;
				else
				{
					//println(en.posX);
					//println(en.topFace());
					temp[row][col] = en.topFace();
					temp[row][col] = temp[row][col] > 0 ? temp[row][col] - minY : 0;
					temp[row][col] -= 10;
				}*/
				col++;
			}
			col = 0;
			row++;
		}
		return temp;
	}

	//Starts the iterative loop over the terrain that modifies it
	//Returns a list of the tables between each diamond-square cycle
	public void dS(int sX, int sY, int width, double startAmp, double ratio)
	{
		while (true)
		{
			for (int r = sX; r <= terrain.length - 2; r += width)
			{
				for (int c = sY; c <= terrain[0].length - 2; c += width)
				{
					//System.out.println(r + " " + t.length);
					//System.out.println(c + " " + t.length);
					//System.out.println("r " + r + " c " + c);
					diamond(r, c, width, startAmp);
				}
			}
			if (width > 1)
			{
				width /= 2;
				startAmp *= ratio;
			}
			else
				break;
		}
	}

	public void diamond(int sX, int sY, int width, double startAmp)
	{
		if (sX + width >= terrain.length || sY + width >= terrain.length) return;
		terrain[sX + width/2][sY + width/2] = (terrain[sX][sY] + terrain[sX+width][sY] + terrain[sX][sY+width] + terrain[sX+width][sY+width])/4 + 
				startAmp*(random.nextDouble() - 0.5)*2;
		/*System.out.println(terrain[sX][sY]);
			System.out.println(terrain[sX+width][sY]);
			System.out.println(terrain[sX][sY+width]);
			System.out.println(terrain[sX+width][sY+width]);
			System.out.println("-------");*/
		//printTable(t);
		//System.out.println("-------");
		if (width > 1)
		{
			square(sX + width/2, sY, width, startAmp);
			square(sX, sY + width/2, width, startAmp);
			square(sX + width, sY + width/2, width, startAmp);
			square(sX + width/2, sY + width, width, startAmp);
			//diamond(sX, sY, width/2);
			//diamond(sX + width/2, sY, width/2);
			//diamond(sX, sY + width/2, width/2);
			//diamond(sX + width/2, sY + width/2, width/2);
		}
	}

	public void square(int sX, int sY, int width, double startAmp)
	{
		if (sX - width/2 < 0)
		{
			//System.out.println(sX + " 1 " + sY);
			terrain[sX][sY] = (terrain[sX][sY - width/2] + terrain[sX][sY + width/2] + terrain[sX + width/2][sY])/3;
		}
		else if (sX + width/2 >= terrain.length)
		{
			//System.out.println(sX + " 2 " + sY);
			terrain[sX][sY] = (terrain[sX][sY - width/2] + terrain[sX][sY + width/2] + terrain[sX - width/2][sY])/3;
		}
		else if (sY - width/2 < 0)
		{
			//System.out.println(sX + " 3 " + sY);
			terrain[sX][sY] = (terrain[sX][sY + width/2] + terrain[sX + width/2][sY] + terrain[sX - width/2][sY])/3;
		}
		else if (sY + width/2 >= terrain.length)
		{
			//System.out.println(sX + " 4 " + sY);
			terrain[sX][sY] = (terrain[sX][sY - width/2] + terrain[sX + width/2][sY] + terrain[sX - width/2][sY])/3;
		}
		else
		{
			//System.out.println(sX + " 5 " + sY);
			terrain[sX][sY] = (terrain[sX][sY + width/2] + terrain[sX][sY - width/2] + terrain[sX + width/2][sY] + terrain[sX - width/2][sY])/4;
		}
		terrain[sX][sY] += startAmp*(random.nextDouble() - 0.5)*2;
		//printTable(t);
		//System.out.println("-------");
	}
	
	public double[][] generate()
	{
		System.out.println("Use the overloaded function with arguments");
		return null;
	}

	public double[][] generate(double[] args)
	{
		//args[0] widthBlock
		//println("-----------------------------------");
		//println(seed);
		entities = new ArrayList<Entity>();
		for (int i = entities.size() - 1; i >= 0; i--)
		{
			entities.remove(i);
		}
		entities = null;
		entities = new ArrayList<Entity>();
		Entity start = new Entity();
		entities.add(start);
		start.setPos(0,50,0);
		start.setSize(50,50,50);
		//TODO: More than one starter block?
		terrain(start,3);
		
		if (args[1] == 1)
		{
			float base = 70;
			float radius = 70; 
			float block = 64;
			for (int i = 0; i < 2; i++) 
			{
				for (int j = 0; j < (int)Math.pow(2,i); j++) 
				{
					double angle = Math.random()*6.28;
					System.out.println(angle);
					int size = (int)(block/(Math.pow(1.5,j-1)));
					if (size >= 16)
					{
						startIsland(size,size,size,(int)(radius*Math.cos(angle)),100,(int)(radius*Math.sin(angle)),(int)(Math.random()*2 + 1));
					}
				}
				radius = radius + (float)Math.pow(base,1.3 - i/10);
			}
		}
		
		int n = 0;
		for (int i = 0; i < entities.size(); i++)
		{
			Entity en = entities.get(i);
			if (en.getMass() > 2000)
			{
				entities.remove(i);
				en = null;
				i--;
			}
			else
			{
				n++;
			}
		}
		terrain = heightMap((int)args[0]);
		terrain = expandData(terrain, terrain.length*expandRatio);
		//println(n + " blocks");
		//printTable(terrain);
		zeroMap = new boolean[terrain.length][terrain[0].length];
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				if (terrain[r][c] == 0)
				{
					zeroMap[r][c] = true;
				}
				else
				{
					zeroMap[r][c] = false;
				}
			}
		}
		//if (args[1] == 1) rough();
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				if (zeroMap[r][c])
				{
					terrain[r][c] = 0;
				}
			}
		}
		terrain = expandData(terrain, terrain.length*2);
		return terrain;
	}
	
	public void startIsland(int x, int y, int z, int posX, int posY, int posZ, int n)
	{
		Entity start = new Entity();
		entities.add(start);
		start.setPos(posX,posY,posZ);
		start.setSize(x,y,z);
		terrain(start,n);
	}

	public void printTable(double[][] t)
	{
		for (int r = 0; r < t.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				int height = (int)terrain[r][c];
				if (height < 10)
				{
					//Attempt to program the equivalent of C/C++'s "%02d" to align the data correctly
					System.out.print("0" + height + " ");
				}
				else
					System.out.print(height + " ");
			}
			System.out.println();
		}
	}

	public ArrayList<Entity> getNear(double r, double c, double slack)
	{
		ArrayList<Entity> temp = new ArrayList<Entity>();
		for (int i = 0; i < entities.size(); i++)
		{
			Entity en = entities.get(i);
			if (Math.sqrt(Math.pow(en.posX - r,2) + Math.pow(en.posZ - c,2)) <= slack)
			{
				temp.add(en);
			}
		}
		return temp;
	}

	public Entity getNearest(ArrayList<Entity> candidates, double r, double c)
	{
		double leastDist = 10000;
		Entity returnThis = null;
		for (int i = 0; i < entities.size(); i++)
		{
			Entity en = entities.get(i);
			double dist = Math.sqrt(Math.pow(en.posX - r,2) + Math.pow(en.posZ - c,2)); 
			if (dist < leastDist)
			{
				leastDist = dist;
				returnThis = en;
			}
		}
		return returnThis;
	}

	public void terrain(Entity en, int times)
	{
		for (int i = 0; i < times; i++)
		{
			//random.nextDouble();
			if (random.nextDouble() < 0.25) 
			{
				for (int n = 0; n < 4; n++)
				{
					//random.nextDouble();
				}
				continue;
			}
			Entity clone = new Entity();
			int newSize = (int)Math.floor((random.nextDouble()*0.5 + 0.5)*en.sizeX);
			newSize -= newSize % 2;
			clone.setSize(newSize,newSize,newSize);

			int corner = (int)(random.nextDouble()*4);
			if (clone.sizeX <= 3 || clone.sizeY <= 3 || clone.sizeZ <= 3 || clone.getMass() < 30) {return;}
			clone.setPos(en.posX, en.posY, en.posZ);
			if (corner == 0)
				clone.move(-en.sizeX/2,0,en.sizeX/2);
			else if (corner == 1)
				clone.move(en.sizeX/2,0,en.sizeX/2);
			else if (corner == 2)
				clone.move(en.sizeX,0,-en.sizeX/2);
			else	
				clone.move(-en.sizeX/2,0,-en.sizeX/2);
			clone.move(0,clone.sizeY/2-en.sizeY/2,0);
			clone.move(-clone.posX%2,-clone.posY%2,-clone.posZ%2);
			//corner++;
			if (random.nextDouble() < 0.4)
				terrain(clone,(int)(random.nextDouble()*3) + 1);
			else
				terrain(clone,(int)(random.nextDouble()*2) + 2);
			entities.add(clone);
		}
	}

	public class Entity
	{
		public float posX, posY, posZ;
		public float sizeX, sizeY, sizeZ;

		public Entity() {}
		public void setPos(float x, float y, float z) {posX = x; posY = y; posZ = z;}
		public void setSize(float x, float y, float z) {sizeX = x; sizeY = y; sizeZ = z;}
		public void move(float x, float y, float z) {posX += x; posY += y; posZ += z;}
		public float getMass() {return sizeX*sizeY*sizeZ;}

		public Integer intersectRay(int x, int z)
		{
			if (x > posX - sizeX/2 && x < posX + sizeX/2 && z > posZ - sizeZ/2 && z < posZ + sizeZ/2)
			{
				return new Integer((int)(posY + sizeY/2));
			}
			return null;
		}

		public int topFace() {return (int)(posY + sizeY/2);}
	}
	
}
