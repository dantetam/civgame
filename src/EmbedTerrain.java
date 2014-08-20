
import processing.core.PApplet;

public class EmbedTerrain extends PApplet {

	public Main main;

	public EmbedTerrain(Main main)
	{
		this.main = main;
	}

	public void setup()
	{
		size(400,300,P3D);
		//noLoop();
		background(255,0,0);
		camera(100,100,100,0,0,0,0,-1,0);
	}

	public void draw()
	{
		//background(255,0,0);
		//camera(100,100,100,0,0,0,0,1,0);
	}
	
	public void keyPressed()
	{
		main.executeKey(key);
	}
	
	public void showTerrain(double[][] data)
	{
		box(100);
		//println("yo");
		//background(255);
		camera(2000,2000,2000,0,0,0,0,1,0);
		fill(0);
		int width = 10;
		for (int r = 0; r < data.length; r++)
		{
			for (int c = 0; c < data[0].length; c++)
			{
				int height = (int)data[r][c];
				pushMatrix();
				//println(r*width + " " + height/2 + " " + c*width);
				translate(r*width,height/2,c*width);
				box(width,height,width);
				popMatrix();
			}
		}
	}
	
	
}
