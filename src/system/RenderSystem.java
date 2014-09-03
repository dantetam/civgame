package system;

import java.util.ArrayList;

import render.CivGame;
import entity.Entity;
import data.Color;

public class RenderSystem extends BaseSystem {

	public ArrayList<Entity> terrain;
	
	public RenderSystem(CivGame civGame)
	{
		super(civGame);
		terrain = new ArrayList<Entity>();
	}
	
	public void tick()
	{
		
	}
	
}
