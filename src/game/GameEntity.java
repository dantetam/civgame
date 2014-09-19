package game;

//An entity that moves around the map i.e. a unit

public class GameEntity extends BaseEntity {

	public GameEntity(String name)
	{
		super(name);
	}
	
	public GameEntity(GameEntity other)
	{
		super(other);
	}
	
	public void tick()
	{
		if (name.equals("Worker") && queue != null)
		{
			queueTurns--;
			if (queueTurns <= 0)
			{
				queueTurns = 0; //just to be sure
				location.grid.addUnit(en, owner, location.row, location.col);
			}
		}
	}
	
}
