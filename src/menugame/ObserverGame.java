package menugame;

import render.CivGame;
import render.Game;

public class ObserverGame extends CivGame {

	public ObserverGame(Game game, int numCivs, int numCityStates,
			int difficultyLevel, String challengeType, String terrainType,
			String civChoice, long seed) {
		super(game, numCivs, numCityStates, difficultyLevel, challengeType,
				terrainType, civChoice, seed);
	}

	public void setup()
	{
		super.setup();
		menuSystem.menus.clear();
		newMenuSystem.menus.clear();
	}
	
	public void draw()
	{
		background(255);
		noCursor();
		for (int i = 0; i < systems.size(); i++)
			systems.get(i).tick();
	}
	
	//Override old methods of normal game
	public void keyPressed() {}
	public void keyReleased() {}

}
