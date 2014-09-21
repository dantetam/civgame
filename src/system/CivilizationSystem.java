package system;

import java.util.ArrayList;

import processing.core.PApplet;
import data.EntityData;
import render.CivGame;
import game.*;
import units.*;

public class CivilizationSystem extends BaseSystem {

	public boolean requestTurn = false;

	public CivilizationSystem(CivGame civGame) 
	{
		super(civGame);
	}

	public void tick() 
	{
		if (requestTurn) 
		{
			requestTurn = false;
			for (int i = 0; i < main.grid.civs.length; i++)
			{
				Civilization civ = main.grid.civs[i];
				//System.out.println(civ.name + ": " + civ.food + " " + civ.gold + " " + civ.metal + " " + civ.research);
				//Automatically move the computer players' units
				if (i != 0)
				{
					for (int j = 0; j < civ.units.size(); j++)
					{
						GameEntity en = civ.units.get(j);
						int r = (int)(Math.random()*3) - 1;
						int c = (int)(Math.random()*3) - 1;
						/*if (main.grid.getTile(en.location.row+r,en.location.col+c) != null && !en.name.equals("Worker"))
						{
							//if (main.grid.getTile(en.location.row+r,en.location.col+c).owner == en.owner ||
									//main.grid.getTile(en.location.row+r,en.location.col+c).owner == null)
							if (main.grid.getTile(en.location.row+r,en.location.col+c).biome != -1)
							{
								GameEntity enemy = main.grid.hasEnemy(en,en.location.row+r,en.location.col+c);
								if (enemy != null)
								{
									if (en.name.equals("Warrior"))
									{
										if (enemy.name.equals("Warrior"))
										{
											if (Math.random() < 0.5)
											{
												main.grid.removeUnit(enemy);
												main.grid.move(en,r,c);
											}
											else
											{
												main.grid.removeUnit(en);
												continue;
											}
										}
										else
										{
											main.grid.removeUnit(enemy);
											main.grid.move(en,r,c);
										}
									}
								}
								else
								{
									//en.tick();
									if (en.queue == null)
										main.grid.move(en,r,c);
									if (en.location.improvement != null)
									{
										if (en.location.improvement.name.equals("City") && !en.owner.equals(en.location.improvement.owner) && en.name.equals("Warrior"))
										{
											City city = (City)en.location.improvement;
											for (int k = city.land.size() - 1; k >= 0; k--)
											{
												Tile t = city.land.get(k);
												city.land.remove(t);
												t.owner = null;
												//System.out.println("Destroyed");
												//en.owner.
												//t.owner = en.owner;
											}
											city.owner.cities.remove(city);
											en.location.improvement = null;
											//city = null;
										}
									}
									else
									{
										if (en.name.equals("Worker") && en.queue == null)
										{
											if (en.location.city != null)
											{
												City city = en.location.city;
												//Factor in the city later
												if (en.location.biome >= 3 && en.location.biome <= 6)
												{
													en.queueTurns = 6;
													en.queue = "Farm";
												}
												else if (en.location.shape == 1)
												{
													en.queueTurns = 6;
													en.queue = "Mine";
												}
												else if (en.location.shape == 2)
												{
													if (en.location.biome >= 0 && en.location.biome <= 3)
													{
														en.queueTurns = 6;
														en.queue = "Mine";
													}
												}
											}
										}
									}
								}
							}
						}*/
						if (Math.random() < 0.1 && en.location.owner == null && en.location.biome != -1 && en.name.equals("Settler"))
						{
							sacrifice(en);
						}
					}
					double tf = 0, tg = 0, tm = 0, tr = 0;
					for (int j = 0; j < civ.cities.size(); j++)
					{
						City c = civ.cities.get(j);
						//Make some settlers to test
						int numSettlers = 0, numWorkers = 0;
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
							}
						}
						for (int k = 0; k < civ.units.size(); k++)
						{
							if (civ.units.get(k).name.equals("Settler"))
							{
								numSettlers++;
							}
							else if (civ.units.get(k).name.equals("Worker"))
							{
								numWorkers++;
							}
						}
						if (c.queue == null)
						{
							//System.out.println(civ.units.size());
							if (c.focus.equals("Growth"))
							{
								if (civ.units.size() == 0)
								{
									c.queue = "Worker";
									c.queueFood = 25;
								}
								else if (numSettlers < 3)
								{
									c.queue = "Settler";
									c.queueFood = 35;
								}
								else if (numWorkers < civ.cities.size())
								{
									c.queue = "Worker";
									c.queueFood = 25;
								}
								else if (civ.units.size() <= civ.cities.size()*2)
								{
									c.queue = "Warrior";
									c.queueFood = 5;
									c.queueMetal = 5;
								}
							}
							else if (c.focus.equals("Production"))
							{
								if (civ.units.size() <= civ.cities.size()*2)
								{
									c.queue = "Warrior";
									c.queueFood = 5;
									c.queueMetal = 5;
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
								c.owner.food -= PApplet.min(c.owner.food,c.population*5,c.queueFood);
								c.queueFood -= PApplet.min(c.owner.food,c.population*5,c.queueFood);
							}
							if (c.queueMetal > 0)
							{
								c.owner.metal -= PApplet.min(c.owner.metal,c.population*5,c.queueMetal);
								c.queueMetal -= PApplet.min(c.owner.metal,c.population*5,c.queueMetal);
							}
							if (c.queueFood <= 0 && c.queueMetal <= 0)
							{
								main.grid.addUnit(EntityData.get(c.queue),civ,c.location.row,c.location.col);
								c.queueFood = 0;
								c.queueMetal = 0;
								c.queue = null;
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
						c.happiness = 4 - c.population;
						if (c.happiness < 0)
							c.workTiles(c.population - c.happiness);
						else
							c.workTiles(c.population);
						c.health = 5 - c.population + c.happiness;
						for (int k = 0; k < c.land.size(); k++)
						{
							c.land.get(k).harvest = false;
						}
						for (int k = 0; k < c.workedLand.size(); k++)
						{
							Tile t = c.workedLand.get(k);
							double[] eval = c.evaluate(t,null);
							double f=eval[0],g=eval[1],m=eval[2],r=eval[3];

							//civ.food += f;
							//civ.gold += g;
							//civ.metal += m;
							//tf += f;
							tf += f; tg += g; tm += m; tr += r;
							c.workedLand.get(k).harvest = true;
						}
						if (c.queue != null)
						{
							if (c.queue.equals("Settler") || c.queue.equals("Worker"))
							{
								civ.food += Math.min(tf, c.population*5);
								tf -= Math.min(tf, c.population*5);
							}
						}
						if (c.health >= 0)
						{
							c.focus = "Growth";
							if (tf > c.population*2)
							{
								if (c.population < 3)
								{
									double amount = Math.min(tf, c.population*3);
									tf -= amount;
									c.percentGrowth += 0.1*(amount/c.population*3);
								}
							}
							/*else if (civ.food > c.population)
							{
								//civ.food -= c.population*3;
								//c.percentGrowth += 0.1;
								double amount = Math.min(civ.food, c.population*3);
								civ.food -= amount;
								c.percentGrowth += 0.1*(amount/c.population*3);
							}*/
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
							else if (c.percentGrowth < 0 && c.population > 1)
							{
								c.percentGrowth = 0.5;
								c.population--;
								//c.focus = "Production";
							}
						}
						else 
						{
							//tf -= c.population*2;
							c.focus = "Production";
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
				}
			}
			for (int r = 0; r < main.grid.rows; r++)
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
						t.occupants.get(i).tick();
					}
				}
			}
		}
	}	

	public void sacrifice(GameEntity en)
	{
		if (en.name.equals("Settler"))
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
	}

}
