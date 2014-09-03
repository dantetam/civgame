package entity;

import java.util.ArrayList;

public class Model {

	public ArrayList<Entity> entities;
	
	public Model()
	{
		entities = new ArrayList<Entity>();
	}
	
	public void add(Entity en)
	{
		entities.add(en);
	}
	
}
