package data;

//Wrapper class for improvement data

public class Improvement
{
	public String name;
	//The types of units it can be applied to
	public String[] units = new String[0];
	//The proportion of the unit of which it will cost
	public double foodPercent, metalPercent, goldPercent;
	
	public double offensivePercent, defensivePercent, rangedPercent; //proportional bonuses
	public double offensiveFlat, defensiveFlat, rangedFlat; //fixed bonuses
	public double workerImprovementTime;
	
	public Improvement(String name)
	{
		this.name = name;
	}
	
	public void fit(String... types) {units = types;}
	public boolean isFit(String type) {
		for (int i = 0; i < units.length; i++)
			if (units[i].equals(type))
				return true;
		return false;
	}
	public void cost(double a, double b, double c) {foodPercent = a; metalPercent = b; goldPercent = c;}
	public void set(double a, double b, double c, double d, double e, double f, double g) 
	{
		offensivePercent = a; defensivePercent = b; rangedPercent = c;
		offensiveFlat = d; defensiveFlat = e; rangedFlat = f;
		workerImprovementTime = g;
	}
	public boolean equals(String name) {return this.name.equals(name);}
}
