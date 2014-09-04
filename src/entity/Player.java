package entity;

import java.util.ArrayList;

import vector.*;

public class Player {

	public float posX, posY, posZ;
	public float tarX, tarY, tarZ;
	
	public Player()
	{
		posX = 500;
		posY = 200;
		posZ = 500;
		tarX = 0;
		tarY = 0;
		tarZ = 0;
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
