package units;

import game.GameEntity;

public class Archer extends GameEntity {

	public Archer(String name, float o, float d, float r) {
		super(name,o,d,r);
		health = 10; maxHealth = 10;
	}

	public Archer(GameEntity en) {
		super(en);
		health = 10; maxHealth = 10;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerTick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void barbarianTick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "Archer";
	}
	
}
