package game;

import java.util.ArrayList;

import lwjglEngine.levels.LevelManager;
import lwjglEngine.levels.ModelManager;

//Extension of the grid to separate LWJGL features from non-LWJGL features

public class LwjglGrid extends Grid {

	public LevelManager level;
	public ModelManager models;
	
	public LwjglGrid(String playerCiv, double[][] terrain, int[][] biomes, int[][] resources, int[][] fields,
			int numCivs, int numCityStates, int difficultyLevel, int numBarbarians, int cutoff, long seed) {
		super(playerCiv, terrain, biomes, resources, fields, numCivs, numCityStates, difficultyLevel, numBarbarians, cutoff,
				seed);
		// TODO Auto-generated constructor stub
	}
	public void setManager(LevelManager lm) //A post-constructor of sorts
	{
		level = lm;
		models = lm.modelManager;
	}

	//The following five methods do not override old behavior. Rather, they extend function to LWJGL systems
	//to ensure efficient updating of models in them.
	//The manager != null checks are to ensure that updating only happens after the initial render (i.e. game starts)
	public BaseEntity addUnit(BaseEntity en, Civilization civ, int r, int c)
	{
		if (level != null)
		{
			models.addUnit(en, r, c);
		}
		return super.addUnit(en, civ, r, c);
	}
	
	public void removeUnit(BaseEntity en)
	{
		if (level != null)
		{
			models.removeUnit(en);
		}
		super.removeUnit(en);
	}
	
	public void moveTo(BaseEntity en, int r, int c)
	{
		if (level != null)
		{
			models.moveUnitTo(en, r, c);
		}
		super.moveTo(en, r, c);
	}
	
	public void move(BaseEntity en, int rDis, int cDis)
	{
		if (level != null)
		{
			models.moveUnitBy(en, rDis, cDis);
		}	
		super.move(en, rDis, cDis);
	}
	
	public boolean stackAttack(ArrayList<GameEntity> attacker, ArrayList<GameEntity> defender)
	{
		boolean temp = super.stackAttack(attacker, defender);
		if (level != null)
		{
			
		}
		return temp;
	}

}
