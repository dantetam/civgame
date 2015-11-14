package game_ai;

import java.util.ArrayList;

import game.Civilization;

//An expectimax tree data structure used for determining AI action
//Depth limited for frequent (per-turn) calculations

public class MaxTree {

	public static MaxTree generateTree(Civilization civ) //Generate an expectimax tree based on civilization's choices
	{
		
	}
	
	public float value(State state)
	{
		if (state.type == StateType.TERMINAL)
			return state.event.value;
		else if (state.type == StateType.MAX)
			return maxValue(state);
		else if (state.type == StateType.EXP)
			return expValue(state);
		System.err.println("Invalid state type, data: " );
		System.err.print("; " + state.event.value);
		return -1;
		//for (Event event: state.events)
			//System.out.print("; " + event.name + " " + event.value + " " + event.chance);
	}
	
	public float maxValue(State state)
	{
		float max = -1;
		for (State child: state.children)
		{
			float value = value(child);
			if (value > max) max = value;
		}
		return max;
	}
	
	public float expValue(State state)
	{
		
	}
	
	public class State
	{
		public StateType type;
		public ArrayList<State> children = new ArrayList<State>();
		
		public Event event;
		//public ArrayList<Event> events = new ArrayList<Event>();
	}
	public class Event
	{
		public String name; public float chance, value;
		public Event(String n, float c, float v) {name = n; chance = c; value = v;}
	}
	
	public enum StateType
	{
		TERMINAL, MAX, EXP
	}
	
}
