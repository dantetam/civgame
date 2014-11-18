package game;

import java.util.ArrayList;

public class Pathfinder {

	public Grid grid;
	public Node[][] nodes;
	public Node start;
	public Node end;
	public ArrayList<Node> openSet;
	public ArrayList<Node> closedSet;

	public Pathfinder(Grid grid)
	{
		this.grid = grid;
		nodes = new Node[grid.rows][grid.cols];
		for (int r = 0; r < grid.rows; r++)
		{
			for (int c = 0; c < grid.cols; c++)
			{
				if (grid.getTile(r,c).biome != -1)
					nodes[r][c] = new Node(r,c);
			}
		}
		//System.out.println(x1 + "," + y1 + "," + x2 + "," + y2);
	}

	//http://theory.stanford.edu/~amitp/GameProgramming/
	public ArrayList<Tile> findPath(Civilization civ, int x1, int y1, int x2, int y2, boolean diagonal)
	{
		for (int r = 0; r < grid.rows; r++)
		{
			for (int c = 0; c < grid.cols; c++)
			{
				if (grid.getTile(r,c).biome != -1)
				{
					//nodes[r][c] = new Node(r,c);
					nodes[r][c].parent = null;
					nodes[r][c].g = 0;
					nodes[r][c].queue = 0;
				}
			}
		}
		start = nodes[x1][y1];
		start.queue = 0;
		end = nodes[x2][y2];
		openSet = new ArrayList<Node>();
		closedSet = new ArrayList<Node>();

		openSet.add(start);

		Node lastNode = start;
		do
		{
			//System.out.println("ran");
			Node current = openSet.get(findLowestQueueIndex(openSet));
			openSet.remove(findLowestQueueIndex(openSet));
			closedSet.add(current);
			ArrayList<Node> ns = findValidNeighbors(current,civ,diagonal);
			for (int i = 0; i < ns.size(); i++)
			{
				double cost;
				if (diagonal)
					cost = current.g + current.dist(ns.get(i));
				else
					cost = current.g + 1;
				/*if (current.r != ns.get(i).r && current.c != ns.get(i).c)
					cost = current.g + 1.4;
				else
					cost = current.g + 1;*/
				if (openSet.contains(ns.get(i)) && cost < ns.get(i).g)
				{
					removeNodeFromOpen(ns.get(i));
					//closedSet.add(ns.get(i));
				}
				if (closedSet.contains(ns.get(i)) && cost < ns.get(i).g)
				{
					removeNodeFromClosed(ns.get(i));
				}
				if (!openSet.contains(ns.get(i)) && !closedSet.contains(ns.get(i)))
				{
					ns.get(i).g = cost;
					openSet.add(ns.get(i));
					double dist = ns.get(i).dist(end);
					if (dist == -1)
					{
						//System.err.println("No path found.");
						return null;
					}
					ns.get(i).queue = ns.get(i).g + 1.1*dist;
					ns.get(i).parent = current;
					lastNode = ns.get(i);
				}
			}
			for (int i = openSet.size() - 1; i >= 0; i--)
			{
				if (openSet.get(i).dist(end) > 1.25*start.dist(end))
				{
					openSet.remove(i);
				}
			}
			if (openSet.size() == 0) 
			{
				//System.err.println("No path found.");
				return null;
			}
		} while (!openSet.get(findLowestQueueIndex(openSet)).equals(end));
		ArrayList<Tile> temp = new ArrayList<Tile>();
		do
		{
			temp.add(grid.getTile(lastNode.r,lastNode.c));
			lastNode = lastNode.parent;
		} while (lastNode.parent != null);
		return temp;
	}

	public ArrayList<Tile> findAdjustedPath(Civilization civ, int x1, int y1, int x2, int y2)
	{
		ArrayList<Tile> temp = findPath(civ,x1,y1,x2,y2,true);
		if (temp == null) return null;
		if (!temp.get(0).equals(grid.getTile(end.r,end.c)))
		{
			temp.remove(0);
			temp.add(0,grid.getTile(end.r,end.c));
		}
		//temp.add(temp.size(),new Location(start.r,start.c));
		return temp;
	}

	public int findLowestQueueIndex(ArrayList<Node> nodes)
	{
		if (nodes.size() == 1) return 0;
		int lowestIndex = 1;
		for (int i = 0; i < nodes.size(); i++)
		{
			if (nodes.get(i).queue < nodes.get(lowestIndex).queue)
				lowestIndex = i;
		}
		return lowestIndex;
	}

	public void removeNodeFromOpen(Node node)
	{
		for (int i = 0; i < openSet.size(); i++)
		{
			if (openSet.get(i).equals(node))
			{
				openSet.remove(i);
				return;
			}
		}
	}

	public void removeNodeFromClosed(Node node)
	{
		for (int i = 0; i < closedSet.size(); i++)
		{
			if (closedSet.get(i).equals(node))
			{
				closedSet.remove(i);
				return;
			}
		}
	}

	public ArrayList<Node> findValidNeighbors(Node node, Civilization civ, boolean diagonal)
	{
		ArrayList<Node> temp = new ArrayList<Node>();
		int r = node.r;
		int c = node.c;
		try
		{
			if (nodes[r+1][c] != null)
			{
				temp.add(nodes[r+1][c]);
			}
			if (nodes[r-1][c] != null)
			{
				temp.add(nodes[r-1][c]);
			}
			if (nodes[r][c+1] != null)
			{
				temp.add(nodes[r][c+1]);
			}
			if (nodes[r][c-1] != null)
			{
				temp.add(nodes[r][c-1]);
			}
			if (diagonal)
			{
				if (nodes[r+1][c+1] != null)
				{
					temp.add(nodes[r+1][c+1]);
				}
				if (nodes[r+1][c-1] != null)
				{
					temp.add(nodes[r+1][c-1]);
				}
				if (nodes[r-1][c+1] != null)
				{
					temp.add(nodes[r-1][c+1]);
				}
				if (nodes[r-1][c-1] != null)
				{
					temp.add(nodes[r-1][c-1]);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {}
		for (int i = temp.size() - 1; i >= 0; i--)
		{
			Civilization civ2 = grid.getTile(temp.get(i).r, temp.get(i).c).owner;
			if (civ2 != null)
				if (!civ.isOpenBorder(civ2) && !civ.isWar(civ2) && !civ.equals(civ2))
					temp.remove(i);
		}
		return temp;
	}

	public class Node
	{
		public int r,c;
		public double g;
		public Node parent;
		public double queue;
		public Node(int r, int c)
		{
			this.r = r;
			this.c = c;
			g = 0;
		}
		public boolean equals(Node n)
		{
			return r == n.r && c == n.c;
		}
		public double dist(Node other)
		{
			if (other != null)
				return Math.sqrt(Math.pow(other.r - r,2) + Math.pow(other.c - c,2));
				//return Math.abs((double)other.r - r) + Math.abs((double)other.c - c);
			return -1;
		}
	}

	public class Location {int r, c; public Location(int x, int y) {r=x; c=y;}}

}
