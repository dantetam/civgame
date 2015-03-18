package entity;

import game.Civilization;
import game.GameEntity;
import game.Grid;

import java.util.ArrayList;

import system.RenderSystem;
import vector.*;

public class Player {

	public float posX, posY, posZ;
	public float tarX, tarY, tarZ;
	public float rotY = (float)Math.PI/4F; //degrees
	public float rotVertical = 0;
	
	public Civilization civ;
	//public ArrayList<GameEntity> moved; //Keep track of which units are moved; should be done for every civ
	//i.e. every civ moves their units until there are none left to move

	public Player()  
	{
		//this.civ = civ;
		//moved = new ArrayList<GameEntity>();
		posX = 1000;
		posY = 60;
		posZ = 1000;
		update();
	}
	
	public String toString()
	{
		return "Player: " + posX + " " + posY + " " + posZ + " " + tarX + " " + tarY + " " + tarZ;
	}

	/*public void moveTo(float x, float y, float z)
	{
		posX = x;
		posY = y;
		posZ = z;
	}

	public void move(float x, float y, float z)
	{
		posX += x;
		posY += y;
		posZ += z;
	}*/
	
	//Orient the player to the middle of the grid
	public void orient(Grid grid)
	{
		posX = grid.rows/2*RenderSystem.widthBlock;
		posY = 60;
		posZ = grid.cols/2*RenderSystem.widthBlock;
		rotY = (float)Math.PI/2;
		rotVertical = (float)Math.PI/3F;
	}
	
	public void update()
	{
		float dist = 100;
		tarX = posX + dist*(float)Math.cos(rotY);
		tarY = posY - dist*(float)Math.sin(rotVertical)*2;
		tarZ = posZ + dist*(float)Math.sin(rotY);
	}

	public Line getLookVector()
	{
		return new Line(tarX-posX,posX,tarY-posY,posY,tarZ-posZ,posZ);
	}

	public boolean lookingAtEntity(Entity en)
	{
		Line lookVector = getLookVector();
		Plane plane = (planeFromPoints(
				new Point(en.posX,en.posY - en.sizeY/2,en.posZ),
				new Point(en.posX+en.sizeY/2,en.posY - en.sizeY/2,en.posZ),
				new Point(en.posX+en.sizeY/2,en.posY - en.sizeY/2,en.posZ+en.sizeZ/2)
				));
		Point intersection = plane.intersect(lookVector);

		if (en.within(intersection.x,intersection.y,intersection.z))
		{
			return true;
		}
		return false;
	}

	public Plane planeFromPoints(Point a, Point b, Point c)
	{
		int x = (int)((b.y - a.y)*(c.z - a.z) - (c.y - a.y)*(b.z - a.z));
		int y = (int)(-(b.x - a.x)*(c.z - a.z) + (c.x - a.x)*(b.z - a.z));
		int z = (int)((b.x - a.x)*(c.y - a.y) - (c.x - a.x)*(b.y - a.y));
		int d = (int)(x*a.x + y*a.y + z*a.z);
		//int e = (int)(x*b.x + y*b.y + z*b.z);
		//System.out.println(d + " " + e);
		return new Plane(x,y,z,d);
	}

}
