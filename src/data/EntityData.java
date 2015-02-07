package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import processing.core.PApplet;
import processing.core.PImage;
//import processing.core.PShape;
import game.*;
import units.*;

public class EntityData {

	public static HashMap<Integer, Color> brickColorMap; //Defines integer brickcolors e.g. 21, 1003
	public static HashMap<String, GameEntity> gameEntityMap;
	public static HashMap<String, TileEntity> tileEntityMap;
	public static HashMap<Integer, Integer> groundColorMap; //Defines color of ground of certain biomes
	private static HashMap<String, float[][]> unitModelMap;
	public static HashMap<String, PImage> unitIconMap;

	//Terrible short hand names
	private static HashMap<String, Integer> f, m, g;

	public static HashMap<String, Improvement> unitImprovementMap;
	public static HashMap<String, Improvement> cityImprovementMap;
	private static HashMap<String, Field> fieldMap;

	public static HashMap<String,ArrayList<String>> encyclopediaEntries;

	public static HashMap<String,Civilization> civs;
	public static HashMap<String,CityState> cityStates;

	public EntityData()
	{

	}

	public static void init()
	{
		brickColorMap = new HashMap<Integer,Color>();
		gameEntityMap = new HashMap<String, GameEntity>();
		tileEntityMap = new HashMap<String, TileEntity>();
		groundColorMap = new HashMap<Integer, Integer>();
		unitModelMap = new HashMap<String, float[][]>();
		unitIconMap = new HashMap<String, PImage>();

		unitImprovementMap = new HashMap<String, Improvement>();
		cityImprovementMap = new HashMap<String, Improvement>();
		fieldMap = new HashMap<String, Field>();

		encyclopediaEntries = new HashMap<String,ArrayList<String>>();
		civs = new HashMap<String,Civilization>();
		cityStates = new HashMap<String,CityState>();

		f = new HashMap<String, Integer>();
		m = new HashMap<String, Integer>();
		g = new HashMap<String, Integer>();

		setupColors();
		setupEntityMap();
		groundColorMap();
		setupUnitCosts();
		setupUnitIcons();
		setupUnitImprovementCosts(); //longest name yet
		setupCityImprovementCosts();
		setupFields();
		setupCivBonuses();

		//setModels();
	}

	private static void setupColors()
	{
		brickColorMap.put(1,new Color(0.94901967048645,0.95294123888016,0.95294123888016));
		brickColorMap.put(5,new Color(0.84313732385635,0.77254909276962,0.60392159223557));
		brickColorMap.put(9,new Color(0.90980398654938,0.7294117808342,0.78431379795074));
		brickColorMap.put(11,new Color(0.50196081399918,0.73333334922791,0.85882359743118));
		brickColorMap.put(18,new Color(0.80000007152557,0.55686277151108,0.41176474094391));
		brickColorMap.put(21,new Color(0.76862752437592,0.15686275064945,0.10980392992496));
		brickColorMap.put(23,new Color(0.050980396568775,0.41176474094391,0.6745098233223));
		brickColorMap.put(24,new Color(0.96078437566757,0.80392163991928,0.18823531270027));
		brickColorMap.put(26,new Color(0.10588236153126,0.16470588743687,0.20784315466881));
		brickColorMap.put(28,new Color(0.15686275064945,0.49803924560547,0.27843138575554));
		brickColorMap.put(29,new Color(0.63137257099152,0.76862752437592,0.54901963472366));
		brickColorMap.put(37,new Color(0.29411765933037,0.59215688705444,0.29411765933037));
		brickColorMap.put(38,new Color(0.62745100259781,0.37254902720451,0.20784315466881));
		brickColorMap.put(45,new Color(0.70588237047195,0.82352948188782,0.89411771297455));
		brickColorMap.put(101,new Color(0.85490202903748,0.52549022436142,0.47843140363693));
		brickColorMap.put(102,new Color(0.43137258291245,0.60000002384186,0.79215693473816));
		brickColorMap.put(104,new Color(0.41960787773132,0.19607844948769,0.48627454042435));
		brickColorMap.put(105,new Color(0.88627457618713,0.60784316062927,0.25098040699959));
		brickColorMap.put(106,new Color(0.85490202903748,0.52156865596771,0.2549019753933));
		brickColorMap.put(107,new Color(0,0.56078433990479,0.61176472902298));
		brickColorMap.put(119,new Color(0.64313727617264,0.74117648601532,0.27843138575554));
		brickColorMap.put(125,new Color(0.91764712333679,0.72156864404678,0.57254904508591));
		brickColorMap.put(135,new Color(0.45490199327469,0.52549022436142,0.61568629741669));
		brickColorMap.put(141,new Color(0.15294118225574,0.27450981736183,0.17647059261799));
		brickColorMap.put(151,new Color(0.47058826684952,0.56470590829849,0.50980395078659));
		brickColorMap.put(153,new Color(0.58431375026703,0.47450983524323,0.46666669845581));
		brickColorMap.put(192,new Color(0.41176474094391,0.25098040699959,0.15686275064945));
		brickColorMap.put(194,new Color(0.63921570777893,0.63529413938522,0.64705884456635));
		brickColorMap.put(199,new Color(0.38823533058167,0.37254902720451,0.38431376218796));
		brickColorMap.put(208,new Color(0.89803928136826,0.89411771297455,0.87450987100601));
		brickColorMap.put(217,new Color(0.48627454042435,0.36078432202339,0.27450981736183));
		brickColorMap.put(226,new Color(0.99215692281723,0.91764712333679,0.55294120311737));
		brickColorMap.put(1001,new Color(0.97254908084869,0.97254908084869,0.97254908084869));
		brickColorMap.put(1002,new Color(0.80392163991928,0.80392163991928,0.80392163991928));
		brickColorMap.put(1003,new Color(0.066666670143604,0.066666670143604,0.066666670143604));
		brickColorMap.put(1004,new Color(1,0,0));
		brickColorMap.put(1005,new Color(1,0.68627452850342,0));
		brickColorMap.put(1006,new Color(0.70588237047195,0.50196081399918,1));
		brickColorMap.put(1007,new Color(0.63921570777893,0.29411765933037,0.29411765933037));
		brickColorMap.put(1008,new Color(0.75686281919479,0.74509805440903,0.258823543787));
		brickColorMap.put(1009,new Color(1,1,0));
		brickColorMap.put(1010,new Color(0,0,1));
		brickColorMap.put(1011,new Color(0,0.12549020349979,0.37647062540054));
		brickColorMap.put(1012,new Color(0.1294117718935,0.32941177487373,0.72549021244049));
		brickColorMap.put(1013,new Color(0.015686275437474,0.68627452850342,0.92549026012421));
		brickColorMap.put(1014,new Color(0.66666668653488,0.33333334326744,0));
		brickColorMap.put(1015,new Color(0.66666668653488,0,0.66666668653488));
		brickColorMap.put(1016,new Color(1,0.40000003576279,0.80000007152557));
		brickColorMap.put(1017,new Color(1,0.68627452850342,0));
		brickColorMap.put(1018,new Color(0.070588238537312,0.93333339691162,0.83137261867523));
		brickColorMap.put(1019,new Color(0,1,1));
		brickColorMap.put(1020,new Color(0,1,0));
		brickColorMap.put(1021,new Color(0.22745099663734,0.49019610881805,0.082352943718433));
		brickColorMap.put(1022,new Color(0.49803924560547,0.55686277151108,0.39215689897537));
		brickColorMap.put(1023,new Color(0.54901963472366,0.35686275362968,0.6235294342041));
		brickColorMap.put(1024,new Color(0.68627452850342,0.8666667342186,1));
		brickColorMap.put(1025,new Color(1,0.78823536634445,0.78823536634445));
		brickColorMap.put(1026,new Color(0.69411766529083,0.65490198135376,1));
		brickColorMap.put(1027,new Color(0.6235294342041,0.95294123888016,0.91372555494308));
		brickColorMap.put(1028,new Color(0.80000007152557,1,0.80000007152557));
		brickColorMap.put(1029,new Color(1,1,0.80000007152557));
		brickColorMap.put(1030,new Color(1,0.80000007152557,0.60000002384186));
		brickColorMap.put(1031,new Color(0.38431376218796,0.14509804546833,0.81960791349411));
		brickColorMap.put(1032,new Color(1,0,0.74901962280273));
	}

	public static void setupCivBonuses()
	{
		civs.clear();
		civs.put("Achaea", new Civilization("Achaea",list(),0,0,255,0.3,0.3,1));
		civs.put("Athens", new Civilization("Athens",list(),200,200,200,0.2,0.8,0.3));
		civs.put("Corinth", new Civilization("Corinth",list(),0,255,255,0.5,0.5,0.5));
		civs.put("Crete", new Civilization("Crete",list(),0,150,0,0.3,0.6,0.3));
		//civs.put("Ephesus", new Civilization("Ephesus",list(),150,150,150));
		civs.put("Epirus", new Civilization("Epirus",list(),0,0,128,0.7,0,0.5));
		civs.put("Illyria", new Civilization("Illyria",list(),0,255,0,0.5,0.5,0.8));
		//civs.put("Lydia", new Civilization("Lydia",list(),150,150,150));
		civs.put("Macedonia", new Civilization("Macedonia",list(),255,150,0,0.8,0.6,1));
		civs.put("Rhodes", new Civilization("Rhodes",list(),175,175,0,0.1,0.8,0.3));
		civs.put("Sparta", new Civilization("Sparta",list(),255,0,0,0.8,0.1,0.3));
		//civs.put("Thessaly", new Civilization("Thessaly",list(),150,150,150));
		civs.put("Thrace", new Civilization("Thrace",list(),175,255,255,0.6,0.4,0.5));

		/*civs.put("Achaea", new Civilization("Achaea",list(),0,0,255,0.3,0.3,1));
		civs.put("Athens", new Civilization("Athens",list(),255,255,255,0.2,0.8,1));
		civs.put("Corinth", new Civilization("Corinth",list(),0,255,255,0.5,0.5,0.8));
		civs.put("Crete", new Civilization("Crete",list(),0,150,0,0.3,0.6,0.8));
		//civs.put("Ephesus", new Civilization("Ephesus",list(),150,150,150));
		civs.put("Epirus", new Civilization("Epirus",list(),0,0,128,0.7,0,0.8));
		civs.put("Illyria", new Civilization("Illyria",list(),0,255,0,0.5,0.5,0.8));
		//civs.put("Lydia", new Civilization("Lydia",list(),150,150,150));
		civs.put("Macedonia", new Civilization("Macedonia",list(),255,150,0,0.8,0.6,1));
		civs.put("Rhodes", new Civilization("Rhodes",list(),175,175,0,0.1,0.8,1));
		civs.put("Sparta", new Civilization("Sparta",list(),255,0,0,0.8,0.1,1));
		//civs.put("Thessaly", new Civilization("Thessaly",list(),150,150,150));
		civs.put("Thrace", new Civilization("Thrace",list(),175,255,255,0.6,0.4,0.8));*/

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

		for (int i = 0; i < 30; i++)
		{
			ArrayList<String> traits = new ArrayList<String>();
			traits.add("Aggressive");
			traits.add("Defensive");
			traits.add("Imperialistic");
			traits.add("Industrious");
			traits.add("Prosperous");
			traits.add("Refined");
			traits.add("Traditional");
			cityStates.put(""+i, new CityState(""+i,list(),
					(float)(Math.random()*255),
					(float)(Math.random()*255),
					(float)(Math.random()*255),
					Math.random(),Math.random(),0
					));
			cityStates.get(""+i).traits(traits.get((int)(Math.random()*traits.size())), "");
		}

	}

	public static String[] traitDesc(String trait)
	{
		/*
		 * Traits for civs:
		 * 3 Aggressive    > +10% when attacking,     1 free promotion per unit
		 * 3 Imperialistic > 2 health per city,       > 1 extra trade route per city
		 * 2 Refined       > 2 culture per city,      > +10% research speed
		 * 4 Prosperous    > +15% food per city,      > +25% speed on Settler 
		 * 3 Defensive     > +15% when defending,     +25% speed on fortifications
		 * 3 Traditional   +25% speed on unique unit, > +15% food per city
		 * 2 Industrious   > +10% production/metal,   +25% on wonders 
		 */
		if (trait.equals("Aggressive"))
			return new String[]{"+10% when attacking","1 free promotion per unit"};
		else if (trait.equals("Defensive"))
			return new String[]{"+15% when defending","+25% speed on fortifications"};
		else if (trait.equals("Imperialistic"))
			return new String[]{"2 health per city","1 extra trade route per city"};
		else if (trait.equals("Industrious"))
			return new String[]{"+10% production/metal","+25% on wonders "};
		else if (trait.equals("Prosperous"))
			return new String[]{"+15% food per city","+25% speed on Settler "};
		else if (trait.equals("Refined"))
			return new String[]{"2 culture per city","+10% research speed"};
		else if (trait.equals("Traditional"))
			return new String[]{"+25% speed on unique unit","+15% food per city"};
		else
		{
			System.out.println("Invalid trait: " + trait);
			return null;
		}
	}

	public static ArrayList<String> list(String... strings)
	{
		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < strings.length; i++)
			temp.add(strings[i]);
		return temp;
	}

	public static float[][] getModel(String name)
	{
		return unitModelMap.get(name);
	}

	public static String getBiome(int biome)
	{
		if (biome == -1)
		{
			return "Sea";
		}
		else if (biome == 0)
		{
			return "Ice";
		}
		else if (biome == 1)
		{
			return "Taiga";
		}
		else if (biome == 2)
		{
			return "Desert";
		}
		else if (biome == 3)
		{
			return "Savannah";
		}
		else if (biome == 4)
		{
			return "Dry Forest";
		}
		else if (biome == 5)
		{
			return "Forest";
		}
		else if (biome == 6)
		{
			return "Rainforest";
		}
		else if (biome == 8)
		{
			return "Oasis";
		}
		System.err.println("Invalid biome");
		return null;
	}

	private static void setupEntityMap()
	{
		gameEntityMap.put("Settler",new Settler("Settler",0,2,0).mode(0));
		gameEntityMap.put("Warrior",new Warrior("Warrior",2,2,0));
		gameEntityMap.put("Worker",new Worker("Worker",0,2,0).mode(0));
		gameEntityMap.put("Scout",new Worker("Scout",0,2,0).mode(0).maxAction(2));

		gameEntityMap.put("Caravan",new Caravan("Caravan",0,2,0).mode(0).maxAction(0));

		gameEntityMap.put("Axeman",new Warrior("Axeman",4,3,0));
		gameEntityMap.put("Warband",new Warrior("Warband",2,4,1).range(1).maxAction(2));
		gameEntityMap.put("Swordsman",new Warrior("Swordsman",5,4,0));
		gameEntityMap.put("Spearman",new Warrior("Spearman",2,4,0));
		gameEntityMap.put("Axe Thrower",new Warrior("Axe Thrower",4,3,3).range(1));

		gameEntityMap.put("Chariot",new Warrior("Chariot",4,1,1).range(2).mode(2).maxAction(2));
		gameEntityMap.put("Horseman",new Warrior("Horseman",4,1,0).maxAction(2));

		gameEntityMap.put("Slinger",new Warrior("Slinger",0,2,2).range(2).mode(2));
		gameEntityMap.put("Archer",new Warrior("Archer",0,4,4).range(2).mode(2));
		gameEntityMap.put("Horse Archer",new Warrior("Horse Archer",0,0,4).range(2).mode(2).maxAction(2));

		gameEntityMap.put("Galley",new Galley("Galley",4,4,0));
		gameEntityMap.put("Work Boat",new WorkBoat("Work Boat",0,2,0).mode(0));

		tileEntityMap.put("City",new City("City"));
		tileEntityMap.put("Farm",new TileEntity("Farm"));
		tileEntityMap.put("Windmill",new TileEntity("Windmill"));
		tileEntityMap.put("Lumbermill",new TileEntity("Lumbermill"));
		tileEntityMap.put("Mine",new TileEntity("Mine"));
		tileEntityMap.put("Forest Yard",new TileEntity("Forest Yard"));
		tileEntityMap.put("Trading Post",new TileEntity("Trading Post"));
		tileEntityMap.put("Pasture",new TileEntity("Pasture"));
		tileEntityMap.put("Fishing Boats",new TileEntity("Fishing Boats"));
		tileEntityMap.put("Ruins",new TileEntity("Ruins"));
	}

	private static void setupUnitCosts()
	{
		cost("Settler",35,0,0);
		cost("Warrior",10,5,0);
		cost("Work Boat",15,0,0);
		cost("Worker",25,0,0);
		cost("Scout",10,5,0);
		cost("Caravan",15,5,0);

		cost("Axeman",10,10,0);
		cost("Warband",15,5,0);
		cost("Swordsman",10,15,0);
		cost("Spearman",10,10,0);
		cost("Chariot",10,15,0);
		cost("Slinger",10,5,0);
		cost("Archer",15,10,0);
		cost("Axe Thrower",10,15,0);
		cost("Horseman",15,5,0);
		cost("Horse Archer",15,10,0);
	}

	private static void cost(String name, int food, int gold, int metal)
	{
		f.put(name, food);
		g.put(name, gold);
		m.put(name, metal);
	}

	public static float[] getCost(String name)
	{
		if (f.get(name) == null)
		{
			Improvement impr = cityImprovementMap.get(name);
			return new float[]{(float)impr.foodFlat, (float)impr.goldFlat, (float)impr.metalFlat};
		}
		return new float[]{f.get(name),g.get(name),m.get(name)};
	}

	private static void setupUnitImprovementCosts()
	{
		Improvement temp;
		temp = new Improvement("Neutral","No improvement","Civilization");
		temp.cost(1,1,1,0,0,0);
		temp.set(1,1,1,0,0,0,1);
		unitImprovementMap.put(temp.name, temp);

		temp = new Improvement("CopperTools","Not impl.","Metal Working");
		temp.cost(1.25,1.25,0,0,0,0);
		temp.set(0,0,0,0,0,0,0.8);
		temp.fit("Worker");
		unitImprovementMap.put(temp.name, temp);
		/*temp = new Improvement("Test","");
		temp.cost(2,0,0,0,0,0);
		temp.set(0,0,0,0,0,0,0.2);
		temp.fit("Worker");
		unitImprovementMap.put(temp.name, temp);*/

		temp = new Improvement("CopperWeapons","Not impl.","Metal Working");
		temp.cost(1.25,1.5,0,0,0,0);
		temp.set(1.25,1.4,0,0,0,0,0);
		temp.fit("allmelee");
		unitImprovementMap.put(temp.name, temp);
		temp = new Improvement("IronWeapons","Not impl.","Metal Working");
		temp.cost(1.25,2,0,0,0,0);
		temp.set(1.5,1.5,0,0,0,0,0);
		temp.fit("allmelee");
		unitImprovementMap.put(temp.name, temp);
		temp = new Improvement("CopperArrows","Not impl.","Casting");
		temp.cost(1.25,1.25,0,0,0,0);
		temp.set(0,0,1.25,0,0,0,0);
		temp.fit("allranged");
		unitImprovementMap.put(temp.name, temp);
		temp = new Improvement("IronArrows","Not impl.","Casting");
		temp.cost(1.25,1.5,0,0,0,0);
		temp.set(0,0,1.5,0,0,0,0);
		temp.fit("allranged");
		unitImprovementMap.put(temp.name, temp);
	}

	private static void setupCityImprovementCosts()
	{
		Improvement temp;
		temp = new Improvement("Obelisk","+1 culture","Civilization");
		temp.cost(0,0,0,10,10,0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Warehouse","Not impl.","Mining");
		temp.cost(0,0,0,5,20,0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Granary","+15% to population growth","Agriculture");
		temp.cost(0,0,0,5,20,0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Metalworks","+25% to metal production","Metal Working");
		temp.cost(0,0,0,10,50,0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Port","+25% food from sea tiles","Fishing");
		temp.cost(0,0,0,25,25,0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Walls","+40% defense in city","Stone Working");
		temp.cost(0,0,0,5,50,0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Palace","+25% revenue, +1 culture","Stone Working");
		temp.cost(0,0,0,25,25,0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Stables","Not impl.","Equestrian Practice");
		temp.cost(0,0,0,25,25,0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Market","+25% revenue","Currency");
		temp.cost(0,0,0,30,20,0);
		cityImprovementMap.put(temp.name, temp);

		temp = new Improvement("Pyramid","+25% culture","Monument Building");
		temp.cost(0,0,0,0,50,0);
		cityImprovementMap.put(temp.name, temp);
		//temp.set();
		//cityImprovementMap.put(temp.name, temp);
	}

	private static void setupFields()
	{
		Field temp;
		temp = new Field(null,"TestField","Test","Agriculture");
		temp.cost(0,0,0,0,20,0);
		fieldMap.put(temp.name, temp);
	}

	public static Field getField(String name)
	{
		return new Field(fieldMap.get(name));
	}

	private static void setupUnitIcons()
	{

	}

	/*public static void awardField(Civilization civ, Tile t, String name)
	{

	}*/

	public static ArrayList<Improvement> getValidImprovements(Civilization civ, BaseEntity en)
	{
		ArrayList<Improvement> temp = new ArrayList<Improvement>();
		for (Entry<String, Improvement> entry: unitImprovementMap.entrySet())
		{
			String name = entry.getKey();
			Improvement i = entry.getValue();
			//Split into many if statements for special improvement conditions later
			if (!civ.techTree.researched(i.requiredTech).researched()) continue;
			if (i.equals("Neutral"))
			{
				continue;
			}
			if (i.isFit(en.name))
			{
				temp.add(i);
			}
			else if (i.units[0].equals("allmelee") && en.offensiveStr > 0)
			{
				temp.add(i);
			}
			else if (i.units[0].equals("allranged") && en.rangedStr > 0)
			{
				temp.add(i);
			}
		}
		//System.out.println(temp.size());
		return temp;
	}

	//Return true if successfully queued in a city not undergoing hostile takeover
	//Edit: Returns an improvement (could be neutral) if successfully queued
	public static Improvement queue(City c, String queue)
	{
		if (queue == null) return null;
		if (c.takeover <= 0 || c.raze)
		{
			c.queue = queue;
			Improvement i = c.owner.unitImprovements.get(queue);
			if (i == null)
			{
				Improvement building = cityImprovementMap.get(queue);
				if (building != null)
				{	
					c.queueFood = (int)building.foodFlat;
					c.queueMetal = (int)building.metalFlat;
				}
				else
				{
					c.queueFood = f.get(queue);
					c.queueMetal = m.get(queue);
				}
				return unitImprovementMap.get("Neutral");
			}
			else
			{
				if (i.foodPercent != 0)
					c.queueFood = (int)(f.get(queue)*i.foodPercent);
				if (i.metalPercent != 0)
					c.queueMetal = (int)(m.get(queue)*i.metalPercent);
				return i;
			}
			//return true;
		}
		return null;
		//return false;
	}

	/*
	 Return the technology that the civilization is priortizing
	 If playing a militaristic game, go for units (particularly metal casting)
	 If playing a turtle/economic game, go for useful improvements
	 */
	public static void queueTechAi(Civilization civ)
	{
		//Temporary algorithm
		civ.beeline.clear();
		if (civ.war > civ.peace)
		{
			civ.beeline.add("Mining");
			civ.beeline.add("Agriculture");
			civ.beeline.add("Metal Working");
			civ.beeline.add("Animal Husbandry");
			civ.beeline.add("Milling");
			civ.beeline.add("Casting");
		}
		else
		{
			civ.beeline.add("Agriculture");
			civ.beeline.add("Mining");
			civ.beeline.add("Milling");
			civ.beeline.add("Hunting");
			civ.beeline.add("Metal Working");
			civ.beeline.add("Fletching");
		}
	}

	//TODO: Factor in level of technology and available units
	public static Improvement queueAi(City c, boolean civ)
	{
		String queue = null;
		int p = 0, cities = c.owner.count("Settler");
		for (int i = 0; i < c.owner.cities.size(); i++) 
		{
			p += c.owner.cities.get(i).population;
			cities++;
		}

		if (c.owner.units.size() < 3)
		{
			queue = "Worker";
		}
		else if (c.owner.cities.size() == 1)
		{
			if (c.expanded == 1)
			{
				if (Math.random() < 0.8*(1-c.owner.tallwide))
				{
					if (c.owner.count("Worker") < c.owner.cities.size()*2)
						queue = "Worker";
					else
						queue = bestBuilding(c);
				}
				else
					if (p < cities*4 && civ && cities < 6)
						queue = "Settler";
					else
						queue = bestUnit(c.owner, c.location.grid.civs);
			}
			else 
				if (Math.random() < 0.4*c.owner.tallwide)
				{
					if (p < cities*4 && civ && cities < 6)
						queue = "Settler";
					else
						queue = bestUnit(c.owner, c.location.grid.civs);
				}
				else if (Math.random() < 0.7)
					queue = bestBuilding(c);
				else
					if (c.owner.count("Worker") < c.owner.cities.size()*2)
						queue = "Worker";
					else
						queue = bestUnit(c.owner, c.location.grid.civs);
		}
		else
		{
			if (Math.random() < 0.3*c.owner.tallwide)
			{
				if (p < cities*4 && civ && cities < 6)// && c.owner.health > -5)
					queue = "Settler";
				else
					queue = bestUnit(c.owner, c.location.grid.civs);
			}
			else
				queue = bestUnit(c.owner, c.location.grid.civs);
		}
		if (queue == null) 
			queue = bestUnit(c.owner, c.location.grid.civs);
		if (c.owner.units.size() > 5*c.owner.cities.size())
			return null;
		//System.out.println(queue);
		return queue(c, queue);
	}

	//Decide which city improvement is best
	public static String bestBuilding(City c)
	{
		ArrayList<String> allowed = c.owner.techTree.allowedCityImprovements;
		if (allowed(c,"Granary")) return "Granary";
		if (c.cityFocus == 3)
		{
			if (Math.random() < 0.25)
				if (allowed(c,"Warehouse")) return "Warehouse";
			if (allowed(c,"Port")) return "Port";
			if (allowed(c,"Market")) return "Market";
			if (allowed(c,"Metalworks")) return "Metalworks";
			if (allowed(c,"Walls")) return "Walls";
		}
		else if (c.cityFocus == 2)
		{
			if (Math.random() < 0.25)
				if (allowed(c,"Warehouse")) return "Warehouse";
			if (allowed(c,"Metalworks")) return "Metalworks";
			if (allowed(c,"Walls")) return "Walls";
			if (allowed(c,"Stables")) return "Stables";
			if (allowed(c,"Market")) return "Market";
		}
		else if (c.cityFocus == 1)
		{
			if (allowed(c,"Library")) return "Library";
			if (allowed(c,"Port")) return "Port";
			if (allowed(c,"Market")) return "Market";
			if (allowed(c,"Palace")) return "Palace";
		}
		else
		{
			if (Math.random() < 0.25)
				if (allowed(c,"Warehouse")) return "Warehouse";
			if (allowed(c,"Port")) return "Port";
			if (allowed(c,"Market")) return "Market";
		}
		for (int i = 0; i < 10; i++) //10 trials
		{
			if (allowed.size() == 0) return null;
			String candidate = allowed.get((int)(Math.random()*allowed.size()));
			if (allowed(c, candidate))
				return candidate;
		}
		return null;
		//return 
	}

	public static boolean allowed(City c, String building)
	{
		return c.owner.techTree.allowedCityImprovements.contains(building) &&
				!c.built(building);
	}

	//Decide which unit is best unit to counter an enemy unit
	public static String bestUnit(Civilization civ, Civilization[] enemies)
	{
		ArrayList<String> allowed = civ.techTree.allowedUnits;
		float heavyMelee = 0, lightMelee = 0, ranged = 0, mounted = 0;
		for (int i = 0; i < enemies.length; i++)
		{
			heavyMelee += enemies[i].count("Axeman", "Spearman", "Swordsman");
			lightMelee += enemies[i].count("Axe Thrower", "Warband", "Warrior");
			ranged += enemies[i].count("Archer", "Slinger");
			mounted += enemies[i].count("Chariot", "Horse Archer", "Horseman");
		}
		float sum = heavyMelee + lightMelee + ranged + mounted;
		//System.out.println(sum);
		if (sum == 0)
			return "Warrior";
		ArrayList<Float> data = new ArrayList<Float>();
		data.add(heavyMelee); data.add(lightMelee); data.add(ranged); data.add(mounted); 
		Collections.sort(data);
		double r = Math.random();
		if (data.get(3) == heavyMelee)
		{
			if (r < 0.2)
			{
				if (allowed.contains("Spearman")) return "Spearman";
			}
			if (r < 0.5)
			{
				if (allowed.contains("Swordsman")) return "Swordsman";
			}
			if (r < 0.6)
			{
				if (allowed.contains("Axe Thrower")) return "Axe Thrower";
			}
			if (allowed.contains("Axeman")) return "Axeman";
		}
		else if (data.get(3) == lightMelee)
		{
			if (r < 0.25)
			{
				if (allowed.contains("Archer")) return "Archer";
			}
			if (r < 0.5)
			{
				if (allowed.contains("Swordsman")) return "Swordsman";
			}
			if (allowed.contains("Axeman")) return "Axeman";
		}
		else if (data.get(3) == ranged)
		{
			if (r < 0.25)
			{
				if (allowed.contains("Horse Archer")) return "Horse Archer";
			}
			if (r < 0.5)
			{
				if (allowed.contains("Horseman")) return "Horseman";
			}
			if (r < 0.6)
			{
				if (allowed.contains("Archer")) return "Archer";
			}
			if (allowed.contains("Warband")) return "Warband";
		}
		else if (data.get(3) == mounted)
		{
			if (r < 0.25)
			{
				if (allowed.contains("Horse Archer")) return "Horse Archer";
			}
			if (r < 0.5)
			{
				if (allowed.contains("Archer")) return "Archer";
			}
			if (allowed.contains("Spearman")) return "Spearman";
			if (allowed.contains("Warband")) return "Warband";
		}
		else
		{
			System.out.println("Invalid queue");
			return null;
		}
		if (allowed.contains("Axeman")) return "Axeman";
		if (allowed.contains("Warband")) return "Warband";
		return "Warrior";
	}

	public static boolean queueCityImprovement(City city, String impr)
	{
		if (!city.hasImprovement(impr))
		{
			Improvement i = cityImprovementMap.get(impr);
			city.queue = i.name;
			city.queueFood = (int)i.foodFlat;
			city.queueMetal = (int)i.metalFlat;
			return true;
		}
		return false;
	}
	
	//Temp. replace with a hashmap
	public static void queueTileImprovement(GameEntity en, String tileImpr)
	{
		en.queue = tileImpr;
		en.queueTurns = tileImprovementTime(en, tileImpr);
	}
	
	public static int tileImprovementTime(GameEntity en, String tileImpr)
	{
		int temp = -1;
		if (tileImpr.equals("Mine"))
			temp = 6;
		else if (tileImpr.equals("Farm"))
			temp = 6;
		else if (tileImpr.equals("Windmill") || tileImpr.equals("Lumbermill"))
			temp = 10;
		else
		{
			System.out.println("Invalid tile improvement: " + tileImpr);
			temp = -1;
		}
		temp = Math.max(1,(int)(temp*((Worker)en).workTime));
		return temp;
	}

	public static Color getResourceColor(int res)
	{
		switch (res)
		{
		case 1: return EntityData.brickColorMap.get(106);
		case 2: return EntityData.brickColorMap.get(1);

		case 10: return EntityData.brickColorMap.get(23);
		case 11: return EntityData.brickColorMap.get(1011);

		case 20: return EntityData.brickColorMap.get(1014);
		case 21: return EntityData.brickColorMap.get(194);
		case 22: return EntityData.brickColorMap.get(26);

		case 30: return EntityData.brickColorMap.get(21);

		case 40: return EntityData.brickColorMap.get(45);

		default: 
			System.err.println("Invalid resource " + res);
			return null;
		}
	}

	public static String getResourceName(int res)
	{
		switch (res)
		{
		case 1: return "Wheat";
		case 2: return "Rice";

		case 10: return "Fish";
		case 11: return "Whale";

		case 20: return "Copper";
		case 21: return "Iron";
		case 22: return "Coal";

		case 30: return "Redwood";

		case 40: return "Spring";

		default: 
			System.err.println("Invalid resource " + res);
			return null;
		}
	}

	public static void passModelData(String name, String[] data)
	{
		float[][] temp = new float[data.length][10];
		for (int line = 0; line < data.length; line++)
		{
			String[] split = PApplet.split(data[line], ",");
			for (int i = 0; i < split.length; i++)
			{
				if (i == 0)
				{
					if (split[0].equals("Color"))
					{
						temp[line][i] = 1;
					}
					else
					{
						temp[line][i] = 0;
					}
				}
				else
				{
					if (i >= 4 && i <= 6)
					{
						temp[line][i] = (float)Math.toRadians(Float.parseFloat(split[i]));
					}
					else
						temp[line][i] = Float.parseFloat(split[i]);
				}
			}
		}
		unitModelMap.put(name, temp);
	}

	public static String[] allUnitNames()
	{
		return new String[]
				{
				"Galley",
				"Settler",
				"Warrior",
				"Work Boat",
				"Worker"
				};
	}

	public static BaseEntity get(String name)
	{
		/*for (Entry e: gameEntityMap.entrySet())
		{
			if (e.getKey().equals(name))
			{
				return new GameEntity()
			}
		}*/
		//System.out.println(name);
		BaseEntity b = gameEntityMap.get(name);
		if (b != null)
		{
			//TODO: Fix this so that it doesn't return a generic GameEntity
			if (b.offensiveStr > 0 || b.rangedStr > 0)
				return new Warrior((GameEntity)b);
			else if (name.equals("Settler"))
				return new Settler((GameEntity)b);
			else if (name.equals("Galley"))
				return new Galley((GameEntity)b);
			else if (name.equals("Work Boat"))
				return new WorkBoat((GameEntity)b);
			else if (name.equals("Worker"))
				return new Worker((GameEntity)b);
		}
		b = tileEntityMap.get(name);
		if (b != null)
		{
			if (name.equals("City"))
			{
				return new City("City");
				//return new City("City");
			}
			return new TileEntity((TileEntity)b);
		}
		//System.out.println("Entity name not found");
		return null;
	}

	private static void groundColorMap()
	{
		groundColorMap.put(-1,26);
		groundColorMap.put(0,1);
		groundColorMap.put(1,102);
		groundColorMap.put(2,5);
		groundColorMap.put(3,1022);
		groundColorMap.put(4,1022);
		groundColorMap.put(5,37);
		groundColorMap.put(6,217);
		groundColorMap.put(7,226);
	}

}
