package render;

import java.util.ArrayList;

public class TextBox {

	public float posX, posY;
	public float sizeX, sizeY;
	public String name;
	public ArrayList<String> tooltip;
	public ArrayList<String> display;
	public Menu menu;
	//public boolean enabled;
	public ArrayList<Order> orders;

	public float origX, origY, origSizeX, origSizeY; //Public or private?
	public boolean expanded = false;
	public int[] noOrdersIfMenu = null;
	public boolean lock = false, shortcut = true;
	public boolean autoClear = true;
	public boolean active = true, monospace = false, noOverlap = false;
	
	public float r = 0, g = 0, b = 0, alpha = 255;
	public float borderR = -1, borderG = -1, borderB = -1; //default nostroke
	public int shape = 0; //0 -> rectangle (default), 1 -> ellipse
	
	public TextBox(String displayString, String t, float a, float b, float c, float d)
	{
		display = new ArrayList<String>();
		display.add(displayString);
		tooltip = new ArrayList<String>();
		tooltip.add(t);
		posX = a;
		posY = b;
		sizeX = c;
		sizeY = d;
		origX = a;
		origY = b;
		origSizeX = c;
		origSizeY = d;
		//enabled = false;
		orders = new ArrayList<Order>();
	}

	public TextBox(ArrayList<String> display, String t, float a, float b, float c, float d)
	{
		this.display = display;
		tooltip = new ArrayList<String>();
		tooltip.add(t);
		posX = a;
		posY = b;
		sizeX = c;
		sizeY = d;
		origX = a;
		origY = b;
		origSizeX = c;
		origSizeY = d;
		//enabled = false;
		orders = new ArrayList<Order>();
	}

	public TextBox(ArrayList<String> display, String t, float a, float b, float c, float d, int[] n)
	{
		this.display = display;
		tooltip = new ArrayList<String>();
		tooltip.add(t);
		noOrdersIfMenu = n;
		posX = a;
		posY = b;
		sizeX = c;
		sizeY = d;
		origX = a;
		origY = b;
		origSizeX = c;
		origSizeY = d;
		//enabled = false;
		orders = new ArrayList<Order>();
	}

	public TextBox(String displayString, String t, float a, float b, float c, float d, int[] n)
	{
		display = new ArrayList<String>();
		display.add(displayString);
		tooltip = new ArrayList<String>();
		tooltip.add(t);
		noOrdersIfMenu = n;
		posX = a;
		posY = b;
		sizeX = c;
		sizeY = d;
		origX = a;
		origY = b;
		origSizeX = c;
		origSizeY = d;
		//enabled = false;
		orders = new ArrayList<Order>();
	}
	
	public boolean equals(TextBox o)
	{
		return origX == o.origX && origY == o.origY && origSizeX == o.origSizeX && origSizeY == o.origSizeY;
	}
	
	public int[] dimTooltip()
	{
		if (tooltip.size() == 0) return new int[]{0,0};
		if (tooltip.size() == 1) return new int[]{7*tooltip.get(0).length(),20}; 
		int index = 0;
		for (int i = 0; i < tooltip.size(); i++)
		{
			//System.out.println(tooltip.get(i));
			if (tooltip.get(i) != null)
				if (tooltip.get(i).length() > tooltip.get(index).length()) 
					index = i;
		}
		return new int[]{7*tooltip.get(index).length(),14*tooltip.size()};
	}

	public void tick()
	{
		for (int i = 0; i < orders.size(); i++)
		{
			executeOrder(i);
		}
	}

	private void executeOrder(int n)
	{
		//System.out.println(orders.size());
		if (n < 0) return;
		if (n < orders.size())
		{
			//System.out.println("Executed button tick");
			Order o = orders.get(n);
			if (n == 0 || o.parallel)
			{
				o.execute();
				if (o.frames <= 0)
				{
					orders.remove(n);
					n--; //ArrayList trap
				}
			}
		}
	}

	public Order moveTo(float x, float y, float frames)
	{
		if (frames == 0) return null;
		Order temp = new Order(this,"move");
		temp.dirX = (x-posX)/frames;
		temp.dirY = (y-posY)/frames;
		temp.frames = (int)frames;
		orders.add(temp);
		return temp;
	}

	public Order moveDis(float x, float y, float frames)
	{
		if (frames == 0) return null;
		Order temp = new Order(this,"move");
		temp.dirX = x/frames;
		temp.dirY = y/frames;
		temp.frames = (int)frames;
		orders.add(temp);
		return temp;
	}

	public Order expand(float x, float y, float frames)
	{
		if (frames == 0) return null;
		expanded = true;
		Order temp = new Order(this,"expand");
		temp.expX = (x-sizeX)/frames;
		temp.expY = (y-sizeY)/frames;
		temp.frames = (int)frames;
		orders.add(temp);
		return temp;
		//System.out.println(temp.expX + " " + temp.expY);
	}

	/*public void shake(float x, float y)
	{
		Order temp = new Order(this,"move");
		temp.expX = x;
		temp.expY = y;
		temp.frames = 2;
		orders.add(temp);
	}*/

	public Order orderOriginal(boolean yn)
	{
		Order temp = new Order(this,"setOriginal");
		temp.parallel = yn;
		temp.frames = 2;
		orders.add(temp);
		return temp;
	}

	public void setOriginal()
	{
		posX = origX;
		posY = origY;
		sizeX = origSizeX;
		sizeY = origSizeY;
		expanded = false;
		orders.clear(); //To be sure
	}

	public boolean orderOfType(String type)
	{
		for (int i = 0; i < orders.size(); i++)
			if (orders.get(i).name.equals(type))
				return true;
		return false;
	}
	
	public boolean within(float x, float y)
	{
		return x > posX && x < posX + sizeX && y > posY && y < posY + sizeY;
	}

	public void move(float x, float y)
	{
		posX = x;
		posY = y;
	}
	
	public void activate(boolean yn) {active = yn;}
	public boolean active() {return active;}
	
	//Return itself for convienence
	public TextBox color(float x, float y, float z) {r = x; g = y; b = z; return this;}
	public TextBox color(float w) {return color(w,w,w);}
	public TextBox borderColor(float x, float y, float z) {borderR = x; borderG = y; borderB = z; return this;}
	public TextBox borderColor(float w) {return borderColor(w,w,w);}
	
	public class Order
	{
		public TextBox button;
		public float dirX, dirY; //speed in these directions per frame
		public float expX, expY; //dimension gain/loss per frame
		public int frames;
		public boolean parallel; //Executed even if not the first element in the "queue"
		public String name;

		public Order(TextBox button, String name)
		{
			this.button = button;
			this.name = name;
			//System.out.println(name);
		}

		public void execute()
		{
			if (!button.lock)
			{
				if (name.equals("move"))
				{
					button.posX += dirX; button.posY += dirY;
				}
				else if (name.equals("expand"))
				{
					button.sizeX += expX; button.sizeY += expY;
				}
				else if (name.equals("setOriginal"))
				{
					setOriginal();
				}
				frames--;
			}
			else
			{
				setOriginal();
			}
			//System.out.println("Frames: " + frames);
		}
	}

}
