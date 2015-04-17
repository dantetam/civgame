package system;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PMatrix3D;
import render.Button;
import render.CivGame;
import render.MouseHelper;
import terrain.DiamondSquare;
import vector.Point;
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
	}

	public void tick()
	{
		Tile h = main.menuSystem.highlighted;

		main.background(255);
		main.perspective(3.14F/2,15F/9F,1,10000);
		setCamera();
		main.noLights();

		/*if (h != null && requestUpdate)
		{
			int sight = 5;
			main.inputSystem.mouseHelper.clear();
			ArrayList<ArrayList<Point>> points = new ArrayList<ArrayList<Point>>();
			for (int r = h.row - sight; r <= h.row + sight; r++)
			{
				for (int c = h.col - sight; c <= h.col + sight; c++)
				{
					main.pushMatrix();
					main.translate(r*widthBlock, (float)main.terrain[r][c]*con/2F, c*widthBlock);
					main.fill(0,0,255);
					main.box(10);
					main.popMatrix();
				}
			}
			for (int y = 0; y < main.height; y += 5)
			{
				for (int x = 0; x < main.height; x += 5)
				{
					if (main.get(x,y) == main.color(0,0,255))
					{

					}
				}
			}
		}*/

		if (h != null && requestUpdate)
		{
			//PMatrix3D projMatrix = new PMatrix3D(1,0,0,);
		}

		main.background(150,225,255);
		//Pre-processing
		/*Tile tile = main.menuSystem.highlighted;
		if (tile != null)
		{

			main.pushMatrix();
			main.translate(tile.row*widthBlock, (float)main.terrain[tile.row][tile.col]*con/2F, tile.col*widthBlock);
			main.fill(255,0,0);
			main.box(widthBlock,widthBlock,widthBlock);
			main.popMatrix();
			//return;
		}*/
		main.background(150,225,255);

		main.pushStyle();
		main.perspective(3.14F/2,15F/9F,1,10000);
		setCamera();
		if (h != null && requestUpdate)
		{
			requestUpdate = false;
			//Update displacement of mouse
			main.pushMatrix();
			main.translate(h.row*widthBlock, (float)main.terrain[h.row][h.col]*con/2F, h.col*widthBlock);
			main.fill(255,0,0);
			main.box(2,5,2);
			main.popMatrix();
			search:
			{
				for (int r = (int)(main.width*2/5); r < (int)(main.width*3/5); r += 2)
				{
					for (int c = (int)(main.height*2/5); c < (int)(main.height*3/5); c += 5)
					{
						if (main.get(r,c) == main.color(255,0,0))
						{
							//System.out.println("Match: " + r + "," + c);
							main.menuSystem.highlightDispX = r; 
							main.menuSystem.highlightDispY = c;
							break search;
						}
					}
				}
			}
		}
		main.popStyle();

		main.perspective(); main.resetCamera(); main.resetShader(); main.resetMatrix();
		//main.background(150,225,255);
		//main.background(0);
		//main.smooth(4);
		//background(background);
		main.noStroke();
		//main.lights();
		main.directionalLight(200, 200, 200, (float)0.5, -1, 0);
		//stroke(0);
		main.background(135, 206, 235);
		main.perspective(3.14F/2,15F/9F,1,10000);
		//System.out.println(player);
		main.shader(main.lightShader, main.TRIANGLES);
		setCamera();

		/*System.out.println("Yooo");
		PMatrix3D matrix = main.pg.getMatrix((PMatrix3D)null);
		System.out.println(matrix.m00 + ", " + matrix.m01 + ", " + matrix.m02 + ", " + matrix.m03);
		System.out.println(matrix.m10 + ", " + matrix.m11 + ", " + matrix.m12 + ", " + matrix.m13);
		System.out.println(matrix.m20 + ", " + matrix.m21 + ", " + matrix.m22 + ", " + matrix.m23);
		System.out.println(matrix.m30 + ", " + matrix.m31 + ", " + matrix.m32 + ", " + matrix.m33);
		main.printMatrix();
		System.out.println("----");
		System.out.println(main.player.tarX + " " + main.player.tarY + " " + main.player.tarZ);*/
		/*for (int i = 0; i < terrain.entities.size(); i++)
		{
			renderBlock(terrain.entities.get(i));
		}*/
		//Look to see if the entity is both within the player's vision and is a close enough distance
		main.menuSystem.highlighted = null;
		for (int r = 0; r < main.terrain.length; r++)
		{
			for (int c = 0; c < main.terrain[0].length; c++)
			{
				int chunk = main.chunkSystem.chunkFromLocation(r*(int)widthBlock,c*(int)widthBlock);
				float dist = main.chunkSystem.dist[chunk];
				if (dist == -1F) continue;
				//TODO: The center of the player's view is the right bound of the viewing angle
				if (main.player.posY > 150 
						&& dist < dist2 && dist != -1F 
						&& angle(main.chunkSystem.angle[chunk]+Math.PI, main.chunkSystem.playerAngle+Math.PI) 
						&& main.chunkSystem.angle[chunk] != -10)
				{
					renderBlock(dist,r,c,true,true);
				}
				else if ((main.player.posY <= 150 && dist < dist0 && angle(main.chunkSystem.angle[chunk]+Math.PI, main.chunkSystem.playerAngle+Math.PI) && main.chunkSystem.angle[chunk] != -10) ||
						(dist < dist1))
				{
					/*if (!main.grid.civs[0].revealed[r][c] || main.showAll)
					{
						continue;
					}*/
					if (main.grid.civs[0].revealed[r][c] == 0 && !main.showAll)
					{
						continue;
					}

					if ((main.grid.civs[0].revealed[r][c] == 2) || main.showAll)
					{
						renderBlock(dist,r,c,false,false);
					}
					else if (main.grid.civs[0].revealed[r][c] == 1)
					{
						renderBlock(dist,r,c,true,false);
						continue;
					}

					Tile t = main.grid.getTile(r,c);
					if (t.improvement != null)
					{
						renderGameEntity(t.improvement,dist,r,c);
					}
					if (r < main.terrain.length - 1)
					{
						if (main.grid.horizontalRivers[r][c]) renderRiver(r+1,c,r,c);
					}
					if (c < main.terrain[0].length - 1)
					{
						if (main.grid.verticalRivers[r][c]) renderRiver(r,c,r,c+1);
					}
					/*if (!main.grid.civs[0].revealed[r][c] || main.showAll)
					{
						continue;
					}*/
					if (t.road)
						renderRoad(r,c);
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

		//Rough approximation of where the mouse is
		//Tile h = main.menuSystem.lastHighlighted;

		/*if (h != null)
		{
			int dX = (int)(main.mouseX - main.centerX);
			int dY = (int)(main.mouseY - main.centerY);
			//main.menuSystem.mouseHighlighted = main.grid.getTile(h.row + dX/70, h.col);
		}*/

		main.resetShader();
		/*main.strokeWeight(5);
		for (int r = 0; r < main.terrain.length; r++)
		{
			for (int c = 0; c < main.terrain[0].length; c++)
			{
				main.pushMatrix();
				main.translate(r*widthBlock, (float)main.terrain[r][c]*con/2F, c*widthBlock);
				main.translate(-widthBlock/2F, 0, -widthBlock/2F);
				float m = 3;
				System.out.println("*");
				for (int nr = r; nr < r + m; nr++)
				{
					for (int nc = c; nc < c + m; nc++)
					{
						main.pushMatrix();
						main.translate((float)(nr - nr%3)*-widthBlock/3F, 0, (float)(nc - nc%3)*-widthBlock/3F);
						main.point((float)nr/m*widthBlock,(float)vertices[nr][nc],(float)nc/m*widthBlock);
						main.point((float)nr/m*widthBlock,(float)vertices[nr][nc+1],(float)(nc+1)/m*widthBlock);
						main.point((float)(nr+1)/m*widthBlock,(float)vertices[nr][nc+1],(float)nc/m*widthBlock);
						main.popMatrix();
					}
				}
				main.popMatrix();
			}
		}*/
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
	//Hidden means not within the player's sight/revealed fog of war
	//
	public float con; public float cutoff;
	private int dist0 = 200, dist1 = 250, dist2 = 300;
	//private int dist0 = 500, dist1 = 750, dist2 = 1000;
	private double viewAngle = Math.PI/2 + Math.PI/12;
	private Point[][] vertices;
	private PImage[][] textures;
	public void renderBlock(float dist, int r, int c, boolean hidden, boolean lazy)
	{
		//if (dist < 1000 && en.sizeY >= cutoff)
		//if (main.terrain[r][c] >= 0)
		//if (main.grid.getTile(r,c).biome)
		{
			Tile t = main.grid.getTile(r,c);
			boolean strokedColor = false; 
			float sampleSize = 1;
			int n = 1; //Number of 3D squares display
			Color color = EntityData.brickColorMap.get(EntityData.groundColorMap.get(t.biome));
			if (t.shape == 2) color = new Color(1,1,1);
			if (!hidden)
				main.fill((float)color.r*255F,(float)color.g*255F,(float)color.b*255F);
			else if (hidden || lazy)
				main.fill((float)color.r*100F,(float)color.g*100F,(float)color.b*100F);
			main.noStroke();
			main.strokeWeight(1);
			Civilization civ = t.owner;

			Entity temp = new Entity();
			temp.size(widthBlock*sampleSize, (float)main.terrain[r][c]*con + 1, widthBlock*sampleSize);
			temp.moveTo(r*widthBlock*sampleSize, (float)main.terrain[r][c]*con/2F, c*widthBlock*sampleSize);
			if (main.player.lookingAtEntity(temp))
			{
				/*main.menuSystem.target = main.grid.getTile(r, c);
				//main.fill(0);
				main.stroke(0,0,255);
				main.strokeWeight(8);
				strokedColor = true;*/
				if (t != null)
				{
					//if ((main.grid.civs[0].revealed[t.row][t.col] == 0 && !main.showAll) || main.menuSystem.findButtonWithin(main.mouseX, main.mouseY) != null)
					if (main.grid.civs[0].revealed[t.row][t.col] == 0 && !main.showAll)	
						main.menuSystem.highlighted = null;
					else
						main.menuSystem.highlighted = t;
				}
			}
			if (main.menuSystem.mouseHighlighted != null)
			{
				if (main.menuSystem.mouseHighlighted.equals(t))
				{
					main.stroke(255,0,0);
					if (main.menuSystem.candidateField != null)
					{
						if (!main.menuSystem.candidateField.isEmpty())
							main.stroke(0,255,0);
					}
					else if (main.menuSystem.stack.size() > 0)
					{
						main.stroke(0,255,0);
						//main.stroke(0,150+100*(float)Math.sin(main.frameCount/50),0);
					}
					main.strokeWeight(8);
					strokedColor = true;
				}
			}
			if (main.menuSystem.stack != null && main.menuSystem.stack.size() > 0)
			{
				if (main.menuSystem.stack.get(0).location.equals(t))
				{
					main.stroke(0,100,255);
					n = 5;
					strokedColor = true;
				}
			}
			else if (main.menuSystem.getSelected() != null)
			{
				if (main.menuSystem.getSelected().location.equals(t))
				{
					main.stroke(150,225,255);
					n = 3;
					strokedColor = true;
				}
			}
			if (!strokedColor) //Don't overwrite a previous stroke
			{
				if (civ != null && !hidden)
				{
					main.stroke(civ.r, civ.g, civ.b);
					if (t.harvest)
					{
						main.strokeWeight(5);
					}
					else
					{
						main.strokeWeight(2);
					}
					//strokedColor = true;
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
							strokedColor = true;
							break;
						}
					}
				}
			}
			if (hidden)
			{
				main.strokeWeight(1);
			}

			main.pushMatrix();
			//main.translate(en.posX + widthBlock, en.posY*con, en.posZ + widthBlock);
			//main.translate(en.posX, en.posY*con, en.posZ);
			main.translate(r*widthBlock*sampleSize, (float)main.terrain[r][c]*con/2F, c*widthBlock*sampleSize);
			if (main.testing) main.box(widthBlock*sampleSize, (float)main.terrain[r][c]*con, widthBlock*sampleSize);

			if (t.biome == -1 && main.grid.coastal(t.row, t.col).size() == 0)
			{
				if (main.grid.adjacentLand(t.row, t.col).size() > 0)
				{
					//Render coast with code later on
				}
				else
				{
					main.box(widthBlock*sampleSize, (float)main.terrain[r][c]*con, widthBlock*sampleSize);
					main.popMatrix();
					return;
				}
			}

			main.pushMatrix();
			main.translate(-widthBlock/2F, 0, -widthBlock/2F);
			int m = multiply;
			//System.out.println("*");

			main.pushMatrix();
			main.translate((float)(r*m - r*m%m)*-widthBlock/m, 0, (float)(c*m - c*m%m)*-widthBlock/m);

			//Custom borders for certain tiles, only outline edge
			if (t.harvest)
			{

			}
			else
			{
				/*main.beginShape(main.QUADS);
				main.vertex(r*widthBlock, 0, c*widthBlock);
				main.vertex((r+1)*widthBlock, 0, (c)*widthBlock);
				main.vertex((r+1)*widthBlock, 0, (c+1)*widthBlock);
				main.vertex((r)*widthBlock, 0, (c+1)*widthBlock);
				main.endShape();*/
				//Replace with 4 loops later
				//done
				//Render sets of multiple sets
				float off = 3;
				for (int layer = 0; layer < n; layer++)
				{
					for (float i = 0; i < m; i++)
					{
						if (!strokedColor && civ != null)
						{
							if (i % 2 == 0)
								main.stroke(civ.r, civ.g, civ.b);
							else
								main.stroke(civ.sR, civ.sG, civ.sB);
						}
						//Renders horizontal squares with equal displacements
						//try {
						if (n <= 1)
						{
							/*main.line((r+(i/m))*widthBlock, vertices[r*m + (int)i][c*m]+layer*off, c*widthBlock, (r+(((float)i+1)/m))*widthBlock, vertices[r*m + (int)i + 1][c*m]+layer*off, c*widthBlock);
							main.line((r+(i/m))*widthBlock, vertices[r*m + (int)i][(c+1)*m]+layer*off, (c+1)*widthBlock, (r+((i+1)/m))*widthBlock, vertices[r*m + (int)i + 1][(c+1)*m]+layer*off, (c+1)*widthBlock);
							main.line(r*widthBlock, vertices[r*m][c*m + (int)i]+layer*off, (c+i/m)*widthBlock, r*widthBlock, vertices[r*m][c*m + (int)i + 1]+layer*off, (c+(((float)i+1)/m))*widthBlock);
							main.line((r+1)*widthBlock, vertices[(r+1)*m][c*m + (int)i]+layer*off, (c+i/m)*widthBlock, (r+1)*widthBlock, vertices[(r+1)*m][c*m + (int)i + 1]+layer*off, (c+(((float)i+1)/m))*widthBlock);*/
							main.line(vertices[r*m + (int)i][c*m], vertices[r*m + (int)i + 1][c*m], layer*off);
							main.line(vertices[r*m + (int)i][(c+1)*m], vertices[r*m + (int)i + 1][(c+1)*m], layer*off);
							main.line(vertices[r*m][c*m + (int)i], vertices[r*m][c*m + (int)i + 1], layer*off);
							main.line(vertices[(r+1)*m][c*m + (int)i], vertices[(r+1)*m][c*m + (int)i + 1], layer*off);
						}
						else
						{
							main.line(vertices[r*m][c*m], vertices[(r+1)*m][c*m], (layer+0.5F)*off);
							main.line(vertices[(r+1)*m][c*m], vertices[(r+1)*m][(c+1)*m], (layer+0.5F)*off);
							main.line(vertices[(r+1)*m][(c+1)*m], vertices[r*m][(c+1)*m], (layer+0.5F)*off);
							main.line(vertices[r*m][(c+1)*m], vertices[r*m][c*m], (layer+0.5F)*off);
						}
						//} catch (Exception e) {if (main.frameCount % 25 == 0) {e.printStackTrace();} continue;}

						/*main.line(r*widthBlock, vertices[r*m][c*m]+layer*off, c*widthBlock, 
								(r+1)*widthBlock, vertices[(r+1)*m][c*m]+layer*off, c*widthBlock);
						main.line((r+1)*widthBlock, vertices[(r+1)*m][c*m]+layer*off, c*widthBlock, 
								(r+1)*widthBlock, vertices[(r+1)*m][(c+1)*m]+layer*off, (c+1)*widthBlock);
						main.line((r+1)*widthBlock, vertices[(r+1)*m][(c+1)*m]+layer*off, (c+1)*widthBlock, 
								r*widthBlock, vertices[r*m][(c+1)*m]+layer*off, (c+1)*widthBlock);
						main.line(r*widthBlock, vertices[r*m][(c+1)*m]+layer*off, (c+1)*widthBlock, 
								r*widthBlock, vertices[r*m][c*m]+layer*off, c*widthBlock);*/

						//main.line((r+(i/m))*widthBlock, vertices[r*m + (int)i][c*m]+layer*off, c*widthBlock, (r+(((float)i+1)/m))*widthBlock, vertices[r*m + (int)i + 1][c*m]+layer*off, c*widthBlock);
						//main.line((r+(i/m))*widthBlock, vertices[r*m + (int)i][(c+1)*m]+layer*off, (c+1)*widthBlock, (r+((i+1)/m))*widthBlock, vertices[r*m + (int)i + 1][(c+1)*m]+layer*off, (c+1)*widthBlock);
						//main.line(r*widthBlock, vertices[r*m][c*m + (int)i]+layer*off, (c+i/m)*widthBlock, r*widthBlock, vertices[r*m][c*m + (int)i + 1]+layer*off, (c+(((float)i+1)/m))*widthBlock);
						//main.line((r+1)*widthBlock, vertices[(r+1)*m][c*m + (int)i]+layer*off, (c+i/m)*widthBlock, (r+1)*widthBlock, vertices[(r+1)*m][c*m + (int)i + 1]+layer*off, (c+(((float)i+1)/m))*widthBlock);

						//main.line((r+1)*widthBlock, vertices[r*m + (int)i][c*m], (c+i/m)*widthBlock, r*widthBlock, vertices[r*m][c*m + (int)i + 1], (c+(((float)i+1)/m))*widthBlock);
						//main.line((r+(i/m))*widthBlock, (float)vertices[r*m + i][(c+1)*m], (c+1)*widthBlock, (r+((i+1)/m))*widthBlock, (float)vertices[r*m + i + 1][(c+1)*m], (c+1)*widthBlock);
					}
				}
				//only for case 3
				/*main.line(r*widthBlock, (float)vertices[r*m][c*m], c*widthBlock, (r+(1F/m))*widthBlock, (float)vertices[r*m + 1][c*m], c*widthBlock);
				main.line((r+(1F/m))*widthBlock, (float)vertices[r*m + 1][c*m], c*widthBlock, (r+(2F/m))*widthBlock, (float)vertices[r*m + 2][c*m], c*widthBlock);
				main.line((r+(2F/m))*widthBlock, (float)vertices[r*m + 2][c*m], c*widthBlock, (r+(m/m))*widthBlock, (float)vertices[r*m + m][c*m], c*widthBlock);

				main.line(r*widthBlock, (float)vertices[r*m][(c+1)*m], (c+1)*widthBlock, (r+(1F/m))*widthBlock, (float)vertices[r*m + 1][(c+1)*m], (c+1)*widthBlock);
				main.line((r+(1F/m))*widthBlock, (float)vertices[r*m + 1][(c+1)*m], (c+1)*widthBlock, (r+(2F/m))*widthBlock, (float)vertices[r*m + 2][(c+1)*m], (c+1)*widthBlock);
				main.line((r+(2F/m))*widthBlock, (float)vertices[r*m + 2][(c+1)*m], (c+1)*widthBlock, (r+(m/m))*widthBlock, (float)vertices[r*m + m][(c+1)*m], (c+1)*widthBlock);

				main.line(r*widthBlock, (float)vertices[r*m][c*m], c*widthBlock, r*widthBlock, (float)vertices[r*m][c*m + 1], (c+(1F/m))*widthBlock);
				main.line(r*widthBlock, (float)vertices[r*m][c*m + 1], (c+(1F/m))*widthBlock, r*widthBlock, (float)vertices[r*m][c*m + 2], (c+(2F/m))*widthBlock);
				main.line(r*widthBlock, (float)vertices[r*m][c*m + 2], (c+(2F/m))*widthBlock, r*widthBlock, (float)vertices[r*m][c*m + m], (c+(m/m))*widthBlock);

				main.line((r+1)*widthBlock, (float)vertices[(r+1)*m][c*m], c*widthBlock, (r+1)*widthBlock, (float)vertices[(r+1)*m][c*m + 1], (c+(1F/m))*widthBlock);
				main.line((r+1)*widthBlock, (float)vertices[(r+1)*m][c*m + 1], (c+(1F/m))*widthBlock, (r+1)*widthBlock, (float)vertices[(r+1)*m][c*m + 2], (c+(2F/m))*widthBlock);
				main.line((r+1)*widthBlock, (float)vertices[(r+1)*m][c*m + 2], (c+(2F/m))*widthBlock, (r+1)*widthBlock, (float)vertices[(r+1)*m][c*m + m], (c+(m/m))*widthBlock);*/
				main.noStroke();
				if (t.city != null)
				{
					if (t.city.raze)
					{
						main.stroke(255,0,0);
					}
				}
			}

			main.popMatrix();

			for (int nr = r*m; nr < r*m + m; nr++)
			{
				for (int nc = c*m; nc < c*m + m; nc++)
				{
					/*
					//Add snow caps
					if (nr == r*m + 1 && nc == c*m + 1)
					{
						if (vertices[nr][nc] > 5) 
						{
							main.fill(255);
						}
					}*/
					//
					//try
					{
						main.pushMatrix();
						
						main.translate((float)(nr - nr%m)*-widthBlock/m, 0, (float)(nc - nc%m)*-widthBlock/m);
						main.beginShape(main.TRIANGLES);
						//main.texture(textures[nr][nc]);
						/*main.vertex(vertices[nr-1][nc-1]);
						main.vertex(vertices[nr-1][nc+1-1]);
						main.vertex(vertices[nr+1-1][nc+1-1]);
						//main.endShape();
						//main.beginShape(main.TRIANGLES);
						//main.texture(textures[nr][nc]);
						main.vertex(vertices[nr-1][nc-1]);
						main.vertex(vertices[nr+1-1][nc-1]);
						main.vertex(vertices[nr+1-1][nc+1-1]);*/
						//Correct coast/sea tiles with non-zero height to render as beach
						main.pushStyle();
						if (t != null && vertices[nr][nc] != null && vertices[nr][nc+1] != null && vertices[nr+1][nc+1] != null)
							if (t.biome == -1 && (vertices[nr][nc].y > 0 || vertices[nr][nc+1].y > 0 || vertices[nr+1][nc+1].y > 0))
								main.fill(245,245,140);
						
						main.vertex(vertices[nr][nc]);
						main.vertex(vertices[nr][nc+1]);
						main.vertex(vertices[nr+1][nc+1]);
						
						main.popStyle();
						
						main.pushStyle();
						if (t != null && vertices[nr][nc] != null && vertices[nr][nc+1] != null && vertices[nr+1][nc+1] != null)
							if (t.biome == -1 && (vertices[nr][nc].y > 0 || vertices[nr+1][nc].y > 0 || vertices[nr+1][nc+1].y > 0))
								main.fill(245,245,140);
						
						main.vertex(vertices[nr][nc]);
						main.vertex(vertices[nr+1][nc]);
						main.vertex(vertices[nr+1][nc+1]);
						
						main.popStyle();
						/*main.vertex((float)nr/m*widthBlock,(float)vertices[nr][nc],(float)nc/m*widthBlock);
						main.vertex((float)nr/m*widthBlock,(float)vertices[nr][nc+1],(float)(nc+1)/m*widthBlock);
						main.vertex((float)(nr+1)/m*widthBlock,(float)vertices[nr+1][nc+1],(float)(nc+1)/m*widthBlock);
						main.vertex((float)nr/m*widthBlock,(float)vertices[nr][nc],(float)nc/m*widthBlock);
						main.vertex((float)(nr+1)/m*widthBlock,(float)vertices[nr+1][nc],(float)nc/m*widthBlock);
						main.vertex((float)(nr+1)/m*widthBlock,(float)vertices[nr+1][nc+1],(float)(nc+1)/m*widthBlock);*/
						main.endShape();
						main.popMatrix();
					} //catch (Exception e) {main.popMatrix();}
				}
			}
			//main.texture(textures[nr][nc]);

			main.popMatrix();

			//Render a hill or mountain
			if (!lazy)
			{
				if (sampleSize == 1)
				{
					/*if (t.shape == 1)
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
					}*/
					int res = t.resource;
					if (res != 0)
					{							
						Color cr = EntityData.getResourceColor(res);
						if (res >= 0 && res <= 9 && !main.testing) //Organics
						{
							renderUniqueModel("Wheat",(float)cr.r*255F,(float)cr.g*255F,(float)cr.b*255F,0,-2,0,r,c);
						}
						else if (res >= 20 && res <= 29 && !main.testing) //Rocks
						{
							renderUniqueModel("Rock",(float)cr.r*255F,(float)cr.g*255F,(float)cr.b*255F,0,-5,0,r,c);
						}
						else if (res == 30) //Redwood
						{
							renderUniqueModel("Forest",200,0,0,0,0,0,r,c);
						}
						else //Default
						{
							/*main.pushMatrix();
							main.fill(EntityData.getResourceColor(res));
							main.translate(0, 15, 0);
							main.box(5);
							main.popMatrix();*/
						}
					}
					if (t.forest)
					{
						//renderModel("Forest4",150,225,255,0,-5,0);
						if (main.testing)
							renderModel("OldForest",150,225,255,0,-5,0);
						else
							renderUniqueModel("Forest",150,225,255,0,-5,0,r,c);
					}
				}
			}
			main.popMatrix();
		}
		main.strokeWeight(1);
	}

	//Render a game entity
	public void renderGameEntity(BaseEntity en, float dist, int r, int c)
	{
		main.texture(textures[0][0]);
		if (en.owner != null)
			main.fill(en.owner.r,en.owner.g,en.owner.b);
		//float dist = (float)Math.sqrt(Math.pow(player.posX - r*widthBlock, 2) + Math.pow(player.posY - main.terrain[r][c], 2) + Math.pow(player.posZ - c*widthBlock, 2));
		main.noStroke();
		float sizeY = widthBlock;

		main.pushMatrix();

		main.strokeWeight(1);

		/*if (en instanceof GameEntity && en.owner.id == 0)
		{
			GameEntity gameEn = (GameEntity)en;
			if (gameEn.queueTiles.size() > 0)
			{
				for (int i = gameEn.queueTiles.size() - 1; i >= 0; i--)
				{
					Tile t = gameEn.queueTiles.get(i);
					main.pushMatrix();
					main.translate(t.row*widthBlock, 25, t.col*widthBlock);
					//main.fill(((float)(i+1)/(float)gameEn.queueTiles.size())*255F);
					main.fill((r*main.grid.rows+c)/(r*c)*255);
					main.box(5,5,5);
					main.popMatrix();
				}
			}
		}*/

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
		if (en.getName().equals("Ruins"))
		{
			main.pushMatrix();
			main.translate(r*widthBlock, 0, c*widthBlock);
			if (main.testing)
				renderModel("Ruins3",r,c,150,225,255);
			else
				renderUniqueModel("Ruins",150,225,255,0,-5,0,r,c);
			main.popMatrix();
		}
		else if (en.getName().equals("Farm"))
		{
			main.pushMatrix();
			main.translate(r*widthBlock, 0, c*widthBlock);
			if (main.testing)
				renderModel("OldFarm",r,c,150,225,255);
			else
				renderUniqueModel("Farm",150,225,255,0,-5,0,r,c);
			main.popMatrix();
		}
		else
		{
			if (en.owner != null)
				renderModel(en.getName(),r,c,en.owner.r,en.owner.g,en.owner.b);
			else
				renderModel(en.getName(),r,c,150,225,255);
		}
		main.noStroke();

		if (en.owner != null)
		{
			if (en.location.harvest)
			{
				main.strokeWeight(5);
				main.stroke(en.owner.r,en.owner.g,en.owner.b);
			}
		}

		float health = (float)en.health/(float)en.maxHealth;
		//Shown in GUI now
		/*if (en.owner != null && health < 1)
		{
			main.pushMatrix();
			main.translate(r*widthBlock, 25, c*widthBlock);
			main.fill(255,0,0);
			main.box(widthBlock,2,2);
			main.popMatrix();

			main.pushMatrix();
			main.translate(r*widthBlock - widthBlock*health/2 + widthBlock/2, 25, c*widthBlock);
			main.fill(0,255,0);
			main.box(widthBlock*health + 2,3,3);
			main.popMatrix();
		}*/
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

	//Generate vertices to be shown in the world
	//this is terrible math
	private int multiply;
	public void generateRoughTerrain(double[][] terrain, int multiply)
	{
		vertices = new Point[terrain.length*multiply + 10][terrain.length*multiply + 10];
		double[][] temp1 = DiamondSquare.makeTable(2,2,2,2,multiply);
		temp1[temp1.length/2][temp1.length/2] = 8;
		double[][] temp2 = DiamondSquare.makeTable(2,2,2,2,multiply);
		temp2[temp1.length/2][temp1.length/2] = 24;
		DiamondSquare map;
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				Tile t = main.grid.getTile(r,c);
				if (t.biome == -1)
				{
					/*for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
							vertices[nr][nc] = null;*/
					for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
							vertices[nr][nc] = new Point((r + (float)(nr%multiply)/(float)multiply)*widthBlock, 
									0,
									(c + (float)(nc%multiply)/(float)multiply)*widthBlock
									);
				}
				//Check to see if there is a land and sea split
				/*ArrayList<Tile> sea = main.grid.coastal(r, c);
				if (sea.size() > 0)
				{
					//Diagonal
					//damn these variables
					for (int i = 0; i < sea.size(); i++)
					{
						int dr = sea.get(i).row - r, dc = sea.get(i).col - c;
						int pr = 0, pc = 0;
						if (dr != 0 && dc != 0)
						{
							if (dr == -1) pr = 0;
							else if (dr == 1) pr = 1;
							else {dr = 0; System.out.println("impossible adjacent sea tile");}
							if (dc == -1) pc = 0;
							else if (dc == 1) pc = 1;
							else {dc = 0; System.out.println("impossible adjacent sea tile");}
							vertices[(r+pr)*multiply][(c+pc)*multiply] = 0;
						}
						else if (dr != 0) // && dc == 0
						{
							if (dr == 1)
							{
								//for (int j = 0; j < )
							}
							else //dr == -1
							{

							}
						}
						else if (dc != 0) // && dr == 0
						{
							if (dc == 1)
							{

							}
							else //dc == -1
							{

							}
						}
						else
						{
							System.out.println("impossible");
						}
					}
				}*/
			}
		}
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				map = new DiamondSquare(temp2);
				map.seed(870L);
				map.random.setSeed((long)(System.currentTimeMillis()*Math.random()*100F));
				Tile t = main.grid.getTile(r,c);
				//map = null;
				if (t.biome == -1) continue;
				if (t.shape == 2)
				{
					double[][] renderHill = map.generate(DiamondSquare.makeTable(5, 5, 5, 5, multiply), new double[]{0, 0, 2, 7, 0.7, 1});
					renderHill = DiamondSquare.max(renderHill, 20);
					//DiamondSquare.printTable(renderHill);
					for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
					{
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
						{
							vertices[nr][nc] = new Point(
									(r + (float)(nr%multiply)/(float)multiply)*widthBlock, 
									(float)renderHill[nr - r*multiply][nc - c*multiply],
									(c + (float)(nc%multiply)/(float)multiply)*widthBlock
									);
							//System.out.print(renderHill[nr - r*multiply][nc - c*multiply] + " ");
						}
						//System.out.println();
					}
				}
				else if (t.shape == 1)
				{
					map = new DiamondSquare(temp1);
					long seed = (long)(System.currentTimeMillis()*Math.random());
					map.seed(seed);
					//System.out.println(seed);
					//map.seed(870L);
					//double[][] renderHill = map.generate(new double[]{0, 0, 2, 6, 0.5});
					double[][] renderHill = map.generate(DiamondSquare.makeTable(0, 0, 0, 0, multiply), new double[]{0, 0, 2, 4, 0.5, 1});
					renderHill = DiamondSquare.max(renderHill, 13);
					for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
					{
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
						{
							vertices[nr][nc] = new Point(
									(r + (float)(nr%multiply)/(float)multiply)*widthBlock, 
									(float)renderHill[nr - r*multiply][nc - c*multiply],
									(c + (float)(nc%multiply)/(float)multiply)*widthBlock
									);
						}
					}
				}
				else
				{
					boolean rough = Math.random() < 0.2;
					for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
					{
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
						{
							//double height = 2;
							//vertices[nr][nc] = terrain[r][c] + Math.random()*height*2 - height;
							if (rough)
								vertices[nr][nc] = new Point(
										(r + (float)(nr%multiply)/(float)multiply)*widthBlock, 
										(float)(Math.random()*2),
										(c + (float)(nc%multiply)/(float)multiply)*widthBlock
										);
							else
								vertices[nr][nc] = new Point(
										(r + (float)(nr%multiply)/(float)multiply)*widthBlock, 
										(float)(Math.random()*0.5),
										(c + (float)(nc%multiply)/(float)multiply)*widthBlock
										);
							//vertices[nr][nc] = 1;
						}
					}
				}
			}
		}
		//Make the top & left border zero
		/*for (int i = 0; i < vertices.length; i++)
		{
			vertices[i][0].y = 0;
			vertices[0][i].y = 0;
		}*/
		/*for (int r = 0; r < vertices.length; r++)
		{
			for (int c = 0; c < vertices[0].length; c++)
			{
				System.out.print((int)vertices[r][c] + " ");
			}
			System.out.println();
		}*/
		this.multiply = multiply;
		for (int nr = 0; nr < vertices.length; nr++)
		{
			for (int nc = 0; nc < vertices[0].length; nc++)
			{
				Tile t = main.grid.getTile(nr / multiply, nc / multiply);
				if (t != null && t.biome == -1 && main.grid.adjacentLand(t.row, t.col).size() == 0) continue;
				//if (nr % multiply != 0 || nc % multiply != 0) continue;
				Point p = vertices[nr][nc];
				if (p != null)
				{
					vertices[nr][nc] = new Point(p.x + Math.random()*4D - 2, p.y, p.z + Math.random()*4D - 2);
				}
			}
		}
	}

	public void smoothRoughTerrain(int len)
	{
		Point[][] temp = new Point[vertices.length][vertices[0].length];
		for (int r = 0; r < vertices.length; r++)
		{
			for (int c = 0; c < vertices.length; c++)
			{
				float sum = 0, n = 0;
				for (int i = r - len; i <= r + len; i++)
					for (int j = c - len; j <= c + len; j++)
						if (i >= 0 && i < vertices.length && j >= 0 && j < vertices[0].length)
						{
							sum += vertices[i][j].y; 
							n++;
						}
				sum /= n;
				temp[r][c].y = sum;
				if (temp[r][c].y < 0.25) temp[r][c].y = 0;
			}
		}
		vertices = temp;
	}

	public void generateTextures(int n)
	{
		textures = new PImage[main.terrain.length*n + 1][main.terrain[0].length*n + 1];
		PImage roughMaster = main.loadImage("roughtexture.jpg"), smoothMaster = main.loadImage("smoothtexture.jpg");
		for (int r = 0; r < textures.length; r++)
		{
			for (int c = 0; c < textures[0].length; c++)
			{
				Tile t = main.grid.getTile(r/n, c/n);
				PImage master;
				if (t == null) 
					master = smoothMaster;
				else
				{
					if (t.biome == -1 || t.biome == 0 || t.biome == 2 || t.biome == 3)
						master = smoothMaster;
					else
						master = roughMaster;
				}
				textures[r][c] = getBlock(master,r,c);
			}
		}
	}

	//Creates a n*n size group of textures that will be put into t
	public PImage getBlock(PImage tex, int row, int col)
	{
		int len = (int)widthBlock;
		PImage temp = main.createImage(len, len, main.ARGB);
		for (int r = 0; r < len; r++)
		{
			for (int c = 0; c < len; c++)
			{
				temp.pixels[r*(int)len + c] = tex.pixels[((row+r)*1024 + (col+c))%(1024*1024)];
				//System.out.println(main.hex(temp.pixels[r*(int)widthBlock + c]));
			}
		}
		return temp;
	}

	//Render if there are multiple types of a model
	public void renderUniqueModel(String name, float red, float green, float blue, float dx, float dy, float dz, int r, int c)
	{
		float[][] candidate = EntityData.getModel(name);
		if (candidate == null)
		{
			int n = 0;
			do
			{
				if (EntityData.getModel(name + (n+1)) != null)
					n++;
				else
					break;
			} while (true);
			if (n == 0)
				System.out.println("No models for " + name);
			else
			{
				renderModel(name + (((r+c)%n) + 1), red, green, blue, dx, dy, dz);
			}
		}
		else
		{
			renderModel(name, red, green, blue, dx, dy, dz);
		}
	}

	public void renderModel(String name, float red, float green, float blue)
	{
		renderModel(name,red,green,blue,0,0,0);
	}

	public void renderModel(String name, float red, float green, float blue, float dx, float dy, float dz)
	{
		//main.pushMatrix();
		main.texture(textures[0][0]);
		main.noStroke();
		float[][] model = EntityData.getModel(name);
		if (model != null)
		{
			for (int i = 0; i < model.length; i++)
			{
				main.pushMatrix();
				if (name.equals("Redwood")) main.scale(2); //TODO: Quick solution
				float[] t = model[i];
				if ((int)t[0] == 0)
					main.fill(150);
				else if ((int)t[0] == 1)
					main.fill(red,green,blue);
				main.translate(t[1]+dx,t[2]+dy,t[3]+dz);
				//if (name.contains("Rock") || name.contains("Ruin") || name.contains("City") || name.contains("Forest"))
				//Distinguish unique models; is the last character a digit?
				if (Character.isDigit(name.substring(name.length() - 1).toCharArray()[0])) 
				{
					main.rotateX((float)Math.toDegrees(t[4])); 
					main.rotateZ((float)Math.toDegrees(t[6]));
				}
				main.rotateY((float)Math.toDegrees(t[5]));
				main.box(t[7],t[8],t[9]);
				main.popMatrix();
			}
		}
		else
		{
			renderModel("Settler", red, green, blue);
		}
		//main.popMatrix();
	}

	public void renderModel(String name, int r, int c, float red, float green, float blue)
	{
		main.pushMatrix();
		main.translate(r*widthBlock, (float)(main.terrain[r][c])*con, c*widthBlock);
		if (name.equals("City"))
			renderModel(name, red, green, blue, 0, -5, 0);
		else
			renderModel(name, red, green, blue);
		main.popMatrix();
	}

	public void renderRiver(int r1, int c1, int r2, int c2)
	{
		main.noStroke();
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
		main.stroke(0);
	}

	public void renderRoad(int r, int c)
	{
		main.noStroke();
		main.fill(150);
		main.pushMatrix();
		main.translate(r*widthBlock,0,c*widthBlock);
		main.box(5,5,5);
		main.popMatrix();
		main.stroke(0);
	}

	public void setCamera()
	{
		main.camera(player.posX,player.posY,player.posZ,player.tarX,player.tarY,player.tarZ,0,-1,0);
	}

	//Make a model of entities with a height map
	public static final float widthBlock = 21;
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
