package entity;

import data.Color;

public class Entity {

	public float posX, posY, posZ;
	public float sizeX, sizeY, sizeZ;
	public Color color;
	
	public Entity()
	{
		
	}
	
	public void moveTo(float x, float y, float z)
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
	}
	
	public void size(float x, float y, float z)
	{
		sizeX = x;
		sizeY = y;
		sizeZ = z;
	}
	
	public boolean within(double x, double y, double z)
	{
		return x >= posX - sizeX/2 && x <= posX + sizeX/2 &&
				 y >= posY - sizeY/2 && y <= posY + sizeY/2 &&
				 z >= posZ - sizeZ/2 && z <= posZ + sizeZ/2;
	}
	
}
