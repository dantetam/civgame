package render;

//A new experimental method of terrain generation using a recursive creation of generated blocks

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Random;

import entity.Player;
import terrain.BicubicInterpolator;
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
	public Player player;

	public static void main(String[] args)
	{
		PApplet.main(new String[] { RecursiveBlock.class.getName() });
	}

	public void setup()
	{
		size(1500,900,P3D);
		generateTerrain(seed);
		player = new Player();
		background = loadImage("desktop.png");
		rough();
	}

	public void draw()
	{
		background(150,225,255);
		smooth(4);
		//background(background);
		lights();
		noStroke();
		//stroke(0);
		fill(135, 206, 235);
		perspective(3.14F/2,15F/9F,1,10000);
		camera(player.posX,player.posY,player.posZ,player.tarX,player.tarY,player.tarZ,0,-1,0);
		if (!drawHeightMap)
		{
			//camera(150,150,150,0,0,0,0,-1,0);
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
			/*camera(50*widthBlock*expandRatio,50*widthBlock*expandRatio,50*widthBlock*expandRatio,
					1*widthBlock*expandRatio,1*widthBlock*expandRatio,1*widthBlock*expandRatio,
					0,-1,0);*/
			for (int r = 0; r < terrain.length; r++)
			{
				for (int c = 0; c < terrain[0].length; c++)
				{
					double height = terrain[r][c];
					int con = 1;
					if (height > 1)
					{
						pushMatrix();
						translate(r*widthBlock, (int)Math.floor(height/2D*con), c*widthBlock);
						box(widthBlock, (int)Math.floor(height*con), widthBlock);
						//println((int)height);
						popMatrix();
					}
				}
			}
		}
		int dist = 5;
		if (keySet[0])
		{
			player.posX -= dist;
			player.tarX -= dist;
		}
		if (keySet[1])
		{
			player.posZ -= dist;
			player.tarZ -= dist;
		}
		if (keySet[2])
		{
			player.posX += dist;
			player.tarX += dist;
		}
		if (keySet[3])
		{
			player.posZ += dist;
			player.tarZ += dist;
		}
		if (keySet[4])
		{
			player.posY += dist;
		}
		if (keySet[5])
		{
			player.posY -= dist;
		}
	}

	public boolean[] keySet = new boolean[6];

	public void keyReleased()
	{
		if (key == 'w')
		{
			keySet[0] = false;
		}
		if (key == 'a')
		{
			keySet[1] = false;
		}
		if (key == 's')
		{
			keySet[2] = false;
		}
		if (key == 'd')
		{
			keySet[3] = false;
		}
		if (key == 'q')
		{
			keySet[4] = false;
		}
		if (key == 'e')
		{
			keySet[5] = false;
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
		if (key == 'w')
		{
			keySet[0] = true;
		}
		if (key == 'a')
		{
			keySet[1] = true;
		}
		if (key == 's')
		{
			keySet[2] = true;
		}
		if (key == 'd')
		{
			keySet[3] = true;
		}
		if (key == 'q')
		{
			keySet[4] = true;
		}
		if (key == 'e')
		{
			keySet[5] = true;
		}
		redraw();
	}
	
	public void rough()
	{
		int first = (int)Math.floor(Math.log(terrain.length)/Math.log(2D))+1;
		if (Math.pow(2,first) == terrain.length) first--;
		int width = (int)Math.pow(2, first) + 1;
		println(terrain.length - width + " " + width);
		for (int i = 0; i < terrain.length; i += width)
		{
			for (int j = 0; j < terrain.length; j += width)
			{
				println(dS(i,j,width,10,0.5).size());
			}
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

	//Starts the iterative loop over the terrain that modifies it
	//Returns a list of the tables between each diamond-square cycle
	public ArrayList<double[][]> dS(int sX, int sY, int width, double startAmp, double ratio)
	{
		ArrayList<double[][]> temp = new ArrayList<double[][]>();
		int origWidth = width;
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
					double[][] record = new double[origWidth][origWidth];
					for (int nr = 0; nr < origWidth; nr++)
					{
						for (int nc = 0; nc < origWidth; nc++)
						{
							record[nr][nc] = (double)terrain[nr][nc];
						}
					}
					temp.add(record);
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
		return temp;
	}

	public void diamond(int sX, int sY, int width, double startAmp)
	{
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
		//TODO: More than one starter block?
		terrain(start,3);
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
		terrain = heightMap();
		terrain = expandData(terrain, terrain.length*expandRatio);
		//println(n + " blocks");
		printTable(terrain);
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
