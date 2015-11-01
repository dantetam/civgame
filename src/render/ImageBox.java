package render;

import lwjglEngine.fontMeshCreator.FontType;

public class ImageBox extends TextBox {

	public float tintR, tintG, tintB;
	
	public ImageBox(int texture, String s, float a, float b,
			float c, float d) {
		super(0, null, 0, false, texture, "", "", a, b, c, d);
		//System.out.println(s);
		// TODO Auto-generated constructor stub
	} //bad inheritance but I just want to group without using an interface
	
	public void tint(float r, float g, float b)
	{
		tintR = r; tintG = g; tintB = b;
	}
	
}
