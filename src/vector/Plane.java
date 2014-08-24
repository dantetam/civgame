package vector;

import vector.*;

//A plane in the form Ax + By + Cz = D

public class Plane {

	public double a,b,c;
	public double d;
	
	public Plane(double a, double b, double c, double d)
	{
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public Point intersect(Line l)
	{
		return l.intersect(this);
	}
	
	public Plane(Point a, Point b, Point c)
	{
		this.a = (int)((b.y - a.y)*(c.z - a.z) - (c.y - a.y)*(b.z - a.z));
		this.b = (int)(-(b.x - a.x)*(c.z - a.z) + (c.x - a.x)*(b.z - a.z));
		this.c = (int)((b.x - a.x)*(c.y - a.y) - (c.x - a.x)*(b.y - a.y));
		this.d = (int)(this.a*a.x + this.b*a.y + this.c*a.z);
	}
	
}
