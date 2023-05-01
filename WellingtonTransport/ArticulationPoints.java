import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

//=============================================================================
//   TODO   Finding Articulation Points
//   Finds all the articulation points in the undirected graph, without walking edges
//   Labels each stop with the number of the subgraph it is in
//   sets the subGraphCount of the graph to the number of subgraphs.
//=============================================================================

public class ArticulationPoints{

    // Based on....

    // Returns the collection of nodes that are articulation points 
    // in the UNDIRECTED graph with no walking edges.
    // 
    public static Collection<Stop> findArticulationPoints(Graph graph) {
        System.out.println("calling findArticulationPoints");
        graph.computeNeighbours();   // To ensure that all stops have a set of (undirected) neighbour stops

        Set<Stop> articulationPoints = new HashSet<Stop>();
        System.out.println("calling findArticulationPoints");
        graph.computeNeighbours(); // Ensure that all stops have a set of undirected neighbour stops
    
        Set<Stop> ArticulationPoints = new HashSet<>();
        Map<Stop, Integer> disc = new HashMap<>(); // Discovery time of each stop
        Map<Stop, Integer> low = new HashMap<>(); // Low time of each stop
        Map<Stop, Stop> parent = new HashMap<>(); // Parent of each stop in the DFS tree
        int time = 0; // Current time in the DFS traversal
    
        for (Stop stop : graph.getStops()) {
            if (!disc.containsKey(stop)) {
                findArticulationPointsHelper(stop, disc, low, parent, articulationPoints, time);
            }
        }
    
        return articulationPoints;
    }
    
    private static void findArticulationPointsHelper(Stop stop, Map<Stop, Integer> disc, Map<Stop, Integer> low,
                                                  Map<Stop, Stop> parent, Set<Stop> articulationPoints, int time) {
        int children = 0;
        disc.put(stop, time);
        low.put(stop, time);
        time++;
    
        for (Stop neighbor : stop.getNeighbours()) {
            if (!disc.containsKey(neighbor)) {
                children++;
                parent.put(neighbor, stop);
                findArticulationPointsHelper(neighbor, disc, low, parent, articulationPoints, time);
    
                // Update low time of stop based on its child
                low.put(stop, Math.min(low.get(stop), low.get(neighbor)));
    
                // Check if stop is an articulation point
                if (parent.containsKey(stop) && low.get(neighbor) >= disc.get(stop)) {
                    articulationPoints.add(stop);
                }
    
                // Check if stop is the root with at least 2 children
                if (!parent.containsKey(stop) && children > 1) {
                    articulationPoints.add(stop);
                }
            } else if (!neighbor.equals(parent.get(stop))) {
                // Update low time of stop based on its non-parent neighbor
                low.put(stop, Math.min(low.get(stop), disc.get(neighbor)));
            }
        }
    }
}
