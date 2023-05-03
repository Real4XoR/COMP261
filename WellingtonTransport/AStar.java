/**
 * Implements the A* search algorithm to find the shortest path
 * in a graph between a start node and a goal node.
 * It returns a Path consisting of a list of Edges that will
 * connect the start node to the goal node.
 */

import java.util.Collections;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;


public class AStar {
    private static String timeOrDistance = "distance";    // way of calculating cost: "time" or "distance"

    // find the shortest path between two stops
    public static List<Edge> findShortestPath(Stop start, Stop goal, String timeOrDistance) {
        if (start == null || goal == null) {return null;}
        timeOrDistance= (timeOrDistance.equals("time"))?"time":"distance";

        PriorityQueue<PathItem> frontier = new PriorityQueue<>();
        Map<Stop, Edge> cameFrom = new HashMap<>();
        Map<Stop, Double> costSoFar = new HashMap<>();
        Set<Stop> visited = new HashSet<>();

        frontier.add(new PathItem(start, null, 0.0, heuristic(start, goal)));

        while (!frontier.isEmpty()) {
            PathItem current = frontier.poll();

            if (current.getStop().equals(goal)) {
                return reconstructedPath(cameFrom, current.getStop());
            }

            visited.add(current.getStop());

            for (Edge edge : current.getStop().getEdges()) {
                Stop next = edge.toStop();

                if (visited.contains(next)) {
                    continue;
                }

                double newCost = costSoFar.getOrDefault(current.getStop(), 0.0) + edgeCost(edge);

                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    double priority = newCost + heuristic(next, goal);
                    frontier.add(new PathItem(next, edge, newCost, priority));
                    cameFrom.put(next, edge);
                }
            }
        }

        return null;
    }
    
    private static List<Edge> reconstructedPath(Map<Stop, Edge> cameFrom, Stop current) {
        LinkedList<Edge> path = new LinkedList<>();
        while (cameFrom.containsKey(current)) {
            Edge edge = cameFrom.get(current);
            path.addFirst(edge);
            current = edge.fromStop();
        }
        return path;
    }
    
    /** Return the heuristic estimate of the cost to get from a stop to the goal */
    public static double heuristic(Stop current, Stop goal) {
        if (timeOrDistance=="distance"){ return current.distanceTo(goal);}
        else if (timeOrDistance=="time"){return current.distanceTo(goal) / Transport.TRAIN_SPEED_MPS;}
        else {return 0;}
    }

    /** Return the cost of traversing an edge in the graph */
    public static double edgeCost(Edge edge){
        if (timeOrDistance=="distance"){ return edge.distance();}
        else if (timeOrDistance=="time"){return edge.time();}
        else {return 1;}
    }
}
