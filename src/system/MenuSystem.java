package system;

import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Tech;
import game.Tile;
import lwjglEngine.fontRendering.TextMaster;
import lwjglEngine.gui.GuiTexture;
import lwjglEngine.render.DisplayManager;
import lwjglEngine.render.Loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import menugame.Tutorial;
import data.EntityData;
import data.Improvement;
import processing.core.PFont;
import render.*;
import units.Caravan;
import units.City;
import units.Settler;
import units.Warrior;
import units.Worker;

public class MenuSystem extends BaseSystem {

	public ArrayList<Menu> menus;
	public TechMenu techMenu;
	public ArrayList<TextBox> textboxes;

	private ArrayList<Click> clicks;

	public int minimapMode = 0; // 0 -> off, 1 -> local, 2 -> global
	public boolean info; // loadout, loadoutDisplay, techMenu, continueMenu; //Access the menu's active
							// property instead
	public int multiplier = 1;
	public float highlightDispX = main.width / 2, highlightDispY = main.width / 2;
	public int sight = 7;

	public Tile target;
	// public ArrayList<String> hintText;
	public Tile highlighted;
	private Tile mouseHighlighted; // Under the player's crosshair versus under the player's mouse

	public Tile getMouseHighlighted() {
		return mouseHighlighted;
	}

	public Tile lastMouseHighlighted;
	public Tile lastHighlighted;
	private BaseEntity selected; // Selected by the player with the mouse explicitly
	public Tile[] settlerChoices;
	public List<Tile> movementChoices = new ArrayList<Tile>(), pathToHighlighted = new ArrayList<Tile>();
	public String typeOfLastSelected = "";

	// 2 click type interactions
	public String candidateField = "";
	public City candidateCityField = null;
	public ArrayList<GameEntity> stack = new ArrayList<GameEntity>(); // Select a stack of units to do an action

	// if non-null, console is on
	public String console = null;

	public int[] rbox;

	public Tooltip tooltip = new Tooltip("", 0, 0, 80, 20);
	public boolean[][] markedTiles;

	public Button[] shortcuts = new Button[10];
	public ArrayList<TextBox> noOverlap = new ArrayList<TextBox>();

	public boolean requestFieldsUpdate = false;

	public float height = 100;

	private Loader loader;
	private int guiDefaultTexture;

	public void setupLoader(Loader l) {
		loader = l;
		guiDefaultTexture = loader.loadTexture("guiDefaultTexture");
	} // Encapsulate so I know not to access this class for the loader

	// public City citySelected;

	// public TextBox hintTextBox;
	// public TextBox selectedTextBox;

	private ArrayList<String> messages;

	public MenuSystem(CivGame civGame) {
		super(civGame);

		menus = new ArrayList<Menu>();
		textboxes = new ArrayList<TextBox>();
		clicks = new ArrayList<Click>();

		// hintText = new ArrayList<String>();
		messages = new ArrayList<String>();
		// highlighted = null;

		// arial = main.loadFont("ArialMT-48.vlw");
	}

	public void setupMenus() {
		// Keep track of the menu's indices in list
		Menu menu0 = new Menu("MainMenu");
		menus.add(menu0);
		int height = 30;
		// menu0.addButton("exitgame", "Exit", "Exit this session of the game.",
		// main.width - 100, 0, 100, height).lock = true;
		menu0.addButton(guiDefaultTexture, "close", "Close All", "Close all open menus.", main.width - 100, 70, 100, height).lock = true;
		menu0.addButton(guiDefaultTexture, "minimap", "Minimap", "Open the minimap of the world.", main.width - 100, 100, 100,
				height).lock = true;
		menu0.addButton(guiDefaultTexture, "info", "Information", "", main.width - 100, 130, 100, height).lock = true;
		// menu0.buttons.add(new Button("loadout", "Loadout", "Change loadouts of
		// certain units.", main.width - 100, 160, 100, height, 3, 4));
		menu0.addButton(guiDefaultTexture, "loadout", "Loadout", "Change loadouts of certain units.", main.width - 100, 160, 100,
				height).lock = true;
		menu0.addButton(guiDefaultTexture, "stats", "Statistics", "Compare stats of different civilizations.", main.width - 100, 190, 100,
				height).lock = true;
		// menu0.addButton("techs", "Techs", "Choose technologies to research.",
		// main.width - 100, 220, 100, height).lock = true;
		menu0.addButton(guiDefaultTexture, "techsweb", "Techs", "Choose technologies to research.", main.width - 100, 220, 100,
				height).lock = true;
		menu0.addButton(guiDefaultTexture, "encyclopedia", "Reference", "A encyclopedia-like list of articles.", main.width - 100, 250,
				100, height).lock = true;
		menu0.addButton(guiDefaultTexture, "relations", "Relations", "The wars and alliances of this world.", main.width - 100, 280, 100,
				height).lock = true;
		menu0.addButton(guiDefaultTexture, "civic", "Civics", "Change the ideals of your government.", main.width - 100, 310, 100,
				height).lock = true;
		menu0.addButton(guiDefaultTexture, "log", "Messages", "", main.width * 3 / 4, 0, main.width * 1 / 4, height).lock = true;
		// menu0.addButton("log", "Messages", "View your messages.", main.width*3/4, 0,
		// main.width*1/4, height).lock = true;

		int pivot = menu0.buttons.size() * height;
		for (int i = 0; i < menu0.buttons.size() - 1; i++) {
			TextBox b = menu0.buttons.get(i);
			b.move(0, 70 + (i) * height);
		}

		TextBox b = menu0.addButton(guiDefaultTexture, "markTile", "MarkTile", "Mark this tile", main.width - 100, 70, 100, height);
		b.lock = true;
		b.activate(false);
		b.autoClear = false;

		Menu menu1 = new Menu("UnitMenu");
		menus.add(menu1);

		Menu menu2 = new Menu("CityMenu");
		menus.add(menu2);

		Menu menu3 = new Menu("LoadoutMenu");
		menus.add(menu3);
		String[] names = EntityData.allUnitNames();
		for (int i = 0; i < names.length; i++) {
			menu3.addButton(guiDefaultTexture, "loadoutDisplay" + names[i], names[i], "", 100, 160 + 30 * i, 200, 30);
		}

		Menu menu4 = new Menu("LoadoutDisplay");
		menus.add(menu4);

		Menu menu5 = new Menu("TechMenu");
		menus.add(menu5);

		Menu menu6 = new Menu("ContinueMenu"); // Menu when player loses the game
		menu6.addButton(guiDefaultTexture, "continue", "You have lost the game. Continue?", "", main.width * 2 / 6, 100,
				main.width * 2 / 6, 200);
		menus.add(menu6);

		Menu menu7 = new Menu("EncyclopediaMenu");
		TextBox temp = new TextBox(guiDefaultTexture, "", "", 100, 190, 700, 500); // "EncyclopediaText",
		// System.out.println("Found " + menu7.findButtonByCommand("EncyclopediaText"));
		temp.name = "EncyclopediaText";
		menu7.buttons.add(temp);
		menus.add(menu7);

		Menu menu8 = new Menu("DiplomacyMenu");
		menus.add(menu8);

		Menu menu9 = new Menu("TalkToCivMenu"); // For lack of a better name...
		menus.add(menu9);

		Menu menu10 = new Menu("Logs"); // For lack of a better name...
		menus.add(menu10);

		Menu menu11 = new Menu("RelationsMenu");
		menus.add(menu11);

		Menu menu12 = new Menu("CivicMenu");
		menus.add(menu12);

		Menu menu13 = new KeyMenu(main.inputSystem, "KeyMenu");
		menus.add(menu13);

		Menu menu14 = new Menu("TacticalMenu"); // rendered and added to in NewMenuSystem
		menu14.noShortcuts = true;
		menus.add(menu14);

		Menu menu15 = new Menu("FieldMenu"); // for displaying fields
		menu15.noShortcuts = true;
		menus.add(menu15);

		Menu menu16 = new Menu("CreateFieldMenu");
		menus.add(menu16);

		menu0.activate(true);

		TextBox text0 = new TextBox(guiDefaultTexture, "", "", main.width - 200, main.height - 250, 200,
				100); // "HintText"
		text0.color.w = 0;
		textboxes.add(text0);

		TextBox text1 = new TextBox(guiDefaultTexture, "", main.width - 400, main.height - 150, 200,
				150); // "SelectedText"
		textboxes.add(text1);

		TextBox text2 = new TextBox(guiDefaultTexture, "", main.width * 3 / 4, 30, main.width / 4, 100); // "Messages"
		textboxes.add(text2);

		TextBox text3 = new TextBox(guiDefaultTexture, "", main.width / 6, 0, 300, 50); // "PlayerStatus"
		textboxes.add(text3);

		TextBox text4 = new TextBox(guiDefaultTexture, "", 100, 190, 500, 250); // "LedgerText"
		textboxes.add(text4);

		TextBox text5 = new TextBox(guiDefaultTexture, "...", main.width - 400, main.height - 200 + 15,
				200, 35); // "ConditionText"
		// ArrayList<String> stringy = new ArrayList<String>(); stringy.add("...");
		// text5.display = stringy;
		text5.autoClear = false;
		textboxes.add(text5);

		TextBox text6 = new TextBox(guiDefaultTexture, "", main.width - 200, main.height - 150, 100,
				150); // "Detail1Text" (goes with HintText)
		textboxes.add(text6);

		TextBox text7 = new TextBox(guiDefaultTexture, "", main.width - 100, main.height - 150, 100,
				150); // "Detail2Text" (goes with HintText)
		text7.monospace = true;
		textboxes.add(text7);

		TextBox text8 = new TextBox(guiDefaultTexture, "", main.width / 6, 50, 300, 30);
		textboxes.add(text8);

		text4.activate(false);

		/*
		 * for (int i = 3; i <= 8; i++) { textboxes.get(i).activate(false); }
		 */

		updateEncyclopedia();

		for (int i = 0; i < textboxes.size(); i++) {
			TextBox t = textboxes.get(i);
			t.color.w = 100;
			t.noOverlap = true;
		}
		for (int i = 0; i < menus.size(); i++) {
			for (int j = 0; j < menus.get(i).buttons.size(); j++) {
				TextBox t = menus.get(i).buttons.get(j);
				if (i != 0)
					t.color.w = 100;
				t.noOverlap = true;
			}
		}
	}

	public PFont arial;

	public boolean menuActivated = false, menuHighlighted = false;

	public void tick() {
		for (int menu = 0; menu < menus.size(); menu++) {
			// if (!main.enabled) break;
			if (menus.get(menu).active()) {
				// System.out.println("Menu " + menu);
				for (int i = clicks.size() - 1; i >= 0; i--) {
					String command = menus.get(menu).click(clicks.get(i).mouseX, clicks.get(i).mouseY);
					if (command != null && !command.equals("")) {
						menuActivated = true;
						// Replace with function that returns true if the menu resetting should happen
						if (executeAction(command)) {
							main.resetCamera();
						}
					}
				}
			}
			if (menus.get(menu).active() && !menus.get(menu).noShortcuts)
				makeShortcut(menus.get(menu));
		}
		if (techMenu.active())
			makeShortcut(techMenu);
		clicks.clear();
	}

	// TODO: Possibly sort shortcuts; higher buttons get lower numbers for shortcuts
	public void makeShortcut(Menu menu) {
		int iter = 1;
		for (int i = 0; i < menu.buttons.size(); i++) {
			TextBox b = menu.buttons.get(i);
			if (b instanceof Button && b.shortcut) {
				shortcuts[iter] = (Button) b;
				if (iter == 9) // Loop from 1 to 9 to 0 for shortcut keys
					iter = 0;
				else if (iter == 0)
					break;
				else
					iter++;
			}
		}
	}

	public void displayMenu(int menu) {

	}

	public void forceUpdate() {
		TextMaster.update(this);
		main.lwjglSystem.renderer.guiRenderer.update(this);
	}

	public class Click {
		float mouseX, mouseY;
		boolean click;

		Click(boolean click, float x, float y) {
			this.click = click;
			mouseX = x;
			mouseY = y;
		}
	}

	public void queueClick(float mouseX, float mouseY) {
		clicks.add(0, new Click(true, mouseX, mouseY));
	}

	public void queueMousePass(float mouseX, float mouseY) {
		clicks.add(0, new Click(false, mouseX, mouseY));
	}

	public boolean executeAction(String command) {
		System.out.println("MenuSystem executed " + command);
		// closeMenus();
		if (command.equals(""))
			return true;
		GameEntity en = null;
		menuActivated = true;
		if (selected != null) {
			if (selected instanceof GameEntity)
				en = (GameEntity) selected;
		}
		if (command.contains("unit")) // Specific unit orders
		{
			if (selected == null)
				return false;
		}
		if (command.equals("exitgame")) {
			System.exit(0);
			return false;
		} else if (command.equals("close")) {
			// Replace with a loop later
			// done
			closeMenus();
			// select(null);
		} else if (command.equals("markTile")) {
			System.out.println("marked tile");
			if (mouseHighlighted != null)
				markedTiles[mouseHighlighted.row][mouseHighlighted.col] = !markedTiles[mouseHighlighted.row][mouseHighlighted.col];
			menus.get(0).findButtonByCommand("markTile").activate(false);
		} else if (command.equals("info") || command.equals("minimap") || command.equals("loadout")
				|| command.contains("loadoutDisplay") || command.equals("stats") || command.equals("continue")
				|| command.equals("techs") || command.equals("encyclopedia") || command.contains("diplomacy")
				|| command.equals("log") || command.equals("relations") || command.equals("civic")
				|| command.equals("techsweb")) {
			closeMenus();
			if (command.equals("info")) {
				info = !info;
			} else if (command.equals("minimap")) {
				minimapMode++;
				if (minimapMode > 2)
					minimapMode = 0;
			} else if (command.equals("loadout")) {
				/*
				 * if (menus.get(3).active) { menus.get(3).activate(false); }
				 * menus.get(4).active = !menus.get(4).active;
				 */
				menus.get(3).activate(true);
			} else if (command.contains("loadoutDisplay")) {
				// loadout = false;
				updateLoadoutDisplay(command.substring(14));
				menus.get(4).activate(true);
			} else if (command.equals("stats")) {
				updateCivStats();
				// ledgerMenu = true;
				textboxes.get(4).activate(true);
				menus.get(0).findButtonByCommand("stats").lock = textboxes.get(4).active;
			} else if (command.equals("continue")) {
				main.grid.civs[0].observe = true;
				menus.get(6).activate(false);
			} else if (command.equals("techs")) {
				displayTechMenu(main.grid.civs[0]);
				menus.get(5).activate(true);

				techMenu.setupButtons();
				techMenu.activate(true);
			} else if (command.equals("techsweb")) {
				techMenu.setupButtons();
				techMenu.activate(true);
			} else if (command.equals("encyclopedia")) {
				menus.get(7).activate(true);
			} else if (command.contains("diplomacy")) {
				menus.get(8).activate(false);
				menus.get(9).activate(true);
				Civilization civ = main.grid.civs[Integer.parseInt(command.substring(9))];
				updateDiplomacyMenu(civ);
			} else if (command.equals("log")) {
				textboxes.get(2).activate(false);
				menus.get(10).activate(true);
				updateMessages();
			} else if (command.equals("relations")) {
				menus.get(11).activate(true);
				pivot = main.grid.civs[0];
				updateRelations();
			} else if (command.equals("civic")) {
				menus.get(12).activate(true);
				updateCivicsMenu(main.grid.civs[0]);
			}
			resetAllButtons();
			return false;
		}

		else if (command.contains("encyclopedia")) // accessing an encyclopedia entry
		{
			ArrayList<String> text = EntityData.encyclopediaEntries.get(command.substring(12));
			TextBox textBox = (TextBox) menus.get(7).findButtonByName("EncyclopediaText");
			textBox.setDisplayText(text);
		}

		else if (command.contains("/")) // if it is a entity-improvement command
		{
			int index = command.indexOf("/");
			String unit = command.substring(0, index);
			for (int j = 0; j < main.grid.civs[0].cities.size(); j++) {
				City city = main.grid.civs[0].cities.get(j);
				if (city.queue != null) {
					if (city.queue.equals(unit)) {
						message("Cannot change production method of queued unit");
						return false;
					}
				}
			}
			message("Changed production method of " + unit);
			main.grid.civs[0].unitImprovements.put(unit,
					EntityData.unitImprovementMap.get(command.substring(index + 1)));
			menus.get(4).activate(false); // Allow player to stay in menu?
			return false;
		} else if (command.equals("buildFarm")) {
			// Recycled code
			if (en.location.resource == 1 || en.location.resource == 2) {
				EntityData.queueTileImprovement(en, "Farm");
			} else if (en.location.biome >= 3 && en.location.biome <= 6
					&& en.location.grid.irrigated(en.location.row, en.location.col)) {
				EntityData.queueTileImprovement(en, "Farm");
			}
			if (en.queue != null && !en.queue.isEmpty())
				en.queueTurns = Math.max(1, (int) (en.queueTurns * ((Worker) en).workTime));
		} else if (command.equals("buildMine")) {
			if (en.location.shape == 2) {
				EntityData.queueTileImprovement(en, "Mine");
			} else if (en.location.resource >= 20 && en.location.resource <= 22) {
				EntityData.queueTileImprovement(en, "Mine");
			} else if (en.location.shape == 1) {
				if (en.location.biome >= 0 && en.location.biome <= 3) {
					EntityData.queueTileImprovement(en, "Mine");
				}
			}
			if (en.queue != null && !en.queue.isEmpty())
				en.queueTurns = Math.max(1, (int) (en.queueTurns * ((Worker) en).workTime));
		} else if (command.equals("buildRoad")) {
			EntityData.queueTileImprovement(en, "Road");
		} else if (command.equals("unitKill")) {
			main.grid.removeUnit(selected);
		} else if (command.equals("unitMeleeMode")) {
			((GameEntity) selected).mode = 1;
			updateUnitMenu((GameEntity) selected);
		} else if (command.equals("unitRangedMode")) {
			((GameEntity) selected).mode = 2;
			updateUnitMenu((GameEntity) selected);
		} else if (command.equals("unitRaze")) {
			((Warrior) selected).raze();
			// ((Warrior)selected).action = 0;
			// selected.playerTick();
		} else if (command.equals("unitSettle")) {
			System.out.println(selected);
			if (selected != null)
				if (!((Settler) selected).settle())
					message("Cannot settle here.");
			select(null);
		} else if (command.equals("stack")) {
			if (stack.size() > 0)
				stack.clear();
			else {
				if (selected != null)
					for (int i = 0; i < selected.location.occupants.size(); i++) {
						GameEntity entity = selected.location.occupants.get(i);
						entity.sleep = false;
						stack.add(entity);
					}
			}
		} else if (command.equals("unitSleep")) {
			if (selected != null)
				selected.sleep = true;
			select(null);
		} else if (command.contains("unitCaravan")) {
			int index = Integer.parseInt(command.substring(7));
			((Caravan) selected).setRoute(selected.owner.cities.get(index));
		} else if (command.equals("unitSkipTurn")) {
			selected.action = 0;
		}

		else if (command.contains("queueBuilding")) {
			City city = ((City) selected);
			String impr = command.substring(13);
			// No need to check if the player's tech is appropriate
			System.out.println(impr);
			if (EntityData.queueCityImprovement(city, impr)) {
				message("Succesfully queued " + impr);
			} else {
				message("Could not queue " + impr);
			}
		} else if (command.contains("queue")) {
			// if (EntityData.queue((City)selected, command.substring(5)))
			if (EntityData.queue((City) selected, command.substring(5)) != null) {
				message("Succesfully queued " + command.substring(5));
			} else {
				message("Cannot queue units in a city being recently captured or razed");
			}
		} else if (command.contains("qfield")) {
			candidateField = command.substring(6);
			candidateCityField = (City) selected;
		} else if (command.equals("razeCity")) {
			((City) selected).raze = true;
		}

		else if (command.contains("fieldMenu")) {
			int index = command.indexOf(',');
			int r = Integer.parseInt(command.substring(9, index)), c = Integer.parseInt(command.substring(index + 1));
			// updateFieldMenu(main.grid.getTile(r,c));
		}
		/*
		 * else if (command.contains("editField")) { int n =
		 * Integer.parseInt(command.substring(9)); updateCreateFieldMenu(editingFields,
		 * n); } else if (command.contains("makeField")) { int index =
		 * command.indexOf(','); int n = Integer.parseInt(command.substring(9,index));
		 * Field f = EntityData.getField(command.substring(index+1)); editingFields =
		 * null; }
		 */
		/*
		 * else if (command.equals("queueSettler")) { ((City)selected).queue =
		 * "Settler"; ((City)selected).queueFood = 35; } else if
		 * (command.equals("queueWarrior")) { ((City)selected).queue = "Warrior";
		 * ((City)selected).queueFood = 5; ((City)selected).queueMetal = 5; } else if
		 * (command.equals("queueWorker")) { ((City)selected).queue = "Worker";
		 * ((City)selected).queueFood = 25; }
		 */
		// Researching tech commands
		else if (command.contains("research")) {
			Tech t = main.grid.civs[0].techTree.researched(command.substring(8));
			if (t.requisite != null && t.requisite.researched()) {
				main.grid.civs[0].researchTech = command.substring(8);
				menus.get(5).activate(false);
				techMenu.activate(false);
			} else if (t.alternative != null && t.alternative.researched()) {
				main.grid.civs[0].researchTech = command.substring(8);
				menus.get(5).activate(false);
				techMenu.activate(false);
			} else
				message(t.name + " is not unlocked.");
		}
		// Change a government or economic civic
		else if (command.contains("gCivic")) {
			String civic = command.substring(6);
			main.grid.civs[0].governmentCivic = civic;
			main.menuSystem.message("Changed form of government to " + civic);
		} else if (command.contains("eCivic")) {
			String civic = command.substring(6);
			main.grid.civs[0].economicCivic = civic;
			main.menuSystem.message("Changed economy to " + civic);
		}
		// The six commands below check to see if the number of idle people is more than
		// the requested number of specialized workers
		else if (command.equals("addAdmin")) {
			City s = ((City) selected);
			if (s.adm + s.art + s.sci + 1 <= s.population - 1)
				s.adm++;
		} else if (command.equals("addArtist")) {
			City s = ((City) selected);
			if (s.adm + s.art + s.sci + 1 <= s.population - 1)
				s.art++;
		} else if (command.equals("addSci")) {
			City s = ((City) selected);
			if (s.adm + s.art + s.sci + 1 <= s.population - 1)
				s.sci++;
		} else if (command.equals("subAdmin")) {
			City s = ((City) selected);
			if (s.adm > 0)
				s.adm--;
		} else if (command.equals("subArtist")) {
			City s = ((City) selected);
			if (s.art > 0)
				s.art--;
		} else if (command.equals("subSci")) {
			City s = ((City) selected);
			if (s.sci > 0)
				s.sci--;
		} else if (command.equals("sortie")) {
			City s = ((City) selected);
			s.sortie();
		} else if (command.equals("endSortie")) {
			City s = ((City) selected);
			s.endSortie();
		}

		// Diplomatic commands
		else if (command.contains("openBorders")) {
			Civilization a = main.grid.civs[0];
			Civilization b = main.grid.civs[Integer.parseInt(command.substring(11))];
			if (!a.isOpenBorder(b)) {
				a.openBorder(b);
				main.menuSystem.message("Requested open borders from " + b.name + ".");
			}
		} else if (command.contains("declareWar")) {
			Civilization a = main.grid.civs[0];
			Civilization b = main.grid.civs[Integer.parseInt(command.substring(10))];
			a.cancelDeals(b);
			a.war(b);
			main.menuSystem.message("You declared war on " + b.name + "!");
			closeMenus();
		} else if (command.contains("declarePeace")) {
			Civilization a = main.grid.civs[0];
			Civilization b = main.grid.civs[Integer.parseInt(command.substring(12))];
			a.peace(b);
			main.menuSystem.message("You made peace with " + b.name + "!");
			updateDiplomacyMenu(b);
		} else if (command.contains("ally")) {
			Civilization a = main.grid.civs[0];
			Civilization b = main.grid.civs[Integer.parseInt(command.substring(4))];
			if (a.opinions[b.id] >= 0 && !a.isWar(b) && !a.isAlly(b)) {
				a.ally(b);
				main.menuSystem.message("You have allied with " + b.name);
			} else {
				main.menuSystem.message("Your relations with this nation do not allow for an alliance.");
			}
		} else if (command.contains("pivot")) {
			pivot = main.grid.civs[Integer.parseInt(command.substring(5))];
			updateRelations();
		} else {
			System.out.println("Invalid or non-functioning command: " + command);
		}
		if (command.contains("build") || command.contains("unit") || command.contains("queue")) {
			main.menuSystem.select(null);
			// main.inputSystem.timeSelection();
		}
		return true;
	}

	public void executeShortcut(int n) {
		if (shortcuts[n] != null) {
			executeAction(shortcuts[n].command);
		}
	}

	public void closeMenus() {
		select(null);
		textboxes.get(2).activate(true);
		info = false;
		minimapMode = 0;
		menus.get(3).activate(false);
		menus.get(4).activate(false);
		textboxes.get(4).activate(false);
		menus.get(5).activate(false);
		for (int i = 7; i <= 13; i++)
			menus.get(i).activate(false);
		menus.get(15).activate(false);
		menus.get(16).activate(false);
		techMenu.activate(false);
		for (int i = 3; i <= 8; i++) {
			textboxes.get(i).active = false;
		}
		// Clear all but the main menu and encyclopedia
		// for (int i = 1; i < menus.size(); i++)
	}

	public TextBox findButtonWithin(float mouseX, float mouseY) {
		for (int i = 0; i < menus.size(); i++) {
			Menu m = menus.get(i);
			if (m.active()) {
				for (int j = 0; j < m.buttons.size(); j++) {
					TextBox b = m.within(mouseX, mouseY);
					if (b != null)
						return b;
				}
			}
		}
		return null;
	}

	// Send a message, checking for repeats
	public void message(String... newMessages) {
		for (int i = 0; i < newMessages.length; i++) {
			String message = newMessages[i];
			/*
			 * if (message.length() < 40) { if (messages.size() == 0) messages.add(message);
			 * if (!messages.get(0).equals(message)) messages.add(0,message); } else {
			 * messages.add(0,message.substring(40));
			 * messages.add(0,message.substring(0,40)); }
			 */
			if (message == null)
				continue;
			if (messages.size() == 0)
				messages.add(message);
			if (!messages.get(0).equals(message))
				messages.add(0, message);
		}
	}

	// Show all the messages on the menu with index 10
	public void updateMessages() {
		menus.get(10).buttons.clear();
		for (int i = 0; i < messages.size(); i++) {
			TextBox msg = new TextBox(loader.loadTexture("partTexture"), messages.get(i), main.width * 4.5F / 6,
					30 + 14 * i, main.width * 1.5F / 6, 14);
			menus.get(10).buttons.add(msg);
			if (i == 19)
				break;
		}
	}

	public void resetAllButtons() {
		for (int i = 0; i < menus.size(); i++) {
			for (int j = 0; j < menus.get(i).buttons.size(); j++) {
				TextBox b = menus.get(i).buttons.get(j);
				if (b.autoClear) {
					b.orderOriginal();
				}
			}
		}
	}

	// Will always refer to the player's tech tree
	public void displayTechMenu(Civilization civ) {
		menus.get(5).activate(true);
		menus.get(5).buttons.clear();

		ArrayList<String> techNames = civ.techTree.findCandidates();
		float disp = techNames.size() * 30;
		for (int i = 0; i < techNames.size(); i++) {
			String s = techNames.get(i);
			Tech t = civ.techTree.researched(s);
			int turns = calcQueueTurnsTech(civ, t);
			String name = turns != -1 ? s + " <" + turns + ">" : s + " <N/A>";
			Button b = (Button) menus.get(5).addButton(guiDefaultTexture, "research" + s, name, "Research " + s + ".", 0,
					main.height * 5 / 6 - disp + 30 * i, main.width * 1 / 6, 30);
			b.lock = true;
			b.clearTooltipText();
			if (turns != -1)
				b.addTooltipText("Estimated research time: " + turns + " turns");
			else
				b.addTooltipText("Estimated research time: N/A");
			b.addTooltipText(t.totalR + " research out of " + t.requiredR + "; "
					+ (int) ((float) t.totalR / (float) t.requiredR * 100) + "%");
			b.addTooltipText("Requires " + t.requisite.name);
			String techString = "";
			for (int j = 0; j < t.techs.length; j++)
				techString += t.techs[j].name + ", ";
			if (t.techs.length != 0)
				b.addTooltipText("Leads to " + techString.substring(0, techString.length() - 2));
			b.addTooltipText("Unlocks " + t.unlockString());
			// menus.get(5).addButton("research" + s, s, "", main.width/3F,
			// (float)main.height*2F/6F + 60*i, 200, 50);
		}
	}

	/*
	 * public void displayCity(City citySelected) { //Selection vs highlight if
	 * (citySelected.equals(selected)) { menus.get(2).activate(true); }
	 * 
	 * ArrayList<String> temp = textboxes.get(1).display; temp.add(citySelected.name
	 * + "; Population: " + citySelected.population); if (citySelected.takeover > 0)
	 * { main.fill(255,0,0); if (citySelected.takeover == 1)
	 * temp.add("IN RESISTANCE FOR 1 TURN."); else temp.add("IN RESISTANCE FOR " +
	 * citySelected.takeover + " TURNS."); main.fill(255); } temp.add("Health: " +
	 * citySelected.health + ", Happiness: " + citySelected.happiness);
	 * temp.add("Culture: " + citySelected.culture); temp.add("Administrators: " +
	 * citySelected.adm + ", Artists: " + citySelected.art); temp.add("Scientists: "
	 * + citySelected.sci); String buildingString = ""; if
	 * (citySelected.buildings.size() > 0) { for (int i = 0; i <
	 * citySelected.buildings.size(); i++) buildingString +=
	 * citySelected.buildings.get(i).name + ", "; buildingString =
	 * buildingString.substring(0,buildingString.length()-2); //Remove a trailing
	 * comma } else buildingString = "No buildings."; temp.add(buildingString); if
	 * (citySelected.queueFood > 0 || citySelected.queueMetal > 0) {
	 * temp.add(calcQueueTurns(citySelected)); } else { temp.add("Nothing queued.");
	 * } }
	 */

	public static int calcQueueTurnsInt(City citySelected, String name) {
		int[] t = citySelected.quickEval();
		float[] cost = EntityData.getCost(name);
		// Division by zero errors
		if ((t[0] == 0 && cost[0] > 0) || (t[2] == 0 && cost[2] > 0) || (t[0] == 0 && t[2] == 0))
			return -1;
		else {
			// System.out.println(t[0] + " " + t[2]);
			float turns;
			if (t[0] == 0)
				turns = cost[2] / (t[2]) + 1;
			else if (t[2] == 0)
				turns = cost[0] / (t[0]) + 1;
			else {
				turns = Math.max(cost[0] / (t[0]) + 1, cost[2] / (t[2]) + 1);
			}
			return (int) turns;
		}
	}

	public static int calcQueueTurnsTech(Civilization civ, Tech tech) {
		int research = 0;
		for (int i = 0; i < civ.cities.size(); i++) {
			int[] t = civ.cities.get(i).quickEval();
			research += t[3];
		}
		if (research == 0)
			return -1;
		return (int) ((tech.requiredR - tech.totalR) / (float) research + 1);
	}

	public String calcQueueTurns(City citySelected) {
		int[] t = citySelected.quickEval();
		// Division by zero errors
		if (t[0] == 0 && citySelected.queueFood > 0) {
			return new String("No food production, will not finish.");
		} else if (t[2] == 0 && citySelected.queueMetal > 0) {
			return new String("No metal production, will not finish.");
		} else if (t[0] == 0 && t[2] == 0) {
			// return new String("Neither food nor metal production");
			return new String("Will not finish.");
		} else {
			// System.out.println(t[0] + " " + t[2]);
			int turns;
			if (t[0] == 0) {
				turns = citySelected.queueMetal / (t[2]) + 1;
			} else if (t[2] == 0) {
				turns = citySelected.queueFood / (t[0]) + 1;
			} else {
				turns = Math.max(citySelected.queueFood / (t[0]) + 1, citySelected.queueMetal / (t[2]) + 1);
			}
			// English grammar...
			if (turns == 1)
				return new String("Queued " + citySelected.queue + " for " + turns + " turn.");
			else
				return new String("Queued " + citySelected.queue + " for " + turns + " turns.");
		}
	}

	// Update the ledger
	public void updateCivStats() {
		textboxes.get(4).clearDisplayText();
		textboxes.get(4).addDisplayText("You:");
		Civilization c = main.grid.civs[0];
		String s = c.name + "; Health: " + c.health + "; Gold: " + c.gold + "; Research: " + c.research;
		textboxes.get(4).addDisplayText(s);
		textboxes.get(4).addDisplayText("");

		textboxes.get(4).addDisplayText("Civilizations:");
		// Menu 8 buttons were moved to menu 11
		// menus.get(8).activate(false);
		for (int i = 1; i < main.grid.civs.length; i++) {
			c = main.grid.civs[i];
			s = c.name + "; Health: " + c.health + "; Gold: " + c.gold + "; Research: " + c.research + "; Relations: "
					+ main.grid.civs[0].opinions[i];
			textboxes.get(4).addDisplayText(s);
			// menus.get(8).addButton("diplomacy"+i, "Talk", "Conduct diplomacy with " +
			// c.name + ".", 600, 190+60+15*(i-1), 90, 15);
		}
		textboxes.get(4).move(textboxes.get(4).pixelPos.x, (main.grid.civs.length - 1 + 4) * 15 + 15);
		// menus.get(8).activate(true);
		// 100,190,500,250
	}

	// TODO: Battlefield perspective
	/*
	 * public void updateBattlePerspective() {
	 * 
	 * }
	 */

	// Choose which buttons to show depending on unit (e.g. only settler can settle)
	public void updateUnitMenu(GameEntity en) {
		float height = 20;

		menus.get(1).buttons.clear();

		if (en == null) {
			menus.get(1).activate(false);
			return;
		}

		// int n = 0;
		menus.get(1).addButton(guiDefaultTexture, "unitKill", "Destroy", "Destroy this unit.", 0, main.height * 5 / 6 + 30,
				main.width * 1 / 6, 30);
		menus.get(1).addButton(guiDefaultTexture, "unitSkipTurn", "Skip Turn", "Do nothing this turn.", 0, main.height * 5 / 6 + 30,
				main.width * 1 / 6, 30);
		menus.get(1).addButton(guiDefaultTexture, "unitSleep", "Sleep", "This unit will be inactive until you select it again.", 0,
				main.height * 5 / 6 + 30, main.width * 1 / 6, 30);
		if (stack.size() == 0)
			menus.get(1).addButton(guiDefaultTexture, "stack", "Create Stack", "Group a set of units together that can be moved.", 0,
					main.height * 5 / 6 + 30, main.width * 1 / 6, 30);
		else
			menus.get(1).addButton(guiDefaultTexture, "stack", "Separate Stack", "Make multiple units out of the stack.", 0,
					main.height * 5 / 6 + 30, main.width * 1 / 6, 30);

		if (en.name.equals("Settler")) {
			menus.get(1).addButton(guiDefaultTexture, "unitSettle", "Settle", "Settle a city here.", 0, main.height * 5 / 6 + 30,
					main.width * 1 / 6, 30);

		} else if (en.name.equals("Warrior")) {
			menus.get(1).addButton(guiDefaultTexture, "unitRaze", "Attack", "Attack the improvement here.", 0, main.height * 5 / 6 + 30,
					main.width * 1 / 6, 30);

		} else if (en.name.equals("Worker")) {
			ArrayList<String> units = main.grid.civs[0].techTree.allowedTileImprovements;
			for (int i = 0; i < units.size(); i++) {
				Button b = (Button) menus.get(1).addButton(guiDefaultTexture, "build" + units.get(i), units.get(i),
						"Construct " + units.get(i) + " here.", 0, main.height * 5 / 6 + 30, main.width * 1 / 6, 30);
				b.clearTooltipText();
				int turns = EntityData.tileImprovementTime(en, units.get(i));
				if (turns != -1)
					b.addTooltipText("Estimated build time: " + turns + " turns");
				else
					b.addTooltipText("Estimated build time: N/A");
				double[] yieldBefore = City.staticEval(en.location),
						yieldAfter = City.staticEval(en.location, units.get(i));
				double[] temp = new double[] { yieldAfter[0] - yieldBefore[0], yieldAfter[1] - yieldBefore[1],
						yieldAfter[2] - yieldBefore[2], yieldAfter[3] - yieldBefore[3] };
				String[] names = new String[] { "food", "gold", "metal", "research" };
				for (int j = 0; j < temp.length; j++) {
					if (temp[j] != 0) {
						if (temp[j] > 0)
							b.addTooltipText("+" + temp[j] + " " + names[j]);
						else
							b.addTooltipText("-" + temp[j] + " " + names[j]);
					}
				}
			}
			if (!en.location.road) {
				Button b = (Button) menus.get(1).addButton(guiDefaultTexture, "buildRoad", "Road",
						"Construct a road, to expand your civilization's network.", 0, main.height * 5 / 6 + 30,
						main.width * 1 / 6, 30);
				b.addTooltipText("Roads allow for increased movement,");
				b.addTooltipText("and connect resources and cities.");
				b.dimTooltip();
			}
			// menus.get(1).addButton("buildfarm", "Farm", (float)main.width/3F + 60,
			// (float)main.height*5F/6F, 50, 50);
			// menus.get(1).addButton("buildmine", "Mine", (float)main.width/3F + 120,
			// (float)main.height*5F/6F, 50, 50);
		} else if (en.name.equals("Caravan")) {
			for (int i = 0; i < en.owner.cities.size(); i++) {
				City c = en.owner.cities.get(i);
				if (!c.equals(((Caravan) en).home)) {
					menus.get(1).addButton(guiDefaultTexture, "unitCaravan" + i, "Caravan" + c.name, "Establish a trade route.", 0,
							main.height * 5 / 6 + 30, main.width * 1 / 6, 30);

				}
			}
		}

		if (en.mode == 1 && en.rangedStr > 0) {
			menus.get(1).addButton(guiDefaultTexture, "rangedMode", "Ranged", "Allow this unit to use ranged attacks.", 0,
					main.height * 5 / 6 + 30, main.width * 1 / 6, 30);

		} else if (en.mode == 2 && en.offensiveStr > 0) {
			menus.get(1).addButton(guiDefaultTexture, "meleeMode", "Melee", "Allow this unit to use melee attacks.", 0,
					main.height * 5 / 6 + 30, main.width * 1 / 6, 30);

		}

		for (int i = 0; i < menus.get(1).buttons.size(); i++) {
			TextBox b = menus.get(1).buttons.get(i);
			b.move(b.pos.x, main.height * 5 / 6 - (menus.get(1).buttons.size() + 2) * height + i * height); // Shift the
																											// buttons
																											// to their
																											// proper
																											// place
			b.resize(150, height);

			b.origPos.x = b.pixelPos.x;
			b.origPos.y = b.pixelPos.y;
			// b.origSizeX = b.sizeX; b.origSizeY = b.sizeY;

			/*
			 * b.origSizeX = 150; b.origSizeY = height; b.origX = b.posX; b.origY = b.posY;
			 */
		}

		/*
		 * ImageBox img = new
		 * ImageBox(loader.loadTexture(en.name),"",0,main.height*5/6,main.height/6,main.
		 * height/6); img.tint(en.owner.r, en.owner.g, en.owner.b);
		 * menus.get(1).buttons.add(img);
		 */

		// TODO: Add encyclopedia entries
		// TextBox b = menus.get(1).addButton("encyclopedia"+en.name, en.name,
		// "Encyclopedia entry for "+en.name+"
		// >",0,main.height*5/6-height,main.height/6,height);
		TextBox b = menus.get(1).addButton(guiDefaultTexture, "", en.name, "Encyclopedia entry for " + en.name + " >", 0,
				main.height * 5 / 6 - height, main.height / 6, height);
		b.shortcut = false;

		menus.get(1).activate(true);
		// System.out.println(menus.get(1).buttons.size());
	}

	// Choose which builds to allow i.e. which can be queued up in the city (factor
	// in techs later)
	public void updateCity(City c) {
		menus.get(2).buttons.clear();
		if (c == null) {
			menus.get(2).activate(false);
			return;
		}

		TextBox button = menus.get(2).addButton(guiDefaultTexture, "unitSleep", "Sleep",
				"Do not queue and produce anything with this city.", 0, 0, 0, 0);
		button.addTooltipText("Not recommended.");
		if (c.takeover > 0) {
			menus.get(2).addButton(guiDefaultTexture, "razeCity", "Raze", "Destroy the city, one citizen at a time.", main.width / 3F,
					(float) main.height * 5F / 6F + 60, 50, 50);
		}

		float height = 20;
		float disp = c.owner.techTree.allowedUnits.size() + c.owner.techTree.allowedCityImprovements.size() + 1;
		disp *= height;

		ArrayList<String> units = c.owner.techTree.allowedUnits;
		for (int i = 0; i < units.size(); i++) {
			unitButton(c, units.get(i), true);
		}

		ArrayList<String> buildings = c.owner.techTree.allowedCityImprovements(c);
		for (int i = 0; i < buildings.size(); i++) {
			buildingButton(c, buildings.get(i), true);
		}

		ArrayList<String> fields = c.owner.techTree.allowedFields;
		for (int i = 0; i < fields.size(); i++) {
			fieldButton(c, fields.get(i), true);
		}

		ArrayList<String> potential = c.owner.techTree.findUnlockables();
		for (int i = 0; i < potential.size(); i++) {
			BaseEntity example = EntityData.get(potential.get(i));
			Improvement impr = EntityData.cityImprovementMap.get(potential.get(i));
			Improvement impr2 = EntityData.getField(potential.get(i));
			if (example != null && EntityData.tileEntityMap.get(potential.get(i)) == null) // Make sure it's not a farm
																							// or something
			{
				unitButton(c, potential.get(i), false);
			} else if (impr != null) {
				buildingButton(c, potential.get(i), false);
			} else if (impr2 != null) {
				fieldButton(c, potential.get(i), false);
			} else {
				// do nothing
			}
		}
		// menus.get(2).addButton("queueSettler", "Settler", main.width/3F,
		// (float)main.height*5F/6F, 50, 50);
		// menus.get(2).addButton("queueWorker", "Worker", main.width/3F + 60,
		// (float)main.height*5F/6F, 50, 50);
		// menus.get(2).addButton("queueWarrior", "Warrior", main.width/3F + 120,
		// (float)main.height*5F/6F, 50, 50);

		menus.get(2).addButton(guiDefaultTexture, "addAdmin", "Admin+", "Convert one citizen to admin.", main.width / 6F,
				(float) main.height * 5F / 6F, 50, 50).shortcut = false;
		menus.get(2).addButton(guiDefaultTexture, "subAdmin", "Admin-", "Revert one admin to citizen.", main.width / 6F,
				(float) main.height * 5F / 6F + 60, 50, 50).shortcut = false;
		menus.get(2).addButton(guiDefaultTexture, "addArtist", "Artist+", "Convert one citizen to artist.", main.width / 6F + 60,
				(float) main.height * 5F / 6F, 50, 50).shortcut = false;
		menus.get(2).addButton(guiDefaultTexture, "subArtist", "Artist-", "Revert one artist to citizen.", main.width / 6F + 60,
				(float) main.height * 5F / 6F + 60, 50, 50).shortcut = false;
		menus.get(2).addButton(guiDefaultTexture, "addSci", "Sci+", "Convert one citizen to scientist.", main.width / 6F + 120,
				(float) main.height * 5F / 6F, 50, 50).shortcut = false;
		menus.get(2).addButton(guiDefaultTexture, "subSci", "Sci-", "Revert one scientist to citizen.", main.width / 6F + 120,
				(float) main.height * 5F / 6F + 60, 50, 50).shortcut = false;

		if (c.sortie == 1) {
			menus.get(2).addButton(guiDefaultTexture, "sortie", "Sortie", "Raise a temporary garrison (cannot leave borders).",
					main.width / 6F - 60, (float) main.height * 5F / 6F, 50, 50);
		} else if (c.sortie == 2) {
			menus.get(2).addButton(guiDefaultTexture, "endSortie", "End sortie", "End the sortie and return troops to city.",
					main.width / 6F - 60, (float) main.height * 5F / 6F, 50, 50);
		}

		int n = menus.get(2).buttons.size();
		for (int i = 0; i < n; i++) {
			TextBox b = menus.get(2).buttons.get(i);
			b.move(0, main.height * 5 / 6 + i * height - (n * 1 + 2) * height); // Shift the buttons to their proper
																				// place
			b.resize(150, height);

			b.origPos.x = b.pixelPos.x;
			b.origPos.y = b.pixelPos.y;
			// b.origSizeX = b.sizeX; b.origSizeY = b.sizeY;
		}
		/*
		 * for (int i = 0; i < n; i++) { TextBox b = menus.get(2).buttons.get(i);
		 * b.move(150 + 50*i, main.height - 50); //Shift the buttons to their proper
		 * place b.origX = b.posX; b.origY = b.posY; b.sizeX = 50; b.sizeY = 50;
		 * b.origSizeX = b.sizeX; b.origSizeY = b.sizeY; }
		 */

		menus.get(2).buttons.add(
				new TextBox(loader.loadTexture("partTexture"), c.name, 0, main.height * 5 / 6 - height, 150, height));

		double[] data = EntityData.calculateYield(c);
		TextBox t = new TextBox(loader.loadTexture("partTexture"), "Food per turn: " + (int) Math.floor(data[0]), 0,
				main.height * 5 / 6, 150, main.height * 1 / 6);
		t.addDisplayText("Gold per turn: " + (int) Math.floor(data[1]));
		t.addDisplayText("Metal per turn: " + (int) Math.floor(data[2]));
		t.addDisplayText("Research per turn: " + (int) Math.floor(data[3]));
		menus.get(2).buttons.add(t);

		menus.get(2).activate(true);
	}

	private void unitButton(City c, String s, boolean enabled) {
		int turns = calcQueueTurnsInt(c, s);
		String name = turns != -1 ? s + " <" + turns + ">" : s + " <N/A>";
		Button b = (Button) menus.get(2).addButton(guiDefaultTexture, "queue" + s, name, "", 0, 0, 0, 0);
		b.clearTooltipText();
		if (!enabled) {
			b.command = "";
			b.color.w = 100;
			b.shortcut = false;
			b.addTooltipText("Unlocked by " + c.owner.techTree.unlockedBy(s));
		}
		// b.addTooltipText("Estimated build time: " + calcQueueTurnsInt(c, units.get(i)) +
		// " turns");
		b.addTooltipText("Queue a " + s + ".");
		if (turns != -1)
			b.addTooltipText("Estimated build time: " + turns + " turns");
		else
			b.addTooltipText("Estimated build time: N/A");

		float[] cost = EntityData.getCost(s);
		b.addTooltipText("Requires " + cost[0] + " food");
		b.addTooltipText("Requires " + cost[2] + " metal");
		// System.out.println(s + ": " + cost[0] + " " + cost[2]);

		GameEntity example = (GameEntity) EntityData.get(s);
		b.addTooltipText("Offensive strength: " + example.offensiveStr);
		b.addTooltipText("Defensive strength: " + example.defensiveStr);
		b.addTooltipText("Ranged strength: " + example.rangedStr);
		// menus.get(2).addButton("queue" + units.get(i), units.get(i), "", 0,
		// main.height*5/6 - disp + 30*i, main.width*1/6, 30);
	}

	private void buildingButton(City c, String s, boolean enabled) {
		int turns = calcQueueTurnsInt(c, s);
		String name = turns != -1 ? s + " <" + calcQueueTurnsInt(c, s) + ">" : s + " <N/A>";
		Button b = (Button) menus.get(2).addButton(guiDefaultTexture, "queueBuilding" + s, name, "", 0, 0, 0, 0);
		b.clearTooltipText();
		if (!enabled) {
			b.command = "";
			b.color.w = 100;
			b.shortcut = false;
			b.addTooltipText("Unlocked by " + c.owner.techTree.unlockedBy(s));
		}
		b.addTooltipText("Queue a " + s + ".");
		// b.addTooltipText(calcQueueTurns(c));
		if (turns != -1)
			b.addTooltipText("Estimated build time: " + turns + " turns");
		else
			b.addTooltipText("Estimated build time: N/A");

		Improvement impr = EntityData.cityImprovementMap.get(s);
		float[] cost = EntityData.getCost(s);
		b.addTooltipText(impr.tooltip);
		b.addTooltipText("Requires " + cost[0] + " food");
		b.addTooltipText("Requires " + cost[2] + " metal");
		/*
		 * menus.get(2).addButton("queueBuilding" + s, s, "", 0, main.height*5/6 - disp
		 * + 30*(i+c.owner.techTree.allowedUnits.size()), main.width*1/6, 30);
		 */
	}

	private void fieldButton(City c, String s, boolean enabled) {
		int turns = calcQueueTurnsInt(c, s);
		Button b = (Button) menus.get(2).addButton(guiDefaultTexture, "qfield" + s, "F: " + s + " <" + calcQueueTurnsInt(c, s) + ">", "",
				0, 0, 0, 0);
		b.clearTooltipText();
		// b.addTooltipText(calcQueueTurns(c));
		if (!enabled) {
			b.command = "";
			b.color.w = 100;
			b.shortcut = false;
			b.addTooltipText("Unlocked by " + c.owner.techTree.unlockedBy(s));
		}
		b.addTooltipText("Add a " + s + " field.");
		if (turns != -1)
			b.addTooltipText("Estimated build time: " + turns + " turns");
		else
			b.addTooltipText("Estimated build time: N/A");

		Improvement impr = EntityData.getField(s);
		b.addTooltipText(impr.tooltip);
		b.addTooltipText("Requires " + (int) impr.foodFlat + " food");
		b.addTooltipText("Requires " + (int) impr.metalFlat + " metal");
		b.addTooltipText("Requires " + (int) impr.goldFlat + " gold");
		/*
		 * menus.get(2).addButton("queueBuilding" + buildings.get(i), buildings.get(i),
		 * "", 0, main.height*5/6 - disp + 30*(i+c.owner.techTree.allowedUnits.size()),
		 * main.width*1/6, 30);
		 */
	}

	/*
	 * public void updateCreateFieldMenu(Tile t, int n) {
	 * menus.get(16).buttons.clear(); closeMenus(); menus.get(16).activate(true);
	 * 
	 * ArrayList<String> fields = main.grid.civs[0].techTree.allowedFields; for (int
	 * i = 0; i < fields.size(); i++) { Field f =
	 * EntityData.getField(fields.get(i)); TextBox b =
	 * menus.get(16).addButton("makeField"+n+","+f.name, f.name, "", 0, 0, 0, 0);
	 * //b.clearTooltipText(); b.tooltip = new ArrayList<String>();
	 * b.addTooltipText(f.name + ""); b.addTooltipText(f.tooltip + "");
	 * b.addTooltipText("Costs " + f.foodFlat + " F, " + f.goldFlat + " G, " +
	 * f.metalFlat + " M"); //System.out.println("Tooltip: " + b.tooltip.get(1));
	 * //b.dimTooltip(); }
	 * 
	 * for (int i = 0; i < menus.get(16).buttons.size(); i++) { TextBox b =
	 * menus.get(16).buttons.get(i); b.move(0, main.height*5/6 + i*30 -
	 * (menus.get(16).buttons.size()+1)*30); //Shift the buttons to their proper
	 * place b.origX = b.posX; b.origY = b.posY; b.sizeX = 100; b.sizeY = 30;
	 * b.origSizeX = b.sizeX; b.origSizeY = b.sizeY; //b.dimTooltip(); } }
	 */

	// private Tile editingFields; //The tile that the player wants to improve
	/*
	 * public void updateFieldMenu(Tile t) { if (t.maxFields == 0) return;
	 * closeMenus(); menus.get(15).buttons.clear(); menus.get(15).activate(true);
	 * //editingFields = t; //Shortcuts are turned off for this menu for (int i = 0;
	 * i < t.maxFields; i++) { Field f = null; if (i < t.fields.size()) f =
	 * t.fields.get(i); if (f == null) menus.get(15).addButton("editField"+i,
	 * "Add field", "There is no field built here. Add a new one.", i*150, 0, 100,
	 * 30); else TextBox b = new TextBox("", "", i*150, 30, 150, 100); b.display =
	 * new ArrayList<String>(); if (f != null) { if (f.owner != null)
	 * b.addDisplayText(f.name + " (" + f.owner.name + ")"); else b.addDisplayText(f.name
	 * + " (unowned)"); } else { b.addDisplayText("No field"); }
	 * menus.get(15).buttons.add(b); } for (int i = 0; i <
	 * menus.get(15).buttons.size(); i++) { TextBox b =
	 * menus.get(15).buttons.get(i); b.move(b.posX + main.mouseX, b.posY +
	 * main.mouseY); //Shift the buttons to their proper place with respect to mouse
	 * b.origX = b.posX; b.origY = b.posY; //b.sizeX = 100; b.sizeY = 30;
	 * //b.origSizeX = b.sizeX; b.origSizeY = b.sizeY; } }
	 */

	public void updateLoadoutDisplay(String name) {
		menus.get(4).buttons.clear();
		BaseEntity en = EntityData.get(name);
		ArrayList<Improvement> valid = EntityData.getValidImprovements(main.grid.civs[0], en);
		for (int i = 0; i < valid.size(); i++) {
			Improvement temp = valid.get(i);
			menus.get(4).addButton(guiDefaultTexture, en.name + "/" + temp.name, temp.name, "", main.width / 3F,
					(float) main.height * 2F / 6F + 60 * i, 200, 50);
		}
	}

	public void updateDiplomacyMenu(Civilization civ) {
		Civilization plr = main.grid.civs[0];
		menus.get(9).buttons.clear();

		TextBox text0 = new TextBox(loader.loadTexture("partTexture"), "", main.width * 2 / 6, main.height * 2 / 6,
				main.width * 2 / 6, main.height / 12); // "HintText"
		text0.addDisplayText(civ.name);

		menus.get(9).addButton(guiDefaultTexture, "openBorders" + civ.id, "Request open borders.",
				"Allow unrestricted travel between you and this nation.", main.width * 2 / 6,
				main.height * 2 / 6 + main.height / 12 + 10, main.width * 2 / 6, main.height / 24);

		if (!plr.isWar(civ)) {
			menus.get(9).addButton(guiDefaultTexture, "declareWar" + civ.id, "Declare war.",
					"Declare war on this civilization (and cancel all deals).", main.width * 2 / 6,
					main.height * 2 / 6 + main.height / 12 + main.height / 24 + 20, main.width * 2 / 6,
					main.height / 24);
		} else {
			menus.get(9).addButton(guiDefaultTexture, "declarePeace" + civ.id, "Declare peace.", "Negotiate peace with this nation.",
					main.width * 2 / 6, main.height * 2 / 6 + main.height / 12 + main.height / 24 + 20,
					main.width * 2 / 6, main.height / 24);
		}

		if (!plr.isAlly(civ))
			menus.get(9).addButton(guiDefaultTexture, "ally" + civ.id, "Request an alliance.",
					"Request a mutual protection and aggression between you and this nation.", main.width * 2 / 6,
					main.height * 2 / 6 + main.height / 12 + 2 * main.height / 24 + 30, main.width * 2 / 6,
					main.height / 24);

		menus.get(9).buttons.add(text0);
	}

	private Civilization pivot; // The civilization that the relations menu will "focus" on

	public void updateRelations() {
		menus.get(11).buttons.clear();

		// Top set
		int width = 60, width2 = 120;
		TextBox text = new TextBox(guiDefaultTexture, "Opinion",
				"Your relations with this nation (-200 to 200).", 100 + width2, 255, width, 20);
		menus.get(11).buttons.add(text);
		text = new TextBox(guiDefaultTexture, "Border", "Your ability to access this nation's lands.",
				100 + width2 + width, 255, width, 20);
		menus.get(11).buttons.add(text);
		text = new TextBox(guiDefaultTexture, "War",
				"The formal declaration of hostility between you and this nation.", 100 + width2 + width * 2, 255,
				width, 20);
		menus.get(11).buttons.add(text);
		text = new TextBox(guiDefaultTexture, "Ally",
				"The existence of a formal alliance between you and this nation.", 100 + width2 + width * 3, 255, width,
				20);
		menus.get(11).buttons.add(text);

		for (int i = 0; i < main.grid.civs.length; i++) {
			Civilization civ = main.grid.civs[i];

			Button b = new Button(guiDefaultTexture, "pivot" + i, civ.name, "", 100, 280 + 25 * (i),
					width2, 20);
			menus.get(11).buttons.add(b);

			b.clearTooltipText();
			String s = civ.name + "; Health: " + civ.health + "; Gold: " + civ.gold + "; Research: " + civ.research
					+ "; Relations: " + main.grid.civs[0].opinions[i];
			b.addTooltipText(s);
			b.addTooltipText("Select to view the diplomatic situation of " + civ.name + ".");
			b.dimTooltip();

			// Allow player to talk with other civs in this menu
			if (i != 0) {
				TextBox textBox = menus.get(11).addButton(guiDefaultTexture, "diplomacy" + i, "Talk",
						"Conduct diplomacy with " + civ.name + ".", 100 + width2 + width * 4, 280 + 25 * (i), width2,
						20);
				textBox.shortcut = false;
			}

			if (civ.equals(pivot))
				continue;

			text = new TextBox(guiDefaultTexture, "" + pivot.opinions[i], 100 + width2, 280 + 25 * (i),
					width, 20);
			menus.get(11).buttons.add(text);

			String temp = pivot.isOpenBorder(civ) ? "Open" : "Closed";
			text = new TextBox(guiDefaultTexture, temp, 100 + width2 + width, 280 + 25 * (i), width,
					20);
			menus.get(11).buttons.add(text);

			temp = pivot.isWar(civ) ? "WAR" : "";
			text = new TextBox(guiDefaultTexture, temp, 100 + width2 + width * 2, 280 + 25 * (i), width,
					20);
			menus.get(11).buttons.add(text);

			temp = pivot.isAlly(civ) ? "Yes" : "No";
			text = new TextBox(guiDefaultTexture, temp, 100 + width2 + width * 3, 280 + 25 * (i), width,
					20);
			menus.get(11).buttons.add(text);
		}

		// Bottom set
		/*
		 * text = new TextBox("","In war"
		 * ,"The list of nations that this nation is currently fighting.", 200,280 +
		 * 25*main.grid.civs.length,200,20); menus.get(11).buttons.add(text);
		 * 
		 * for (int i = 0; i < main.grid.civs.length; i++) { Civilization civ =
		 * main.grid.civs[i];
		 * 
		 * text = new TextBox("",civ.name,"",100,280 +
		 * 25*(i+1+main.grid.civs.length),100,20); menus.get(11).buttons.add(text);
		 * 
		 * String temp = "At Peace"; if (civ.enemies.size() > 1) text = new
		 * TextBox("",temp,"",300,280 + 25*(i-1),100,20);
		 * menus.get(11).buttons.add(text); }
		 */
	}

	public void updateCivicsMenu(Civilization civ) {
		menus.get(12).buttons.clear();
		for (int i = 0; i < civ.techTree.governmentCivics.size(); i++) {
			String s = civ.techTree.governmentCivics.get(i);
			menus.get(12).addButton(guiDefaultTexture, "gCivic" + s, s, "", main.width / 3F, (float) main.height * 2F / 6F + 60 * i, 200,
					50);
		}
		for (int i = 0; i < civ.techTree.governmentCivics.size(); i++) {
			String s = civ.techTree.economicCivics.get(i);
			menus.get(12).addButton(guiDefaultTexture, "eCivic" + s, s, "", main.width / 3F + 250, (float) main.height * 2F / 6F + 60 * i,
					200, 50);
		}
	}

	// Only done once
	public void updateEncyclopedia() {
		int n = 0;
		for (Entry<String, ArrayList<String>> i : EntityData.encyclopediaEntries.entrySet()) {
			String key = i.getKey();
			menus.get(7).addButton(guiDefaultTexture, "encyclopedia" + key, key, "", 830, 190 + 30 * n, 100, 30);
			n++;
		}
	}

	// Find the spaces that a selected unit could potentially move to
	ArrayList<Tile> temp = new ArrayList<Tile>();

	public void movementChoice(ArrayList<Tile> initial, boolean first, int action) {
		if (first)
			temp = new ArrayList<Tile>();
		// action--;
		if (action <= 0) {
			movementChoices = temp;
			return;
		}
		for (int i = 0; i < initial.size(); i++) {
			ArrayList<Tile> adj = main.grid.adjacent(initial.get(i).row, initial.get(i).col);
			for (int j = 0; j < adj.size(); j++) {
				if (!temp.contains(adj.get(j)))
					temp.add(adj.get(j));
			}
		}
		// System.out.println(initial.size() + " " + temp.size());
		if (action > 0)
			movementChoice(temp, false, action - 1);
	}

	// Draw a path from the selected's entity tile to another
	public void pathTo(Tile t) {
		pathToHighlighted = main.grid.pathFinder.findAdjustedPath(selected.owner, selected.location.row,
				selected.location.col, t.row, t.col);
		if (pathToHighlighted == null) // Handle case that there is no point or invalid tile
			pathToHighlighted = new ArrayList<Tile>();
	}

	// Encapsulation for selected
	public BaseEntity getSelected() {
		return selected;
	}

	public void selectAndFocus(BaseEntity en) {
		textboxes.get(5).clearDisplayText();
		//textboxes.get(5).addDisplayText("A UNIT NEEDS ORDERS");
		textboxes.get(5).addDisplayText("Please order your unit.");
		textboxes.get(5).addDisplayText("PRESS SPACE");
		select(en);
		main.fixCamera(en.location.row, en.location.col);
		// main.chunkSystem.update();
		// main.requestUpdate();
	}

	public void setMouseHighlighted(Tile t) {
		if (mouseHighlighted == null && t == null)
			return;
		if (mouseHighlighted == null) {
			mouseHighlighted = t;

			// Give a new highlight map to give a different color to the tile underneath
			// mouse, and other events
			// main.takeBlendMap(main.sendHighlightMap(main.grid),
			// "res/generatedHighlightMap.png");
		} else if (!mouseHighlighted.equals(t)) {
			mouseHighlighted = t;
			// main.takeBlendMap(main.sendHighlightMap(main.grid),
			// "res/generatedHighlightMap.png");
		}
	}

	public void select(BaseEntity en) {
		selected = en;
		// main.takeBlendMap(main.sendHighlightMap(main.grid),
		// "res/generatedHighlightMap.png");
		// main.newMenuSystem.updateUnitMenu(en);
		// main.requestUpdate();
		if (en != null) {
			en.sleep = false;
			if (en instanceof Settler) {
				settlerChoices = main.grid.returnBestCityScores(en.location.row, en.location.col, 0.25);
			} else {
				settlerChoices = null;
			}
			if (en instanceof City) {
				updateCity((City) en);
				updateUnitMenu(null);
			} else {
				updateUnitMenu((GameEntity) en);
				updateCity(null);
			}
			// textboxes.get(1).orders.clear();
			textboxes.get(1).activate(true);
			textboxes.get(1).move(main.width - 400, main.height);
			// textboxes.get(1).moveTo(textboxes.get(1).origX,textboxes.get(1).origY,20);
		} else {
			stack.clear();
			// textboxes.get(1).orders.clear();
			textboxes.get(1).activate(false);
			textboxes.get(1).move(DisplayManager.width - 400, DisplayManager.height - 150);

			updateUnitMenu(null);
			updateCity(null);
			// menus.get(1).buttons.clear();
		}
		/*
		 * selected = en; main.newMenuSystem.updateUnitMenu(en); //main.requestUpdate();
		 * if (en != null) { en.sleep = false; if (en instanceof Settler) {
		 * settlerChoices = main.grid.returnBestCityScores(en.location.row,
		 * en.location.col, 0.25); } else { settlerChoices = null; } if (en instanceof
		 * City) { updateCity((City)en); } textboxes.get(1).orders.clear();
		 * textboxes.get(1).activate(true); textboxes.get(1).move(main.width -
		 * 400,main.height);
		 * textboxes.get(1).moveTo(textboxes.get(1).origX,textboxes.get(1).origY,20); }
		 * else { stack.clear(); textboxes.get(1).orders.clear();
		 * textboxes.get(1).activate(false); textboxes.get(1).move(main.width -
		 * 400,main.height-150);
		 * 
		 * menus.get(1).buttons.clear(); }
		 */
	}

}
