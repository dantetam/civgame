package system;

import render.CivGame;

public abstract class BaseSystem {

	public CivGame civGame;
	
	public BaseSystem(CivGame civGame)
	{
		this.civGame = civGame;
	}
	
	public abstract void tick();
	
}
