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
	
	public int sight = 2;
	public void reveal()
	{
		for (int i = location.row - sight; i <= location.row + sight; i++)
		{
			for (int j = location.col - sight; j <= location.col + sight; j++)
			{
				if (location.grid.getTile(i, j) != null)
				{
					owner.revealed[i][j] = true;
				}
			}
		}
		System.out.println(name + " reveal");
	}
	
	public void tick() {}
	public void playerTick() {};
	
	public abstract String getName();
	
}
