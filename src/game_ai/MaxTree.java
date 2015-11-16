package game_ai;

import java.util.ArrayList;
import java.util.HashMap;

import game.Civilization;
import game.Grid;
import game.TechTree;
import system.MenuSystem;
import units.City;

//An expectimax tree data structure used for determining AI action
//Depth limited for frequent (per-turn) calculations

public class MaxTree {

	public static MaxTree generateTree(City c)
	{
		TechTree tech = c.owner.techTree;
		HashMap<String, Integer> predictedGain = new HashMap<String, Integer>(); 
		for (String action: tech.allowedUnits)
		{
			predictedGain.put(action, generateTreeForCityAction(c, action));
		}
		for (String action: tech.allowedCityImprovements)
		{
			
		}
	}
	
	public static int generateTreeForCityAction(City c, String action) //Generate an expectimax tree based on civilization's choices
	{
		//Calculate other most advanced civ
		Grid grid = c.location.grid;
		/*if (Intelligence.civScores == null) Intelligence.calculateCivScores(grid);
		int idMax = 0;
		for (int i = 1; i < Intelligence.civScores.length; i++)
		{
			if (Intelligence.civScores[i] > Intelligence.civScores[idMax] && c.owner.id != i)
				idMax = i;
		}
		Civilization rival = grid.civs[idMax];
		int actionQueueTurns = MenuSystem.calcQueueTurnsInt(c, action);
		int[] heuristic = Intelligence.calculateHeurYield(rival, action, actionQueueTurns);*/
		
	}

	public float value(State state)
	{
		if (state.type == StateType.TERMINAL)
			state.calcValue = state.event.value;
		else if (state.type == StateType.MAX)
			state.calcValue = maxValue(state);
		else if (state.type == StateType.EXP)
			state.calcValue = expValue(state);
		System.err.println("Invalid state type, data: " );
		System.err.print("; " + state.event.value);
		return state.calcValue;
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
		float sum = 0;
		for (State child: state.children)
		{
			float value = value(child);
			sum += value * child.event.chance;
		}
		return sum;
	}

	public class State
	{
		public StateType type;
		public ArrayList<State> children = new ArrayList<State>();

		public float calcValue;

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
