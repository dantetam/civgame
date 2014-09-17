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
								main.grid.move(en,r,c);
							}
						}
						if (Math.random() < 0.1 && en.location.owner == null && en.location.biome != -1)
						{
							sacrifice(en);
						}
					}
					double tf = 0, tg = 0, tm = 0;
					for (int j = 0; j < civ.cities.size(); j++)
					{
						City c = civ.cities.get(j);
						//Make some settlers to test
						int numSettlers = 0;
						for (int k = 0; k < civ.cities.size(); k++)
						{
							if (civ.cities.get(k).queue != null)
							{
								if (civ.cities.get(k).queue.equals("Settler"))
								{
									numSettlers++;
								}
							}
						}
						if (c.queue == null && civ.units.size() + numSettlers < 3)
						{
							//System.out.println(civ.units.size());
							c.queue = "Settler";
							c.queueTurns = 10;
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
						 * 0 ice 0,1,2
						 * 1 taiga 1,1,1
						 * 2 desert 0,0,2
						 * 3 savannah 2,0,1
						 * 4 dry forest 2,1,1
						 * 5 forest 3,0,1
						 * 6 rainforest 3,1,0
						 * 7 beach (outdated)
						 * 
						 * modifiers:
						 * 8 oasis 3,3,0
						 * hill -1,0,1
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
							double f,g,m;
							Tile t = c.workedLand.get(k);
							if (t.biome == -1)
							{
								f = 1; g = 1; m = 0;
							}
							else if (t.biome == 0)
							{
								f = 0; g = 1; m = 2;
							}
							else if (t.biome == 1)
							{
								f = 1; g = 1; m = 1;
							}
							else if (t.biome == 2)
							{
								f = 0; g = 0; m = 2;
							}
							else if (t.biome == 3)
							{
								f = 2; g = 0; m = 1;
							}
							else if (t.biome == 4)
							{
								f = 2; g = 1; m = 1;
							}
							else if (t.biome == 5)
							{
								f = 3; g = 0; m = 1;
							}
							else if (t.biome == 6)
							{
								f = 3; g = 1; m = 0;
							}
							else
							{
								System.err.println("Invalid biomerrr " + t.biome);
								f = 0; g = 0; m = 0;
							}
							//civ.food += f;
							//civ.gold += g;
							//civ.metal += m;
							tf += f; tg += g; tm += m;
							c.workedLand.get(k).harvest = true;
						}
					}
					for (int j = 0; j < civ.cities.size(); j++)
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
					}
					civ.food += tf;
					civ.gold += tg;
					civ.metal += tm;
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
