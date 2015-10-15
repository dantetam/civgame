package lwjglEngine.levels;

import java.util.HashMap;

import data.EntityData;
import lwjglEngine.entities.Group;
import lwjglEngine.tests.MainGameLoop;
import game.BaseEntity;
import game.Grid;
import game.LwjglGrid;
import game.Tile;

//Loads models into the world based on a grid and given 'OBJ' files

public class ModelManager {

	private LevelManager lm;
	private int rows, cols; //For reference by this class only

	public HashMap<BaseEntity, Group> units, improvements;
	public HashMap<Tile, Group> resources, features;

	public ModelManager(LevelManager main, LwjglGrid grid, double[][] heightMap) 
	{
		lm = main;
		rows = grid.rows; cols = grid.cols;
		units = new HashMap<BaseEntity, Group>();
		improvements = new HashMap<BaseEntity, Group>();
		resources = new HashMap<Tile, Group>();
		features = new HashMap<Tile, Group>();
		
		modelsFromGrid(grid, heightMap);
	}

	public void addUnit(BaseEntity en, int r, int c)
	{
		Group candidate = LevelManager.loadFromXML(EntityData.getUniqueModel(en.name), "partTexture", "colorTexture" + (int)en.owner.primaryBrickColor);
		units.put(en, candidate);
		moveUnitTo(en,r,c);
	}
	public void removeUnit(BaseEntity en)
	{
		/*Group candidate = access(en);
		if (candidate != null)
			lm.groups.remove(candidate);*/
		units.put(en, null);
		improvements.put(en, null);
	}
	public void moveUnitTo(BaseEntity en, int r, int c)
	{
		Group candidate = access(en);
		moveCandidate(candidate, r, c);
	}
	public void moveUnitBy(BaseEntity en, int r, int c)
	{
		Group candidate = access(en);
		moveCandidate(candidate, r, c);
	}

	private void moveCandidate(Group candidate, int r, int c)
	{
		//Accidentally did not set rows and cols to non-zero value. Did not call div zero error.
		//Floating point precision? Infinity vector?
		candidate.move(((float)r+0.5F)/(float)rows*1600F*0.9F, -candidate.boundingBox()[1], ((float)c+0.5F)/(float)cols*1600F*0.9F);
	}

	private Group access(BaseEntity en)
	{
		Group candidate = null;
		if (units.get(en) != null) candidate = units.get(en); 
		else if (improvements.get(en) != null) candidate = improvements.get(en); 
		return candidate;
	}
	private Group access(Tile en)
	{
		Group candidate = null;
		if (resources.get(en) != null) candidate = resources.get(en); 
		else if (features.get(en) != null) candidate = features.get(en); 
		return candidate;
	}

	private void modelsFromGrid(Grid grid, double[][] heightMap)
	{
		for (int r = 0; r < grid.rows; r++)
		{
			for (int c = 0; c < grid.cols; c++)
			{
				Tile t = grid.getTile(r,c);
				for (BaseEntity en: t.occupants)
				{
					Group candidate = LevelManager.loadFromXML(EntityData.getUniqueModel(en.name), "partTexture", "colorTexture"+(int)en.owner.primaryBrickColor);
					moveCandidate(candidate, r, c);
					units.put(en, candidate);
				}
				if (t.improvement != null)
				{
					String texture = t.owner != null ? "colorTexture"+(int)t.improvement.owner.primaryBrickColor : "partTexture";
					Group candidate = LevelManager.loadFromXML(EntityData.getUniqueModel(t.improvement.name), "partTexture", texture);
					moveCandidate(candidate, r, c);
					improvements.put(t.improvement, candidate);
				}
				Group candidate = getResources(t);
				if (candidate != null)
				{
					resources.put(t, candidate);
					moveCandidate(candidate, r, c);
				}
				candidate = getFeatures(t);
				if (candidate != null)
				{
					features.put(t, candidate);
					moveCandidate(candidate, r, c);
				}
			}
		}
	}

	//Not used because coloring requires knowing color of owner
	/*private String getModels(Tile t)
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
	}*/

	private Group getFeatures(Tile t)
	{
		Group candidate = null;
		if (t.forest)
		{
			candidate = LevelManager.loadFromXML(EntityData.getUniqueModel("Forest"), "partTexture", "colorTexture102");
		}
		return candidate;
	}
	private Group getResources(Tile t)
	{
		Group candidate = null;
		if (t.resource != 0)
		{
			if (t.resource == 1 || t.resource == 2)
				candidate = LevelManager.loadFromXML(EntityData.getUniqueModel("Wheat"), "partTexture", "colorTexture"+EntityData.getResourceBrickColor(t.resource));
			else if (t.resource >= 20 || t.resource <= 22)
				candidate = LevelManager.loadFromXML(EntityData.getUniqueModel("Rock"), "partTexture", "colorTexture"+EntityData.getResourceBrickColor(t.resource));
		}
		return candidate;
	}

}
