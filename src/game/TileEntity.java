package game;

//A static entity that occupies a tile i.e. an improvement

public class TileEntity extends BaseEntity {

	public TileEntity(String name)
	{
		super(name);
	}
	
	public TileEntity(TileEntity other)
	{
		super(other);
	}
	
}
