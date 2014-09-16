package data;

import java.util.HashMap;
import java.util.Map.Entry;

import entity.Entity;
import game.*;
import units.*;

public class EntityData {

	public static HashMap<Integer, Color> brickColorMap; //Defines integer brickcolors e.g. 21, 1003
	public static HashMap<String, GameEntity> gameEntityMap;
	public static HashMap<String, TileEntity> tileEntityMap;
	public static HashMap<Integer, Integer> groundColorMap; //Defines color of ground of certain biomes
	
	public EntityData()
	{
		
	}
	
	public static void init()
	{
		brickColorMap = new HashMap<Integer,Color>();
		gameEntityMap = new HashMap<String, GameEntity>();
		tileEntityMap = new HashMap<String, TileEntity>();
		groundColorMap = new HashMap<Integer, Integer>();
		
		setupColors();
		setupEntityMap();
		groundColorMap();
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
	
	private static void setupEntityMap()
	{
		gameEntityMap.put("Settler",new GameEntity("Settler"));
		gameEntityMap.put("Worker",new GameEntity("Worker"));
		
		tileEntityMap.put("City",new City("City"));
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
			return new GameEntity((GameEntity)b);
		}
		b = tileEntityMap.get(name);
		if (b != null)
		{
			if (name.equals("City"))
			{
				return new City((City)b);
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
