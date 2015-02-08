package render;

//import java.util.ArrayList;

import processing.core.PImage;
import data.EntityData;

public class ImageBox extends TextBox {

	public String imageString;
	public PImage image;
	public float tintR, tintG, tintB;
	
	public ImageBox(String s, float a, float b,
			float c, float d) {
		super("", "", a, b, c, d);
		imageString = s;
		image = EntityData.iconMap.get(s);
		//System.out.println(s);
		// TODO Auto-generated constructor stub
	} //bad inheritance but I just want to group without using an interface
	
	public void tint(float r, float g, float b)
	{
		tintR = r; tintG = g; tintB = b;
	}
	
}
