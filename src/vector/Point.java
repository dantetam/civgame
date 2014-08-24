package vector;

//A 3D point in the form (x,y,z)

public class Point {
	
	public double x,y,z;

	public Point(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double dist(Point o)
	{
		return Math.sqrt(Math.pow(o.x - x,2) + Math.pow(o.y - y,2) + Math.pow(o.z - z,2));
	}
	
	public String toString()
	{
		return "(" + x + "," + y + "," + z + ")"; 
	}
	
}
