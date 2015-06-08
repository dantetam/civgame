package lwjglEngine.levels;

import game.Grid;
import game.Tile;

//Loads models into the world based on a grid and given 'OBJ' files

public class ModelManager {

	private LevelManager lm;

	public ModelManager(LevelManager main, Grid grid) 
	{
		lm = main;
		modelsFromGrid(grid);
	}

	private void modelsFromGrid(Grid grid)
	{
		for (int r = 0; r < grid.rows; r++)
		{
			for (int c = 0; c < grid.cols; c++)
			{
				String temp = getModels(grid.getTile(r,c));
				String[] models = temp.split(" ");
				for (int i = 0; i < models.length; i++)
				{
					if (lm.loadFromXML(models[i]) != null)
				}
			}
		}
	}

	private String getModels(Tile t)
	{
		String temp = "";
		if (t.improvement != null)
			temp += " " + t.improvement.name;
		if (t.occupants.size() > 0)
			temp += " " + t.occupants.get(0).name;
		if (t.forest)
			temp += " " + "Forest";
		if (t.resource != 0)
		{
			if (t.resource == 1 || t.resource == 2)
				temp += " " + "Wheat";
			if (t.resource >= 20 || t.resource <= 22)
				temp += " " + "Wheat";
		}
		return temp.substring(1);
	}

}
