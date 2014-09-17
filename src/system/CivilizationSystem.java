package system;

import java.util.ArrayList;

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
						if (main.grid.getTile(en.location.row+r,en.location.col+c) != null)
						{
							if (main.grid.getTile(en.location.row+r,en.location.col+c).owner == en.owner ||
									main.grid.getTile(en.location.row+r,en.location.col+c).owner == null)
							{
								GameEntity enemy = main.grid.hasEnemy(en,r,c);
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
									main.grid.move(en,r,c);
								}
							}
						}
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
								else if (civ.cities.get(k).queue.equals("Settler"))
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
								if (numSettlers < 3)
								{
									c.queue = "Settler";
									c.queueTurns = 15;
								}
								else if (numWorkers < civ.cities.size())
								{
									c.queue = "Worker";
									c.queueTurns = 15;
								}
							}
							else if (c.focus.equals("Production"))
							{
								c.queue = "Warrior";
								c.queueTurns = 6;
							}
						}
						else if (c.queue != null)
						{
							c.queueTurns--;
							if (c.queueTurns == 0)
							{
								main.grid.addUnit(EntityData.get(c.queue),civ,c.location.row,c.location.col);
								c.queue = null;
							}
						}
						//Loop through a city's tiles
						/*
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
							double f,g,m,r;
							Tile t = c.workedLand.get(k);
							if (t.biome == -1)
							{
								f = 1; g = 1; m = 0; r = 2;
							}
							else if (t.biome == 0)
							{
								f = 0; g = 1; m = 2; r = 1;
							}
							else if (t.biome == 1)
							{
								f = 1; g = 1; m = 1; r = 1;
							}
							else if (t.biome == 2)
							{
								f = 0; g = 0; m = 2; r = 1;
							}
							else if (t.biome == 3)
							{
								f = 2; g = 0; m = 1; r = 2;
							}
							else if (t.biome == 4)
							{
								f = 2; g = 1; m = 1; r = 2;
							}
							else if (t.biome == 5)
							{
								f = 3; g = 0; m = 1; r = 2;
							}
							else if (t.biome == 6)
							{
								f = 3; g = 1; m = 0; r = 3;
							}
							else
							{
								System.err.println("Invalid biomerrr " + t.biome);
								f = 0; g = 0; m = 0; r = 0;
							}
							if (t.shape == 1)
							{
								f--;
								m++;
							}
							else if (t.shape == 2)
							{
								f -= 2;
								m += 2;
								r += 2;
							}
							if (c.location.equals(t))
							{
								f = 1; g = 2; m = 1; r = 2;
							}
							//civ.food += f;
							//civ.gold += g;
							//civ.metal += m;
							//tf += f;
							if (c.focus.equals("Growth") && c.health >= 0)
							{
								if (f >= c.population*3)
								{
									f -= c.population*3;
									c.percentGrowth += 0.1;
								}
								else
								{
									c.percentGrowth -= 0.1;
								}
								if (c.percentGrowth >= 1)
								{
									c.percentGrowth = 0;
									c.population++;
									c.focus = "Production";
								}
								else if (c.percentGrowth < 0 && c.population > 1)
								{
									c.percentGrowth = 0;
									c.population--;
									c.focus = "Growth";
								}
							}
							else if (c.health <= 0)
							{
								f -= c.population*2;
							}
							tf += f; tg += g; tm += m; tr += r;
							c.workedLand.get(k).harvest = true;
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
