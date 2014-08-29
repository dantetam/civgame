package terrain;
import java.util.ArrayList;


public class DiamondSquare extends BaseTerrain {

	public double[][] t;

	/*public static void main(String[] args)
	{
		double[][] temp = makeTable(50,50,50,50,17);
		DiamondSquare ds = new DiamondSquare(temp);
		//ds.diamond(0, 0, 4);
		ds.dS(0, 0, 16, 15, 0.5);
		
		Data data = new Data(ds.t);
		data.divIndex(0, 0, 16);
		//System.out.println(ds.t[1][1]);
	}*/

	public DiamondSquare(double[][] start)
	{
		t = start;
	}
	
	//Creates a table with 4 corners set to argument values
	public static double[][] makeTable(double topLeft, double topRight, double botLeft, double botRight, int width)
	{
		double[][] temp = new double[width][width];
		for (int r = 0; r < width; r++)
		{
			for (int c = 0; c < width; c++)
			{
				temp[r][c] = 0; //???
			}
		}
		temp[0][0] = topLeft;
		temp[0][width-1] = topRight;
		temp[width-1][0] = botLeft;
		temp[width-1][width-1] = botRight;
		return temp;
	}

	public void printTable(double[][] a)
	{
		for (int i = 0; i < a.length; i++)
		{
			for (int j = 0; j < a[0].length; j++)
			{
				System.out.print((int)a[i][j] + " ");
			}
			System.out.println();
		}
	}

	//Starts the iterative loop over the terrain that modifies it
	//Returns a list of the tables between each diamond-square cycle
	public ArrayList<byte[][]> dS(int sX, int sY, int width, double startAmp, double ratio)
	{
		ArrayList<byte[][]> temp = new ArrayList<byte[][]>();
		int origWidth = width;
		while (true)
		{
			for (int r = sX; r <= t.length - 2; r += width)
			{
				for (int c = sY; c <= t[0].length - 2; c += width)
				{
					//System.out.println(r + " " + t.length);
					//System.out.println(c + " " + t.length);
					//System.out.println("r " + r + " c " + c);
					diamond(r, c, width, startAmp);
					byte[][] record = new byte[origWidth][origWidth];
					for (int nr = 0; nr < origWidth; nr++)
					{
						for (int nc = 0; nc < origWidth; nc++)
						{
							record[nr][nc] = (byte)t[nr][nc];
						}
					}
					temp.add(record);
				}
			}
			if (width > 1)
			{
				width /= 2;
				startAmp *= ratio;
			}
			else
				break;
		}
		return temp;
	}

	public void diamond(int sX, int sY, int width, double startAmp)
	{
		t[sX + width/2][sY + width/2] = (t[sX][sY] + t[sX+width][sY] + t[sX][sY+width] + t[sX+width][sY+width])/4 + 
				startAmp*(random.nextDouble() - 0.5)*2;
		/*System.out.println(t[sX][sY]);
		System.out.println(t[sX+width][sY]);
		System.out.println(t[sX][sY+width]);
		System.out.println(t[sX+width][sY+width]);
		System.out.println("-------");*/
		//printTable(t);
		//System.out.println("-------");
		if (width > 1)
		{
			square(sX + width/2, sY, width, startAmp);
			square(sX, sY + width/2, width, startAmp);
			square(sX + width, sY + width/2, width, startAmp);
			square(sX + width/2, sY + width, width, startAmp);
			//diamond(sX, sY, width/2);
			//diamond(sX + width/2, sY, width/2);
			//diamond(sX, sY + width/2, width/2);
			//diamond(sX + width/2, sY + width/2, width/2);
		}
	}

	public void square(int sX, int sY, int width, double startAmp)
	{
		if (sX - width/2 < 0)
		{
			//System.out.println(sX + " 1 " + sY);
			t[sX][sY] = (t[sX][sY - width/2] + t[sX][sY + width/2] + t[sX + width/2][sY])/3;
		}
		else if (sX + width/2 >= t.length)
		{
			//System.out.println(sX + " 2 " + sY);
			t[sX][sY] = (t[sX][sY - width/2] + t[sX][sY + width/2] + t[sX - width/2][sY])/3;
		}
		else if (sY - width/2 < 0)
		{
			//System.out.println(sX + " 3 " + sY);
			t[sX][sY] = (t[sX][sY + width/2] + t[sX + width/2][sY] + t[sX - width/2][sY])/3;
		}
		else if (sY + width/2 >= t.length)
		{
			//System.out.println(sX + " 4 " + sY);
			t[sX][sY] = (t[sX][sY - width/2] + t[sX + width/2][sY] + t[sX - width/2][sY])/3;
		}
		else
		{
			//System.out.println(sX + " 5 " + sY);
			t[sX][sY] = (t[sX][sY + width/2] + t[sX][sY - width/2] + t[sX + width/2][sY] + t[sX - width/2][sY])/4;
		}
		t[sX][sY] += startAmp*(random.nextDouble() - 0.5)*2;
		//printTable(t);
		//System.out.println("-------");
	}

	@Override
	public double[][] generate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[][] generate(long[] arguments) {
		// TODO Auto-generated method stub
		return null;
	}

}
