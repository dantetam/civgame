package system;

import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Tile;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import processing.core.*;
import data.EntityData;
import data.Field;
import render.CivGame;
import render.MouseHelper;
import render.TextBox;
import system.MenuSystem.Click;
import units.City;
import units.Settler;

public class InputSystem extends BaseSystem {

	private ArrayList<Character> keyPresses;
	public HashMap<Character,String> keyPressBinds = new HashMap<Character,String>();
	public HashMap<Character,String> keyHoldBinds = new HashMap<Character,String>();

	public boolean moving = false;
	public boolean lastMoving = false;

	public boolean on = true;
	public MouseHelper mouseHelper;
	public PMatrix3D matrix;

	public int time = 20; private int nextSelection = 0;
	public boolean autoSelect;

	public enum KeyPressBind
	{
		ADVANCE_TURN   	(32, 0),
		TOGGLE_MINIMAP 	('m'),
		TOGGLE_FOG		('r'),
		TOGGLE_TACTICAL ('t'),
		ZOOM_IN			('i'),
		ZOOM_OUT		('o'),
		CLOSE_ALL		('x'),
		CONSOLE			('`', '~'),
		FUNCTION_1 		('1', 131),
		FUNCTION_2 		('2', 132),
		FUNCTION_3 		('3', 133),
		FUNCTION_4 		('4', 134),
		FUNCTION_5 		('5', 135),
		FUNCTION_6 		('6', 136),
		FUNCTION_7 		('7', 137),
		FUNCTION_8 		('8', 138),
		FUNCTION_9 		('9', 139),
		FUNCTION_0 		('0', 140),
		TOGGLE_KEY_MENU (9, 0),
		;
		private KeyPressBind(char k1, char k2) {key1 = k1; key2 = k2;}
		private KeyPressBind(char k1) {key1 = k1; key2 = (char)0;}
		private KeyPressBind(int k1, int k2) {key1 = (char)k1; key2 = (char)k2;}
		private KeyPressBind(char k1, int k2) {key1 = k1; key2 = (char)k2;}
		public char key1, key2;
	}

	public enum KeyHoldBind
	{
		PAN_LEFT	('a'),
		PAN_RIGHT	('d'),
		PAN_UP		('w'),
		PAN_DOWN	('s'),
		;
		private KeyHoldBind(char k1, char k2) {key1 = k1; key2 = k2;}
		private KeyHoldBind(char k1) {key1 = k1; key2 = (char)0;}
		private KeyHoldBind(int k1, int k2) {key1 = (char)k1; key2 = (char)k2;}
		private KeyHoldBind(char k1, int k2) {key1 = k1; key2 = (char)k2;}
		public char key1, key2;
	}

	public InputSystem(CivGame main)
	{
		super(main);
		keyPresses = new ArrayList<Character>();
		mouseHelper = new MouseHelper(main.width, main.height);
		setKeyBinds();
	}

	public void setKeyBinds()
	{
		keyPressBinds.clear(); keyHoldBinds.clear();
		for (KeyPressBind kb: KeyPressBind.values())
		{
			keyPressBinds.put(kb.key1, kb.toString());
			if (kb.key2 != (char)0)
				keyPressBinds.put(kb.key2, kb.toString());
		}
		for (KeyHoldBind kb: KeyHoldBind.values())
		{
			keyHoldBinds.put(kb.key1, kb.toString());
			if (kb.key2 != (char)0)
				keyHoldBinds.put(kb.key2, kb.toString());
		}
	}

	//Goes through keys backwards to avoid arraylist trap
	public void tick()
	{
		moving = false;
		if (!autoSelect)
		{
			nextSelection = 0;
		}
		else
		{
			if (nextSelection == main.frameCount)
			{
				nextSelection = 0;
				selectAvailableUnit();
			}
		}
		for (int i = keyPresses.size() - 1; i >= 0; i--)
		{
			executeAction(keyPresses.get(i));
			keyPresses.remove(i);
		}
		//Prevent key presses
		if (main.menuSystem.console != null)
		{

		}
		else
		{
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
					//Prevent height changes to make mousing over tiles easier
					/*else if (i == 113 - 97) //q
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
					}*/
					//if (i == 0 || i == 3 || i == 4 || i == 16 || i == 18 || i == 22)
					if (i == 0 || i == 3 || i == 18 || i == 22)
					{
						//main.setUpdateFrame(50);
						//if (moving) main.setUpdateFrame(10);
						moving = true;
					}
					//System.out.println(moving);
					//main.redraw();
				}
			}
		}
		if (moving == false && lastMoving) //if the player has stopped moving
		{
			main.chunkSystem.update();
			main.requestUpdate();
			//System.out.println("Update");
		}
		lastMoving = moving;
		if (main.menuSystem.menuActivated)
		{
			clicks.clear(); return;
		}
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
		main.menuSystem.menuActivated = false;
	}

	//Stores which keys are being held (such as panning with WASD)
	public boolean[] keyHeld = new boolean[200];
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
			}
		}
		main.player.update();
		int[] tile = mouseHelper.findTile(mouseX + (main.width/2 - main.menuSystem.highlightDispX), mouseY + (main.height/2 - main.menuSystem.highlightDispY));
		//int[] tile = mouseHelper.findTile(mouseX + (main.width/2 - main.menuSystem.highlightDispX), mouseY + (main.width/2 - main.menuSystem.highlightDispX));
		//V Temp
		//int[] tile = mouseHelper.findTile(mouseX, mouseY);
		//System.out.println(":" + mouseX);
		//System.out.println(mouseX + main.menuSystem.highlightDispX);
		if (tile == null || main.menuSystem.menuHighlighted)
		{
			main.menuSystem.mouseHighlighted = null;
		}
		else
		{
			Tile h = main.menuSystem.highlighted;
			if (h != null)
				main.menuSystem.mouseHighlighted = main.grid.getTile(h.row + tile[1], h.col - tile[0]);
			if (main.rMouseX != -1 && main.rMouseY != -1)
			{
				if (main.menuSystem.lastMouseHighlighted != null && main.menuSystem.mouseHighlighted != null)
					if (!main.menuSystem.lastMouseHighlighted.equals(main.menuSystem.mouseHighlighted) && main.menuSystem.getSelected() != null)
						main.menuSystem.pathTo(main.menuSystem.mouseHighlighted);
			}
		}
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
		Field field = EntityData.getField(main.menuSystem.candidateField);
		if (field != null)
		{
			Tile t = main.menuSystem.mouseHighlighted;
			if (t != null)
			{
				if (main.menuSystem.candidateCityField.land.contains(main.menuSystem.mouseHighlighted))
				{
					City city = (City)main.menuSystem.candidateCityField;
					if (city.owner.gold < (int)field.goldFlat)
						main.menuSystem.message("Cannot afford field");
					else if (t.fields.size() + 1 > t.maxFields)
						main.menuSystem.message("Maximum number of fields built");
					else
					{
						city.potentialField = t;
						city.queueFood = (int)field.foodFlat;
						city.queueMetal = (int)field.metalFlat;
						city.owner.gold -= (int)field.goldFlat;
						city.queue = field.name;
						main.menuSystem.select(null);
					}
				}
				else
				{
					main.menuSystem.message("Cannot build fields outside city");
				}
				main.menuSystem.candidateCityField = null;
				main.menuSystem.candidateField = null;
			}
			return;
		}
		if (main.menuSystem.mouseHighlighted != null && !main.menuSystem.menuActivated)
		{
			if (main.menuSystem.mouseHighlighted.occupants.size() > 0)
			{
				int r = (int)(main.menuSystem.mouseHighlighted.occupants.size()*Math.random()); 
				if (main.menuSystem.mouseHighlighted.occupants.get(r).owner.equals(main.grid.civs[0]))
				{
					if (main.menuSystem.getSelected() != null)
					{
						if (!main.menuSystem.getSelected().equals(main.menuSystem.mouseHighlighted.occupants.get(r)))
						{
							main.menuSystem.select(main.menuSystem.mouseHighlighted.occupants.get(r));
							return;
						}
					}
					else
					{
						main.menuSystem.select(main.menuSystem.mouseHighlighted.occupants.get(r));
						//continue on to the next if statement
					}
				}
			}
			else
			{
				main.menuSystem.select(null);
				main.resetCamera();
			}
			if (main.menuSystem.mouseHighlighted.improvement != null)
				if (main.grid.civs[0].cities.contains(main.menuSystem.mouseHighlighted.improvement))
				{
					City c = (City)main.menuSystem.mouseHighlighted.improvement;
					main.menuSystem.select(c);
					//return;
				}
		}
		main.menuSystem.settlerChoices = null;
		if (main.menuSystem.getSelected() == null)
		{
			return;
		}
		else if (main.menuSystem.getSelected().owner == null)
		{
			return;
		}
		else if (main.menuSystem.getSelected() instanceof Settler)
		{
			main.menuSystem.settlerChoices = main.grid.returnBestCityScores(main.menuSystem.getSelected().location.row, main.menuSystem.getSelected().location.col,0.25);
		}
	}

	public void passRightMouseClick(float mouseX, float mouseY)
	{	
		ArrayList<GameEntity> s = main.menuSystem.stack;
		if (s.size() > 0)
		{
			//System.out.println(s.size());
			Tile t = main.menuSystem.mouseHighlighted;
			if (t != null)
			{
				if (main.grid.hasEnemy(s.get(0), t.row, t.col) != null)
				{
					if (!s.get(0).attackWithTheStack(s, t.row, t.col));
						return;
				}
				for (int i = 0; i < s.size(); i++)
					playerAction(s.get(i), false);
				s.clear();
				timeSelection();
				main.menuSystem.select(null);
			}
		}
		else if (main.menuSystem.getSelected() instanceof GameEntity && !main.menuSystem.menuActivated)
		{
			playerAction((GameEntity)main.menuSystem.getSelected(), true);
		}
		else if (main.menuSystem.getSelected() == null)
		{
			TextBox b = main.menuSystem.menus.get(0).findButtonByCommand("markTile");
			if (b.active)
			{
				//b.activate(false);
			}
			else
			{
				b.active = true;
				b.posX = main.mouseX; b.posY = main.mouseY;
				b.origX = main.mouseX; b.origY = main.mouseY;
			}
		}
		else
		{
			main.menuSystem.menus.get(0).findButtonByCommand("markTile").active = false;
			main.menuSystem.closeMenus();
		}
	}

	//Simulate a turn for the player's units
	//advanceToNextUnit -> whether or not to select another unit
	//Don't advance if moving a stack
	private void playerAction(GameEntity en, boolean advanceToNextUnit)
	{
		Tile t = main.menuSystem.mouseHighlighted;
		if (en != null && t != null)
		{
			if (t.biome != -1 && en.owner != null) //Removing does not seem to clear from memory, check if owner is null then
			{
				String msg = en.playerWaddleToExact(t.row, t.col); //Returns null if cleared to move
				if (msg == null && en.action > 0)
				{
					en.playerTick();
					if (en.action <= 0)
					{
						if (advanceToNextUnit)
						{
							timeSelection();
							main.menuSystem.select(null);
						}
					}
					main.menuSystem.rbox = en.owner.revealedBox();
				}
				else
					main.menuSystem.message(msg);
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

	public void timeSelection()
	{
		main.menuSystem.textboxes.get(5).display.clear();
		main.menuSystem.textboxes.get(5).display.add(0, "...");
		main.menuSystem.textboxes.get(5).tooltip.set(0, "Please wait...");
		if (autoSelect)
			nextSelection = main.frameCount + time;
		else
			nextSelection = 0;
	}

	private void selectAvailableUnit()
	{
		BaseEntity en = availableUnit();
		if (en != null)
		{
			if (en instanceof City)
			{
				main.menuSystem.textboxes.get(5).display.clear();
				main.menuSystem.textboxes.get(5).display.add(0, "QUEUE PRODUCTION");
				main.menuSystem.textboxes.get(5).tooltip.set(0, "A city needs orders to produce something.");
				City c = (City)en;
				main.fixCamera(c.location.row, c.location.col);
				main.menuSystem.select(c);
				main.menuSystem.updateCity(c);
			}
			else
			{
				main.menuSystem.textboxes.get(5).display.clear();
				main.menuSystem.textboxes.get(5).display.add(0, "A UNIT NEEDS ORDERS");
				main.menuSystem.textboxes.get(5).display.add("PRESS SPACE");
				main.menuSystem.textboxes.get(5).tooltip.set(0, "Please order your unit.");
				main.fixCamera(en.location.row, en.location.col);
				main.menuSystem.select(en);
				//main.menuSystem.message(en.name + " needs orders.");
			}
		}
		else
		{
			main.menuSystem.textboxes.get(5).display.clear();
			main.menuSystem.textboxes.get(5).display.add("NO UNITS NEED ORDERS");
			main.menuSystem.textboxes.get(5).display.add("PRESS SPACE TO ADVANCE");
			main.menuSystem.textboxes.get(5).tooltip.set(0, "Press SPACE.");
		}
	}

	//A comparator to sort units by distance
	//Thank you stackoverflow
	/*class GameEntityCompare implements Comparator<GameEntity> {
		private Tile t;
		public GameEntityCompare(Tile t) {this.t = t;}
	    public int compare(GameEntity o1, GameEntity o2) {
	        if (o1.location.dist(t) < o2.location.dist(t))
		        return -1;
	        else if (o1.location.dist(t) > o2.location.dist(t))
		        return 1;
	        return 0;
	    }
	}*/
	//Find the next unit with action and return it
	//If there are no available units, return null
	public BaseEntity availableUnit()
	{
		Civilization civ = main.grid.civs[0];
		ArrayList<GameEntity> candidates = new ArrayList<GameEntity>();
		Tile t = main.menuSystem.mouseHighlighted;
		for (int i = 0; i < civ.units.size(); i++)
		{
			GameEntity en = civ.units.get(i);
			if (en.action > 0 && en.queueTiles.size() == 0 && en.queue == null && en.queueTurns <= 0 && !en.sleep)
			{
				candidates.add(en);
				if (t == null) return candidates.get(0);
				//return en;
			}
		}
		//System.out.println("AAAA");
		if (candidates.size() > 0)
		{
			GameEntity en = candidates.get(0);
			for (int i = 0; i < candidates.size(); i++)
			{
				//System.out.println(candidates.get(i).location.dist(t) + " " + en.location.dist(t));
				if (candidates.get(i).location.dist(t) < en.location.dist(t))
				{
					en = candidates.get(i);
				}
			}
			return en;
		}
		for (int i = 0; i < civ.cities.size(); i++)
		{
			City c = civ.cities.get(i);
			if (c.queue == null && !c.sleep)
			{
				return c;
			}
		}
		return null;
	}

	public void executeAction(String action)
	{
		if (action.equals("ADVANCE_TURN"))
		{
			Civilization civ = main.grid.civs[0];
			BaseEntity selected = availableUnit();
			if (selected == null)
			{
				main.menuSystem.textboxes.get(5).display.clear();
				main.menuSystem.textboxes.get(5).display.add(0, "...");
				main.menuSystem.textboxes.get(5).tooltip.set(0, "Please wait...");
				if (civ.researchTech == null || civ.researchTech == "")
				{
					main.menuSystem.textboxes.get(5).display.clear();
					main.menuSystem.textboxes.get(5).display.add(0, "RESEARCH TECH");
					main.menuSystem.textboxes.get(5).tooltip.set(0, "Please research a technology.");
					/*main.menuSystem.displayTechMenu(civ);
					main.menuSystem.menus.get(5).activate(true);*/
					//Switch to new tech web
					main.menuSystem.techMenu.setupButtons();
					main.menuSystem.techMenu.activate(true);
					main.menuSystem.message("A tech is needed to research.");
					//main.menuSystem.menus.get(5).requestUpdate = true;
					return;
				}
				if (civ.observe || civ.units.size() > 0 || civ.cities.size() > 0)
					main.civilizationSystem.requestTurn = true;
				else
				{
					main.menuSystem.textboxes.get(5).display.clear();
					main.menuSystem.textboxes.get(5).display.add(0, "Press SPACE.");
					main.menuSystem.textboxes.get(5).tooltip.set(0, "");
					main.menuSystem.menus.get(6).activate(true);
					main.menuSystem.message("You have no cities or units!");
				}
			}
			else if (nextSelection != 0)
			{
				return;
			}
			else
			{
				main.menuSystem.selectAndFocus(selected);
			}
		}
		/*else if (key == 'c')
		{
			on = !on;
			main.resetCamera();
		}*/
		/*else if (key == 'f')
		{
			//main.resetCamera();
			main.centerX = main.width/2;
			main.centerY = main.height/2;
		}*/
		else if (action.equals("TOGGLE_MINIMAP"))
		{
			main.menuSystem.minimapMode++;
			if (main.menuSystem.minimapMode > 2)
				main.menuSystem.minimapMode = 0;
			main.menuSystem.rbox = main.grid.civs[0].revealedBox();
		}
		else if (action.equals("TOGGLE_FOG"))
		{
			main.showAll = !main.showAll;
		}
		else if (action.equals("TOGGLE_TACTICAL"))
		{
			main.tacticalView = !main.tacticalView;
			//main.menuSystem.menus.get(14).activate(main.tacticalView);
			main.chunkSystem.update(); //Update the fields menu
		}
		else if (action.contains("FUNCTION_"))
		{
			main.menuSystem.executeShortcut(Integer.parseInt(action.substring(9)));
		}
		else if (action.equals("TOGGLE_KEY_MENU"))
		{
			main.keyMenu = !main.keyMenu;
		}
		else if (main.menuSystem.minimapMode == 1)
		{
			if (action.equals("ZOOM_IN"))
			{
				if (main.menuSystem.sight > 3)
					main.menuSystem.sight--;
			}
			else if (action.equals("ZOOM_OUT"))
			{
				if (main.menuSystem.sight < 20)
					main.menuSystem.sight++;
			}
		}
		else if (action.equals("CLOSE_ALL"))
		{
			main.menuSystem.closeMenus();
		}
		else if (action.equals("CONSOLE"))
		{
			if (main.menuSystem.console == null)
				main.menuSystem.console = "";
		}
	}

	public void executeAction(char key)
	{
		String action = keyPressBinds.get(key);
		//if (action != null) {System.out.println(action);}
		if (main.menuSystem.console != null) //Give priority to toggling console
		{
			if (action != null)
			{
				if (action.equals("CONSOLE"))
				{
					main.menuSystem.console = null;
					return; //Do not add the tilde key to console
				}
			}
			if (key == main.BACKSPACE && !main.menuSystem.console.isEmpty())
				main.menuSystem.console = main.menuSystem.console.substring(0, main.menuSystem.console.length()-1);
			else
				main.menuSystem.console += key;
			return;
		}
		if (action == null) return;
		executeAction(action);
	}

}
