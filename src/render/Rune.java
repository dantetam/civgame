package render;

import java.util.ArrayList;

public class Rune extends Button {

	public String imageName;
	
	public Rune(int texture, String imageName, String command, float a, float b, float c, float d) {
		super(texture, command, "", "", a, b, c, d);
		// TODO Auto-generated constructor stub
		this.imageName = imageName;
	}
	
	public boolean equals(Rune r)
	{
		if (r == null) return false;
		return super.equals(r);
	}
	
	
	
}
