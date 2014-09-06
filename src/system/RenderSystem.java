package system;

import java.util.ArrayList;

import render.CivGame;
import entity.*;
import data.Color;

public class RenderSystem extends BaseSystem {

	public Model terrain;
	public Player player;

	public RenderSystem(CivGame civGame)
	{
		super(civGame);
		terrain = new Model();
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
		for (int i = 0; i < terrain.entities.size(); i++)
		{
			renderBlock(terrain.entities.get(i));
		}
	}

	//Render a block by accessing main's P3D abilities
	public float con; public float cutoff;
	public void renderBlock(Entity en)
	{
		float dist = (float)Math.sqrt(Math.pow(player.posX - en.posX, 2) + Math.pow(player.posY - en.posY, 2) + Math.pow(player.posZ - en.posZ, 2));
		//if (dist < 1000 && en.sizeY >= cutoff)
		if (en.sizeY >= cutoff)
		{	
			main.pushMatrix();
			main.translate(en.posX, en.posY*con, en.posZ);

			main.box(en.sizeX, (en.sizeY - cutoff)*con, en.sizeZ);
			
			main.popMatrix();
		}
	}

	//Make a model of entities with a height map
	public void addTerrain(double[][] t, float con, float cutoff)
	{
		for (int r = 0; r < t.length; r++)
		{
			for (int c = 0; c < t[0].length; c++)
			{
				int widthBlock = 10;
				double h = t[r][c];
				//float con = (1F/10F)*widthBlock;
				Entity en = new Entity();
				//en.moveTo(r*widthBlock, (float)h/2F*con, c*widthBlock);
				//en.size(widthBlock, (float)h*con, widthBlock);
				en.moveTo(r*widthBlock, (float)h/2F, c*widthBlock);
				en.size(widthBlock, (float)h, widthBlock);
				terrain.add(en);
				this.con = con;
				this.cutoff = cutoff;
			}
		}
	}

}
