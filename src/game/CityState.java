package game;

import java.util.ArrayList;

public class CityState extends Civilization {

	public CityState(String name, ArrayList<String> bonuses, float r, float g, float b, double w, double p, double t) {
		super(name, bonuses, r, g, b, w, p, t);
	}

	public CityState(CityState cityState) {
		super(cityState);
	}

}
