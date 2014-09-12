package units;

import game.*;

public class City extends TileEntity {

	public int population;
	public Tile[] land;
	
	public City(String name)
	{
		super(name);
	}
	
	public City(TileEntity other) {
		super(other);
	}

	
}
