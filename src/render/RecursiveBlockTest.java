package render;

//A new experimental method of terrain generation using a recursive creation of generated blocks

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

import entity.Player;
import terrain.RecursiveBlock;
import terrain.RecursiveBlock.Entity;
import vector.*;

public class RecursiveBlockTest extends PApplet {
	
	public boolean drawHeightMap = false;
	public Player player;
	public PImage background;
	public long seed = 87069200L;
	public RecursiveBlock t;
	public int widthBlock = 10;

	public static void main(String[] args)
	{
		PApplet.main(new String[] { RecursiveBlockTest.class.getName() });
	}

	public void setup()
	{
		size(1500,900,P3D);
		t = new RecursiveBlock();
		t.generateTerrain(seed,widthBlock);
		player = new Player();
		background = loadImage("desktop.png");
	}

	public void draw()
	{
		background(150,225,255);
		smooth(4);
		//background(background);
		lights();
		noStroke();
		//stroke(0);
		fill(135, 206, 235);
		perspective(3.14F/2,15F/9F,1,10000);
		camera(player.posX,player.posY,player.posZ,player.tarX,player.tarY,player.tarZ,0,-1,0);
		ArrayList<Entity> entities = t.entities;
		double[][] terrain = t.terrain;
		if (!drawHeightMap)
		{
			//camera(150,150,150,0,0,0,0,-1,0);
			for (int i = 0; i < entities.size(); i++)
			{
				Entity en = entities.get(i);
				pushMatrix();
				translate(en.posX, en.posY, en.posZ);
				box(en.sizeX, en.sizeY, en.sizeZ);
				popMatrix();
			}
		}
		else
		{
			/*camera(50*widthBlock*expandRatio,50*widthBlock*expandRatio,50*widthBlock*expandRatio,
					1*widthBlock*expandRatio,1*widthBlock*expandRatio,1*widthBlock*expandRatio,
					0,-1,0);*/
			for (int r = 0; r < terrain.length; r++)
			{
				for (int c = 0; c < terrain[0].length; c++)
				{
					double height = terrain[r][c];
					float dist = dist(player.posX,player.posZ,r*widthBlock,c*widthBlock);
					int con = 3;
					if (dist > 500)
					{
						int sampleSize = 2;
						if (dist > 1000) sampleSize = 4;
						if (height > 1 && r % sampleSize == 0 && c % sampleSize == 0)
						{
							pushMatrix();
							translate(r*widthBlock, (float)Math.floor((double)height/2D*con), c*widthBlock);
							box(widthBlock*sampleSize, (float)Math.floor((double)height*con), widthBlock*sampleSize);
							//println((int)height);
							popMatrix();
						}
					}
					else
					{
						if (dist <= 150)
						{
							stroke(0);
						}
						else
						{
							noStroke();
						}
						if (height > 1)
						{
							pushMatrix();
							translate(r*widthBlock, (float)Math.floor((double)height/2D*con), c*widthBlock);
							box(widthBlock, (float)Math.floor((double)height*con), widthBlock);
							//println((int)height);
							popMatrix();
						}
					}
				}
			}
		}
		int dist = 5;
		if (keySet[0])
		{
			player.posX -= dist;
			player.tarX -= dist;
		}
		if (keySet[1])
		{
			player.posZ -= dist;
			player.tarZ -= dist;
		}
		if (keySet[2])
		{
			player.posX += dist;
			player.tarX += dist;
		}
		if (keySet[3])
		{
			player.posZ += dist;
			player.tarZ += dist;
		}
		if (keySet[4])
		{
			player.posY += dist;
		}
		if (keySet[5])
		{
			player.posY -= dist;
		}
	}

	public boolean[] keySet = new boolean[6];

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

	public void keyPressed()
	{
		if (key == 'r')
		{
			seed = System.currentTimeMillis();
			t.generateTerrain(seed,widthBlock);
		}
		else if (key == 't')
		{
			drawHeightMap = !drawHeightMap;
		}
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
		redraw();
	}

}
