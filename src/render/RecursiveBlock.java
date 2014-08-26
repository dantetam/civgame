package render;

//A new experimental method of terrain generation using a recursive creation of generated blocks

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Random;

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
		entities = new ArrayList<Entity>();
		
		random = new Random(seed);
		
		Entity start = new Entity();
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
	
	public void draw()
	{
		background(255);
		lights();
		noStroke();
		fill(135, 206, 235);
		camera(200,200,200,0,0,0,0,-1,0);
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
	
	public void terrain(Entity en, int times)
	{
		for (int i = 0; i < times; i++)
		{
			if (random.nextDouble() < 0.25) {continue;} 
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
			if (random.nextDouble() < 0.5)
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
	}
	
}
