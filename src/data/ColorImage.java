package data;

import processing.core.PImage;

public class ColorImage extends PImage {

	//Tint color when drawn
	public float r,g,b,alpha=255;
	public PImage image;
	public ColorImage(PImage img, float x, float y, float z) {image = img; r = x; g = y; b = z;}
	
}
