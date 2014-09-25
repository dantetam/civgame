package system;

import java.util.ArrayList;

import processing.core.PApplet;
import render.Button;
import render.CivGame;
import entity.*;
import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Tile;
import game.TileEntity;
import data.Color;
import data.EntityData;

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
		//main.smooth(4);
		//background(background);
		main.noStroke();
		main.lights();
		//stroke(0);
		main.fill(135, 206, 235);
		main.perspective(3.14F/2,15F/9F,1,10000);
		//System.out.println(player);
		setCamera();
		/*for (int i = 0; i < terrain.entities.size(); i++)
		{
			renderBlock(terrain.entities.get(i));
		}*/
		//Look to see if the entity is both within the player's vision and is a close enough distance
		for (int r = 0; r < main.terrain.length; r++)
		{
			for (int c = 0; c < main.terrain[0].length; c++)
			{
				int chunk = main.chunkSystem.chunkFromLocation(r*(int)widthBlock,c*(int)widthBlock);
				float dist = main.chunkSystem.dist[chunk]; 
				//TODO: The center of the player's view is the right bound of the viewing angle
				if ((main.player.posY <= 100 && dist < dist2 && dist != -1F && angle(main.chunkSystem.angle[chunk]+Math.PI, main.chunkSystem.playerAngle+Math.PI) && main.chunkSystem.angle[chunk] != -10) ||
						(dist < dist1 && dist != -1F))
				{
					renderBlock(dist,r,c);
					Tile t = main.grid.getTile(r,c);
					if (t.improvement != null)
					{
						renderGameEntity(t.improvement,dist,r,c);
					}
					if (t.occupants.size() > 0)
					{
						for (int i = 0; i < t.occupants.size(); i++)
						{
							GameEntity en = t.occupants.get(i);
							renderGameEntity(en,dist,r,c);
						}
					}
					if (t.forest)
					{
						main.pushMatrix();
						//main.translate(r*widthBlock, (float)main.terrain[r][c]*con/2F, c*widthBlock);
						renderModel("Forest",r,c,0,0,0);
						main.popMatrix();
					}
					if (r < main.terrain.length - 1)
					{
						if (main.horizontalRivers[r][c]) renderRiver(r+1,c,r,c);
					}
					if (c < main.terrain[0].length - 1)
					{
						if (main.verticalRivers[r][c]) renderRiver(r,c,r,c+1);
					}
				}
				else
				{
					/*if (dist < dist1 && dist != -1F)
					{
						renderBlock(dist,r,c);
						Tile t = main.grid.getTile(r,c);
						if (t.improvement != null)
						{
							renderGameEntity(t.improvement,dist,r,c);
						}
						if (t.occupants.size() > 0)
						{
							for (int i = 0; i < t.occupants.size(); i++)
							{
								GameEntity en = t.occupants.get(i);
								renderGameEntity(en,dist,r,c);
							}
						}
					}*/
				}
			}
		}
		/*for (int r = 0; r < main.verticalRivers.length; r++)
		{
			for (int c = 0; c < main.verticalRivers[0].length; c++)
			{
				if (main.verticalRivers[r][c]) renderRiver(r,c,r,c+1);
			}
		}
		for (int r = 0; r < main.horizontalRivers.length; r++)
		{
			for (int c = 0; c < main.horizontalRivers[0].length; c++)
			{
				if (main.horizontalRivers[r][c]) renderRiver(r+1,c,r+1,c);
			}
		}*/
		/*for (int r = 0; r < main.grid.rows; r++)
		{
			for (int c = 0; c < main.grid.cols; c++)
			{
				int chunk = main.chunkSystem.chunkFromLocation(r*(int)widthBlock,c*(int)widthBlock);
				float dist = main.chunkSystem.dist[chunk];
				if (dist < dist1 && dist != -1F && angle(main.chunkSystem.angle[chunk]+Math.PI, main.chunkSystem.playerAngle+Math.PI) && main.chunkSystem.angle[chunk] != -10)
				{

				}
			}
		}*/
		/*main.hint(PApplet.DISABLE_DEPTH_TEST);
		main.camera();
		main.perspective();
		main.rect(500, 500, 500, 500);
		main.hint(PApplet.ENABLE_DEPTH_TEST);*/
		//main.perspective();
		//main.ortho();
		//main.stroke(255);
		//float lineWidth = 20;
		//main.line(main.width/2 - lineWidth/2, main.height/2 - lineWidth/2, main.width/2 + lineWidth/2, main.height/2 + lineWidth/2);
	}

	//Render a block by accessing main's P3D abilities
	public float con; public float cutoff;
	private final int dist0 = 300;
	private final int dist1 = 1000; private final int dist2 = 1350;
	private double viewAngle = Math.PI/2 + Math.PI/12;
	public void renderBlock(float dist, int r, int c)
	{
		//if (dist < 1000 && en.sizeY >= cutoff)
		if (main.terrain[r][c] >= 0)
		{
			float sampleSize;
			//float dist = (float)Math.sqrt(Math.pow(player.posX - en.posX, 2) + Math.pow(player.posY - en.posY, 2) + Math.pow(player.posZ - en.posZ, 2));
			//main.fill(135, 206, 235);
			Color color = EntityData.brickColorMap.get(EntityData.groundColorMap.get(main.grid.getTile(r, c).biome));
			main.fill((float)color.r*255F,(float)color.g*255F,(float)color.b*255F);
			main.noStroke();
			/*if (main.grid.irrigated(r, c))
				main.fill(0,0,0);*/
			Tile t = main.grid.getTile(r,c);
			/*if (dist > dist2)
			{
				sampleSize = 4;
				if (!((r) % sampleSize == 0 && (c) % sampleSize == 0))
				{
					return;
				}
			}
			else if (dist > dist1)
			{	
				sampleSize = 2;
				if (!((r+1) % sampleSize == 0 && (c+1) % sampleSize == 0))
				{
					return;
				}
			}*/
			
			sampleSize = 1;
			main.pushMatrix();
			
			Entity temp = new Entity();
			temp.size(widthBlock*sampleSize, (float)main.terrain[r][c]*con + 1, widthBlock*sampleSize);
			temp.moveTo(r*widthBlock*sampleSize, (float)main.terrain[r][c]*con/2F, c*widthBlock*sampleSize);
			if (main.player.lookingAtEntity(temp))
			{
				main.menuSystem.target = main.grid.getTile(r, c);
				//main.fill(0);
				main.stroke(0,0,255);
				main.strokeWeight(8);
				if (main.grid.getTile(r,c).occupants.size() > 0)
				{
					main.menuSystem.highlighted = main.grid.getTile(r,c).occupants;
				}
				else
				{
					main.menuSystem.highlighted = new ArrayList<GameEntity>();
				}
			}
			else
			{
				if (main.grid.getTile(r,c).owner != null)
				{
					Civilization civ = t.owner;
					main.stroke(civ.r, civ.g, civ.b);
					if (t.harvest)
					{
						main.strokeWeight(5);
					}
					else
					{
						main.strokeWeight(1);
					}
				}
			}
			//main.translate(en.posX + widthBlock, en.posY*con, en.posZ + widthBlock);
			//main.translate(en.posX, en.posY*con, en.posZ);
			main.translate(r*widthBlock*sampleSize, (float)main.terrain[r][c]*con/2F, c*widthBlock*sampleSize);
			main.box(widthBlock*sampleSize, (float)main.terrain[r][c]*con, widthBlock*sampleSize);

			//Render a hill or mountain

			if (sampleSize == 1)
			{
				if (t.shape == 1)
				{
					main.pushMatrix();
					main.translate(0, (float)main.terrain[r][c]*con/2, 0);
					main.box(widthBlock/2*sampleSize);
					main.popMatrix();
				}
				else if (t.shape == 2)
				{
					main.pushMatrix();
					main.translate(0, (float)main.terrain[r][c]*con/2, 0);
					main.translate(0, widthBlock*sampleSize/4, 0);
					main.box(widthBlock/2*sampleSize, widthBlock*sampleSize*1.5F, widthBlock/2*sampleSize);
					main.popMatrix();
				}
				int res = t.resource;
				if (res != 0)
				{
					main.pushMatrix();
					main.fill(EntityData.get(res));
					main.translate(0, 15, 0);
					main.box(5);
					main.popMatrix();
				}
			}
			main.popMatrix();
		}
	}

	//Render a game entity
	public void renderGameEntity(BaseEntity en, float dist, int r, int c)
	{
		main.fill(en.owner.r,en.owner.g,en.owner.b);
		//float dist = (float)Math.sqrt(Math.pow(player.posX - r*widthBlock, 2) + Math.pow(player.posY - main.terrain[r][c], 2) + Math.pow(player.posZ - c*widthBlock, 2));
		main.noStroke();
		float sizeY = widthBlock*3F;
		main.pushMatrix();
		if (en.location.harvest)
		{
			main.strokeWeight(5);
		}
		else
		{
			main.strokeWeight(1);
		}
		/*if (en.name.equals("City"))
			{
				main.fill(0);
				main.stroke(en.owner.r,en.owner.g,en.owner.b);
				main.translate(r*widthBlock, (float)(main.terrain[r][c]-cutoff)*con + sizeY/2, c*widthBlock);
				main.box(widthBlock*0.4F,sizeY,widthBlock*0.4F);
			}*/
		//System.out.println(en.name);
		//System.out.println(EntityData.getModel(en.name));
		renderModel(en.getName(),r,c,en.owner.r,en.owner.g,en.owner.b);
		/*else
		{
			main.fill(0);
			main.stroke(en.owner.r,en.owner.g,en.owner.b);
			main.translate(r*widthBlock, (float)(main.terrain[r][c])*con, c*widthBlock);
			main.box(widthBlock*0.4F,sizeY,widthBlock*0.4F);
		}*/
		/*else
		{
			main.translate(r*widthBlock, (float)(main.terrain[r][c]-cutoff)*con + sizeY/2, c*widthBlock);
			main.box(widthBlock*0.4F,sizeY,widthBlock*0.4F);
			if (en.name.equals("Settler"))
			{
				main.translate(0,sizeY/2 + widthBlock*0.4F,0);
				main.fill(150,225,255);
				main.box(widthBlock*0.4F*2);
			}
			else if (en.name.equals("Worker"))
			{
				main.translate(0,sizeY/2 + widthBlock*0.4F/2,0);
				main.fill(150,225,255);
				main.box(widthBlock*0.4F);
			}
			else if (en.name.equals("Warrior"))
			{
				main.translate(0,sizeY/2 + widthBlock*0.4F/2,0);
				main.fill(255,0,0);
				main.box(widthBlock*0.4F);
			}
		}*/
		main.popMatrix();
	}

	public void renderModel(String name, int r, int c, float red, float green, float blue)
	{
		//System.out.println(name);
		float[][] model = EntityData.getModel(name);
		if (model != null)
		{
			main.translate(r*widthBlock, (float)(main.terrain[r][c])*con, c*widthBlock);
			main.pushMatrix();
			for (int i = 0; i < model.length; i++)
			{
				main.pushMatrix();
				float[] t = model[i];
				if ((int)t[0] == 0)
				{
					main.fill(150);
				}
				else if ((int)t[0] == 1)
				{
					main.fill(red,green,blue);
				}
				main.translate(t[1],t[2],t[3]);
				main.rotateY(t[5]);
				main.box(t[7],t[8],t[9]);
				main.popMatrix();
			}
			main.popMatrix();
		}
	}

	public void renderRiver(int r1, int c1, int r2, int c2)
	{
		main.fill(0,0,150);
		if (r1 == r2) //"Vertical"
		{
			main.pushMatrix();
			main.translate(r1*widthBlock,0,(c1+0.5F)*widthBlock);
			main.box(widthBlock,5,5);
			main.popMatrix();
		}
		else if (c1 == c2) //"Horizontal"
		{
			main.pushMatrix();
			main.translate((r1-0.5F)*widthBlock,0,c1*widthBlock);
			main.box(5,5,widthBlock);
			main.popMatrix();
		}
		else
		{
			System.err.println("Invalid river");
		}
	}

	public void setCamera()
	{
		main.camera(player.posX,player.posY,player.posZ,player.tarX,player.tarY,player.tarZ,0,-1,0);
	}

	//Make a model of entities with a height map
	public float widthBlock = 20;
	/*public void addTerrain(double[][] t, float con, float cutoff)
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
				en.moveTo(r*widthBlock, (float)(h-cutoff)/2F, c*widthBlock);
				en.size(widthBlock, (float)(h-cutoff), widthBlock);
				terrain.add(en,r,c);
				this.con = con;
				this.cutoff = cutoff;
			}
		}
	}*/

	//Compares two angles between 0 and 6.28 (2*Math.PI)
	public boolean angle(double a1, double a2)
	{
		if (a2 > a1)
		{
			return (2*Math.PI - a2) + a1 <= viewAngle || a2 - a1 <= viewAngle;
		}
		else
		{
			return (2*Math.PI - a1) + a2 <= viewAngle || a1 - a2 <= viewAngle;
		}
	}

}
