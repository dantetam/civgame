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
				//System.out.println(i+97);
				if (i == 97 - 97) //a
				{
					//Limit movement to an axis
					main.player.posZ -= 10;
					main.player.tarZ -= 10;
					main.player.posX += 10;
					main.player.tarX += 10;
				}
				else if (i == 100 - 97) //d
				{
					//Limit movement to an axis
					main.player.posZ += 10;
					main.player.tarZ += 10;
					main.player.posX -= 10;
					main.player.tarX -= 10;
				}
				else if (i == 115 - 97) //s
				{
					//Limit movement to an axis
					main.player.posX += 10;
					main.player.tarX += 10;
					main.player.posZ += 10;
					main.player.tarZ += 10;
				}
				else if (i == 119 - 97) //w
				{
					//Limit movement to an axis
					main.player.posX -= 10;
					main.player.tarX -= 10;
					main.player.posZ -= 10;
					main.player.tarZ -= 10;
				}
				else if (i == 113 - 97) //q
				{
					//Limit movement to an axis
					main.player.posY -= 10;
					main.player.tarY -= 10;
				}
				else if (i == 101 - 97) //e
				{
					//Limit movement to an axis
					main.player.posY += 10;
					main.player.tarY += 10;
				}
				main.redraw();
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
	
	/*public void test()
	{
		for (int i = 0; i < keyHeld.length; i++)
		{
			if (keyHeld[i])
			{
				//System.out.println(i+97);
				if (i == 0)
				{
					//Limit movement to an axis
					main.player.posX += 10;
					main.player.tarX += 10;
				}
			}
		}
	}*/
	
	public void executeAction(char key)
	{
		if (key == 32)
		{
			//System.out.println("Space");
			main.civilizationSystem.requestTurn = true;
		}
	}
	
}
