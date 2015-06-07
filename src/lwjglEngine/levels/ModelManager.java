package lwjglEngine.levels;

import game.Grid;
import game.Tile;

//Loads models into the world based on a grid and given 'OBJ' files

public class ModelManager {

	private LevelManager main;
	
	public ModelManager(LevelManager main) 
	{
		this.main = main;
		modelsFromGrid();
	}
	
	private void modelsFromGrid(Grid grid)
	{
		for (int r = 0; r < grid.rows; r++)
		{
			for (int c = 0; c < grid.cols; c++)
			{
				String temp = getModels(grid.getTile(r,c));
				String[] models = temp.split(" ");
			}
		}
	}
	
	private String getModels(Tile t)
	{
		String temp = "";
		if (t.improvement != null)
		{
			
		}
		if (t.occupants.size() > 0)
		{
			
		}
		return temp.substring(1);
	}
	
}
