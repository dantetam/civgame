package system;

import java.util.ArrayList;

import render.CivGame;

public class InputSystem extends BaseSystem {

	public ArrayList<Character> keyPresses;
	
	public InputSystem(CivGame main)
	{
		super(main);
	}
	
	//Goes through keys backwards to avoid arraylist trap
	public void tick()
	{
		for (int i = keyPresses.size() - 1; i >= 0; i--)
		{
			executeAction(keyPresses.get(i));
			keyPresses.remove(i);
		}
	}
	
	//Stores which keys are being held (such as panning with WASD)
	public boolean[] keyHeld = new boolean[26];
	public void executeAction(char key)
	{
		if (key >= 97 && key <= 122)
		{
			keyHeld = 
		}
	}
	
}
