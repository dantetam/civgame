package terrain;

//A new experimental method of terrain generation using a recursive creation of generated blocks

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Random;

import vector.*;

public class RecursiveBlock extends PApplet {

	public ArrayList<Entity> entities;
	public long seed = 87069200L;
	public Random random;
	public double[][] terrain;
	public PImage background;
	public boolean drawHeightMap = false;
	public int widthBlock = 5;
	public int expandRatio = 2;
	
	public static void main(String[] args)
	{
		PApplet.main(new String[] { RecursiveBlock.class.getName() });
	}

	public void setup()
	{
		size(1500,900,P3D);
		generateTerrain(seed);
		background = loadImage("desktop.png");
	}

	public void draw()
	{
		background(150,225,255);
		//background(background);
		lights();
		noStroke();
		fill(135, 206, 235);
		perspective(3.14F/2,15F/9F,1,10000);
		if (!drawHeightMap)
		{
			camera(150,150,150,0,0,0,0,-1,0);
			for (int i = 0; i < entities.size(); i++)
			{
				Entity en = entities.get(i);
				pushMatrix();
				translate(en.posX, en.posY, en.posZ);
				box(en.sizeX, en.sizeY, en.sizeZ);
				popMatrix();
			}
		}
		else
		{
			camera(100*widthBlock*expandRatio,100*widthBlock*expandRatio,100*widthBlock*expandRatio,
					50*widthBlock*expandRatio,50*widthBlock*expandRatio,50*widthBlock*expandRatio,
					0,-1,0);
			for (int r = 0; r < terrain.length; r++)
			{
				for (int c = 0; c < terrain[0].length; c++)
				{
					double height = terrain[r][c];
					int con = 2;
					if (height > 5)
					{
						pushMatrix();
						translate(r*widthBlock, (int)(height/2D)*con, c*widthBlock);
						box(widthBlock, (int)height*con, widthBlock);
						//println((int)height);
						popMatrix();
					}
				}
			}
		}
	}

	public void keyPressed()
	{
		if (key == 'r')
		{
			seed = System.currentTimeMillis();
			generateTerrain(seed);
		}
		else if (key == 't')
		{
			drawHeightMap = !drawHeightMap;
		}
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
	public double[][] heightMap()
	{
		int minX = 0; int maxX = 0; int minZ = 0; int maxZ = 0;
		int minY = 10000;
		int width = 3;
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

	public void generateTerrain(long seed)
	{
		println("-----------------------------------");
		println(seed);
		entities = new ArrayList<Entity>();
		for (int i = entities.size() - 1; i >= 0; i--)
		{
			entities.remove(i);
		}
		entities = null;
		entities = new ArrayList<Entity>();
		Entity start = new Entity();
		random = new Random(seed);
		entities.add(start);
		start.setPos(0,100,0);
		start.setSize(100,100,100);
		terrain(start,3);
		int n = 0;
		for (int i = 0; i < entities.size(); i++)
		{
			Entity en = entities.get(i);
			if (en.getMass() > 1500)
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
		terrain = heightMap();
		terrain = expandData(terrain, terrain.length*expandRatio);
		//println(n + " blocks");
		printTable(terrain);
	}

	public void printTable(double[][] t)
	{
		for (int r = 0; r < t.length; r++)
		{
			for (int c = 0; c < t[0].length; c++)
			{
				int height = (int)t[r][c];
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
