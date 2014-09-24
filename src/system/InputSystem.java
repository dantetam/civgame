package system;

import java.util.ArrayList;

import render.CivGame;

public class InputSystem extends BaseSystem {

	private ArrayList<Character> keyPresses;
	public boolean moving = false;
	public boolean lastMoving = false;

	public boolean on = true;

	public InputSystem(CivGame main)
	{
		super(main);
		keyPresses = new ArrayList<Character>();
	}

	//Goes through keys backwards to avoid arraylist trap
	public void tick()
	{
		moving = false;
		for (int i = keyPresses.size() - 1; i >= 0; i--)
		{
			executeAction(keyPresses.get(i));
			keyPresses.remove(i);
		}
		for (int i = 0; i < keyHeld.length; i++)
		{
			if (keyHeld[i])
			{
				float dist = 15;
				//System.out.println(i+97);
				if (i == 97 - 97) //a
				{
					//Limit movement to an axis
					main.player.posX += dist*Math.cos(main.player.rotY + Math.PI/2);
					main.player.tarX += dist*Math.cos(main.player.rotY + Math.PI/2);
					main.player.posZ += dist*Math.sin(main.player.rotY + Math.PI/2);
					main.player.tarZ += dist*Math.sin(main.player.rotY + Math.PI/2);
				}
				else if (i == 100 - 97) //d
				{
					//Limit movement to an axis
					main.player.posX += dist*Math.cos(main.player.rotY - Math.PI/2);
					main.player.tarX += dist*Math.cos(main.player.rotY - Math.PI/2);
					main.player.posZ += dist*Math.sin(main.player.rotY - Math.PI/2);
					main.player.tarZ += dist*Math.sin(main.player.rotY - Math.PI/2);
				}
				else if (i == 115 - 97) //s
				{
					//Limit movement to an axis
					main.player.posX -= dist*Math.cos(main.player.rotY);
					main.player.tarX -= dist*Math.cos(main.player.rotY);
					main.player.posZ -= dist*Math.sin(main.player.rotY);
					main.player.tarZ -= dist*Math.sin(main.player.rotY);
				}
				else if (i == 119 - 97) //w
				{
					//Limit movement to an axis
					main.player.posX += dist*Math.cos(main.player.rotY);
					main.player.tarX += dist*Math.cos(main.player.rotY);
					main.player.posZ += dist*Math.sin(main.player.rotY);
					main.player.tarZ += dist*Math.sin(main.player.rotY);
				}
				else if (i == 113 - 97) //q
				{
					//Limit movement to an axis
					main.player.posY -= dist;
					main.player.tarY -= dist;
				}
				else if (i == 101 - 97) //e
				{
					//Limit movement to an axis
					main.player.posY += dist;
					main.player.tarY += dist;
				}
				if (i == 0 || i == 3 || i == 4 || i == 16 || i == 18 || i == 22)
				{
					//main.setUpdateFrame(50);
					//if (moving) main.setUpdateFrame(10);
					moving = true;
				}
				//System.out.println(moving);
				main.redraw();
			}
		}
		if (moving == false && lastMoving)
		{
			main.chunkSystem.update();
			//System.out.println("Update");
		}
		lastMoving = moving;
	}

	//Stores which keys are being held (such as panning with WASD)
	public boolean[] keyHeld = new boolean[26];
	public void queueKey(char key)
	{
		if (key >= 97 && key <= 122)
		{
			keyHeld[key-97] = true;
		}
		keyPresses.add(0,key);
	}

	public void keyReleased(char key)
	{
		if (key >= 97 && key <= 122)
		{
			keyHeld[key-97] = false;
		}
	}

	public float lastMouseX = main.width/2; public float lastMouseY = main.height/2;
	public void passMouse(float mouseX, float mouseY)
	{
		if (on)
		{
			float dX = mouseX - lastMouseX;
			float dY = mouseY - lastMouseY;
			main.player.rotY -= dX/125; //Axis is weird, oh well
			main.player.rotVertical -= dY/125;
			main.player.update();
			if (Math.abs(dX) <= 20)
			{
				main.chunkSystem.update();
			}
			lastMouseX = mouseX;
			lastMouseY = mouseY;
			
			main.menuSystem.hintText.clear();
			//Find the target of the player's cursor
			main.player.getLookVector();
			
			if (Math.sqrt(Math.pow(dX,2) + Math.pow(dY, 2)) >= 10)
			{
				main.menuSystem.hintText.add("Test");
			}
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
		else if (key == 'c')
		{
			on = !on;
		}
		else if (key == 'm')
		{
			main.menuSystem.minimap = !main.menuSystem.minimap;
		}
	}

}
