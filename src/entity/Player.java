package entity;

import java.util.ArrayList;

import vector.*;

public class Player {

	public float posX, posY, posZ;
	public float tarX, tarY, tarZ;
	public float rotY = (float)Math.PI/4F; //degrees
	public float rotVertical = 0;

	public Player()
	{
		posX = 2000;
		posY = 300;
		posZ = 2000;
		update();
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

}
