package data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import processing.core.PApplet;
import processing.core.PImage;

import terrain.DiamondSquare;
//import processing.core.PShape;
import game.*;
import game_ai.MaxTree;
import units.*;

public class EntityData {

	public static HashMap<Integer, Color> brickColorMap; // Defines integer brickcolors e.g. 21, 1003
	public static HashMap<String, GameEntity> gameEntityMap;
	public static HashMap<String, TileEntity> tileEntityMap;
	public static HashMap<Integer, Integer> groundColorMap; // Defines color of ground of certain biomes
	private static HashMap<String, float[][]> unitModelMap;
	public static HashMap<String, PImage> iconMap;

	// Terrible short hand names
	private static HashMap<String, Integer> f, m, g;

	public static HashMap<String, Improvement> unitImprovementMap;
	public static HashMap<String, Improvement> cityImprovementMap;
	private static HashMap<String, Field> fieldMap;

	public static HashMap<String, ArrayList<String>> encyclopediaEntries;

	public static HashMap<String, Civilization> civs;
	public static HashMap<String, CityState> cityStates;

	public static HashMap<String, int[]> yield;
	public static double[] resourceLevels;

	public EntityData() {

	}

	public static void init() {
		brickColorMap = new HashMap<Integer, Color>();
		gameEntityMap = new HashMap<String, GameEntity>();
		tileEntityMap = new HashMap<String, TileEntity>();
		groundColorMap = new HashMap<Integer, Integer>();
		unitModelMap = new HashMap<String, float[][]>();
		iconMap = new HashMap<String, PImage>();

		unitImprovementMap = new HashMap<String, Improvement>();
		cityImprovementMap = new HashMap<String, Improvement>();
		fieldMap = new HashMap<String, Field>();

		encyclopediaEntries = new HashMap<String, ArrayList<String>>();
		civs = new HashMap<String, Civilization>();
		cityStates = new HashMap<String, CityState>();

		f = new HashMap<String, Integer>();
		m = new HashMap<String, Integer>();
		g = new HashMap<String, Integer>();

		yield = new HashMap<String, int[]>();

		setupColors();
		setupEntityMap();
		groundColorMap();
		setupUnitCosts();
		setupUnitIcons();
		setupUnitImprovementCosts(); // longest name yet
		setupCityImprovementCosts();
		setYields();
		setupFields();
		setupCivBonuses();
		setResourceLevels();

		// setModels();
		/*
		 * for (Entry<String, Integer> en : f.entrySet()) {
		 * System.out.println(en.getKey() + ": " + en.getValue() + " food"); } for
		 * (Entry<String, Integer> en : m.entrySet()) { System.out.println(en.getKey() +
		 * ": " + en.getValue() + " food"); }
		 */
	}

	private static void setupColors() {
		brickColorMap.put(1, new Color(0.94901967048645, 0.95294123888016, 0.95294123888016));
		brickColorMap.put(5, new Color(0.84313732385635, 0.77254909276962, 0.60392159223557));
		brickColorMap.put(9, new Color(0.90980398654938, 0.7294117808342, 0.78431379795074));
		brickColorMap.put(11, new Color(0.50196081399918, 0.73333334922791, 0.85882359743118));
		brickColorMap.put(18, new Color(0.80000007152557, 0.55686277151108, 0.41176474094391));
		brickColorMap.put(21, new Color(0.76862752437592, 0.15686275064945, 0.10980392992496));
		brickColorMap.put(23, new Color(0.050980396568775, 0.41176474094391, 0.6745098233223));
		brickColorMap.put(24, new Color(0.96078437566757, 0.80392163991928, 0.18823531270027));
		brickColorMap.put(26, new Color(0.10588236153126, 0.16470588743687, 0.20784315466881));
		brickColorMap.put(28, new Color(0.15686275064945, 0.49803924560547, 0.27843138575554));
		brickColorMap.put(29, new Color(0.63137257099152, 0.76862752437592, 0.54901963472366));
		brickColorMap.put(37, new Color(0.29411765933037, 0.59215688705444, 0.29411765933037));
		brickColorMap.put(38, new Color(0.62745100259781, 0.37254902720451, 0.20784315466881));
		brickColorMap.put(45, new Color(0.70588237047195, 0.82352948188782, 0.89411771297455));
		brickColorMap.put(101, new Color(0.85490202903748, 0.52549022436142, 0.47843140363693));
		brickColorMap.put(102, new Color(0.43137258291245, 0.60000002384186, 0.79215693473816));
		brickColorMap.put(104, new Color(0.41960787773132, 0.19607844948769, 0.48627454042435));
		brickColorMap.put(105, new Color(0.88627457618713, 0.60784316062927, 0.25098040699959));
		brickColorMap.put(106, new Color(0.85490202903748, 0.52156865596771, 0.2549019753933));
		brickColorMap.put(107, new Color(0, 0.56078433990479, 0.61176472902298));
		brickColorMap.put(119, new Color(0.64313727617264, 0.74117648601532, 0.27843138575554));
		brickColorMap.put(125, new Color(0.91764712333679, 0.72156864404678, 0.57254904508591));
		brickColorMap.put(135, new Color(0.45490199327469, 0.52549022436142, 0.61568629741669));
		brickColorMap.put(141, new Color(0.15294118225574, 0.27450981736183, 0.17647059261799));
		brickColorMap.put(151, new Color(0.47058826684952, 0.56470590829849, 0.50980395078659));
		brickColorMap.put(153, new Color(0.58431375026703, 0.47450983524323, 0.46666669845581));
		brickColorMap.put(192, new Color(0.41176474094391, 0.25098040699959, 0.15686275064945));
		brickColorMap.put(194, new Color(0.63921570777893, 0.63529413938522, 0.64705884456635));
		brickColorMap.put(199, new Color(0.38823533058167, 0.37254902720451, 0.38431376218796));
		brickColorMap.put(208, new Color(0.89803928136826, 0.89411771297455, 0.87450987100601));
		brickColorMap.put(217, new Color(0.48627454042435, 0.36078432202339, 0.27450981736183));
		brickColorMap.put(226, new Color(0.99215692281723, 0.91764712333679, 0.55294120311737));
		brickColorMap.put(1001, new Color(0.97254908084869, 0.97254908084869, 0.97254908084869));
		brickColorMap.put(1002, new Color(0.80392163991928, 0.80392163991928, 0.80392163991928));
		brickColorMap.put(1003, new Color(0.066666670143604, 0.066666670143604, 0.066666670143604));
		brickColorMap.put(1004, new Color(1, 0, 0));
		brickColorMap.put(1005, new Color(1, 0.68627452850342, 0));
		brickColorMap.put(1006, new Color(0.70588237047195, 0.50196081399918, 1));
		brickColorMap.put(1007, new Color(0.63921570777893, 0.29411765933037, 0.29411765933037));
		brickColorMap.put(1008, new Color(0.75686281919479, 0.74509805440903, 0.258823543787));
		brickColorMap.put(1009, new Color(1, 1, 0));
		brickColorMap.put(1010, new Color(0, 0, 1));
		brickColorMap.put(1011, new Color(0, 0.12549020349979, 0.37647062540054));
		brickColorMap.put(1012, new Color(0.1294117718935, 0.32941177487373, 0.72549021244049));
		brickColorMap.put(1013, new Color(0.015686275437474, 0.68627452850342, 0.92549026012421));
		brickColorMap.put(1014, new Color(0.66666668653488, 0.33333334326744, 0));
		brickColorMap.put(1015, new Color(0.66666668653488, 0, 0.66666668653488));
		brickColorMap.put(1016, new Color(1, 0.40000003576279, 0.80000007152557));
		brickColorMap.put(1017, new Color(1, 0.68627452850342, 0));
		brickColorMap.put(1018, new Color(0.070588238537312, 0.93333339691162, 0.83137261867523));
		brickColorMap.put(1019, new Color(0, 1, 1));
		brickColorMap.put(1020, new Color(0, 1, 0));
		brickColorMap.put(1021, new Color(0.22745099663734, 0.49019610881805, 0.082352943718433));
		brickColorMap.put(1022, new Color(0.49803924560547, 0.55686277151108, 0.39215689897537));
		brickColorMap.put(1023, new Color(0.54901963472366, 0.35686275362968, 0.6235294342041));
		brickColorMap.put(1024, new Color(0.68627452850342, 0.8666667342186, 1));
		brickColorMap.put(1025, new Color(1, 0.78823536634445, 0.78823536634445));
		brickColorMap.put(1026, new Color(0.69411766529083, 0.65490198135376, 1));
		brickColorMap.put(1027, new Color(0.6235294342041, 0.95294123888016, 0.91372555494308));
		brickColorMap.put(1028, new Color(0.80000007152557, 1, 0.80000007152557));
		brickColorMap.put(1029, new Color(1, 1, 0.80000007152557));
		brickColorMap.put(1030, new Color(1, 0.80000007152557, 0.60000002384186));
		brickColorMap.put(1031, new Color(0.38431376218796, 0.14509804546833, 0.81960791349411));
		brickColorMap.put(1032, new Color(1, 0, 0.74901962280273));
	}

	public static void setupCivBonuses() {
		civs.clear();
		civs.put("Achaea", new Civilization("Achaea", list(), 0, 0, 255, 0.3, 0.3, 1));
		civs.put("Athens", new Civilization("Athens", list(), 200, 200, 200, 0.2, 0.8, 0.3));
		civs.put("Corinth", new Civilization("Corinth", list(), 0, 255, 255, 0.5, 0.5, 0.5));
		civs.put("Crete", new Civilization("Crete", list(), 0, 150, 0, 0.3, 0.6, 0.3));
		// civs.put("Ephesus", new Civilization("Ephesus",list(),150,150,150));
		civs.put("Epirus", new Civilization("Epirus", list(), 80, 80, 128, 0.7, 0, 0.5));
		civs.put("Illyria", new Civilization("Illyria", list(), 0, 255, 0, 0.5, 0.5, 0.8));
		// civs.put("Lydia", new Civilization("Lydia",list(),150,150,150));
		civs.put("Macedonia", new Civilization("Macedonia", list(), 255, 150, 0, 0.8, 0.6, 1));
		civs.put("Rhodes", new Civilization("Rhodes", list(), 175, 175, 0, 0.1, 0.8, 0.3));
		civs.put("Sparta", new Civilization("Sparta", list(), 255, 0, 0, 0.8, 0.1, 0.3));
		// civs.put("Thessaly", new Civilization("Thessaly",list(),150,150,150));
		civs.put("Thrace", new Civilization("Thrace", list(), 175, 255, 255, 0.6, 0.4, 0.5));
		// civs.put("TEST", new Civilization("TEST",list(),0,0,0,1,0,1));

		/*
		 * civs.put("Achaea", new Civilization("Achaea",list(),0,0,255,0.3,0.3,1));
		 * civs.put("Athens", new Civilization("Athens",list(),255,255,255,0.2,0.8,1));
		 * civs.put("Corinth", new
		 * Civilization("Corinth",list(),0,255,255,0.5,0.5,0.8)); civs.put("Crete", new
		 * Civilization("Crete",list(),0,150,0,0.3,0.6,0.8)); //civs.put("Ephesus", new
		 * Civilization("Ephesus",list(),150,150,150)); civs.put("Epirus", new
		 * Civilization("Epirus",list(),0,0,128,0.7,0,0.8)); civs.put("Illyria", new
		 * Civilization("Illyria",list(),0,255,0,0.5,0.5,0.8)); //civs.put("Lydia", new
		 * Civilization("Lydia",list(),150,150,150)); civs.put("Macedonia", new
		 * Civilization("Macedonia",list(),255,150,0,0.8,0.6,1)); civs.put("Rhodes", new
		 * Civilization("Rhodes",list(),175,175,0,0.1,0.8,1)); civs.put("Sparta", new
		 * Civilization("Sparta",list(),255,0,0,0.8,0.1,1)); //civs.put("Thessaly", new
		 * Civilization("Thessaly",list(),150,150,150)); civs.put("Thrace", new
		 * Civilization("Thrace",list(),175,255,255,0.6,0.4,0.8));
		 */

		civs.get("Achaea").traits("Prosperous", "Imperialistic");
		civs.get("Athens").traits("Refined", "Prosperous");
		civs.get("Corinth").traits("Prosperous", "Industrious");
		civs.get("Crete").traits("Defensive", "Refined");
		civs.get("Epirus").traits("Aggressive", "Prosperous");
		civs.get("Illyria").traits("Defensive", "Traditional");
		civs.get("Macedonia").traits("Imperialistic", "Aggressive");
		civs.get("Rhodes").traits("Defensive", "Industrious");
		civs.get("Sparta").traits("Aggressive", "Traditional");
		civs.get("Thrace").traits("Traditional", "Imperialistic");

		civs.get("Achaea").tech("Agriculture", 1);
		civs.get("Athens").tech("Polytheism", 1);
		civs.get("Corinth").tech("Currency", 0.5);
		civs.get("Crete").tech("Sailing", 1).tech("Metal Working", 0.25);
		civs.get("Epirus").tech("Metal Working", 0.5);
		civs.get("Illyria").tech("Hunting", 0.5);
		civs.get("Macedonia").tech("Metal Working", 0.5);
		civs.get("Rhodes").tech("Sailing", 1).tech("Fishing", 0.5);
		civs.get("Sparta").tech("Mining", 1).tech("Metal Working", 0.25);
		civs.get("Thrace").tech("Mining", 0.5).tech("Casting", 0.5);
		// civs.get("TEST").traits("Aggressive", "Prosperous");

		for (int i = 0; i < 30; i++) {
			ArrayList<String> traits = new ArrayList<String>();
			traits.add("Aggressive");
			traits.add("Defensive");
			traits.add("Imperialistic");
			traits.add("Industrious");
			traits.add("Prosperous");
			traits.add("Refined");
			traits.add("Traditional");
			cityStates.put("" + i,
					new CityState("" + i, list(), (float) (Math.random() * 255), (float) (Math.random() * 255),
							(float) (Math.random() * 255), Math.random() * 0.6, Math.random(), 0));
			cityStates.get("" + i).traits(traits.get((int) (Math.random() * traits.size())), "");
		}

	}

	public static String[] traitDesc(String trait) {
		/*
		 * Traits for civs: 3 Aggressive > +10% when attacking, 1 free promotion per
		 * unit 3 Imperialistic > 2 health per city, > 1 extra trade route per city 2
		 * Refined > 2 culture per city, > +10% research speed 4 Prosperous > +15% food
		 * per city, > +25% speed on Settler 3 Defensive > +15% when defending, +25%
		 * speed on fortifications 3 Traditional +25% speed on unique unit, > +15% food
		 * per city 2 Industrious > +10% production/metal, +25% on wonders
		 */
		if (trait.equals("Aggressive"))
			return new String[] { "+10% when attacking", "1 free promotion per unit" };
		else if (trait.equals("Defensive"))
			return new String[] { "+15% when defending", "+25% speed on fortifications" };
		else if (trait.equals("Imperialistic"))
			return new String[] { "2 health per city", "1 extra trade route per city" };
		else if (trait.equals("Industrious"))
			return new String[] { "+10% production/metal", "+25% on wonders " };
		else if (trait.equals("Prosperous"))
			return new String[] { "+15% food per city", "+25% speed on Settler " };
		else if (trait.equals("Refined"))
			return new String[] { "2 culture per city", "+10% research speed" };
		else if (trait.equals("Traditional"))
			return new String[] { "+25% speed on unique unit", "+15% food per city" };
		else {
			System.out.println("Invalid trait: " + trait);
			return null;
		}
	}

	public static int brickColorFromRGB(float r, float g, float b) // input is 0-255
	{
		int bestGuess = 1;
		int bestError = (int) (Math.pow(brickColorMap.get(bestGuess).r * 255 - r, 2)
				+ Math.pow(brickColorMap.get(bestGuess).g * 255 - g, 2)
				+ Math.pow(brickColorMap.get(bestGuess).b * 255 - b, 2));
		for (Entry<Integer, Color> entry : brickColorMap.entrySet()) {
			Color c = entry.getValue();
			int error = (int) (Math.pow(c.r * 255 - r, 2) + Math.pow(c.g * 255 - g, 2) + Math.pow(c.b * 255 - b, 2));
			if (error < bestError) {
				bestGuess = entry.getKey();
				bestError = error;
			}
		}
		/*
		 * System.out.println("---");
		 * System.out.println(brickColorMap.get(bestGuess).r*255 + "," +
		 * brickColorMap.get(bestGuess).g*255 + "," +
		 * brickColorMap.get(bestGuess).b*255); System.out.println(r + "," + g + "," +
		 * b);
		 */
		return bestGuess;
	}

	public static ArrayList<String> list(String... strings) {
		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < strings.length; i++)
			temp.add(strings[i]);
		return temp;
	}

	public static float[][] getModel(String name) {
		return unitModelMap.get(name);
	}

	public static String getBiomeName(int biome) {
		if (biome == -1)
			return "Sea";
		else if (biome == 0)
			return "Ice";
		else if (biome == 1)
			return "Taiga";
		else if (biome == 2)
			return "Desert";
		else if (biome == 3)
			return "Steppe";
		else if (biome == 4)
			return "Dry Forest";
		else if (biome == 5)
			return "Forest";
		else if (biome == 6)
			return "Rainforest";
		else if (biome == 8)
			return "Oasis";
		System.err.println("Invalid biome");
		return null;
	}

	private static void setupEntityMap() {
		gameEntityMap.put("Settler", new Settler("Settler", 0, 2, 0).mode(0));
		gameEntityMap.put("Warrior", new Warrior("Warrior", 2, 2, 0));
		gameEntityMap.put("Worker", new Worker("Worker", 0, 2, 0).mode(0));
		gameEntityMap.put("Scout", new Worker("Scout", 0, 2, 0).mode(0).maxAction(2));

		gameEntityMap.put("Caravan", new Caravan("Caravan", 0, 2, 0).mode(0).maxAction(0));

		gameEntityMap.put("Axeman", new Warrior("Axeman", 4, 3, 0));
		gameEntityMap.put("Warband", new Warrior("Warband", 2, 4, 1).range(1).maxAction(2));
		gameEntityMap.put("Swordsman", new Warrior("Swordsman", 5, 4, 0));
		gameEntityMap.put("Spearman", new Warrior("Spearman", 2, 4, 0));
		gameEntityMap.put("Axe Thrower", new Warrior("Axe Thrower", 4, 3, 3).range(1));

		gameEntityMap.put("Chariot", new Warrior("Chariot", 4, 1, 1).range(2).mode(2).maxAction(2));
		gameEntityMap.put("Horseman", new Warrior("Horseman", 4, 1, 0).maxAction(2));

		gameEntityMap.put("Slinger", new Warrior("Slinger", 0, 2, 2).range(2).mode(2));
		gameEntityMap.put("Archer", new Warrior("Archer", 0, 4, 4).range(2).mode(2));
		gameEntityMap.put("Horse Archer", new Warrior("Horse Archer", 0, 0, 4).range(2).mode(2).maxAction(2));

		gameEntityMap.put("Galley", new Galley("Galley", 4, 4, 0));
		gameEntityMap.put("Work Boat", new WorkBoat("Work Boat", 0, 2, 0).mode(0));

		tileEntityMap.put("City", new City("City"));
		tileEntityMap.put("Farm", new TileEntity("Farm"));
		tileEntityMap.put("Windmill", new TileEntity("Windmill"));
		tileEntityMap.put("Lumbermill", new TileEntity("Lumbermill"));
		tileEntityMap.put("Mine", new TileEntity("Mine"));
		tileEntityMap.put("Forest Yard", new TileEntity("Forest Yard"));
		tileEntityMap.put("Trading Post", new TileEntity("Trading Post"));
		tileEntityMap.put("Pasture", new TileEntity("Pasture"));
		tileEntityMap.put("Fishing Boats", new TileEntity("Fishing Boats"));
		tileEntityMap.put("Ruins", new TileEntity("Ruins"));
	}

	private static void setupUnitCosts() {
		cost("Settler", 35, 0, 0);
		cost("Warrior", 10, 0, 5);
		cost("Work Boat", 15, 0, 0);
		cost("Worker", 25, 0, 0);
		cost("Scout", 10, 0, 5);
		cost("Caravan", 15, 0, 5);

		cost("Axeman", 10, 0, 10);
		cost("Warband", 15, 0, 5);
		cost("Swordsman", 10, 0, 15);
		cost("Spearman", 10, 0, 10);
		cost("Chariot", 10, 0, 15);
		cost("Slinger", 10, 0, 5);
		cost("Archer", 15, 0, 10);
		cost("Axe Thrower", 10, 0, 15);
		cost("Horseman", 15, 0, 5);
		cost("Horse Archer", 15, 0, 10);
	}

	private static void cost(String name, int food, int gold, int metal) {
		f.put(name, food);
		g.put(name, gold);
		m.put(name, metal);
	}

	public static float[] getCost(String name) {
		if (name == null)
			return null;
		Field field = getField(name);
		if (field != null) {
			return new float[] { (float) field.foodFlat, (float) field.goldFlat, (float) field.metalFlat };
		} else if (f.get(name) == null) {
			Improvement impr = cityImprovementMap.get(name);
			// System.out.println(name);
			try {
				return new float[] { (float) impr.foodFlat, (float) impr.goldFlat, (float) impr.metalFlat };
			} catch (Exception e) {
				System.out.println("No cost data for improvement " + name);
				e.printStackTrace();
				return null;
			}
		}
		return new float[] { f.get(name), g.get(name), m.get(name) };
	}

	private static void setupUnitImprovementCosts() {
		Improvement temp;
		temp = new Improvement("Neutral", "No improvement");
		temp.cost(1, 1, 1, 0, 0, 0);
		temp.set(1, 1, 1, 0, 0, 0, 1);
		unitImprovementMap.put(temp.name, temp);

		temp = new Improvement("CopperTools", "Not impl.");
		temp.cost(1.25, 1.25, 0, 0, 0, 0);
		temp.set(0, 0, 0, 0, 0, 0, 0.8);
		temp.fit("Worker");
		unitImprovementMap.put(temp.name, temp);
		/*
		 * temp = new Improvement("Test",""); temp.cost(2,0,0,0,0,0);
		 * temp.set(0,0,0,0,0,0,0.2); temp.fit("Worker");
		 * unitImprovementMap.put(temp.name, temp);
		 */

		temp = new Improvement("CopperWeapons", "Not impl.");
		temp.cost(1.25, 1.5, 0, 0, 0, 0);
		temp.set(1.25, 1.4, 0, 0, 0, 0, 0);
		temp.fit("allmelee");
		unitImprovementMap.put(temp.name, temp);
		temp = new Improvement("IronWeapons", "Not impl.");
		temp.cost(1.25, 2, 0, 0, 0, 0);
		temp.set(1.5, 1.5, 0, 0, 0, 0, 0);
		temp.fit("allmelee");
		unitImprovementMap.put(temp.name, temp);
		temp = new Improvement("CopperArrows", "Not impl.");
		temp.cost(1.25, 1.25, 0, 0, 0, 0);
		temp.set(0, 0, 1.25, 0, 0, 0, 0);
		temp.fit("allranged");
		unitImprovementMap.put(temp.name, temp);
		temp = new Improvement("IronArrows", "Not impl.");
		temp.cost(1.25, 1.5, 0, 0, 0, 0);
		temp.set(0, 0, 1.5, 0, 0, 0, 0);
		temp.fit("allranged");
		unitImprovementMap.put(temp.name, temp);
	}

	private static void setupCityImprovementCosts() {
		Improvement temp;
		temp = new Improvement("Obelisk", "+1 culture");
		temp.cost(0, 0, 0, 10, 10, 0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Warehouse", "Not impl.");
		temp.cost(0, 0, 0, 5, 20, 0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Granary", "+15% to population growth");
		temp.cost(0, 0, 0, 5, 20, 0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Metalworks", "+25% to metal production");
		temp.cost(0, 0, 0, 10, 50, 0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Port", "+25% food from sea tiles");
		temp.cost(0, 0, 0, 25, 25, 0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Walls", "+40% defense in city");
		temp.cost(0, 0, 0, 5, 50, 0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Palace", "+25% revenue, +1 culture");
		temp.cost(0, 0, 0, 25, 25, 0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Stables", "Not impl.");
		temp.cost(0, 0, 0, 25, 25, 0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Market", "+25% revenue");
		temp.cost(0, 0, 0, 30, 20, 0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Pyramid", "+25% culture");
		temp.cost(0, 0, 0, 0, 50, 0);
		cityImprovementMap.put(temp.name, temp);
		// temp.set();
		// cityImprovementMap.put(temp.name, temp);
	}

	private static void setupFields() {
		Field temp;
		temp = new Field(null, "TestField", "Test");
		temp.cost(1.25, 0, 0, 0, 20, 0);
		fieldMap.put(temp.name, temp);

		temp = new Field(null, "Stockpile", "A stockpile for resources.");
		temp.cost(0, 0, 0, 0, 20, 0);
		fieldMap.put(temp.name, temp);

		temp = new Field(null, "Hamlet", "A small city for workers.");
		temp.cost(0, 1.25, 0, 10, 10, 30);
		fieldMap.put(temp.name, temp);
	}

	public static Field getField(String name) {
		if (fieldMap.get(name) == null)
			return null;
		return new Field(fieldMap.get(name));
	}

	public static void setYields() {
		// f, g, m, r
		yield.put("From sea", new int[] { 1, 1, 0, 2 });
		yield.put("From ice", new int[] { 0, 1, 2, 1 });
		yield.put("From taiga", new int[] { 1, 1, 1, 1 });
		yield.put("From desert", new int[] { 0, 0, 2, 1 });
		yield.put("From steppe", new int[] { 2, 0, 1, 2 });
		yield.put("From dry forest", new int[] { 2, 1, 1, 2 });
		yield.put("From forest", new int[] { 3, 0, 1, 2 });
		yield.put("From rainforest", new int[] { 3, 1, 0, 3 });
		yield.put("Fertile", new int[] { 2, 0, 0, 0 });
		yield.put("Fresh water", new int[] { 1, 0, 0, 0 });
		yield.put("Barren", new int[] { -1, 0, 0, -1 });

		yield.put("Rocky", new int[] { -1, 0, 1, 0 });
		yield.put("Rocky w/ mine", new int[] { 0, 0, 2, 0 });

		yield.put("Mountainous", new int[] { -1, 0, 1, 0 });
		yield.put("Mountainous w/ mine", new int[] { 0, 0, 3, 0 });

		yield.put("Cultivated wheat", new int[] { 3, 0, 0, 0 });
		yield.put("Cultivated rice", new int[] { 4, 0, 0, 0 });
		yield.put("Cultivated", new int[] { 2, 0, 0, 0 });

		yield.put("Wild wheat", new int[] { 1, 0, 0, 0 });
		yield.put("Wild rice", new int[] { 2, 0, 0, 0 });

		yield.put("Built pasture", new int[] { 1, 1, 0, 0 });

		yield.put("Harvested fish", new int[] { 3, 0, 0, 0 });
		yield.put("Harvested whale", new int[] { 2, 3, 0, 3 });

		yield.put("Mined copper", new int[] { 0, 2, 2, 1 });
		yield.put("Mined iron", new int[] { 0, 1, 3, 2 });
		yield.put("Mined coal", new int[] { 0, 1, 2, 1 });

		yield.put("From spring", new int[] { 2, 1, 0, 2 });
		yield.put("Silviculture", new int[] { 1, 1, 3, 1 });

		yield.put("Operating windmill", new int[] { 1, 1, 0, 0 });
		yield.put("Operating lumbermill", new int[] { -1, 1, 2, 0 });
		yield.put("Operating forge", new int[] { -1, 0, 1, 0 });
		yield.put("Forge with copper", new int[] { -1, 0, 3, 0 });
		yield.put("Forge with iron", new int[] { -1, 0, 4, 0 });
		yield.put("Forge with coal", new int[] { -1, 0, 4, 0 });

		yield.put("Built trade outpost", new int[] { 0, 2, 1, 0 });
		yield.put("Built fort", new int[] { -2, 2, -1, 0 });
	}

	// Possibly a duplication of code (see Grid::bestToImprove())
	public static String optimalImpr(ArrayList<String> imprs, Tile t) {
		if (imprs.size() == 0)
			return null;
		if (imprs.size() == 1)
			return imprs.get(0);
		// Why do I feel like I should stop rewriting this algorithm?
		int bestScore = 0;
		String best = null;
		for (int i = 0; i < imprs.size(); i++) {
			double[] yieldBefore = City.staticEval(t), yieldAfter = City.staticEval(t, imprs.get(i));
			int sum = 0;
			for (double j : yieldBefore)
				sum -= j;
			for (double j : yieldAfter)
				sum += j;
			if (sum > bestScore) {
				bestScore = sum;
				best = imprs.get(i);
			}
		}
		return best;
	}

	private static void setupUnitIcons() {
		// Done in Game class in setModels()
	}

	/*
	 * public static void awardField(Civilization civ, Tile t, String name) {
	 * 
	 * }
	 */

	public static ArrayList<Improvement> getValidImprovements(Civilization civ, BaseEntity en) {
		ArrayList<Improvement> temp = new ArrayList<Improvement>();
		/*
		 * for (Entry<String, Improvement> entry: unitImprovementMap.entrySet()) {
		 * String name = entry.getKey(); Improvement i = entry.getValue(); //Split into
		 * many if statements for special improvement conditions later if
		 * (!civ.techTree.researched(i.requiredTech).researched()) continue; if
		 * (i.equals("Neutral")) { continue; } if (i.isFit(en.name)) temp.add(i); else
		 * if (i.units[0].equals("allmelee") && en.offensiveStr > 0) temp.add(i); else
		 * if (i.units[0].equals("allranged") && en.rangedStr > 0) temp.add(i); }
		 * //System.out.println(temp.size());
		 */
		for (int i = 0; i < civ.techTree.allowedUnitImprovements.size(); i++) {
			String name = civ.techTree.allowedUnitImprovements.get(i);
			Improvement impr = unitImprovementMap.get(name);
			if (impr == null)
				impr = cityImprovementMap.get(name);
			// if (!civ.techTree.researched(i.requiredTech).researched()) continue;
			// Above condition already checked; list of names can only be from unlocked
			// techs
			if (impr.equals("Neutral")) {
				continue;
			}
			if (impr.isFit(en.name))
				temp.add(impr);
			else if (impr.units[0].equals("allmelee") && en.offensiveStr > 0)
				temp.add(impr);
			else if (impr.units[0].equals("allranged") && en.rangedStr > 0)
				temp.add(impr);
		}
		return temp;
	}

	// Calculate the yield as was previously done by Civilization system
	// This is also used as a preview for the potential yield of a city
	public static double[] calculateYield(City c) {
		Grid grid = c.location.grid;
		double tf = 0, tg = 0, tm = 0, tr = 0;
		// Make some settlers to test
		// int numSettlers = 0, numWorkers = 0, numWarriors = 0;
		int numWorkers = c.owner.count("Worker");
		// Loop through a city's tiles
		/*
		 * -2 freshwater 2,1,0,2 -1 sea 1,1,0,2 0 ice 0,1,2,1 1 taiga 1,1,1,1 2 desert
		 * 0,0,2,1 3 savannah 2,0,1,2 4 dry forest 2,1,1,2 5 forest 3,0,1,2 6 rainforest
		 * 3,1,0,3 7 beach (outdated)
		 * 
		 * modifiers: 8 oasis 3,3,0,2 (shape 1) hill -1,0,1,0 (shape 2) mountain
		 * -2,0,2,2
		 *
		 */
		/*
		 * if (i != 0) { //Assign specialized workers, prioritize scientists if
		 * (c.population >= 5) { int idle = c.population - 4; c.sci = Math.min(4, idle);
		 * idle -= c.sci; if (idle > 0) { c.art += idle; idle = 0; } } }
		 */

		c.happiness = 4 - c.population;
		if (grid.difficultyLevel == 1 && c.owner.id == 0)
			c.happiness += 3;
		else if (grid.difficultyLevel == 2 && c.owner.id == 0)
			c.happiness += 1;
		else if (grid.difficultyLevel == 4 && c.owner.id != 0)
			c.happiness += 1;
		else if (grid.difficultyLevel == 5 && c.owner.id != 0)
			c.happiness += 3;

		if (c.built("Palace")) {
			c.happiness++;
		} else if (c.built("Pyramid")) {
			c.happiness += 2;
		}

		int sumCityWorkers = c.adm + c.art + c.sci;
		if (c.happiness < 0)
			c.workTiles(c.population - c.happiness - sumCityWorkers + 1);
		else
			c.workTiles(c.population - sumCityWorkers + 1);

		c.health = 7 - c.population + Math.min(0, c.happiness);
		if (grid.difficultyLevel == 1 && c.owner.id == 0)
			c.health += 4;
		else if (grid.difficultyLevel == 2 && c.owner.id == 0)
			c.health += 2;
		else if (grid.difficultyLevel == 4 && c.owner.id != 0)
			c.health += 2;
		else if (grid.difficultyLevel == 5 && c.owner.id != 0)
			c.health += 4;
		if (c.owner.trait("Imperialistic"))
			c.health += 2;
		if (c.sortie == 2)
			c.health -= 4;
		// Civilization wide health calculation

		for (int k = 0; k < c.land.size(); k++)
			c.land.get(k).harvest = false;

		// Work tiles and harvest their numerical yields
		for (int k = 0; k < c.workedLand.size(); k++) {
			Tile t = c.workedLand.get(k);
			t.turnsSettled++;
			// System.out.println(t.row + " " + t.col + " " + t.turnsSettled);
			double[] eval = c.evaluate(t, -1);
			double f = eval[0], g = eval[1], m = eval[2], r = eval[3];

			if (t.biome == -1 && c.built("Port"))
				f += 2;

			// civ.food += f;
			// civ.gold += g;
			// civ.metal += m;
			// tf += f;
			tf += f;
			tg += g;
			tm += m;
			tr += r;
			c.workedLand.get(k).harvest = true;
		}
		if (c.built("Metalworks")) {
			tm *= 1.25;
		}
		if (grid.difficultyLevel == 1 && c.owner.id == 0)
			tm *= 1.25;
		else if (grid.difficultyLevel == 2 && c.owner.id == 0)
			tm *= 1.1;
		else if (grid.difficultyLevel == 4 && c.owner.id != 0)
			tm *= 1.1;
		else if (grid.difficultyLevel == 5 && c.owner.id != 0)
			tm *= 1.25;
		// Factor in specialized workers
		double taxBase = tg;
		tr += c.sci * 2;
		tg += Math.floor(c.adm * 0.25 * taxBase);
		tg -= 5 * c.population;
		c.culture += Math.floor(c.art * 0.25 * taxBase);
		if (c.owner.trait("Refined"))
			c.culture += 2;
		c.culture++;
		/*
		 * if (civ.capital != null) if (civ.capital.equals(c) && !(c.owner instanceof
		 * CityState)) c.culture++;
		 */
		if (c.built("Palace")) {
			tg *= 1.25;
			tg += 8;
			c.culture++;
		}
		if (c.built("Obelisk")) {
			c.culture++;
		}
		if (c.built("Market")) {
			tg *= 1.25;
		}
		if (c.built("Pyramid")) {
			c.culture++;
			c.culture *= 1.25;
		}

		for (int k = 0; k < c.activeCaravansOut.size(); k++) {
			// Temporary algorithm
			tf += 2;
			tm++;
			tg += 2;
		}
		for (int k = 0; k < c.activeCaravansIn.size(); k++) {
			// Temporary algorithm
			tf++;
			tm++;
		}
		if (c.owner.trait("Prosperous") || c.owner.trait("Traditional"))
			tf *= 1.15;
		if (c.owner.trait("Industrious"))
			tm *= 1.1;
		if (c.owner.trait("Refined"))
			tr *= 1.1;
		if (c.owner.trait("Prosperous") && c.queue != null)
			if (c.queue.equals("Settler"))
				tf *= 1.25;

		tf *= c.morale * 0.75 + 0.25;
		tm *= c.morale;

		return new double[] { tf, tg, tm, tr };
	}

	// Return true if successfully queued in a city not undergoing hostile takeover
	// Edit: Returns an improvement (could be neutral) if successfully queued
	public static Improvement queue(City c, String queue) {
		if (queue == null || c == null)
			return null;
		if (c.takeover <= 0 || c.raze) {
			c.queue = queue;
			Improvement i = c.owner.unitImprovements.get(queue);
			if (i == null) {
				Improvement building = cityImprovementMap.get(queue);
				if (building != null) {
					c.queueFood = (int) building.foodFlat;
					c.queueMetal = (int) building.metalFlat;
				} else {
					c.queueFood = f.get(queue);
					c.queueMetal = m.get(queue);
				}
				return unitImprovementMap.get("Neutral");
			} else {
				if (i.foodPercent != 0)
					c.queueFood = (int) (f.get(queue) * i.foodPercent);
				if (i.metalPercent != 0)
					c.queueMetal = (int) (m.get(queue) * i.metalPercent);
				return i;
			}
			// return true;
		}
		return null;
		// return false;
	}

	/*
	 * Return the technology that the civilization is priortizing If playing a
	 * militaristic game, go for units (particularly metal casting) If playing a
	 * turtle/economic game, go for useful improvements
	 */
	public static void queueTechAi(Civilization civ) {
		// Temporary algorithm
		civ.beeline.clear();
		if (civ.war > civ.peace) {
			civ.beeline.add("Mining");
			civ.beeline.add("Agriculture");
			civ.beeline.add("Metal Working");
			civ.beeline.add("Animal Husbandry");
			civ.beeline.add("Milling");
			civ.beeline.add("Casting");
		} else {
			civ.beeline.add("Agriculture");
			civ.beeline.add("Mining");
			civ.beeline.add("Milling");
			civ.beeline.add("Hunting");
			civ.beeline.add("Metal Working");
			civ.beeline.add("Fletching");
		}
	}

	// TODO: Factor in level of technology and available units
	public static Improvement queueAi(City c, boolean civ, int d) {
		String queue = MaxTree.generateTree(c);
		if (queue == null)
			return null;
		// System.out.println(queue);
		return queue(c, queue);
	}

	public static Improvement queueAiOld(City c, boolean civ) {
		String queue = null;
		int p = 0, cities = c.owner.count("Settler");
		for (int i = 0; i < c.owner.cities.size(); i++) {
			p += c.owner.cities.get(i).population;
			cities++;
		}

		if (c.owner.units.size() < 3) {
			queue = "Worker";
		} else if (c.owner.cities.size() == 1) {
			if (c.expanded == 1) {
				if (Math.random() < 0.8 * (1 - c.owner.tallwide)) {
					if (c.owner.count("Worker") < c.owner.cities.size() * 2)
						queue = "Worker";
					else
						queue = bestBuilding(c);
				} else if (p < cities * 4 && civ && cities < 6)
					queue = "Settler";
				else
					queue = bestUnit(c.owner, c.location.grid.civs);
			} else if (Math.random() < 0.4 * c.owner.tallwide) {
				if (p < cities * 4 && civ && cities < 6)
					queue = "Settler";
				else
					queue = bestUnit(c.owner, c.location.grid.civs);
			} else if (Math.random() < 0.7)
				queue = bestBuilding(c);
			else if (c.owner.count("Worker") < c.owner.cities.size() * 2)
				queue = "Worker";
			else
				queue = bestUnit(c.owner, c.location.grid.civs);
		} else {
			if (Math.random() < 0.3 * c.owner.tallwide) {
				if (p < cities * 4 && civ && cities < 6)// && c.owner.health > -5)
					queue = "Settler";
				else
					queue = bestUnit(c.owner, c.location.grid.civs);
			} else
				queue = bestUnit(c.owner, c.location.grid.civs);
		}
		if (queue == null)
			queue = bestUnit(c.owner, c.location.grid.civs);
		if (c.owner.units.size() > 5 * c.owner.cities.size())
			return null;
		// System.out.println(queue);
		return queue(c, queue);
	}

	// Decide which city improvement is best
	// Use a loop to iterate through candidates
	public static String bestBuilding(City c) {
		ArrayList<String> allowed = c.owner.techTree.allowedCityImprovements;
		if (allowed(c, "Granary"))
			return "Granary";
		if (c.cityFocus == 3) {
			if (Math.random() < 0.25)
				if (allowed(c, "Warehouse"))
					return "Warehouse";
			if (allowed(c, "Port"))
				return "Port";
			if (allowed(c, "Market"))
				return "Market";
			if (allowed(c, "Metalworks"))
				return "Metalworks";
			if (allowed(c, "Walls"))
				return "Walls";
		} else if (c.cityFocus == 2) {
			if (Math.random() < 0.25)
				if (allowed(c, "Warehouse"))
					return "Warehouse";
			if (allowed(c, "Metalworks"))
				return "Metalworks";
			if (allowed(c, "Walls"))
				return "Walls";
			if (allowed(c, "Stables"))
				return "Stables";
			if (allowed(c, "Market"))
				return "Market";
		} else if (c.cityFocus == 1) {
			if (allowed(c, "Library"))
				return "Library";
			if (allowed(c, "Port"))
				return "Port";
			if (allowed(c, "Market"))
				return "Market";
			if (allowed(c, "Palace"))
				return "Palace";
		} else {
			if (Math.random() < 0.25)
				if (allowed(c, "Warehouse"))
					return "Warehouse";
			if (allowed(c, "Port"))
				return "Port";
			if (allowed(c, "Market"))
				return "Market";
		}
		for (int i = 0; i < 10; i++) // 10 trials
		{
			if (allowed.size() == 0)
				return null;
			String candidate = allowed.get((int) (Math.random() * allowed.size()));
			if (allowed(c, candidate))
				return candidate;
		}
		return null;
		// return
	}

	public static boolean allowed(City c, String building) {
		return c.owner.techTree.allowedCityImprovements.contains(building) && !c.built(building);
	}

	// Decide which unit is best unit to counter an enemy unit
	public static String bestUnit(Civilization civ, Civilization[] enemies) {
		ArrayList<String> allowed = civ.techTree.allowedUnits;
		float heavyMelee = 0, lightMelee = 0, ranged = 0, mounted = 0;
		for (int i = 0; i < enemies.length; i++) {
			heavyMelee += enemies[i].count("Axeman", "Spearman", "Swordsman");
			lightMelee += enemies[i].count("Axe Thrower", "Warband", "Warrior");
			ranged += enemies[i].count("Archer", "Slinger");
			mounted += enemies[i].count("Chariot", "Horse Archer", "Horseman");
		}
		float sum = heavyMelee + lightMelee + ranged + mounted;
		// System.out.println(sum);
		if (sum == 0)
			return "Warrior";
		ArrayList<Float> data = new ArrayList<Float>();
		data.add(heavyMelee);
		data.add(lightMelee);
		data.add(ranged);
		data.add(mounted);
		Collections.sort(data);
		double r = Math.random();
		if (data.get(3) == heavyMelee) {
			if (r < 0.2) {
				if (allowed.contains("Spearman"))
					return "Spearman";
			}
			if (r < 0.5) {
				if (allowed.contains("Swordsman"))
					return "Swordsman";
			}
			if (r < 0.6) {
				if (allowed.contains("Axe Thrower"))
					return "Axe Thrower";
			}
			if (allowed.contains("Axeman"))
				return "Axeman";
		} else if (data.get(3) == lightMelee) {
			if (r < 0.25) {
				if (allowed.contains("Archer"))
					return "Archer";
			}
			if (r < 0.5) {
				if (allowed.contains("Swordsman"))
					return "Swordsman";
			}
			if (allowed.contains("Axeman"))
				return "Axeman";
		} else if (data.get(3) == ranged) {
			if (r < 0.25) {
				if (allowed.contains("Horse Archer"))
					return "Horse Archer";
			}
			if (r < 0.5) {
				if (allowed.contains("Horseman"))
					return "Horseman";
			}
			if (r < 0.6) {
				if (allowed.contains("Archer"))
					return "Archer";
			}
			if (allowed.contains("Warband"))
				return "Warband";
		} else if (data.get(3) == mounted) {
			if (r < 0.25) {
				if (allowed.contains("Horse Archer"))
					return "Horse Archer";
			}
			if (r < 0.5) {
				if (allowed.contains("Archer"))
					return "Archer";
			}
			if (allowed.contains("Spearman"))
				return "Spearman";
			if (allowed.contains("Warband"))
				return "Warband";
		} else {
			System.out.println("Invalid queue");
			return null;
		}
		if (allowed.contains("Axeman"))
			return "Axeman";
		if (allowed.contains("Warband"))
			return "Warband";
		return "Warrior";
	}

	public static boolean queueCityImprovement(City city, String impr) {
		if (!city.hasImprovement(impr)) {
			Improvement i = cityImprovementMap.get(impr);
			city.queue = i.name;
			city.queueFood = (int) i.foodFlat;
			city.queueMetal = (int) i.metalFlat;
			return true;
		}
		return false;
	}

	// Temp. replace with a hashmap
	public static void queueTileImprovement(GameEntity en, String tileImpr) {
		// System.out.println(">>> " + tileImpr);
		en.queue = tileImpr;
		en.queueTurns = tileImprovementTime(en, tileImpr);
	}

	// Return if the tile can support a worker's tileImpr.
	// Does not factor in tech level of civ, which should be checked before
	public static boolean allowedTileImprovement(Tile t, String tileImpr) {
		if (tileImpr.equals("Farm")) {
			if ((t.resource >= 1 && t.resource <= 3) || (t.resource >= 30 && t.resource <= 30))
				return true;
			return t.grid.irrigated(t.row, t.col) && t.biome >= 3 && t.biome <= 6;
		} else if (tileImpr.equals("Mine")) {
			if ((t.resource >= 20 && t.resource <= 22) || t.shape == 2)
				return true;
			return t.shape == 1 && t.biome >= 0 && t.biome <= 3;
		} else if (tileImpr.equals("Windmill")) {
			return ((t.biome >= 1 && t.biome <= 2) || t.shape > 1);
		} else if (tileImpr.equals("Road")) {
			return t.shape != 2;
		} else if (tileImpr.equals("Pasture")) {
			return t.shape != 2;
		} else if (tileImpr.equals("Forge")) {
			return true;
		}
		System.err.println("Invalid improvement being checked: " + tileImpr);
		return false;
	}

	public static int tileImprovementTime(GameEntity en, String tileImpr) {
		int temp = -1;
		if (tileImpr.equals("Mine"))
			temp = 6;
		else if (tileImpr.equals("Farm"))
			temp = 6;
		else if (tileImpr.equals("Windmill") || tileImpr.equals("Lumbermill"))
			temp = 8;
		else if (tileImpr.equals("Road"))
			temp = 3;
		else {
			System.out.println("Invalid tile improvement: " + tileImpr);
			temp = -1;
		}
		temp = Math.max(1, (int) (temp * ((Worker) en).workTime));
		return temp;
	}

	public static String createTexture(int brickColor) {
		try {
			int width = 256;
			BufferedImage bi = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB); // no alpha here
			Color color = brickColorMap.get(brickColor);
			int red = (int) (color.r * 255);
			int green = (int) (color.g * 255);
			int blue = (int) (color.b * 255);
			// int alpha = 255;
			// int col = (alpha << 24) | (red << 16) | (green << 8) | blue;
			int col = (red << 16) | (green << 8) | blue;
			for (int r = 0; r < width; r++)
				for (int c = 0; c < width; c++)
					bi.setRGB(r, c, col);
			File outputfile = new File("res/colorTexture" + brickColor + ".png");
			ImageIO.write(bi, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return "res/colorTexture" + brickColor + ".png";
	}
	/*
	 * private static int tempTextureIndex = 0; public static String
	 * createTexture(int red, int green, int blue) { String name = null; try { int
	 * width = 256; BufferedImage bi = new BufferedImage(width, width,
	 * BufferedImage.TYPE_INT_RGB); //no alpha here int col = (red << 16) | (green
	 * << 8) | blue; for (int r = 0; r < width; r++) for (int c = 0; c < width; c++)
	 * bi.setRGB(r, c, col); name =
	 * "res/rgbTexture"+(int)(System.currentTimeMillis()*Math.random())+".png"; File
	 * outputfile = new File(name); ImageIO.write(bi, "png", outputfile); } catch
	 * (Exception e) { e.printStackTrace(); } return name; }
	 */

	public static void createHeightMap(String fileLocation) {
		// Why Math.floor() returns a double is beyond me...
		// int adj = (int)Math.floor(Math.log(width)/Math.log(2)) + 1;
		try {
			BufferedImage bi = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB); // no alpha here

			double[][] temp = DiamondSquare.makeTable(0, 0, 0, 0, 257); // default size is 256^2
			DiamondSquare ds = new DiamondSquare(temp);
			// ds.diamond(0, 0, 4);
			ds.dS(0, 0, 256, 80, 0.6, false, true);

			for (int r = 0; r < 256; r++)
				for (int c = 0; c < 256; c++) {
					// if (r % 8 == 0) System.out.println(ds.t[r][c]);
					if (ds.t[r][c] > 255)
						ds.t[r][c] = 255; // upper and lower bound on white -> 0-255
					else if (ds.t[r][c] < 0)
						ds.t[r][c] = 0;
					int col = ((int) (ds.t[r][c]) << 16) | ((int) (ds.t[r][c]) << 8) | (int) (ds.t[r][c]);
					bi.setRGB(r, c, col);
				}
			File outputfile = new File(fileLocation);
			ImageIO.write(bi, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Color getResourceColor(int res) {
		int brickColor = getResourceBrickColor(res);
		if (brickColor == -1) {
			System.err.println("Invalid resource " + res);
			return null;
		}
		return EntityData.brickColorMap.get(brickColor);
	}

	public static int getResourceBrickColor(int res) {
		switch (res) {
		case 1:
			return 106;
		case 2:
			return 1;

		case 10:
			return 23;
		case 11:
			return 1011;

		case 20:
			return 1014;
		case 21:
			return 194;
		case 22:
			return 26;

		case 30:
			return 21;

		case 40:
			return 45;

		default:
			System.err.println("Invalid resource " + res);
			return -1;
		}
	}

	public static String getResourceName(int res) {
		switch (res) {
		case 1:
			return "Wheat";
		case 2:
			return "Rice";

		case 10:
			return "Fish";
		case 11:
			return "Whale";

		case 20:
			return "Copper";
		case 21:
			return "Iron";
		case 22:
			return "Coal";

		case 30:
			return "Redwood";

		case 40:
			return "Spring";

		default:
			// System.err.println("Invalid resource " + res);
			return null;
		}
	}

	public static void setResourceLevels() {
		resourceLevels = new double[41];
		for (int i = 0; i < resourceLevels.length; i++)
			resourceLevels[i] = 0.0125;
		resourceLevels[1] = 0.04;
		resourceLevels[2] = 0.03;
	}

	public static void passModelData(String name, String[] data) {
		float[][] temp = new float[data.length][10];
		for (int line = 0; line < data.length; line++) {
			String[] split = PApplet.split(data[line], ",");
			for (int i = 0; i < split.length; i++) {
				if (i == 0) {
					if (split[0].equals("Color"))
						temp[line][i] = 1;
					else
						temp[line][i] = 0;
				} else {
					if (i >= 4 && i <= 6)
						temp[line][i] = (float) Math.toRadians(Float.parseFloat(split[i]));
					else
						temp[line][i] = Float.parseFloat(split[i]);
				}
			}
		}
		unitModelMap.put(name, temp);
	}

	public static String getUniqueModel(String generic) {
		if (unitModelMap.get(generic) != null)
			return generic; // One singular model, assumed to be no numbered extras
		ArrayList<String> candidates = new ArrayList<String>();
		for (String en : unitModelMap.keySet()) {
			if (en.contains(generic))
				candidates.add(en);
		}
		if (candidates.size() == 0) {
			System.out.println("Model " + generic + " not found");
			return null;
		}
		return candidates.get((int) (candidates.size() * Math.random()));
	}

	public static String[] allUnitNames() {
		return new String[] { "Galley", "Settler", "Warrior", "Work Boat", "Worker" };
	}

	public static BaseEntity get(String name) {
		/*
		 * for (Entry e: gameEntityMap.entrySet()) { if (e.getKey().equals(name)) {
		 * return new GameEntity() } }
		 */
		// System.out.println(name);
		BaseEntity b = gameEntityMap.get(name);
		if (b != null) {
			// TODO: Fix this so that it doesn't return a generic GameEntity
			if (b.offensiveStr > 0 || b.rangedStr > 0)
				return new Warrior((GameEntity) b);
			else if (name.equals("Settler"))
				return new Settler((GameEntity) b);
			else if (name.equals("Galley"))
				return new Galley((GameEntity) b);
			else if (name.equals("Work Boat"))
				return new WorkBoat((GameEntity) b);
			else if (name.equals("Worker"))
				return new Worker((GameEntity) b);
		}
		b = tileEntityMap.get(name);
		if (b != null) {
			if (name.equals("City")) {
				return new City("City");
				// return new City("City");
			}
			return new TileEntity((TileEntity) b);
		}
		// System.out.println("Entity name not found");
		return null;
	}

	private static void groundColorMap() {
		groundColorMap.put(-1, 26);
		groundColorMap.put(0, 1);
		groundColorMap.put(1, 102);
		groundColorMap.put(2, 5);
		groundColorMap.put(3, 153);
		groundColorMap.put(4, 1022);
		groundColorMap.put(5, 37);
		groundColorMap.put(6, 217);
		groundColorMap.put(7, 226);
	}

}
