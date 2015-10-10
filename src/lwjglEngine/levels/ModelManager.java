package lwjglEngine.levels;

import lwjglEngine.entities.Group;
import lwjglEngine.tests.MainGameLoop;
import game.Grid;
import game.Tile;

//Loads models into the world based on a grid and given 'OBJ' files

public class ModelManager {

	private LevelManager lm;

	public ModelManager(LevelManager main, Grid grid, double[][] heightMap) 
	{
		lm = main;
		modelsFromGrid(grid, heightMap);
	}

	private void modelsFromGrid(Grid grid, double[][] heightMap)
	{
		for (int r = 0; r < grid.rows; r++)
		{
			for (int c = 0; c < grid.cols; c++)
			{
				//String temp = getModels(grid.getTile(r,c));
				String temp = null;
				if ((r > 10 && r < 15 && c > 10 && c < 16) && grid.getTile(r,c).biome != -1)
				{
					temp = "Farm1";
				}
				if (temp != null)
				{
					String[] models = temp.split(" ");
					for (int i = 0; i < models.length; i++)
					{
						Group candidate = LevelManager.loadFromXML(models[i]);
						/*if (candidate == null)
						{
							candidate = LevelManager.loadFromXML("Old" + models[i]);
						}*/
						if (candidate != null)
						{
							/*int m = MainGameLoop.multiply; int n = 0;
							float height = 0;
							for (int nr = r*m; nr < r*(m+1); nr++)
							{
								for (int nc = c*m; nc < c*(m+1); nc++)
								{
									if (nr < heightMap.length || nc < heightMap[0].length)
									{
										height += heightMap[nr][nc];
										n++;
									}
								}
							}
							height /= (float)(n);
							float modelHeight = candidate.boundingBox()[4]/2F;*/
							candidate.move(((float)r+0.5F)/(float)grid.rows*1600F*0.9F, -candidate.boundingBox()[1], ((float)c+0.5F)/(float)grid.cols*1600F*0.9F);
							lm.groups.add(candidate);
						}
					}
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
				temp += " " + "Rock";
		}
		if (temp.equals("")) return null;
		return temp.substring(1);
	}

}
