package render;

import game.Tile;

import java.util.ArrayList;

public class MouseHelper {

	public ArrayList<Line> vertLines, horizonLines;
	public ArrayList<Line> rVertLines, rHorizonLines;
	public Point[][] intersections, guiPositions;
	public Shape[][] shapes;

	public MouseHelper(float width, float height)
	{
		clear();

		//Temporary hard coded numbers
		//Allow player to select tiles
		/*vertical(379,426,328,469);
		vertical(445,426,402,469);
		vertical(513,424,479,464);
		vertical(581,426,556,468);
		vertical(649,426,635,469);
		vertical(717,424,711,467);
		vertical(785,423,789,467);
		vertical(851,425,864,467);
		vertical(919,425,943,466);
		vertical(988,425,1019,466);
		vertical(1050,424,1094,471);
		vertical(1120,424,1174,468);*/
		
		vertical(206,361,124,465);
		vertical(329,360,262,467);
		vertical(447,359,342,612);
		vertical(541,464,500,611);
		vertical(693,360,685,460);
		vertical(812,354,820,465);
		vertical(937,362,963,464);
		vertical(1054,359,1099,460);
		vertical(1120,275,1165,354);
		vertical(1295,360,1385,469);
		
		/*horizontal(0,340,width,340);
		horizontal(0,364,width,364);
		horizontal(0,392,width,392);
		horizontal(0,424,width,424);
		horizontal(0,468,width,468);
		horizontal(0,527,width,527);
		horizontal(0,602,width,602);
		horizontal(0,720,width,720);*/
		horizontal(707,159,790,159);
		horizontal(702,211,797,211);
		horizontal(697,277,803,277);
		horizontal(687,355,812,355);
		horizontal(676,462,824,462);
		horizontal(665,612,834,612);
		horizontal(652,810,851,810);
		horizontal(652,1000,851,1000);
		
		//Provide reference for 2d GUIs 
		/*rVertical(463,446,420,498);
		rVertical(534,442,505,492);
		rVertical(604,443,585,493);
		rVertical(678,439,666,493);
		rVertical(749,443,747,494);
		rVertical(821,444,834,497);
		rVertical(896,444,916,492);
		rVertical(964,437,997,486);
		rVertical(1040,441,1082,492);*/
		
		rVertical(200,466,279,360);
		rVertical(336,466,394,359);
		rVertical(475,467,514,360);
		rVertical(617,464,637,360);
		rVertical(752,462,754,357);
		rVertical(895,463,879,360);
		rVertical(1036,464,998,360);
		rVertical(1171,465,1112,358);
		rVertical(1310,466,1234,360);

		/*rHorizontal(801,349,750,348);
		rHorizontal(691,370,749,370);
		rHorizontal(748,407,814,405);
		rHorizontal(678,440,749,440);
		rHorizontal(667,495,752,494);
		rHorizontal(653,553,750,553);
		rHorizontal(626,648,749,649);*/
		/*rHorizontal(710,142,792,141);
		rHorizontal(720,185,798,183);
		rHorizontal(710,239,805,241);
		rHorizontal(700,314,807,313);
		rHorizontal(685,407,815,407);
		rHorizontal(671,516,824,525);
		rHorizontal(657,709,839,708);*/
		for (int i = 0; i < horizonLines.size() - 1; i++)
		{
			float h = (horizonLines.get(i).yPoint + horizonLines.get(i+1).yPoint)/2;
			rHorizontal(0, h, width, h);
		}
		//rHorizontal(0, 900, width, 900);

		//horizontal(751,373,807,374);

		intersections = getIntersections(horizonLines, vertLines);
		guiPositions = getIntersections(rHorizonLines, rVertLines);
		//guiPositions = new Point[(horizonLines.size())][(vertLines.size()-1)];
		
		shapes = new Shape[(horizonLines.size()-1)][(vertLines.size()-1)];
		for (int i = 0; i < intersections.length - 1; i++)
		{
			for (int j = 0; j < intersections[0].length - 1; j++)
			{
				Shape shape = new Shape(intersections[i][j],intersections[i+1][j],intersections[i+1][j+1],intersections[i][j+1]);
				shapes[i][j] = shape;
				
				/*Point a = new Point((intersections[i][j].x + intersections[i+1][j].x)/2, (intersections[i][j].y + intersections[i+1][j].y)/2);
				Point c = new Point((intersections[i+1][j].x + intersections[i+1][j+1].x)/2, (intersections[i+1][j].y + intersections[i+1][j+1].y)/2);
				
				Point b = new Point((intersections[i][j].x + intersections[i][j+1].x)/2, (intersections[i][j].y + intersections[i][j+1].y)/2);
				Line h = new Line(b,0);
				Line v = new Line(a,c);
				guiPositions[i][j] = h.intersect(v)*/;
			}
		}
		
	}
	
	public void clear()
	{
		vertLines = new ArrayList<Line>(); rVertLines = new ArrayList<Line>();
		horizonLines = new ArrayList<Line>(); rHorizonLines = new ArrayList<Line>();
	}

	public Point[][] getIntersections(ArrayList<Line> hl, ArrayList<Line> vl)
	{
		Point[][] intersections = new Point[hl.size()][vl.size()];
		for (int i = 0; i < hl.size(); i++)
		{
			Line h = hl.get(i);
			for (int j = 0; j < vl.size(); j++)
			{
				Line v = vl.get(j);
				intersections[i][j] = h.intersect(v);
			}
		}
		return intersections;
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

	public void rVertical(float a, float b, float c, float d)
	{
		Line l = new Line(new Point(a,b),new Point(c,d));
		rVertLines.add(l);
	}

	public void rHorizontal(float a, float b, float c, float d)
	{
		Line l = new Line(new Point(a,b),new Point(c,d));
		rHorizonLines.add(l);
	}

	public float[] center()
	{
		Point p = guiPositions[(guiPositions.length-1)/2][(guiPositions[0].length-1)/2];
		return new float[]{p.x, p.y};
	}

	//http://alienryderflex.com/polygon/
	public boolean within(float x, float y, float[] polyX, float[] polyY) 
	{
		if (polyX.length != polyY.length) return false;
		int i, j = polyX.length-1;
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

	public int[] findTile(float x, float y)
	{
		for (int r = 0; r < shapes.length; r++)
		{
			for (int c = 0; c < shapes[0].length; c++)
			{
				/*System.out.println("*");
				System.out.println(shapes[r][c].x.toString());
				System.out.println(shapes[r][c].y.toString());*/
				if (within(x, y, shapes[r][c].x, shapes[r][c].y))
				{
					return new int[]{r - (horizonLines.size()-1)/2,c - (vertLines.size()-1)/2}; //Adjust for pivot
				}
			}
		}
		return null;
	}

	public float[] positionGui(int r, int c)
	{
		if (r >= 0 && r < guiPositions.length && c >= 0 && c < guiPositions[0].length)
		{
			Point p = guiPositions[r][c];
			return new float[]{p.x, p.y};
		}
		System.out.println("Out of bounds access for intersections");
		return null;
	}

	/*public int[] positionGui(Tile highlighted, int r, int c)
	{
		int notAdj = r - (horizonLines.size()-1)/2;
		if (r < )
		{
		Point p = 
		return new int[]{p.x, p.y};
		}
		return null;
	}*/

	public class Line
	{
		public float slope, xPoint, yPoint;

		public Line(Point a, float slope)
		{
			this.slope = slope;
			xPoint = a.x;
			yPoint = a.y;
		}
		
		public Line(Point a, Point b)
		{
			slope = (b.y-a.y)/(b.x-a.x);
			xPoint = a.x;
			yPoint = a.y;
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
	public class Shape 
	{
		public float[] x,y; 
		public Shape(float[] a, float[] b) {x = a; y = b;}
		public Shape(Point... points)
		{
			x = new float[points.length]; y = new float[points.length];
			for (int i = 0; i < points.length; i++) {x[i] = points[i].x; y[i] = points[i].y;}
		}
	}

}
