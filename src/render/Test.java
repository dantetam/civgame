package render;

import processing.core.PApplet;
import render.MouseHelper.Shape;

public class Test extends PApplet {

	public MouseHelper mouseHelper;
	
	public void setup()
	{
		size(1900,1000);
		mouseHelper = new MouseHelper(1900,1000);
		for (int r = 0; r < mouseHelper.intersections.length; r++)
		{
			for (int c = 0; c < mouseHelper.intersections[0].length; c++)
			{
				//print(mouseHelper.intersections[r][c].x + "," + mouseHelper.intersections[r][c].y + " ");
			}
			//println();
		}
	}
	
	public void draw()
	{
		background(255);
		for (int r = 0; r < mouseHelper.intersections.length; r++)
		{
			for (int c = 0; c < mouseHelper.intersections[0].length; c++)
			{
				strokeWeight(5);
				fill(255,0,0);
				stroke(255,0,0);
				//println(mouseHelper.intersections[r][c].x + "," + mouseHelper.intersections[r][c].y);
				point(mouseHelper.intersections[r][c].x,mouseHelper.intersections[r][c].y);
			}
		}
		for (int r = 0; r < mouseHelper.guiPositions.length; r++)
		{
			for (int c = 0; c < mouseHelper.guiPositions[0].length; c++)
			{
				strokeWeight(5);
				fill(0,0,255);
				stroke(0,0,255);
				//println(mouseHelper.intersections[r][c].x + "," + mouseHelper.intersections[r][c].y);
				if (mouseHelper.guiPositions[r][c] != null)
					point(mouseHelper.guiPositions[r][c].x,mouseHelper.guiPositions[r][c].y);
			}
		}
		//println(mouseHelper.shapes.length + " " + mouseHelper.shapes[0].length);
		for (int r = 0; r < mouseHelper.shapes.length; r++)
		{
			for (int c = 0; c < mouseHelper.shapes[0].length; c++)
			{
				if (r != activeX || c != activeY) continue;
				Shape s = mouseHelper.shapes[r][c];
				//if (s == null) continue;
				fill(150*c/15,225*r/15,255*r/15);
				beginShape(QUADS);
				for (int i = 0; i < s.x.length; i++)
				{
					vertex(s.x[i],s.y[i]);
				}
				vertex(s.x[0],s.y[0]);
				endShape();
			}
		}
		int[] s = mouseHelper.findTile(mouseX, mouseY);
		if (s != null)
		{
			activeX = s[0] + mouseHelper.shapes.length/2; activeY = s[1] + mouseHelper.shapes.length/2;
			
		}
	}
	
	public int activeX, activeY;
	public void mousePressed()
	{
		int[] s = mouseHelper.findTile(mouseX, mouseY);
		if (s != null)
		{
			println(s[0] + "," + s[1]);
		}
	}
	
}
