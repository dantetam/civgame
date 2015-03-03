package data;

//Wrapper class for improvement data
//Catch all class for flat/proportional bonuses to objects

public class Improvement
{
	public String name, tooltip;
	//The types of units it can be applied to
	public String[] units = new String[0];
	//The proportion of the unit of which it will cost
	public double foodPercent, metalPercent, goldPercent;
	//Flat resource "fees"
	public double foodFlat, metalFlat, goldFlat;
	
	public double offensivePercent, defensivePercent, rangedPercent; //proportional bonuses
	public double offensiveFlat, defensiveFlat, rangedFlat; //fixed bonuses
	public double workerImprovementTime;
	
	//public String requiredTech;
	
	public Improvement(String name, String tooltip)
	{
		this.name = name; this.tooltip = tooltip;
		//requiredTech = tech;
	}
	
	public Improvement(Improvement i)
	{
		name = i.name; tooltip = i.tooltip;
		fit(i.units);
		cost(i.foodPercent, i.metalPercent, i.goldPercent, i.foodFlat, i.metalFlat, i.goldFlat);
		set(i.offensivePercent, i.defensivePercent, i.rangedPercent, i.offensiveFlat, i.defensiveFlat, i.rangedFlat, i.workerImprovementTime);
		//requiredTech = i.requiredTech;
	}
	
	public void fit(String... types) {units = types;}
	public boolean isFit(String type) {
		for (int i = 0; i < units.length; i++)
			if (units[i].equals(type))
				return true;
		return false;
	}
	public void cost(double a, double b, double c, double d, double e, double f) 
	{
		foodPercent = a; metalPercent = b; goldPercent = c;
		foodFlat = d; metalFlat = e; goldFlat = f;
	}
	public void set(double a, double b, double c, double d, double e, double f, double g) 
	{
		offensivePercent = a; defensivePercent = b; rangedPercent = c;
		offensiveFlat = d; defensiveFlat = e; rangedFlat = f;
		workerImprovementTime = g;
	}
	public boolean equals(String name) {return this.name.equals(name);}
}
