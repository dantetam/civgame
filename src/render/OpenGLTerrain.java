package render;

import processing.core.PApplet;

//Renders terrain specifically with the P3D library

public class OpenGLTerrain extends PApplet {

	public double[][] terrain;
	public int cutoff;

	public OpenGLTerrain()
	{

	}

	public void setup()
	{
		size(1500,900,P3D);
		//camera(1500,1500,1500,0,0,0,0,-1,0);
		noLoop();
	}

	public void draw()
	{
		background(135, 206, 235);
		fill(0,200,0);
		int width = 20; int con = 2;
		camera(width/2*terrain.length,500,width/2*terrain.length,0,0,0,0,-1,0);
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				pushMatrix();
				if ((int)terrain[r][c] > cutoff)
				{
					translate(r*width,(int)(terrain[r][c]-cutoff)*con,c*width);
					box(width,(int)(terrain[r][c] - cutoff)*con,width);
				}
				popMatrix();
			}
		}
	}

	public void setTerrain(double[][] terrain, int cutoff)
	{
		this.terrain = terrain;
		this.cutoff = cutoff;
	}

}
