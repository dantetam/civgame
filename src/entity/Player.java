package entity;

import game.Civilization;
import game.GameEntity;
import game.Grid;

import java.util.ArrayList;

import system.RenderSystem;
import vector.*;

public class Player {
	
	public Civilization civ;
	//public ArrayList<GameEntity> moved; //Keep track of which units are moved; should be done for every civ
	//i.e. every civ moves their units until there are none left to move

	public int cornerX, cornerY;
	public int windowX, windowY;
	
	public Player()  
	{
		//this.civ = civ;
		//moved = new ArrayList<GameEntity>();
		cornerX = 0; cornerY = 0;
		windowX = 32; windowY = 32;
	}
	
	public String toString()
	{
		return null;
		//return "Player: " + posX + " " + posY + " " + posZ + " " + tarX + " " + tarY + " " + tarZ;
	}
	
	public void update()
	{
		
	}


}
