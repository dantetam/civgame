package render;

import java.util.ArrayList;

import system.MenuSystem;
import game.Tech;
import game.TechTree;

public class TechMenu extends Menu {

	public TechTree tree;
	public ArrayList<DisplayLine> lines = new ArrayList<DisplayLine>();

	public TechMenu(TechTree t, String name) {
		super(name);
		tree = t;
	}

	public void setupButtons()
	{
		buttons.clear();
		for (int i = 0; i < tree.first.techs.length; i++)
			drawButton(tree.first.techs[i], 175, i*175);
	}

	private void drawButton(Tech t, float x, float y)
	{
		float sX = 80, sY = 80, space = 10;
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
				drawButton(t.techs[0],x+sX+space,y+sY/2+space/2);
			}
			else if (t.techs.length == 3)
			{
				line(x,y,x+sX+space,y);
				drawButton(t.techs[0],x+sX+space,y);
				line(x,y,x+sX+space,y-sY-space);
				drawButton(t.techs[0],x+sX+space,y-sY-space);
				line(x,y,x+sX+space,y+sY+space);
				drawButton(t.techs[0],x+sX+space,y+sY+space);
			}
		}
		else
		{
			//Do nothing, no other techs to show
		}
		Button b = getTechButton(t);
		b.posX = x; b.origX = x; b.posY = y; b.origY = y;
		b.sizeX = sX; b.sizeY = sY; b.origSizeX = sX; b.origSizeY = sY;
		buttons.add(b);
	}

	private Button getTechButton(Tech t)
	{
		int turns = MenuSystem.calcQueueTurnsTech(tree.civ, t);
		String s = t.name;
		Button b = new Button("research" + s, s + " <" + turns + ">", "Research " + s + ".", 0, 0, 0, 0);
		b.lock = true;
		b.tooltip.clear();
		b.tooltip.add("Estimated research time: " + turns);
		b.tooltip.add(t.totalR + " research out of " + t.requiredR + "; " + (int)((float)t.totalR/(float)t.requiredR*100) + "%");
		if (t.requisite != null)
			b.tooltip.add("Requires " + t.requisite.name);
		String techString = "";
		for (int j = 0; j < t.techs.length; j++)
			techString += t.techs[j].name + ", ";
		if (t.techs.length != 0)
			b.tooltip.add("Leads to " + techString.substring(0, techString.length()-2));
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
