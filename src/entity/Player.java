package entity;

import java.util.ArrayList;

import vector.*;

public class Player {

	public float posX, posY, posZ;
	public float tarX, tarY, tarZ;
	public float rotY = 45; //degrees
	
	public Player()
	{
		posX = 2000;
		posY = 700;
		posZ = 2000;
		tarX = 1000;
		tarY = 0;
		tarZ = 1000;
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
	
	public Line getLookVector()
	{
		return new Line(tarX-posX,posX,tarY-posY,posY,tarZ-posZ,posZ);
	}
	
}
