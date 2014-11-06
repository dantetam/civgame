package game;

import java.util.ArrayList;

public class CityState extends Civilization {

	public CityState(String name, ArrayList<String> bonuses, float r, float g, float b) {
		super(name, bonuses, r, g, b);
	}

	public CityState(CityState cityState) {
		super(cityState);
	}

}
