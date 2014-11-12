package render;

import processing.core.PApplet;

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
				print(mouseHelper.intersections[r][c].x + "," + mouseHelper.intersections[r][c].y + " ");
			}
			println();
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
				println(mouseHelper.intersections[r][c].x + "," + mouseHelper.intersections[r][c].y);
				point(mouseHelper.intersections[r][c].x,mouseHelper.intersections[r][c].y);
			}
		}
	}
	
}
