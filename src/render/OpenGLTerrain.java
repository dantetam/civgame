package render;

import processing.core.PApplet;
import terrain.Erosion.Droplet;

//Renders terrain specifically with the P3D library

public class OpenGLTerrain extends PApplet {

	public Main main;
	public double[][] terrain;
	public int cutoff;

	public OpenGLTerrain(Main main)
	{
		this.main = main;
	}

	//Keys are w,a,s,d,q,e respectively
	public boolean[] keySet = new boolean[6];

	public void keyPressed()
	{
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

		main.executeKey(key);
		redraw();
	}

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

	public void setup()
	{
		size(1500,900,P3D);
		frameRate(30);
		//camera(1500,1500,1500,0,0,0,0,-1,0);
		//noLoop();
	}
	
	public void draw()
	{
		background(135, 206, 235);
		perspective(3.14F/2,15F/9F,1,10000);
		//noStroke();
		int width = 20; int con = 1;
		camera(main.player.posX,main.player.posY,main.player.posZ,main.player.tarX,main.player.tarY,main.player.tarZ,0,1,0);
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				pushMatrix();
				if ((int)terrain[r][c] > cutoff)
				{
					fill(0,200,0);
					translate(r*width,-(float)((terrain[r][c] - cutoff)/2*con),c*width);
					box(width,-(int)(terrain[r][c] - cutoff)*con,width);
					Droplet d = main.erosion.waterLevel[r][c];
					if (d != null) 
					{
						fill(0,0,255);
						//pushMatrix();
						translate(0,-(float)((terrain[r][c] - cutoff)/2*con)-10,0);
						//translate(r*width,-(int)(terrain[r][c] - cutoff)/2*con - 10,c*width);
						box(width,10,width);
						//popMatrix();
					}
				}
				popMatrix();
			}
		}

		int dist = 15;
		if (keySet[0])
		{
			main.player.posZ -= dist;
			main.player.tarZ -= dist;
		}
		if (keySet[1])
		{
			main.player.posX -= dist;
			main.player.tarX -= dist;
		}
		if (keySet[2])
		{
			main.player.posZ += dist;
			main.player.tarZ += dist;
		}
		if (keySet[3])
		{
			main.player.posX += dist;
			main.player.tarX += dist;
		}
		if (keySet[4])
		{
			main.player.posY += dist;
		}
		if (keySet[5])
		{
			main.player.posY -= dist;
		}
	}

	public void setTerrain(double[][] terrain, int cutoff)
	{
		this.terrain = terrain;
		this.cutoff = cutoff;
	}

}
