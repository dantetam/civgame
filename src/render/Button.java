package render;

import java.util.ArrayList;

public class Button {

	public float posX, posY;
	public float sizeX, sizeY;
	public String command;
	public ArrayList<String> display;
	//public boolean enabled;
	public ArrayList<Order> orders;

	public float origX, origY, origSizeX, origSizeY; //Public or private?
	public boolean expanded = false;
	public int[] noOrdersIfMenu = null;
	public boolean lock = false;
	public boolean active = true;
	
	public Button(String command, String displayString, float a, float b, float c, float d)
	{
		this.command = command;
		display = new ArrayList<String>();
		display.add(displayString);
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

	public Button(String command, ArrayList<String> display, float a, float b, float c, float d)
	{
		this.command = command;
		this.display = display;
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

	public Button(String command, ArrayList<String> display, float a, float b, float c, float d, int[] n)
	{
		this.command = command;
		this.display = display;
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

	public Button(String command, String displayString, float a, float b, float c, float d, int[] n)
	{
		this.command = command;
		display = new ArrayList<String>();
		display.add(displayString);
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

	private void setOriginal()
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

	public void move(float x, float y)
	{
		posX = x;
		posY = y;
	}

	public class Order
	{
		public Button button;
		public float dirX, dirY; //speed in these directions per frame
		public float expX, expY; //dimension gain/loss per frame
		public int frames;
		public boolean parallel; //Executed even if not the first element in the "queue"
		public String name;

		public Order(Button button, String name)
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
