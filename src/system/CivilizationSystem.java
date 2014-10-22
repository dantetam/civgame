package system;

import java.util.ArrayList;

import processing.core.PApplet;
import data.EntityData;
import render.CivGame;
import game.*;
import units.*;

public class CivilizationSystem extends BaseSystem {

	public boolean requestTurn = false;
	public int turnsPassed = 0;

	public CivilizationSystem(CivGame civGame) 
	{
		super(civGame);
	}

	public void tick() 
	{
		if (requestTurn) 
		{
			requestTurn = false;
			main.menuSystem.message("Executed AI actions");
			for (int i = 0; i < main.grid.civs.length; i++)
			{
				Civilization civ = main.grid.civs[i];
				//System.out.println(civ.name + ": " + civ.food + " " + civ.gold + " " + civ.metal + " " + civ.research);
				//Automatically move the computer players' units
				if (true)
				{
					for (int j = 0; j < civ.units.size(); j++)
					{
						//Reveal all tiles within sight
						civ.units.get(j).reveal();
					}
					for (int j = 0; j < civ.improvements.size(); j++)
					{
						//Reveal all tiles within sight
						civ.improvements.get(j).reveal();
					}
					/*for (int j = 0; j < civ.units.size(); j++)
					{

					}*/
					double tf = 0, tg = 0, tm = 0, tr = 0;
					int population = 0;
					for (int j = 0; j < civ.cities.size(); j++)
					{
						City c = civ.cities.get(j);
						population += c.population;

						if (c.takeover > 0)
						{
							c.takeover--;
							continue;
						}

						//Make some settlers to test
						int numSettlers = 0, numWorkers = 0, numWarriors = 0;
						if (i != 0)
						{
							for (int k = 0; k < civ.cities.size(); k++)
							{
								if (civ.cities.get(k).queue != null)
								{
									if (civ.cities.get(k).queue.equals("Settler"))
									{
										numSettlers++;
									}
									else if (civ.cities.get(k).queue.equals("Worker"))
									{
										numWorkers++;
									}
									else if (civ.cities.get(k).queue.equals("Worker"))
									{
										numWarriors++;
									}
								}
							}
							for (int k = 0; k < civ.units.size(); k++)
							{
								if (civ.units.get(k) instanceof Settler)
								{
									numSettlers++;
								}
								else if (civ.units.get(k) instanceof Worker)
								{
									numWorkers++;
								}
								else if (civ.units.get(k) instanceof Warrior)
								{
									numWarriors++;
								}
							}
						}
						//Loop through a city's tiles
						/*
						 * -2 freshwater 2,1,0,2
						 * -1 sea 1,1,0,2
						 * 0 ice 0,1,2,1
						 * 1 taiga 1,1,1,1
						 * 2 desert 0,0,2,1
						 * 3 savannah 2,0,1,2
						 * 4 dry forest 2,1,1,2
						 * 5 forest 3,0,1,2
						 * 6 rainforest 3,1,0,3
						 * 7 beach (outdated)
						 * 
						 * modifiers:
						 * 8 oasis 3,3,0,2
						 * (shape 1) hill -1,0,1,0
						 * (shape 2) mountain -2,0,2,2
						 *
						 */
						if (i != 0)
						{
							//Assign specialized workers, prioritize scientists
							if (c.population >= 5)
							{
								int idle = c.population - 4;
								c.sci = Math.min(4, idle);
								idle -= c.sci;
								if (idle > 0)
								{
									c.art += idle;
									idle = 0;
								}
							}
						}

						c.happiness = 4 - c.population;
						int sumCityWorkers = c.adm + c.art + c.sci;
						if (c.happiness < 0)
							c.workTiles(c.population - c.happiness - sumCityWorkers);
						else
							c.workTiles(c.population - sumCityWorkers);
						c.health = 7 - c.population + Math.min(0,c.happiness);
						for (int k = 0; k < c.land.size(); k++)
							c.land.get(k).harvest = false;

						//Work tiles and harvest their numerical yields
						for (int k = 0; k < c.workedLand.size(); k++)
						{
							Tile t = c.workedLand.get(k);
							t.turnsSettled++;
							//System.out.println(t.row + " " + t.col + " " + t.turnsSettled);
							double[] eval = c.evaluate(t,null);
							double f=eval[0],g=eval[1],m=eval[2],r=eval[3];

							//civ.food += f;
							//civ.gold += g;
							//civ.metal += m;
							//tf += f;
							tf += f; tg += g; tm += m; tr += r;
							c.workedLand.get(k).harvest = true;
						}
						//Factor in specialized workers
						double taxBase = tg;
						tr += c.sci*2;
						tg += Math.floor(c.adm*0.25*taxBase);
						c.culture += Math.floor(c.art*0.25*taxBase);
						if (civ.capital.equals(c))
						{
							c.culture++;
						}
						//System.out.println(c.culture + " " + c.expanded);
						if (c.culture >= 20 && c.expanded == 1)
						{
							//c.culture -= 20;
							c.expand(2);
						}
						else if (c.culture >= 100 && c.expanded == 2 && c.population > 6)
						{
							c.expand(3);
						}
						//System.out.println(tf + " " + c.owner.food);
						if (civ instanceof CityState)
						{
							if (c.focus.equals("Growth"))
							{
								if (civ.units.size() == 0)
								{
									EntityData.queue(c, "Worker");
								}
								else if (numWorkers < civ.cities.size())
								{
									if (main.grid.coastal(c.location.row, c.location.col) && Math.random() < 0.2)
									{
										EntityData.queue(c, "Work Boat");
									}
									else
									{
										EntityData.queue(c, "Worker");
									}
								}
								else if (civ.units.size() <= civ.cities.size()*3)
								{
									if (main.grid.coastal(c.location.row, c.location.col) && Math.random() < 0.2)
									{
										EntityData.queue(c, "Work Boat");
									}
									else
									{
										EntityData.queue(c, "Warrior");
									}
								}
							}
							else if (c.focus.equals("Production"))
							{
								if (civ.units.size() <= 5)
								{
									EntityData.queue(c, "Warrior");
								}
							}
						}
						if (c.queue == null && i != 0)
						{
							//System.out.println(civ.units.size());
							if (c.focus.equals("Growth"))
							{
								if (civ.units.size() == 0)
								{
									EntityData.queue(c, "Worker");
								}
								else if (numSettlers < 3)
								{
									EntityData.queue(c, "Settler");
								}
								else if (numWorkers < civ.cities.size())
								{
									if (main.grid.coastal(c.location.row, c.location.col) && Math.random() < 0.2)
									{
										EntityData.queue(c, "Work Boat");
									}
									else
									{
										EntityData.queue(c, "Worker");
									}
								}
								else if (civ.units.size() <= civ.cities.size()*3)
								{
									if (main.grid.coastal(c.location.row, c.location.col) && Math.random() < 0.2)
									{
										EntityData.queue(c, "Work Boat");
									}
									else
									{
										EntityData.queue(c, "Warrior");
									}
								}
								/*else if (civ.units.size() <= civ.cities.size()*3)
								{
									if (main.grid.coastal(c.location.row, c.location.col))
									{
										c.queue = "Galley";
										c.queueFood = 15;
										c.queueMetal = 15;
									}
								}*/
							}
							else if (c.focus.equals("Production"))
							{
								if (civ.units.size() <= 7)
								{
									EntityData.queue(c, "Warrior");
								}
							}
						}
						else if (c.queue != null)
						{
							/*c.queueTurns--;
							if (c.queueTurns == 0)
							{
								main.grid.addUnit(EntityData.get(c.queue),civ,c.location.row,c.location.col);
								c.queue = null;
							}*/
							if (c.queueFood > 0)
							{
								float amount = PApplet.min((float)tf/2,c.population*5,c.queueFood);
								tf -= amount;
								c.queueFood -= amount;
							}
							if (c.queueMetal > 0)
							{
								float amount = PApplet.min((float)tm,c.population*5,c.queueMetal);
								tm -= amount;
								c.queueMetal -= amount;
							}
							//System.out.println(c.queueFood);
							if (c.queueFood <= 0 && c.queueMetal <= 0)
							{
								//System.out.println(c.queue);
								main.grid.addUnit(EntityData.get(c.queue),civ,c.location.row,c.location.col);
								c.queueFood = 0;
								c.queueMetal = 0;
								c.queue = null;
							}
						}

						/*if (c.queue != null)
						{
							if (c.queue.equals("Settler") || c.queue.equals("Worker"))
							{
								civ.food += Math.min(tf, c.population*2);
								tf -= Math.min(tf, c.population*2);
							}
						}*/
						if (c.health >= 0)
						{
							c.focus = "Growth";
							if (tf > c.population*2)
							{
								if (c.population < 3)
								{
									double amount = Math.min(tf/2, c.population*2);
									tf -= amount;
									c.percentGrowth += 0.1*(amount/(c.population*2));
									//System.out.println(c.percentGrowth);
								}
							}
							else if (civ.food/2 > c.population)
							{
								//civ.food -= c.population*3;
								//c.percentGrowth += 0.1;
								double amount = Math.min(civ.food/2, c.population*2);
								civ.food -= amount;
								c.percentGrowth += 0.1*(amount/(c.population*2));
								//System.out.println(c.percentGrowth);
							}
							else
							{
								c.percentGrowth -= 0.05;
							}
							if (c.percentGrowth >= 1)
							{
								c.percentGrowth = 0;
								c.population++;
								//c.focus = "Growth";
							}
							else if (c.percentGrowth < 0)
							{
								c.percentGrowth = 0;
								if (c.population > 1)
								{
									c.percentGrowth = 0.5;
									c.population--;
								}
								//c.focus = "Production";
							}
						}
						else 
						{
							//tf -= c.population*2;
							c.focus = "Production";
						}
						if (c.population > 4)
						{
							c.focus = "Growth";
						}
						for (int k = 0; k < c.land.size(); k++)
						{
							Tile t = c.land.get(k);
							if (t.turnsSettled % 50 == 0 && t.turnsSettled > 0 && !t.forest)
							{
								if (t.biome >= 3 && t.biome <= 6)
								{
									t.biome--;
								}
							}
						}
					}
					/*for (int j = 0; j < civ.cities.size(); j++)
					{
						City c = civ.cities.get(j);
						if (c.focus.equals("Growth") && c.health >= 0)
						{
							if (tf >= c.population*3)
							{
								tf -= c.population*3;
								c.percentGrowth += 0.2;
								if (c.percentGrowth >= 1)
								{
									c.percentGrowth = 0;
									c.population++;
								}
							}
						}
					}*/
					civ.food += tf;
					civ.gold += tg;
					civ.metal += tm;
					civ.research += tr;

					//Resource caps
					civ.food = Math.min(civ.food, population*5);
					civ.metal = Math.min(civ.metal, population*5);
				}
				//Update civilization's opinions of each other
				for (int j = 0; j < main.grid.civs.length; j++)
				{
					int baseOpinion = 0;
					Civilization oCiv = main.grid.civs[j];
					if (i != j)
					{
						if (main.grid.civs[j].capital != null && civ.capital != null)
						{
							if (civ.capital.location.dist(oCiv.capital.location) < 30)
							{
								//baseOpinion -= 50;
							}
							if (civ.war(oCiv))
							{
								baseOpinion -= 50;
							}
							int borderTiles = civ.bordering(oCiv);
							if (borderTiles > 0)
							{
								baseOpinion -= borderTiles*10;
							}
						}
					}
					else
					{
						baseOpinion = 200;
					}
					if (baseOpinion < -200) baseOpinion = -200;
					else if (baseOpinion > 200) baseOpinion = 200;
					//These correspond to the indices of the civs within the grid
					civ.opinions[j] = baseOpinion;
					oCiv.opinions[i] = baseOpinion;
				}
				//Declare war on other civilizations
				if (!(civ instanceof CityState))
				{
					for (int j = 0; j < main.grid.civs.length; j++)
					{
						if (j == 0 || main.grid.civs[j] instanceof CityState) continue;
						if (civ.opinions[j] < -10)//main.grid.civs[j].cities.size() > 2)
						{
							if (main.grid.civs[j].capital != null && civ.capital != null)
							{
								/*System.out.println("----");
							System.out.println(!civ.equals(main.grid.civs[j]));
							System.out.println(civ.capital.location.dist(main.grid.civs[j].capital.location) < main.grid.aggroDistance);
							System.out.println(!civ.enemies.contains(main.grid.civs[j]));*/
								if (//civ.cities.size() > 1.25*main.grid.civs[j].cities.size() &&
										//Math.random() < 0.03 &&
										!civ.equals(main.grid.civs[j]) &&
										//civ.capital.location.dist(main.grid.civs[j].capital.location) < main.grid.aggroDistance &&
										!civ.enemies.contains(main.grid.civs[j]))
								{
									System.out.println("war");
									civ.enemies.add(main.grid.civs[j]);
									main.grid.civs[j].enemies.add(civ);
								}
							}
						}
						else
						{
							//main.grid.civs[j].enemies.remove(civ);
							//civ.enemies.remove(main.grid.civs[j]);
						}
					}
				}
				//Begin starvation if there is lack of food
				if (civ.food <= -10)
				{
					if (Math.random() < 0.2) //&& civ.units.size() > 5)
					{
						main.grid.removeUnit(civ.units.get((int)(Math.random()*civ.units.size())));
					}
				}
				//Begin researching techs (enemy AI only)
				if (i == 0)
				{
					if (civ.researchTech == null)
					{
						if (civ.beeline.size() > 0)
						{
							civ.researchTech = civ.beeline.get(0);
							civ.beeline.remove(0);
						}
					}
					else if (civ.researchTech != null) //Could be null, check for this possibility
					{
						Tech tech = civ.techTree.researched(civ.researchTech);
						tech.totalR += civ.research;
						civ.research = 0;
						if (tech.researched())
						{
							civ.researchTech = null;
						}
					}
				}
			}
			//Loop through tiles
			/*for (int r = 0; r < main.grid.rows; r++)
			{
				for (int c = 0; c < main.grid.cols; c++)
				{
					Tile t = main.grid.getTile(r,c);
					if (t.improvement != null)
					{
						t.improvement.tick();
					}
					for (int i = 0; i < t.occupants.size(); i++)
					{
						GameEntity en = t.occupants.get(i);
						if (en.owner != null)
						{
							en.owner.food--;
							if (!en.owner.equals(main.grid.civs[0]))
							{
								while (en.action > 0)
								{
									en.tick();
									en.action--;
								}
							}
							else
							{
								while (en.action > 0)
								{
									t.occupants.get(i).playerTick();
									en.action--;
								}
							}
						}
					}
				}
			}*/
			//if (main.grid.civs.length > 1)
			//{
			//loop through player units
			Civilization player = main.grid.civs[0];
			for (int j = 0; j < player.improvements.size(); j++)
			{
				player.improvements.get(j).playerTick();
			}
			for (int j = 0; j < player.units.size(); j++)
			{
				player.units.get(j).playerTick();
			}
			//loop through enemy units
			for (int i = 1; i < main.grid.civs.length; i++)
			{
				Civilization civ = main.grid.civs[i];
				for (int j = 0; j < civ.improvements.size(); j++)
				{
					civ.improvements.get(j).tick();
				}
				for (int j = 0; j < civ.units.size(); j++)
				{
					civ.units.get(j).tick();
				}
			}
			//}
			/*for (int r = 0; r < main.grid.rows; r++)
			{
				for (int c = 0; c < main.grid.cols; c++)
				{
					for (int i = 0; i < main.grid.getTile(r,c).occupants.size(); i++)
					{
						GameEntity en = main.grid.getTile(r,c).occupants.get(i);
						en.action = en.maxAction;
					}
				}
			}*/
			//Restore action "bars"
			for (int i = 0; i < main.grid.civs.length; i++)
			{
				Civilization civ = main.grid.civs[i];
				for (int j = 0; j < civ.units.size(); j++)
				{
					civ.units.get(j).action = civ.units.get(j).maxAction;
				}
			}
			main.menuSystem.message("Ended AI actions");
			main.menuSystem.message("Ended turn " + main.civilizationSystem.turnsPassed);
			turnsPassed++;
			main.menuSystem.message("Began turn " + main.civilizationSystem.turnsPassed);
		}
	}	

	/*public void sacrifice(GameEntity en)
	{
		if (en instanceof Settler)
		{
			//Make the city and set its surrounding tiles to 
			City city = (City)EntityData.get("City");
			city.owner = en.owner;
			city.owner.cities.add(city);
			main.grid.addUnit(city, en.owner, en.location.row, en.location.col);
			for (int i = en.location.row - 2; i <= en.location.row + 2; i++)
			{
				for (int j = en.location.col - 2; j <= en.location.col + 2; j++)
				{
					if (i >= 0 && i < main.grid.rows && j >= 0 && j < main.grid.cols)
					{
						Tile t = main.grid.getTile(i,j);
						if (t != null)
						{
							if (t.owner == null)
							{
								main.grid.addTile(en.owner, t);
							}
							if (t.owner == city.owner && t.city == null)
							{
								t.city = city;
								city.land.add(t);
							}
						}
					}
				}
			}
			//Remove the settler
			main.grid.removeUnit(en);
		}
	}*/

}
