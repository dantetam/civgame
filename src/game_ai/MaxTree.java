package game_ai;

import java.util.ArrayList;

import game.Civilization;

//An expectimax tree data structure used for determining AI action
//Depth limited for frequent (per-turn) calculations

public class MaxTree {

	public static MaxTree generateTree(Civilization civ) //Generate an expectimax tree based on civilization's choices
	{
		
	}
	
	public int value(State state)
	{
		
	}
	
	public int maxValue(State state)
	{
		
	}
	
	public int expValue(State state)
	{
		
	}
	
	public class State
	{
		public StateType type;
		public ArrayList<State> children = new ArrayList<State>();
		
		public float value;
		public ArrayList<Event> events = new ArrayList<Event>();
	}
	public class Event
	{
		public String name; public float chance, value;
	}
	
	public enum StateType
	{
		TERMINAL, MAX, EXP
	}
	
}
