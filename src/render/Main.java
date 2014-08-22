package render;


import javax.swing.JFrame;

import processing.core.*;
import terrain.*;

public class Main extends PApplet {

	public int rows = 32; public int cols = 32; public double nDiv = 128;
	private double[][] test;
	private long seed = 870L;
	private boolean helpMode = true; private boolean stopRendering = false;
	private Erosion erosion;

	//Hopefully will render in a separate window
	private OpenGLTerrain renderer;

	public static void main(String[] args)
	{
		PApplet.main(new String[] { Main.class.getName() });
	}

	public void setup() 
	{
		size(1800,900);
		textSize(10);
		noStroke();
		frameRate(25);
		//noLoop();
		//double[][] source = new double[][]{{1,1,1,1}, {2,2,2,2}, {3,3,3,3}, {2,2,2,2}};
		/*double[][] source = new PerlinNoise().makePerlinNoise(rows,cols,128,4,3,0.5,2);
		double[][] newSource = expand(source,nDiv);
		test = newSource;
		for (int i = 0; i < newSource.length; i++)
		{
			for (int j = 0; j < newSource[0].length; j++)
			{
				System.out.print((int)newSource[i][j] + "   ");
			}
			System.out.println();
		}*/
		assignNewTerrain(seed);
		//Data data = new Data(test,cutoff);
		//data.recurDivIndex(0, 0, test.length);

		erosion = new Erosion(test,cutoff);

		PFrame f = new PFrame(this,1500,900);
		f.setTitle("3D Renderer");
	}

	//Taken from stack overflow
	public class PFrame extends JFrame {
		public PFrame(Main main, int width, int height) {
			setBounds(100, 100, width, height);
			renderer = new OpenGLTerrain(main);
			add(renderer);
			renderer.init();
			renderer.setTerrain(test,cutoff);
			show();
		}
	}

	public void assignNewTerrain(long seed)
	{
		//(int)(Math.log(rows)/Math.log(2))-1
		double[][] source = new PerlinNoise(seed).makePerlinNoise(rows,cols,150,8,1,0.6,3);
		//double[][] newSource = PerlinNoise.recurInter(source,2,nDiv/4);
		double[][] newSource = PerlinNoise.expand(PerlinNoise.expand(source,nDiv/2),nDiv);
		//double[][] newSource = source;
		test = newSource;

		/*for (int i = 0; i < newSource.length; i++)
		{
			for (int j = 0; j < newSource[0].length; j++)
			{
				System.out.print((int)newSource[i][j] + "   ");
			}
			System.out.println();
		}*/
		erosion = null;
		erosion = new Erosion(test,cutoff);
	}

	public void displayTable(double[][] t)
	{
		float len = 20; float con = 5;
		fill(0); stroke(0);
		for (int r = 0; r < t.length; r++)
		{
			for (int c = 0; c < t.length; c++)
			{
				if (r == t.length - 1 && c == t[0].length - 1) return;
				else if (r == t.length - 1)
				{
					line(r*len, (float)t[r][c]*con, c*len, r*len, (float)t[r][c+1]*con, (c+1)*len);
				}
				else if (c == t[0].length - 1)
				{
					line(r*len, (float)t[r][c]*con, c*len, (r+1)*len, (float)t[r+1][c]*con, c*len);
				}
				else
				{
					line(r*len, (float)t[r][c]*con, c*len, r*len, (float)t[r][c+1]*con, (c+1)*len);
					line(r*len, (float)t[r][c]*con, c*len, (r+1)*len, (float)t[r+1][c]*con, c*len);
					line(r*len, (float)t[r][c]*con, c*len, (r+1)*len, (float)t[r+1][c+1]*con, (c+1)*len);
				}
			}
		}
	}

	public void keyPressed()
	{
		executeKey(key);
	}
	
	public void executeKey(char key)
	{
		if (key == 'r')
		{
			seed = System.currentTimeMillis();
			assignNewTerrain(seed);
			erosion = null;
			erosion = new Erosion(test,cutoff);
			renderer.setTerrain(test,cutoff);
			renderer.redraw();
		}
		else if (key == 'i')
		{
			if (zoom > 1)
			{
				zoom /= 2;
				sight /= 2;
			}
		}
		else if (key == 'o')
		{
			if (sight*2 < test.length)
			{
				zoom *= 2;
				sight *= 2;
			}
		}
		else if (key == 'k')
		{
			if (sight > 5)
				sight--;
		}
		else if (key == 'l')
		{
			sight++;
		}
		else if (key == 'u')
		{
			cutoff += 1;
			erosion = null;
			erosion = new Erosion(test,cutoff);
			renderer.setTerrain(test,cutoff);
			renderer.redraw();
		}
		else if (key == 'j')
		{
			cutoff -= 1;
			erosion = null;
			erosion = new Erosion(test,cutoff);
			renderer.setTerrain(test,cutoff);
			renderer.redraw();
		}
		else if (key == 'h')
		{
			helpMode = !helpMode;
		}
		else if (key == 't')
		{
			erosion.tick();
			renderer.setTerrain(test,cutoff);
			renderer.redraw();
		}
		else if (key == 'n')
		{
			for (int i = 0; i < 50; i++)
			{
				erosion.flood((int)(test.length*Math.random()),(int)(test.length*Math.random()),10);
			}
		}
		else if (key == 'x')
		{
			stopRendering = !stopRendering;
		}
	}
	
	float width = 900/(float)nDiv; float height = 900/(float)nDiv; 
	int cutoff = 55;
	private int sight = 8;
	private int zoom = 1;
	public void draw()
	{
		background(0);
		if (stopRendering) {return;}
		//camera(2000,2000,2000,0,0,0,0,-1,0);
		//displayTable(test);
		float land = 0; float sea = 0;
		for (int i = 0; i < test.length; i++)
		{
			for (int j = 0; j < test[0].length; j++)
			{
				//fill((int)(cutoff-test[i][j]));
				if (test[i][j] > cutoff)
				{
					fill(0,(float)(test[i][j] - cutoff + 20)*4,0);
					land++;
				}
				/*else if (test[i][j] > cutoff - 12)
				{
					fill(255,255,0);
					land++;
				}*/
				else
				{
					fill(0,0,(float)test[i][j]);
					sea++;
				}
				rect(i*width,j*width,width+1,height+1);
				//fill(120,120,255);
				//text(test[i][j]+"",i*width,j*width);
			}
		}
		if (helpMode)
		{
			fill(255,0,0);
			textSize(20);
			text("Sea level: " + cutoff,50,50);
			text("Percent of world submerged: " + (sea/(land+sea)),50,80);
			text("Seed: " + seed,50,110);

			if (zoom == 1)
			{
				text("Maximum zoom", 950, 25);
			}
			else
			{
				text("Zoomed out", 950, 25);
			}

			int top = 770;
			text("[I/O] Zoom in/out minimap", 950, top);
			text("[K/L] Minimize/enlarge minimap", 950, top + 30);
			text("[U/J] Raise/lower sea level", 950, top + 60);
			text("[R] Generate new seed and terrain", 950, top + 90);
			text("[T/N] Test erosion; add water", 950, top + 120);
		}

		//if (mouseX < 900 && mouseY < 900)
		textSize(10);
		int r = (int)(mouseX/900F*test.length);
		int c = (int)(mouseY/900F*test[0].length);
		int rCount = 0; int cCount = 0;
		for (int i = r - sight; i < r + sight; i += zoom)
		{
			for (int j = c - sight; j < c + sight; j += zoom)
			{
				//System.out.println(i + " " + j);
				if (i >= 0 && i < test.length && j >= 0 && j < test[0].length)
				{
					if (test[i][j] < cutoff)
						fill(0,0,255);
					else
						fill(0,255,0);
					int height;//display this level
					//If zoomed in, display the exact value.
					//If zoomed out, display an appropriate average.
					if (zoom == 1)
					{
						height = (int)test[i][j];
					}
					else
					{
						double avg = 0; double n = 0;
						for (int row = i - zoom/2; row < i + zoom/2; row++)
						{
							for (int col = j - zoom/2; col < j + zoom/2; col++)
							{
								if (row >= 0 && row < test.length && col >= 0 && col < test[0].length)
								{
									avg += test[row][col];
									n++;
								}
							}
						}
						height = (int)(avg/n);
					}
					text(height + "", 950 + 20*rCount, 50 + 20*cCount);
				}
				cCount++;
			}
			rCount++;
			cCount = 0;
		}
	}

}
