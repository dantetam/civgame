package render;

import java.util.ArrayList;

import system.MenuSystem;
import game.Tech;
import game.TechTree;

public class TechMenu extends Menu {

	public TechTree tree;
	public ArrayList<DisplayLine> lines = new ArrayList<DisplayLine>();
	private float sX = 80, sY = 80, space = 10; //Used for initializing buttons

	public TechMenu(TechTree t, String name) {
		super(name);
		super.name = "TechMenu";
		tree = t;
	}

	public void setupButtons()
	{
		float largeSpace = 400;
		buttons.clear();
		lines.clear();
		for (int i = 0; i < 2; i++)
			drawButton(tree.first.techs[i], 425, (i)*largeSpace + 200);
		for (int i = 2; i < tree.first.techs.length; i++)
			drawButton(tree.first.techs[i], 825, (i-2)*largeSpace + 200);
		for (int i = 0; i < lines.size(); i++)
		{
			DisplayLine l = lines.get(i);
			l.x1 += sX/2; l.x2 += sX/2; l.y1 += sY/2; l.y2 += sY/2;
		}
	}

	private void drawButton(Tech t, float x, float y)
	{
		//System.out.println("Length: " + t.techs.length);
		if (t.techs.length != 0)
		{
			if (t.techs.length == 1)
			{
				line(x,y,x+sX+space,y);
				drawButton(t.techs[0],x+sX+space,y);
			}
			else if (t.techs.length == 2)
			{
				line(x,y,x+sX+space,y-sY/2-space/2);
				drawButton(t.techs[0],x+sX+space,y-sY/2-space/2);
				line(x,y,x+sX+space,y+sY/2+space/2);
				drawButton(t.techs[1],x+sX+space,y+sY/2+space/2);
			}
			else if (t.techs.length == 3)
			{
				line(x,y,x+sX+space,y);
				drawButton(t.techs[1],x+sX+space,y);
				line(x,y,x+sX+space,y-sY-space);
				drawButton(t.techs[0],x+sX+space,y-sY-space);
				line(x,y,x+sX+space,y+sY+space);
				drawButton(t.techs[2],x+sX+space,y+sY+space);
			}
			else if (t.techs.length == 4)
			{
				line(x,y,x+sX+space,y-sY/2-space/2);
				drawButton(t.techs[1],x+sX+space,y-sY/2-space/2);
				line(x,y,x+sX+space,y+sY/2+space/2);
				drawButton(t.techs[2],x+sX+space,y+sY/2+space/2);
				line(x,y,x+sX+space,y-sY*3/2-space/2);
				drawButton(t.techs[0],x+sX+space,y-sY*3/2-space);
				line(x,y,x+sX+space,y+sY*3/2+space/2);
				drawButton(t.techs[3],x+sX+space,y+sY*3/2+space);
			}
		}
		else
		{
			//Do nothing, no other techs to show
		}
		Button b = getTechButton(t);
		b.posX = x; b.origX = x; b.posY = y; b.origY = y;
		b.sizeX = sX; b.sizeY = sY; b.origSizeX = sX; b.origSizeY = sY;
		addButton(b);
	}

	private Button getTechButton(Tech t)
	{
		int turns = MenuSystem.calcQueueTurnsTech(tree.civ, t);
		String s = t.name;
		Button b = new Button("research" + s, s, "Research " + s + ".", 0, 0, 0, 0);
		b.shortcut = false;
		//Not researched -> black, researched -> blue, researching -> green, candidate -> gray, queuing/researching -> yellow
		//Give player shortcut buttons for candidates and researching
		if (t.researched()) {b.color(75,150,205);} //{b.r = tree.civ.r; b.g = tree.civ.g; b.b = tree.civ.b;}
		else if (tree.civ.researchTech != null && s.equals(tree.civ.researchTech)) {b.color(150,150,0); b.shortcut = true;}
		else if (t.totalR > 0) {b.color(0,150,0); b.shortcut = true;}
		else if (t.requisite != null)
		{
			if (t.requisite.researched()) 
			{
				b.color(150);
				b.shortcut = true;
			}
		}
		else if (t.alternative != null)
		{
			if (t.alternative.researched())
			{
				b.color(150);
				b.shortcut = true;
			}
		}
		//else {b.color(0);}
		b.display.add("<" + turns + ">");
		b.lock = true;
		b.tooltip.clear();
		//b.tooltip.add("Estimated research time: " + turns);
		b.tooltip.add(t.totalR + " research out of " + t.requiredR + "; " + (int)((float)t.totalR/(float)t.requiredR*100) + "%");
		if (t.requisite != null)
			b.tooltip.add("Requires " + t.requisite.name);
		String techString = "";
		for (int j = 0; j < t.techs.length; j++)
			techString += t.techs[j].name + ", ";
		if (t.techs.length != 0)
			b.tooltip.add("Leads to " + techString.substring(0, techString.length()-2));
		if (!t.unlockString().isEmpty())
			b.tooltip.add("Unlocks " + t.unlockString());
		return b;
	}

	public class DisplayLine
	{
		public float x1, y1, x2, y2; 
		public DisplayLine(float a, float b, float c, float d) {x1 = a; y1 = b; x2 = c; y2 = d;} 
	}

	private void line(float a, float b, float c, float d)
	{
		lines.add(new DisplayLine(a,b,c,d));
	}

}
