package terrain;

//A new experimental method of terrain generation using a recursive creation of generated blocks

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Random;

import vector.*;

public class RecursiveBlock extends PApplet {

	public ArrayList<Entity> entities;
	public long seed = 87069200L;
	public Random random;

	public static void main(String[] args)
	{
		PApplet.main(new String[] { RecursiveBlock.class.getName() });
	}

	public void setup()
	{
		size(1500,900,P3D);
		generateTerrain(seed);
		heightMap();
	}

	public void draw()
	{
		background(255);
		lights();
		noStroke();
		fill(135, 206, 235);
		camera(150,150,150,0,0,0,0,-1,0);
		perspective(3.14F/2,15F/9F,1,10000);
		for (int i = 0; i < entities.size(); i++)
		{
			Entity en = entities.get(i);
			pushMatrix();
			translate(en.posX, en.posY, en.posZ);
			box(en.sizeX, en.sizeY, en.sizeZ);
			popMatrix();
		}
	}

	public void keyPressed()
	{
		if (key == 'r')
		{
			seed = System.currentTimeMillis();
			println(seed);
			generateTerrain(seed);
		}
	}

	//Returns a more familiar 2d array of heights
	public double[][] heightMap()
	{
		int minX = 0; int maxX = 0; int minZ = 0; int maxZ = 0;
		int width = 3;
		for (int i = 0; i < entities.size(); i++)
		{
			Entity en = entities.get(i);
			if (en.posX < minX) minX = (int)en.posX;
			if (en.posZ < minZ) minZ = (int)en.posZ;
			if (en.posX > maxX) maxX = (int)en.posX;
			if (en.posZ > maxZ) maxZ = (int)en.posZ;
		}
		double[][] temp = new double[(int)(maxX-minX)/width + 10][(int)(maxZ-minZ)/width + 10];
		println(temp.length + " " + temp[0].length);
		int row = 0; int col = 0; //Keep track of position in table
		for (int r = minX; r <= maxX; r++)
		{
			for (int c = minZ; c <= maxZ; c++)
			{
				ArrayList<Entity> candidates = getNear(r,c,10);
				int max = 0;
				for (int i = 0; i < candidates.size(); i++)
				{
					Integer height = candidates.get(i).intersectRay(r, c);
					if (height != null)
					{
						if (height > max) max = height;
					}
				}
				try 
				{
					temp[row][col] = max;
				} catch (Exception e) 
				{
					//e.printStackTrace();
					//println(row + " " + col);
				}
				
				col++;
			}
			col = 0;
			row++;
		}
		return temp;
	}

	public void generateTerrain(long seed)
	{
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
		for (int i = 0; i < entities.size(); i++)
		{
			Entity en = entities.get(i);
			if (en.getMass() > 1500)
			{
				entities.remove(i);
				en = null;
				i--;
			}
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
	}

}
