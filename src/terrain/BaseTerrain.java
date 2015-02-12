package terrain;

//Base class of all terrain generation algorithms
//TODO: Edit classes so that they all use standard methods found here
//TODO: Move functions from data into here
//TODO: Allow 2D, 3D block, and 3D wireframe rendering of terrain

import java.util.Random;

public abstract class BaseTerrain {

	public double[][] terrain;
	public Random random;
	
	public void seed(long seed)
	{
		random = null;
		random = new Random(seed); 
	}
	
	public abstract double[][] generate();
	public abstract double[][] generate(double[] arguments);
	
}
