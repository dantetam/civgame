package system;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
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
	public boolean requestUpdate = false;

	public RenderSystem(CivGame civGame)
	{
		super(civGame);
		player = main.player;
		blankTile = getBlock(32,32,150,150,150);
	}

	private PImage[] tileTemplates;
	private PImage blankTile;
	public void tick()
	{
		if (requestUpdate)
		{
			main.background(255);
			//PImage temp = getBlock(0,0,32,32);
			for (int r = 0; r < main.terrain.length; r++)
			{
				for (int c = 0; c < main.terrain[0].length; c++)
				{
					/*main.image(getBlock(r,c,(int)(main.width/main.grid.rows),(int)(main.height/main.grid.cols)),
						r*(main.width/main.grid.rows),
						c*(main.height/main.grid.cols)
						);*/
					Civilization civ = main.grid.getTile(r,c).owner;
					if (civ != null)
					{
						PImage temp = tileTemplates[civ.id];
						//temp = getBlock(r,c,32,32);
						main.image(temp,
								r*32,
								c*32
								);
					}
					else
					{
						main.image(blankTile,
								r*32,
								c*32
								);
					}
					//main.fill((float)(Math.random()*255));
					//main.rect(r*32, c*32, 32, 32);
				}
			}
		}
	}

	//Calculate the images before so that we have them
	int tileWidth = 32, tileHeight = 32;
	public void getTileTemplates(Civilization[] civs)
	{
		tileTemplates = new PImage[civs.length];
		for (int i = 0; i < civs.length; i++)
		{
			tileTemplates[i] = getBlock(tileWidth, tileHeight, civs[i].r, civs[i].g, civs[i].b);
		}
	}

	public PImage getBlock(int w, int h, float red, float green, float blue)
	{
		PImage temp = main.createImage(w, h, main.ARGB);
		//main.pushStyle();
		for (int r = 0; r < w; r++)
		{
			for (int c = 0; c < h; c++)
			{
				temp.pixels[r*w + c] = main.color(red,green,blue,((float)(h-r)/(float)h)*255);
			}
		}

		int borderWidth = 2;
		for (int i = borderWidth; i >= 0; i--)
		{
			red = 150; green = 150; blue = 150;
			float a = Math.min(i,w-i-1)/(float)borderWidth*255F - 50F;
			//float a = 255;
			for (int row = 0; row < h; row++)
			{
				temp.pixels[row*h + i] = main.color(red,green,blue,a);
				temp.pixels[row*h + (w-i-1)] = main.color(red,green,blue,a);
				//temp.pixels[(w-i)*h + row] = color(0,0,0,a);
			}
			for (int col = 0; col < w; col++)
			{
				temp.pixels[i*w + col] = main.color(red,green,blue,a);
				temp.pixels[(w-i-1)*h + col] = main.color(red,green,blue,a);
			}
		}
		temp.updatePixels();
		//main.popStyle();
		return temp;
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
				if (main.grid.getTile(r,c) != null)
				{
					main.menuSystem.highlighted = main.grid.getTile(r,c);
				}
				else
				{
					main.menuSystem.highlighted = null;
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
				else if (main.menuSystem.settlerChoices != null)
				{
					for (int i = 0; i < main.menuSystem.settlerChoices.length; i++)
					{
						Tile tile = main.menuSystem.settlerChoices[i];
						if (tile.equals(t))
						{
							main.stroke(200,0,255);
							main.strokeWeight(5);
							break;
						}
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

	public void renderHiddenBlock(float dist, int r, int c)
	{
		//if (dist < 1000 && en.sizeY >= cutoff)
		if (main.terrain[r][c] >= 0)
		{
			float sampleSize = 1;
			Color color = EntityData.brickColorMap.get(EntityData.groundColorMap.get(main.grid.getTile(r, c).biome));
			main.fill((float)color.r*125F,(float)color.g*125F,(float)color.b*125F);
			main.noStroke();
			Tile t = main.grid.getTile(r,c);

			if (dist > dist2)
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
			}
			Entity temp = new Entity();
			temp.size(widthBlock*sampleSize, (float)main.terrain[r][c]*con + 1, widthBlock*sampleSize);
			temp.moveTo(r*widthBlock*sampleSize, (float)main.terrain[r][c]*con/2F, c*widthBlock*sampleSize);
			if (main.player.lookingAtEntity(temp))
			{
				main.menuSystem.target = main.grid.getTile(r, c);
				//main.fill(0);
				main.stroke(0,0,255);
				main.strokeWeight(8);
				if (main.grid.getTile(r,c) != null)
				{
					main.menuSystem.highlighted = main.grid.getTile(r,c);
				}
				else
				{
					main.menuSystem.highlighted = null;
				}
			}
			//sampleSize = 1;
			main.pushMatrix();
			main.translate(r*widthBlock*sampleSize, (float)main.terrain[r][c]*con/2F, c*widthBlock*sampleSize);
			main.box(widthBlock*sampleSize, (float)main.terrain[r][c]*con, widthBlock*sampleSize);
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
					Color resColor = EntityData.get(res);
					main.fill((float)resColor.r*125F, (float)resColor.g*125F, (float)resColor.b*125F);
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
			main.stroke(en.owner.r,en.owner.g,en.owner.b);
		}
		else
		{
			main.strokeWeight(1);
		}

		if (main.menuSystem.getSelected() != null)
			if (en.equals(main.menuSystem.getSelected()))
			{
				main.stroke(0,0,255);
				main.strokeWeight(5);
				if (en instanceof GameEntity)
				{
					GameEntity gameEn = (GameEntity)en;
					if (gameEn.queueTiles.size() > 0)
					{
						//System.out.println(gameEn.queueTiles.size());
						for (int i = gameEn.queueTiles.size() - 1; i >= 0; i--)
						{
							Tile t = gameEn.queueTiles.get(i);
							main.pushMatrix();
							main.translate(t.row*widthBlock, 25, t.col*widthBlock);
							main.fill(((float)(i+1)/(float)gameEn.queueTiles.size())*255F);
							main.box(5,5,5);
							main.popMatrix();
						}
					}
				}
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
		main.noStroke();
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
	public static float widthBlock = 20;
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
