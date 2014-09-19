package entity;

import java.util.ArrayList;

import vector.*;

public class Player {

	public float posX, posY, posZ;
	public float tarX, tarY, tarZ;
	public float rotY = (float)Math.PI/4F; //degrees

	public Player()
	{
		posX = 2000;
		posY = 700;
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
		tarX = posX + 1000*(float)Math.cos(rotY);
		tarY = 0;
		tarZ = posZ + 1000*(float)Math.sin(rotY);
	}

	public Line getLookVector()
	{
		return new Line(tarX-posX,posX,tarY-posY,posY,tarZ-posZ,posZ);
	}

}
