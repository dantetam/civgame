package render;

import java.util.ArrayList;

public class Button {

	public float posX, posY;
	public float sizeX, sizeY;
	public String command;
	public String display;
	//public boolean enabled;
	public ArrayList<Order> orders;
	
	private float origX, origY, origSizeX, origSizeY;
	public boolean expanded = false;
	
	public Button(String command, String display, float a, float b, float c, float d)
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

	public void tick()
	{
		executeOrder(0);
		for (int i = 0; i < orders.size(); i++)
		{
			executeOrder(i);
		}
	}

	private void executeOrder(int n)
	{
		if (n < orders.size())
		{
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

	public void moveTo(float x, float y, float frames)
	{
		if (frames == 0) return;
		Order temp = new Order(this,"move");
		temp.dirX = (x-posX)/frames;
		temp.dirY = (y-posY)/frames;
		orders.add(temp);
	}

	public void expand(float x, float y, float frames)
	{
		if (frames == 0) return;
		expanded = true;
		Order temp = new Order(this,"expand");
		temp.expX = (x-sizeX)/frames;
		temp.expY = (y-sizeY)/frames;
		orders.add(temp);
		System.out.println(temp.expX + " " + temp.expY);
	}

	public void setOriginal()
	{
		posX = origX;
		posY = origY;
		sizeX = origSizeX;
		sizeY = origSizeY;
	}
	
	public boolean orderOfType(String type)
	{
		for (int i = 0; i < orders.size(); i++)
			if (orders.get(i).name.equals(type))
				return true;
		return false;
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
			button.posX += dirX; button.posY += dirY;
			button.sizeX += expX; button.sizeY += expY;
			frames--;
			System.out.println("executed");
		}
	}

}
