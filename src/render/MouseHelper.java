package render;

import java.util.ArrayList;

public class MouseHelper {

	public ArrayList<Line> vertLines, horizonLines;
	public Point[][] intersections;
	
	public MouseHelper(float width, float height)
	{
		vertLines = new ArrayList<Line>();
		horizonLines = new ArrayList<Line>();
		
		vertical(717,424,711,467);
		vertical(785,423,789,467);
		vertical(851,425,864,467);
		vertical(919,425,943,466);
		vertical(988,425,1019,466);
		vertical(1055,424,1094,471);
		vertical(1120,424,1174,468);
		vertical(649,426,635,469);
		vertical(581,426,556,468);
		vertical(513,424,479,464);
		vertical(445,426,402,469);

		horizontal(0,340,width,340);
		horizontal(0,364,width,364);
		horizontal(0,392,width,392);
		horizontal(0,424,width,424);
		horizontal(0,468,width,468);
		horizontal(0,527,width,527);
		horizontal(0,602,width,602);
		horizontal(0,720,width,720);
		
		intersections = new Point[vertLines.size()][horizonLines.size()];
		for (int i = 0; i < horizonLines.size(); i++)
		{
			Line h = horizonLines.get(i);
			for (int j = 0; j < vertLines.size(); j++)
			{
				Line v = vertLines.get(i);
				intersections[i][j] = h.intersect(v);
			}
		}
	}
	
	public void vertical(float a, float b, float c, float d)
	{
		Line l = new Line(new Point(a,b),new Point(c,d));
		vertLines.add(l);
	}
	
	public void horizontal(float a, float b, float c, float d)
	{
		Line l = new Line(new Point(a,b),new Point(c,d));
		horizonLines.add(l);
	}
	
	//http://alienryderflex.com/polygon/
	public boolean within(float x, float y, float[] polyX, float[] polyY) 
	{
		if (polyX.length != polyY.length) return false;
		int i, j = polyX.length-1 ;
		boolean oddNodes = false;
		for (i = 0; i < polyX.length; i++) 
		{
			if (polyY[i] < y && polyY[j] >= y || polyY[j] < y && polyY[i] >= y) 
			{
				if (polyX[i] + (y-polyY[i])/(polyY[j]-polyY[i])*(polyX[j]-polyX[i]) < x) 
				{
					oddNodes = !oddNodes;
				}
			}
			j = i;
		}
		return oddNodes; 
	}
	
	public class Line
	{
		public float slope, xPoint, yPoint;
		
		public Line(Point a, Point b)
		{
			slope = (b.y-a.y)/(b.x-a.x);
			xPoint = a.x;
			yPoint = b.x;
		}
		
		public Point intersect(Line l)
		{
			if (slope == l.slope) return null;
			float x = (slope*xPoint - l.slope*l.xPoint - yPoint + l.yPoint)/(slope - l.slope);
			float y = slope*(x-xPoint) + yPoint;
			return new Point(x,y);
		}
		
		/*public float f(float x)
		{
			return slope*()
		}*/
	}
	
	public class Point {public float x,y; public Point(float a, float b) {x = a; y = b;}}
	
}
