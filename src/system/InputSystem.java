package system;

import java.util.ArrayList;

import render.CivGame;

public class InputSystem extends BaseSystem {

	private ArrayList<Character> keyPresses;
	
	public InputSystem(CivGame main)
	{
		super(main);
		keyPresses = new ArrayList<Character>();
	}
	
	//Goes through keys backwards to avoid arraylist trap
	public void tick()
	{
		for (int i = keyPresses.size() - 1; i >= 0; i--)
		{
			executeAction(keyPresses.get(i));
			keyPresses.remove(i);
		}
		for (int i = 0; i < keyHeld.length; i++)
		{
			if (keyHeld[i])
			{
				System.out.println(i+97);
			}
		}
	}
	
	//Stores which keys are being held (such as panning with WASD)
	public boolean[] keyHeld = new boolean[26];
	public void queueKey(char key)
	{
		if (key >= 97 && key <= 122)
		{
			keyHeld[key-97] = true;
		}
		keyPresses.add(key);
	}
	
	public void keyReleased(char key)
	{
		if (key >= 97 && key <= 122)
		{
			keyHeld[key-97] = false;
		}
	}
	
	public void executeAction(char key)
	{

	}
	
}
