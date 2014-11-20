package render;

import java.util.ArrayList;

public class Rune extends Button {

	public String imageName;
	
	public Rune(String imageName, String command, float a, float b, float c, float d) {
		super(command, "", "", a, b, c, d);
		// TODO Auto-generated constructor stub
		this.imageName = imageName;
	}
	
}
