package system;

import java.util.ArrayList;

import render.CivGame;
import entity.*;
import game.Civilization;
import data.Color;

public class RenderSystem extends BaseSystem {

	public GridModel terrain;
	public Player player;

	public RenderSystem(CivGame civGame)
	{
		super(civGame);
		player = main.player;
	}

	public void tick()
	{
		main.background(150,225,255);
		main.smooth(4);
		//background(background);
		main.noStroke();
		main.lights();
		//stroke(0);
		main.fill(135, 206, 235);
		main.perspective(3.14F/2,15F/9F,1,10000);
		//System.out.println(player);
		main.camera(player.posX,player.posY,player.posZ,player.tarX,player.tarY,player.tarZ,0,-1,0);
		/*for (int i = 0; i < terrain.entities.size(); i++)
		{
			renderBlock(terrain.entities.get(i));
		}*/
		for (int r = 0; r < terrain.entities.length; r++)
		{
			for (int c = 0; c < terrain.entities[0].length; c++)
			{
				renderBlock(terrain.entities[r][c],r,c);
			}
		}
	}

	//Render a block by accessing main's P3D abilities
	public float con; public float cutoff;
	public void renderBlock(Entity en, int r, int c)
	{
		//if (dist < 1000 && en.sizeY >= cutoff)
		if (en.sizeY >= cutoff)
		{
			int sampleSize;
			int dist1 = 500;
			int dist2 = 1000;
			float dist = (float)Math.sqrt(Math.pow(player.posX - en.posX, 2) + Math.pow(player.posY - en.posY, 2) + Math.pow(player.posZ - en.posZ, 2));
			main.fill(135, 206, 235);
			main.noStroke();
			if (dist > dist2)
			{
				sampleSize = 4;
				if (!(r % sampleSize == 0 && c % sampleSize == 0))
				{
					return;
				}
			}
			else if (dist > dist1)
			{	
				sampleSize = 2;
				if (!(r % sampleSize == 0 && c % sampleSize == 0))
				{
					return;
				}
			}
			else
			{
				if (main.grid.tiles[r][c].owner != null)
				{
					main.stroke(255);
					Civilization civ = main.grid.tiles[r][c].owner;
					main.fill(civ.r, civ.g, civ.b);
				}
				sampleSize = 1;
			}
			main.pushMatrix();
			main.translate(en.posX, en.posY*con, en.posZ);
			main.box(en.sizeX*sampleSize, (en.sizeY - cutoff)*con, en.sizeZ*sampleSize);
			main.popMatrix();
		}
	}

	//Make a model of entities with a height map
	public void addTerrain(double[][] t, float con, float cutoff)
	{
		terrain = new GridModel(t.length, t[0].length);
		for (int r = 0; r < t.length; r++)
		{
			for (int c = 0; c < t[0].length; c++)
			{
				int widthBlock = 20;
				double h = t[r][c];
				//float con = (1F/10F)*widthBlock;
				Entity en = new Entity();
				//en.moveTo(r*widthBlock, (float)h/2F*con, c*widthBlock);
				//en.size(widthBlock, (float)h*con, widthBlock);
				en.moveTo(r*widthBlock, (float)h/2F, c*widthBlock);
				en.size(widthBlock, (float)h, widthBlock);
				terrain.add(en,r,c);
				this.con = con;
				this.cutoff = cutoff;
			}
		}
	}

}
