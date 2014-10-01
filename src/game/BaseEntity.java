package game;

//Base class for all objects in the world, living or not living

public abstract class BaseEntity {

	public Tile location;
	public String name;
	public String id;
	public Civilization owner;
	
	public String queue;
	public int queueTurns;
	
	public int health;
	public float offensiveStr, rangedStr, defensiveStr;
	
	public BaseEntity(String name)
	{
		this.name = name;
		id = Double.toString(Math.random()*System.currentTimeMillis());
	}
	
	public BaseEntity(BaseEntity other)
	{
		name = other.name; 
	}
	
	public void tick() {}
	public void playerTick() {};
	
	public abstract String getName();
	
}
