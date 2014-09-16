package units;

import game.*;

public class City extends TileEntity {

	public int population;
	public Tile[] land;
	public String queue;
	public int queueTurns;
	public Civilization owner;
	
	public City(String name)
	{
		super(name);
		//queue = null;
		queueTurns = 0;
	}
	
	public City(TileEntity other) {
		super(other);
	}
	
	public void tick()
	{
		
	}
	
}
