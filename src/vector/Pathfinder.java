package vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Dante on 6/23/2016.
 * Took this from 61b proj3 because it is cleaner, even if it uses more memory
 *
 * This is an implementation of A* which relies on the Traversable interface.
 * A* finds a best path (a,b) between two points. It searches through candidates v
 * by the lowest sum of current distance (a -> v) and a heuristic distance (v -> b).
 */
public class Pathfinder<GraphNode extends Traversable<GraphNode>> {

    public List<GraphNode> findPath(GraphNode start, final GraphNode end) {
        List<GraphNode> results = new ArrayList<>();
        if (start.equals(end)) {
            results.add(end);
            return results;
        }
        HashSet<GraphNode> visited = new HashSet<>();
        final HashMap<GraphNode, Double> dist = new HashMap<>();
        HashMap<GraphNode, GraphNode> prev = new HashMap<>();
        PriorityQueue<GraphNode> fringe;

        fringe = new PriorityQueue<GraphNode>(16, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                GraphNode n1 = (GraphNode) o1;
                GraphNode n2 = (GraphNode) o2;
                if (n1.equals(n2)) return 0;
                //return (int)((dist.get(n1) - dist.get(n2) + end.dist(n1) - end.dist(n2)*16.0d));
                return dist.get(n1) - dist.get(n2) + end.dist(n1) - end.dist(n2) > 0 ? 1 : -1;
            }
        });

        fringe.add(start);
        dist.put(start, 0.0);
        while (!fringe.isEmpty()) {
            GraphNode v = fringe.poll();
            if (visited.contains(v)) {
                continue;
            }
            visited.add(v);
            if (v.equals(end)) {
                do {
                    results.add(0, v);
                    v = prev.get(v);
                } while (v != null);
                return results;
            }
            for (Traversable t : v.neighbors()) {
                GraphNode c = (GraphNode) t;
                if ((!dist.containsKey(c)) || (dist.containsKey(c) && dist.get(c) > dist.get(v) + v.dist(c))) {
                    dist.put(c, dist.get(v) + v.dist(c));
                    //c.queue = dist.get(v) + v.dist(c) + c.dist(end);
                    fringe.add(c);
                    prev.put(c, v);
                }
            }
        }
        return null;
    }

}
