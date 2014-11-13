package game;

//A static entity that occupies a tile i.e. an improvement

public class TileEntity extends BaseEntity {

	public TileEntity(String name)
	{
		super(name,0,0,0);
		health = 1;
	}
	
	public TileEntity(String name, float o, float d, float r)
	{
		super(name,o,d,r);
		health = 1;
	}
	
	public TileEntity(TileEntity other)
	{
		super(other);
		health = 1;
	}
	
	public String getName()
	{
		return name;
	}
	
}
