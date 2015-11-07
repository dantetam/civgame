package render;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import lwjglEngine.fontMeshCreator.FontType;
import lwjglEngine.fontRendering.TextMaster;
import lwjglEngine.gui.GuiTexture;
import lwjglEngine.render.DisplayManager;

public class TextBox extends GuiTexture {
	
	public String name;
	public ArrayList<String> tooltip;
	public ArrayList<String> display;
	public Menu menu;
	//public boolean enabled;

	//public float origX, origY, origSizeX, origSizeY; //Public or private?
	public boolean expanded = false;
	public int[] noOrdersIfMenu = null;
	public boolean lock = false, shortcut = true;
	public boolean autoClear = true;
	public boolean active = false, monospace = false, noOverlap = false;
	
	//0-255
	public Vector3f textColor = new Vector3f(255,0,0); 
	public float borderR = -1, borderG = -1, borderB = -1; //default nostroke
	public int shape = 0; //0 -> rectangle (default), 1 -> ellipse
	
	//public String textString;
	public float fontSize;
	public int textMeshVao;
	public int vertexCount;
	public float lineMaxSize;
	public FontType font;

	public boolean centerText = false;

	/**
	 * Creates a new text, loads the text's quads into a VAO, and adds the text
	 * to the screen.
	 * 
	 * @param text
	 *            - the text.
	 * @param fontSize
	 *            - the font size of the text, where a font size of 1 is the
	 *            default size.
	 * @param font
	 *            - the font that this text should use.
	 * @param position
	 *            - the position on the screen where the top left corner of the
	 *            text should be rendered. The top left corner of the screen is
	 *            (0, 0) and the bottom right is (1, 1).
	 * @param maxLineLength
	 *            - basically the width of the virtual page in terms of screen
	 *            width (1 is full screen width, 0.5 is half the width of the
	 *            screen, etc.) Text cannot go off the edge of the page, so if
	 *            the text is longer than this length it will go onto the next
	 *            line. When text is centered it is centered into the middle of
	 *            the line, based on this line length value.
	 * @param centered
	 *            - whether the text should be centered or not.
	 */

	public void remove() {
		TextMaster.removeText(this);
	}
	
	public TextBox(int fontSize, FontType font, int maxLineLength, boolean centered, int texture, String text, String tip, float a, float b, float c, float d)
	{
		super(texture, new Vector2f(a,b), new Vector2f(c,d));
		display = new ArrayList<String>();
		display.add(text);
		tooltip = new ArrayList<String>();
		tooltip.add(tip);
		//tooltip.add(text);
		//enabled = false;
		
		this.fontSize = fontSize;
		//this.font = font;
		this.fontSize = 0.5f;
		this.lineMaxSize = 1f;
		//this.lineMaxSize = c/DisplayManager.width;
		this.centerText = true;
		if (TextMaster.init)
			TextMaster.loadText(this);
	}
	
	public TextBox(int texture, String text, float a, float b, float c, float d)
	{
		super(texture, new Vector2f(a,b), new Vector2f(c,d));
		display = new ArrayList<String>();
		display.add(text);
		tooltip = new ArrayList<String>();
		tooltip.add("");
		//enabled = false;
		
		this.centerText = true;
		this.fontSize = 0.5f;
		this.lineMaxSize = 1f;
		//this.lineMaxSize = c/DisplayManager.width;
		if (TextMaster.init)
			TextMaster.loadText(this);
	}
	
	public TextBox(int texture, String text, String tip, float a, float b, float c, float d)
	{
		super(texture, new Vector2f(a,b), new Vector2f(c,d));
		display = new ArrayList<String>();
		display.add(text);
		tooltip = new ArrayList<String>();
		tooltip.add(tip);
		//enabled = false;
		this.centerText = true;
		this.fontSize = 0.5f;
		this.lineMaxSize = 1f;
		//this.lineMaxSize = c/DisplayManager.width;
		if (TextMaster.init)
			TextMaster.loadText(this);
	}
	
	public boolean equals(TextBox o)
	{
		return pos.equals(o.pos) && size.equals(o.size);
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
	
	public void move(float x, float y)
	{
		pos.x = x/DisplayManager.width;
		pos.y = y/DisplayManager.height;
		pixelPos.x = x;
		pixelPos.y = y; //pixelSize.y = y haha
	}
	
	//Return itself for convienence
	public TextBox color(float x, float y, float z) {color.x = x; color.y = y; color.z = z; return this;}
	public TextBox color(float w) {return color(w,w,w);}
	public TextBox borderColor(float x, float y, float z) {borderR = x; borderG = y; borderB = z; return this;}
	public TextBox borderColor(float w) {return borderColor(w,w,w);}
	
	//Legacy methods
	public void activate(boolean a) {active = a;}
	
	/*public void tick()
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
		temp.dirX = (x-pos.x)/frames;
		temp.dirY = (y-pos.y)/frames;
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

	public void shake(float x, float y)
	{
		Order temp = new Order(this,"move");
		temp.expX = x;
		temp.expY = y;
		temp.frames = 2;
		orders.add(temp);
	}

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
	
	public void activate(boolean yn) {active = yn;}
	public boolean active() {return active;}
	
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
	}*/

}
