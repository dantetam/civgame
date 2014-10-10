package system;

import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Tile;

import java.util.ArrayList;

import render.CivGame;
import system.MenuSystem.Click;
import units.City;
import units.Settler;

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
				//main.redraw();
			}
		}
		if (moving == false && lastMoving)
		{
			main.chunkSystem.update();
			//System.out.println("Update");
		}
		lastMoving = moving;
		for (int i = clicks.size() - 1; i >= 0; i--)
		{
			Click c = clicks.get(i);
			if (c.type.equals("Left"))
			{
				passLeftMouseClick(c.mouseX, c.mouseY);
			}
			else if (c.type.equals("Right"))
			{
				passRightMouseClick(c.mouseX, c.mouseY);
			}
			clicks.remove(i);
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
		keyPresses.add(0,key);
	}

	public void keyReleased(char key)
	{
		if (key >= 97 && key <= 122)
		{
			keyHeld[key-97] = false;
		}
	}

	//public float lastMouseX = main.width/2; //public float lastMouseY = main.height/2;
	public void passMouse(float mouseX, float mouseY)
	{
		if (on) //&& main.menuSystem.selected == null)
		{
			float dX = (mouseX - main.centerX)/(main.centerX);
			float dY = (mouseY - main.centerY)/(main.centerY);
			main.player.rotY = -(float)Math.PI*dX; //Axis is weird, oh well
			main.player.rotVertical = (float)Math.PI/4*dY;
			if (Math.abs(dX) <= 20)
			{
				main.chunkSystem.update();
				main.requestUpdate(true);
			}
		}
		main.player.update();
	}

	public ArrayList<Click> clicks = new ArrayList<Click>();
	public class Click {String type; float mouseX, mouseY; Click(String t, float x, float y) {type = t; mouseX = x; mouseY = y;}}
	public void queueLeftClick(float mouseX, float mouseY)
	{
		clicks.add(0, new Click("Left",mouseX, mouseY));
	}
	public void queueRightClick(float mouseX, float mouseY)
	{
		clicks.add(0, new Click("Right",mouseX, mouseY));
	}

	//Make a system to cycle through units on a list
	//private ArrayList<GameEntity> lastList = null;
	//private int num = 0;
	public void passLeftMouseClick(float mouseX, float mouseY)
	{
		if (main.menuSystem.highlighted != null && !main.menuSystem.menuActivated)
		{
			if (main.menuSystem.highlighted.occupants.size() > 0)
			{
				int r = (int)(main.menuSystem.highlighted.occupants.size()*Math.random()); 
				if (main.menuSystem.highlighted.occupants.get(r).owner.equals(main.grid.civs[0]))
				{
					if (main.menuSystem.getSelected() != null)
					{
						if (!main.menuSystem.getSelected().equals(main.menuSystem.highlighted.occupants.get(r)))
						{
							main.menuSystem.select(main.menuSystem.highlighted.occupants.get(r));
							return;
						}
					}
					else
					{
						main.menuSystem.select(main.menuSystem.highlighted.occupants.get(r));
						//continue on to the next if statement
					}
				}
			}
			else
			{
				main.menuSystem.select(null);
				main.resetCamera();
			}
			if (main.menuSystem.highlighted.improvement != null)
				if (main.grid.civs[0].cities.contains(main.menuSystem.highlighted.improvement))
				{
					City c = (City)main.menuSystem.highlighted.improvement;
					main.menuSystem.select(c);
					//return;
				}
		}
		if (main.menuSystem.getSelected() instanceof Settler)
		{
			main.menuSystem.settlerChoices = main.grid.returnBestCityScores(main.menuSystem.getSelected().location.row, main.menuSystem.getSelected().location.col);
		}
		else
		{
			main.menuSystem.settlerChoices = null;
		}
		main.requestUpdate(true);
	}

	public void passRightMouseClick(float mouseX, float mouseY)
	{
		if (main.menuSystem.getSelected() instanceof GameEntity)
		{
			GameEntity en = (GameEntity)main.menuSystem.getSelected();
			Tile t = main.menuSystem.highlighted;
			if (en != null && t != null)
			{
				if (t.biome != -1 && en.owner != null) //Removing does not seem to clear from memory, check if owner is null then
				{
					//System.out.println(en.location.row + " " + en.location.col + " to " + t.row + " " + t.col);
					int r = t.row - en.location.row;
					int c = t.col - en.location.col;
					//System.out.println(en.location.row + " " + en.location.col + " to " + t.row + " " + t.col);
					//System.out.println(r + " " + c);
					en.queueTiles.clear();
					en.waddleTo(r,c);
					/*while (en.action > 0)
					{
						en.playerTick();
						en.action--;
					}*/
				}
			}
		}
		main.requestUpdate(true);
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
			Civilization civ = main.grid.civs[0];
			for (int i = 0; i < civ.units.size(); i++)
			{
				GameEntity en = civ.units.get(i);
				if (en.action > 0 && en.queueTiles.size() == 0)
				{
					main.fixCamera(en.location.row, en.location.col);
					//lastMouseX = main.mouseX; //lastMouseY = main.mouseY;
					main.menuSystem.select(en);
					main.menuSystem.message(en.name + " needs orders.");
					return;
				}
			}
			for (int i = 0; i < civ.cities.size(); i++)
			{
				City c = civ.cities.get(i);
				if (c.queue == null)
				{
					main.fixCamera(c.location.row, c.location.col);
					//lastMouseX = main.mouseX; //lastMouseY = main.mouseY;
					main.menuSystem.select(c);
					main.menuSystem.updateCity(c);
					return;
				}
			}
			main.civilizationSystem.requestTurn = true;
		}
		else if (key == 'c')
		{
			on = !on;
			main.resetCamera();
		}
		else if (key == 'f')
		{
			//main.resetCamera();
			main.centerX = main.width/2;
			main.centerY = main.height/2;
		}
		else if (key == 'm')
		{
			main.menuSystem.minimap = !main.menuSystem.minimap;
		}
		else if (key == 't')
		{
			main.showAll = !main.showAll;
		}
		main.requestUpdate(true);
	}

}
