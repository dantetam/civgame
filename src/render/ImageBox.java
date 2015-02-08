package render;

import java.util.ArrayList;

import processing.core.PImage;
import data.EntityData;

public class ImageBox extends TextBox {

	public PImage image;
	
	public ImageBox(String s, float a, float b,
			float c, float d) {
		super("", "", a, b, c, d);
		image = EntityData.iconMap.get(s);
		//System.out.println(s);
		// TODO Auto-generated constructor stub
	} //bad inheritance but I just want to group without using an interface
	
}
