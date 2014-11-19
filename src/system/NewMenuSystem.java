package system;

import render.CivGame;

public class NewMenuSystem extends BaseSystem {

	public NewMenuSystem(CivGame civGame) {
		super(civGame);
	}
	
	public void tick() 
	{
		
		
		main.hint(main.ENABLE_DEPTH_TEST);
	}

}
