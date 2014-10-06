package system;

import render.CivGame;

//Civilizations send all their orders here, to be evaluated every turn (with the exception of the player)
//Commands are only put here as opposed to being split in many different classes

public class OrderSystem extends BaseSystem {

	public OrderSystem(CivGame civGame) {
		super(civGame);
	}

	public void tick()
	{
		
	}
	
}
