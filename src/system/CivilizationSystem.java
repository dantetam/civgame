package system;

import java.util.ArrayList;

import menugame.MenuGame;
import processing.core.PApplet;
import data.EntityData;
import render.CivGame;
import game.*;
import units.*;

public class CivilizationSystem extends BaseSystem {

	public boolean requestTurn = false;
	public int turnsPassed = 0;
	public Grid theGrid;

	public CivilizationSystem(CivGame civGame) 
	{
		super(civGame);
	}

	public CivilizationSystem(MenuGame menuGame)
	{
		super(null);
	}

	//Generalize it for all grids
	public void tick()
	{
		if (main == null)
			tick(theGrid,false);
		else
			tick(theGrid,true);
	}

	private void tick(Grid grid, boolean guiExists) 
	{
		if (requestTurn) 
		{
			requestTurn = false;
			if (guiExists) main.menuSystem.message("Executed AI actions");
			for (int i = 0; i < grid.civs.length; i++)
			{
				Civilization civ = grid.civs[i];
				//System.out.println(civ.name + ": " + civ.food + " " + civ.gold + " " + civ.metal + " " + civ.research);
				//Automatically move the computer players' units
				if (true)
				{
					if (i >= grid.barbarians) //Barbarian reset
					{
						//civ.revealed = new boolean[civ.revealed.length][civ.revealed[0].length];
					}
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
						if (civ.capital != null)
							if (civ.capital.equals(c) && !(c.owner instanceof CityState))
								c.culture++;
						//c.culture++;
						//System.out.println(c.culture + " " + c.expanded);
						if (c.culture >= 20 && c.expanded == 1)
						{
							//c.culture -= 20;
							c.expand(2);
						}
						if (c.culture >= 100 && c.expanded == 2)// && c.population > 6)
						{
							c.expand(3);
						}
						//System.out.println(tf + " " + c.owner.food);
						if (civ instanceof CityState || civ.name.contains("Barbarians"))
						{
							if (c.focus.equals("Growth"))
							{
								if (civ.units.size() == 0)
								{
									EntityData.queue(c, "Worker");
								}
								else if (numWorkers < civ.cities.size())
								{
									if (grid.coastal(c.location.row, c.location.col) && Math.random() < 0.2)
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
									if (grid.coastal(c.location.row, c.location.col) && Math.random() < 0.2)
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
							if (civ.units.size() < 15)
								EntityData.queueAi(c);
							//System.out.println(civ.units.size());
							/*if (i >= grid.barbarians)
							{
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
										if (grid.coastal(c.location.row, c.location.col) && Math.random() < 0.2)
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
										if (grid.coastal(c.location.row, c.location.col) && Math.random() < 0.2)
										{
											EntityData.queue(c, "Work Boat");
										}
										else
										{
											EntityData.queue(c, "Warrior");
										}
									}
									else if (civ.units.size() <= civ.cities.size()*3)
								{
									if (grid.coastal(c.location.row, c.location.col))
									{
										c.queue = "Galley";
										c.queueFood = 15;
										c.queueMetal = 15;
									}
								}
								}
								else if (c.focus.equals("Production"))
								{
									if (civ.units.size() <= 20)
									{
										EntityData.queue(c, "Warrior");
									}
								}
							}
							else
							{
								if (c.focus.equals("Growth"))
								{
									if (civ.units.size() == 0)
									{
										EntityData.queue(c, "Worker");
									}
									else if (numWorkers < civ.cities.size())
									{
										if (grid.coastal(c.location.row, c.location.col) && Math.random() < 0.2)
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
										if (grid.coastal(c.location.row, c.location.col) && Math.random() < 0.2)
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
									if (civ.units.size() <= 25)
									{
										EntityData.queue(c, "Warrior");
									}
								}
							}*/
						}
						else if (c.queue != null)
						{
							/*c.queueTurns--;
							if (c.queueTurns == 0)
							{
								grid.addUnit(EntityData.get(c.queue),civ,c.location.row,c.location.col);
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
								BaseEntity en = EntityData.get(c.queue);
								//Check if it's an actual unit or a building
								if (en != null)
								{
									grid.addUnit(en,civ,c.location.row,c.location.col);
									en.unitImprovement = civ.unitImprovements.get(c.queue);
									en.improve();
									if (!civ.name.contains("Barbarians"))
										if (en instanceof GameEntity && !(en instanceof Settler) && !(en instanceof Worker))
											if (civ.units.size() < 4)
												((GameEntity)en).explorer = true;
								}
								else
								{
									c.buildings.add(EntityData.cityImprovementMap.get(c.queue));
								}
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
						for (int k = 0; k < c.workedLand.size(); k++)
						{
							Tile t = c.workedLand.get(k);
							if (t.turnsSettled % 50 == 0 && t.turnsSettled > 0 && !t.forest)
							{
								if (t.biome >= 3 && t.biome <= 6)
								{
									t.biome--;
								}
							}
						}
						if (c.raze)
						{
							for (int k = 0; k < c.land.size(); k++)
							{
								Tile t = c.land.get(k);
								if (t.biome >= 3 && t.biome <= 6)
								{
									t.biome--;
								}
							}
							c.population--;
							System.out.println(c.population);
							if (c.population <= 0)
							{
								c.queue = null;
								c.queueFood = 0;
								c.queueMetal = 0;
								if (c.owner.capital != null)
									if (c.equals(c.owner.capital))
										c.owner.capital = null;
								for (int k = c.land.size() - 1; k >= 0; k--)
								{
									c.land.get(k).owner = null;
									c.land.get(k).city = null;
								}
								c.owner.cities.remove(c);
								if (c.owner.cities.size() > 0)
									c.owner.capital = c.owner.cities.get(0);
								c.owner = null;
								grid.removeUnit(c);
							}
						}
						if (c.enemiesInTerritory() && c.sortie == 0)
						{
							c.sortie = 1;
						}
						else if (!c.enemiesInTerritory() && c.sortie == 1)
						{
							c.sortie = 0;
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
					//civ.metal += tm;
					civ.research += tr;

					//Resource caps
					civ.food = Math.min(civ.food, population*3);
					//civ.metal = Math.min(civ.metal, population*3);
				}
				//Update civilization's opinions of each other
				for (int j = 0; j < grid.civs.length; j++)
				{
					int baseOpinion = 0;
					Civilization oCiv = grid.civs[j];
					if (i != j)
					{
						if (grid.civs[j].capital != null && civ.capital != null)
						{
							if (civ.capital.location.dist(oCiv.capital.location) < 30)
							{
								//baseOpinion -= 50;
							}
							if (civ.isWar(oCiv))
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
					for (int j = 0; j < grid.civs.length; j++)
					{
						Civilization civ2 = grid.civs[j];
						//if (j == 0 || civ2 instanceof CityState) continue;
						if (civ.opinions[j] < -10)//grid.civs[j].cities.size() > 2)
						{
							if (grid.civs[j].capital != null && civ.capital != null)
							{
								/*System.out.println("----");
							System.out.println(!civ.equals(grid.civs[j]));
							System.out.println(civ.capital.location.dist(grid.civs[j].capital.location) < grid.aggroDistance);
							System.out.println(!civ.enemies.contains(grid.civs[j]));*/
								if (//civ.cities.size() > 1.25*grid.civs[j].cities.size() &&
										//Math.random() < 0.03 &&
										!civ.equals(civ2) &&
										//civ.capital.location.dist(grid.civs[j].capital.location) < grid.aggroDistance &&
										!civ.isWar(civ2))
								{
									//System.out.println("war between " + civ.name + " and " + grid.civs[j]);
									civ.war(civ2);
									//Call in allies
									ArrayList<Civilization> allies = civ.allies();
									for (int k = 0; k < allies.size(); k++)
									{
										//Don't call in people allied to both
										Civilization a = allies.get(k);
										if (a.isAlly(civ) && a.isAlly(civ2))
										{
											continue;
										}
										else //Implies not allied to civ2
										{
											a.war(civ2);
											main.menuSystem.message(a.name + " has been called to war against " + civ2 + "!");
										}
									}
									allies = civ2.allies();
									for (int k = 0; k < allies.size(); k++)
									{
										Civilization a = allies.get(k);
										if (a.isAlly(civ) && a.isAlly(civ2))
										{
											continue;
										}
										else //Implies not allied to civ
										{
											a.war(civ);
											main.menuSystem.message(a.name + " has been called to war against " + civ + "!");
										}
									}
									if (guiExists)
										main.menuSystem.message(civ.name + " has declared war on " + civ2.name + "!");
								}
							}
						}
						else
						{
							//grid.civs[j].enemies.remove(civ);
							//civ.enemies.remove(grid.civs[j]);
						}
					}
				}
				//Ally with others
				/*for (int j = 0; j < grid.civs.length; j++)
				{
					if (civ.opinions[j] > 0)
					{
						if (grid.civs[j].capital != null && civ.capital != null)
						{
							if (!civ.equals(grid.civs[j]) && !civ.isAlly(grid.civs[j]) &&
									civ.capital.location.dist(grid.civs[j].capital.location) < 20)
							{
								civ.ally(grid.civs[j]);
								if (guiExists)
									main.menuSystem.message(civ.name + " has allied " + grid.civs[j].name + "!");
							}
						}
					}
				}*/

				//Begin starvation if there is lack of food
				if (civ.food <= -10)
				{
					if (Math.random() < 0.2) //&& civ.units.size() > 5)
					{
						grid.removeUnit(civ.units.get((int)(Math.random()*civ.units.size())));
					}
				}
				//Begin researching techs (enemy AI only)
				if (true)
				{
					if ((civ.researchTech == null && !guiExists) || (i != 0 && guiExists))
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
							if (i == 0)
							{
								if (guiExists) main.menuSystem.message("Finished researching " + civ.researchTech);
							}
							tech.unlockForCiv(civ);
							civ.researchTech = null;
						}
					}
				}
			}
			//Loop through tiles
			/*for (int r = 0; r < grid.rows; r++)
			{
				for (int c = 0; c < grid.cols; c++)
				{
					Tile t = grid.getTile(r,c);
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
							if (!en.owner.equals(grid.civs[0]))
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
			//if (grid.civs.length > 1)
			//{
			//loop through player units
			if (guiExists)
			{
				Civilization theCiv = grid.civs[0];
				for (int j = 0; j < theCiv.units.size(); j++)
				{
					GameEntity u = theCiv.units.get(j);
					if (u.action > 0)
						theCiv.units.get(j).playerTick();
				}
			}

			int iter = guiExists ? 1 : 0; //iterate through the "player" civilization if it's a menu simulation
			//loop through enemy units
			for (; iter < grid.barbarians; iter++)
			{
				Civilization civ = grid.civs[iter];
				for (int j = 0; j < civ.improvements.size(); j++)
				{
					civ.improvements.get(j).tick();
				}
				for (int j = 0; j < civ.units.size(); j++)
				{
					GameEntity u = civ.units.get(j);
					//u.recordPos();
					if (!civ.isWar(u.location.owner) && !civ.isOpenBorder(u.location.owner) && !civ.equals(u.location.owner))
					{
						Tile t = grid.nearestFriendly(civ, u.location.row, u.location.col);
						if (t != null)
						{
							grid.moveTo(u, t.row, t.col);
						}
					}
					if (u.owner.units.size() >= 4) u.explorer = false;
					u.tick();
				}
			}
			//loop through barbarians
			for (int i = grid.barbarians; i < grid.civs.length; i++)
			{
				Civilization bar = grid.civs[i];
				for (int j = 0; j < bar.improvements.size(); j++)
				{
					bar.improvements.get(j).tick();
				}
				for (int j = 0; j < bar.units.size(); j++)
				{
					//bar.units.get(j).recordPos();
					bar.units.get(j).barbarianTick();
				}
			}
			//}

			//Spawn barbarians in unrevealed tiles (do not include tiles revealed by barbarians)
			boolean[][] revealedByCivs = new boolean[grid.rows][grid.cols];
			for (int i = 0; i < grid.barbarians; i++)
			{
				Civilization civ = grid.civs[i];
				for (int r = 0; r < civ.revealed.length; r++)
				{
					for (int c = 0; c < civ.revealed[0].length; c++)
					{
						revealedByCivs[r][c] = revealedByCivs[r][c] || civ.revealed[r][c];
					}
				}
			}
			if (turnsPassed >= 10)
			{
				for (int civNumber = grid.barbarians; civNumber < grid.civs.length; civNumber++)
				{
					for (int r = 0; r < revealedByCivs.length; r++)
					{
						for (int c = 0; c < revealedByCivs[0].length; c++)
						{
							//String test = revealedByCivs[r][c] ? "T" : "F";
							//System.out.print(test + " ");
							if (!revealedByCivs[r][c])
								if (grid.getTile(r, c).biome != -1)
									if (Math.random() < 0.005)
									{
										spawnBarbarians(grid, civNumber, r, c);
									}
						}
						//System.out.println();
					}
				}
			}

			/*for (int r = 0; r < grid.rows; r++)
			{
				for (int c = 0; c < grid.cols; c++)
				{
					for (int i = 0; i < grid.getTile(r,c).occupants.size(); i++)
					{
						GameEntity en = grid.getTile(r,c).occupants.get(i);
						en.action = en.maxAction;
					}
				}
			}*/
			//Restore action "bars"
			for (int i = 0; i < grid.civs.length; i++)
			{
				Civilization civ = grid.civs[i];
				for (int j = 0; j < civ.units.size(); j++)
				{
					civ.units.get(j).action = civ.units.get(j).maxAction;
				}
			}
			//if (guiExists) main.menuSystem.message("Ended processing and AI actions");
			//if (guiExists) main.menuSystem.message("Ended turn " + main.civilizationSystem.turnsPassed);
			turnsPassed++;
			if (guiExists) 
			{
				main.menuSystem.message("Began turn " + main.civilizationSystem.turnsPassed);

				//Check to see if any civilizations lost or won
				double[] civLand = new double[grid.civs.length];
				double sum = 0;
				for (int i = 0; i < grid.civs.length; i++)
				{
					Civilization civ = grid.civs[i];
					civLand[i] = civ.land().size();
					sum += civLand[i];
				}
				for (int i = 0; i < grid.barbarians; i++)
				{
					Civilization civ = grid.civs[i];
					if (civLand[i] == 0 && civ.units.size() == 0 && !civ.observe)
					{
						//Redundant checks
						if (guiExists) main.menuSystem.message(civ.name + " has been destroyed!");
						civ.observe = true;
					}
					else if (civLand[i]/sum > 0.6 && civLand[i] > 50 && !grid.won)
					{
						grid.won = true;
						if (guiExists) main.menuSystem.message(civ.name + " has conquered 60% of the civilized world!");
					}
				}
			}
		}
	}	

	public void spawnBarbarians(Grid grid, int index, int r, int c)
	{
		if (grid.civs[index].cities.size()*4 +
				grid.civs[index].units.size() < turnsPassed/10)
		{
			if (grid.civs[index].cities.size() == 0)
				grid.addUnit(EntityData.get("Settler"),grid.civs[index],r,c);
			double rand = Math.random();
			if (rand < 0.02)
				grid.addUnit(EntityData.get("Settler"),grid.civs[index],r,c);
			else
				grid.addUnit(EntityData.get("Warrior"),grid.civs[index],r,c);
			//System.out.println("Spawned barbarian: " + r + ", " + c);
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
			grid.addUnit(city, en.owner, en.location.row, en.location.col);
			for (int i = en.location.row - 2; i <= en.location.row + 2; i++)
			{
				for (int j = en.location.col - 2; j <= en.location.col + 2; j++)
				{
					if (i >= 0 && i < grid.rows && j >= 0 && j < grid.cols)
					{
						Tile t = grid.getTile(i,j);
						if (t != null)
						{
							if (t.owner == null)
							{
								grid.addTile(en.owner, t);
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
			grid.removeUnit(en);
		}
	}*/

}
