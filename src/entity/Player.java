package entity;

import java.util.ArrayList;

import vector.*;

public class Player {

	public float posX, posY, posZ;
	public float tarX, tarY, tarZ;
	
	public Player()
	{
		posX = 500;
		posY = -500;
		posZ = 500;
		tarX = 0;
		tarY = 0;
		tarZ = 0;
	}
	
	public Line getLookVector()
	{
		return new Line(tarX-posX,posX,tarY-posY,posY,tarZ-posZ,posZ);
	}
	
}
