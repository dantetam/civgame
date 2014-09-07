package entity;

//A list of entities that can be ordered in a grid

public class GridModel {

	public Entity[][] entities;
	
	public GridModel(int row, int col)
	{
		entities = new Entity[row][col];
	}
	
	public void add(Entity en, int row, int col)
	{
		entities[row][col] = en;
	}
	
}
