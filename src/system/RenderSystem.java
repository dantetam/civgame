package system;

import java.util.ArrayList;
import java.util.HashMap;

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
	public String[] pictures = {"pickaxe","sword"};

	public RenderSystem(CivGame civGame)
	{
		super(civGame);
		player = main.player;
		blankLandTile = getBlock(32,32,150,150,150);
		blankSeaTile = getBlock(32,32,150,225,255);
		imageMap(pictures);
		//System.out.println(imageMap.get("pickaxe"));
	}

	private PImage[] tileTemplates;
	private PImage blankLandTile, blankSeaTile;
	public void tick()
	{
		if (requestUpdate)
		{
			main.background(255);
			//PImage temp = getBlock(0,0,32,32);
			for (int r = main.player.cornerX; r < main.player.cornerX + main.player.windowX; r++)
			{
				for (int c = main.player.cornerY; c < main.player.cornerY + main.player.windowY; c++)
				{
					/*main.image(getBlock(r,c,(int)(main.width/main.grid.rows),(int)(main.height/main.grid.cols)),
						r*(main.width/main.grid.rows),
						c*(main.height/main.grid.cols)
						);*/
					Tile t = main.grid.getTile(r,c);
					if (t != null)
					{
						float imgX = (r-main.player.cornerX)*32, imgY = (c-main.player.cornerY)*32;
						if (t.owner != null)
						{
							PImage temp = tileTemplates[t.owner.id];
							//temp = getBlock(r,c,32,32);
							main.image(temp,imgX,imgY);
						}
						else
						{
							if (main.terrain[r][c] >= 0)
							{
								main.image(blankLandTile,imgX,imgY);
							}
							else
							{
								main.image(blankSeaTile,imgX,imgY);
							}
						}
						if (t.occupants.size() > 0)
						{
							for (int i = 0; i < t.occupants.size(); i++)
							{
								BaseEntity en = t.occupants.get(i);
								main.pushStyle();
								main.tint(en.owner.r, en.owner.g, en.owner.b);
								if (en.name.equals("Worker"))
								{
									main.image(imageMap.get("pickaxe"),imgX,imgY);
								}
								else if (en.offensiveStr > 0)
								{
									main.image(imageMap.get("sword"),imgX,imgY);
								}
								main.popStyle();
							}
						}
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

	//Load all the image objects so we have them
	public HashMap<String,PImage> imageMap;
	public void imageMap(String[] pictures)
	{
		imageMap = new HashMap<String,PImage>();
		for (int i = 0; i < pictures.length; i++)
		{
			imageMap.put(pictures[i], main.loadImage("/pictures/"+pictures[i]+".png"));
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
		//main.camera(player.posX,player.posY,player.posZ,player.tarX,player.tarY,player.tarZ,0,-1,0);
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
