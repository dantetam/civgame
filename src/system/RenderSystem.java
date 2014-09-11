package system;

import java.util.ArrayList;

import render.CivGame;
import entity.*;
import game.Civilization;
import game.GameEntity;
import game.Tile;
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
				int chunk = main.chunkSystem.chunkFromLocation(r*widthBlock,c*widthBlock);
				float dist = main.chunkSystem.dist[chunk];
				if (dist < 2000 && dist != -1F)
					renderBlock(terrain.entities[r][c],dist,r,c);
			}
		}
		for (int r = 0; r < main.grid.getTiles().length; r++)
		{
			for (int c = 0; c < main.grid.getTiles()[0].length; c++)
			{
				int chunk = main.chunkSystem.chunkFromLocation(r*widthBlock,c*widthBlock);
				float dist = main.chunkSystem.dist[chunk];
				if (dist < dist1 && dist != -1F)
				{
					Tile t = main.grid.getTiles()[r][c];
					if (t.improvement != null)
					{
						//Render the improvement
					}
					if (t.occupants.size() > 0)
					{
						for (int i = 0; i < t.occupants.size(); i++)
						{
							GameEntity en = t.occupants.get(i);
							renderGameEntity(en,dist,r,c);
						}
					}
				}
			}
		}
	}

	//Render a block by accessing main's P3D abilities
	public float con; public float cutoff;
	private int dist1 = 1000; private int dist2 = 1500;
	public void renderBlock(Entity en, float dist, int r, int c)
	{
		//if (dist < 1000 && en.sizeY >= cutoff)
		if (en.sizeY >= cutoff)
		{
			int sampleSize;
			//float dist = (float)Math.sqrt(Math.pow(player.posX - en.posX, 2) + Math.pow(player.posY - en.posY, 2) + Math.pow(player.posZ - en.posZ, 2));
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
				if (main.grid.getTiles()[r][c].owner != null)
				{
					main.stroke(255);
					Civilization civ = main.grid.getTiles()[r][c].owner;
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

	//Render a game entity
	public void renderGameEntity(GameEntity en, float dist, int r, int c)
	{
		main.fill(en.owner.r,en.owner.g,en.owner.b);
		//float dist = (float)Math.sqrt(Math.pow(player.posX - r*widthBlock, 2) + Math.pow(player.posY - main.terrain[r][c], 2) + Math.pow(player.posZ - c*widthBlock, 2));
		main.noStroke();
		float sizeY = widthBlock*3F;
		main.pushMatrix();
		main.translate(r*widthBlock, (float)(main.terrain[r][c]-cutoff)*con + sizeY/2, c*widthBlock);
		main.box(widthBlock*0.4F,sizeY,widthBlock*0.4F);
		main.popMatrix();
	}

	//Make a model of entities with a height map
	public int widthBlock = 20;
	public void addTerrain(double[][] t, float con, float cutoff)
	{
		terrain = new GridModel(t.length, t[0].length);
		for (int r = 0; r < t.length; r++)
		{
			for (int c = 0; c < t[0].length; c++)
			{
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
