package game;

//Base class for all objects in the world, living or not living

public abstract class BaseEntity {

	public Tile location;
	public String name;
	public Civilization owner;
	
	public BaseEntity(String name)
	{
		this.name = name;
	}
	
	public BaseEntity(BaseEntity other)
	{
		name = other.name; 
	}
	
}
