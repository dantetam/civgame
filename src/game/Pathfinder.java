package game;

import java.util.ArrayList;

public class Pathfinder {

	public Grid grid;

	public Pathfinder(Grid grid)
	{
		this.grid = grid;
	}

	public Node[][] nodes;
	public Node start;
	public Node end;
	public ArrayList<Node> openSet;
	public ArrayList<Node> closedSet;

	public Pathfinder(Grid grid, int x1, int y1, int x2, int y2)
	{
		this.grid = grid;
		nodes = new Node[grid.rows+1][grid.rows+1];
		for (int r = 0; r <= grid.rows; r++)
		{
			for (int c = 0; c <= grid.cols; c++)
			{
				if (grid.getTile(r,c).biome != -1)
					nodes[r][c] = new Node(r,c);
			}
		}
		start = nodes[x1][y1];
		start.queue = 0;
		end = nodes[x2][y2];
		System.out.println(x1 + "," + y1 + "," + x2 + "," + y2);
	}

	public ArrayList<Location> findPath(boolean diagonal)
	{
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
			ArrayList<Node> ns = findValidNeighbors(current,diagonal);
			for (int i = 0; i < ns.size(); i++)
			{
				double cost;
				if (diagonal)
					cost = current.g + current.dist(ns.get(i));
				else
					cost = current.g + 1;
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
					ns.get(i).queue = ns.get(i).g + 1.1*ns.get(i).dist(end);
					ns.get(i).parent = current;
					lastNode = ns.get(i);
				}
			}
			if (openSet.size() == 0) 
			{
				System.err.println("No path found.");
				return null;
			}
		} while (!openSet.get(findLowestQueueIndex(openSet)).equals(end));
		ArrayList<Location> temp = new ArrayList<Location>();
		do
		{
			temp.add(new Location(lastNode.r,lastNode.c));
			lastNode = lastNode.parent;
		} while (lastNode.parent != null);
		return temp;
	}

	public ArrayList<Location> findAdjustedPath()
	{
		ArrayList<Location> temp = findPath(false);
		if (temp == null) return null;
		if (!temp.get(0).equals(new Location(end.r,end.c)))
		{
			temp.remove(0);
			temp.add(0,new Location(end.r,end.c));
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

	public ArrayList<Node> findValidNeighbors(Node node, boolean diagonal)
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
			return Math.abs((double)other.r - r) + Math.abs((double)other.c - c);
		}
	}
	
	public class Location {int r, c; public Location(int x, int y) {r=x; c=y;}}

}
