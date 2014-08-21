
import java.util.ArrayList;

import processing.core.PApplet;

public class DiamondSquareTest extends PApplet {

	public double[][] temp;
	public ArrayList<byte[][]> displayTables;
	public int step = 0;
	public int len = 128;

	public void setup()
	{
		size(1900,1000,P3D);
		temp = DiamondSquare.makeTable(50,50,50,50,len+1);
		DiamondSquare ds = new DiamondSquare(temp);
		//ds.diamond(0, 0, 4);
		displayTables = ds.dS(0, 0, len, 40, 0.5);

		Data data = new Data(ds.t,30);
		data.divIndex(0, 0, len);

		Data.IslandHelper helper = data.islandHelper();
		ArrayList<ArrayList<Data.IslandHelper.Location>> islands = helper.listIslands;
		//data.divIndex(islands.get(0));
		//System.out.println(ds.t[1][1]);
		frameRate(40);
		strokeWeight(3);
	}

	public int zoom = 1500;
	public int stepSpeed = 1;
	public void draw()
	{
		//perspective((float)Math.PI/4,1.9F,0,1000);
		camera(zoom,zoom,zoom,0,400,0,0,-1,0);
		background(255);
		displayTable(displayTables.get(step));
		step += stepSpeed;
		if (step >= displayTables.size())
		{
			step = 0;
		}
	}

	public void keyPressed()
	{
		if (key == 'i')
		{
			zoom -= 50;
		}
		else if (key == 'o')
		{
			zoom += 50;
		}
		else if (key == 'u')
		{
			stepSpeed++;
		}
		else if (key == 'j')
		{
			if (stepSpeed > 0)
				stepSpeed--;
		}
	}

	public void line(float a, float b, float c, float d, float e, float f)
	{
		strokeWeight(1);
		super.line(a, b, c, d, e, f);
	}

	public void point(float a, float b, float c)
	{
		strokeWeight(3);
		super.point(a, b, c);
	}

	//Removes zeroes and resizes table
	public double[][] fix(double[][] t)
	{
		//Count terms
		/*double n = 0;
		for (int r = 0; r < t.length; r++)
		{
			for (int c = 0; c < t[0].length; c++)
			{
				if (t[r][c] != 0)
					n++;
			}
		}
		double size = Math.pow(n,0.5);
		double[][] temp = new double[(int)Math.pow(n,0.5)][(int)Math.pow(n,0.5)];
		int step = 0;
		for (int r = 0; r < t.length; r++)
		{
			for (int c = 0; c < t[0].length; c++)
			{
				if (t[r][c] != 0)
				{
					println(step%temp.length + "-" + step/temp[0].length);
					temp[step%temp.length][step/temp[0].length] = t[r][c];
					step++;
				}
			}
		}
		return temp;*/
		double[][] temp = new double[t.length][t[0].length];
		BicubicInterpolator bi = new BicubicInterpolator();
		for (int r = 0; r < t.length; r++)
		{
			for (int c = 0; c < t[0].length; c++)
			{
				if (t[r][c] == 0)
					temp[r][c] = bi.getValue(t, r, c);
			}
		}
		return temp;
	}

	public void displayTable(byte[][] t)
	{
		float len = 20; float con = 10;
		fill(0); stroke(0);
		for (int r = 0; r < t.length; r++)
		{
			for (int c = 0; c < t.length; c++)
			{
				if (t[r][c] == 0) continue;
				if (r == t.length - 1 && c == t[0].length - 1) return;
				else if (r == t.length - 1)
				{
					if (t[r][c+1] != 0)
						line(r*len, (float)t[r][c]*con, c*len, r*len, (float)t[r][c+1]*con, (c+1)*len);
					else
						point(r*len, (float)t[r][c]*con, c*len);
				}
				else if (c == t[0].length - 1)
				{
					if (t[r+1][c] != 0)
						line(r*len, (float)t[r][c]*con, c*len, (r+1)*len, (float)t[r+1][c]*con, c*len);
					else
						point(r*len, (float)t[r][c]*con, c*len);
				}
				else
				{
					if (t[r+1][c+1] != 0)
					{
						line(r*len, (float)t[r][c]*con, c*len, r*len, (float)t[r][c+1]*con, (c+1)*len);
						line(r*len, (float)t[r][c]*con, c*len, (r+1)*len, (float)t[r+1][c]*con, c*len);
						line(r*len, (float)t[r][c]*con, c*len, (r+1)*len, (float)t[r+1][c+1]*con, (c+1)*len);
					}
					else
						point(r*len, (float)t[r][c]*con, c*len);
				}
			}
		}
	}

	public static class CubicInterpolator {
		public static double getValue(double[] p, double x) {
			int xi = (int) x;
			x -= xi;
			double p0 = p[Math.max(0, xi - 1)];
			double p1 = p[xi];
			double p2 = p[Math.min(p.length - 1,xi + 1)];
			double p3 = p[Math.min(p.length - 1, xi + 2)];
			return p1 + 0.5 * x * (p2 - p0 + x * (2.0 * p0 - 5.0 * p1 + 4.0 * p2 - p3 + x * (3.0 * (p1 - p2) + p3 - p0)));
		}
	}

	public static class BicubicInterpolator extends CubicInterpolator {
		private double[] arr = new double[4];

		public double getValue(double[][] p, double x, double y) {
			int xi = (int) x;
			x -= xi;
			arr[0] = getValue(p[Math.max(0, xi - 1)], y);
			arr[1] = getValue(p[xi], y);
			arr[2] = getValue(p[Math.min(p.length - 1,xi + 1)], y);
			arr[3] = getValue(p[Math.min(p.length - 1, xi + 2)], y);
			return getValue(arr, x+ 1);
		}
	}

}
