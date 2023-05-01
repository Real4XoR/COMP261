import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

//=============================================================================
//   TODO   Finding Components
//   Finds all the strongly connected subgraphs in the graph
//   Labels each stop with the number of the subgraph it is in
//   sets the subGraphCount of the graph to the number of subgraphs.
//   Uses Kosaraju's_algorithm   (see lecture slides, based on
//   https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm)
//=============================================================================

public class Components {

    public static void findComponents(Graph graph) {
        graph.resetSubGraphIds();

        // Phase 1: Perform a depth-first search in reverse order to determine the finishing times
        Set<Stop> visited = new HashSet<>();
        List<Stop> reverseFinishTimes = new ArrayList<>();
        for (Stop stop : graph.getStops()) {
            if (!visited.contains(stop)) {
                dfsReverse(stop, visited, reverseFinishTimes);
            }
        }

        // Phase 2: Perform a depth-first search in original order using the finishing times to
        // determine the strongly connected components
        Map<Stop, Stop> roots = new HashMap<>();
        int componentId = 0;
        visited.clear();
        for (int i = reverseFinishTimes.size() - 1; i >= 0; i--) {
            Stop stop = reverseFinishTimes.get(i);
            if (!visited.contains(stop)) {
                componentId++;
                dfsForward(stop, visited, roots, componentId);
            }
        }

        graph.setSubGraphCount(componentId);
    }

    private static void dfsReverse(Stop stop, Set<Stop> visited, List<Stop> reverseFinishTimes) {
        visited.add(stop);
        for (Edge edge : stop.getBackwardEdges()) {
            Stop fromStop = edge.fromStop();
            if (!visited.contains(fromStop)) {
                dfsReverse(fromStop, visited, reverseFinishTimes);
            }
        }
        reverseFinishTimes.add(stop);
    }

    private static void dfsForward(Stop stop, Set<Stop> visited, Map<Stop, Stop> roots, int componentId) {
        visited.add(stop);
        stop.setSubGraphId(componentId);
        roots.put(stop, stop);
        for (Edge edge : stop.getForwardEdges()) {
            Stop toStop = edge.toStop();
            if (!visited.contains(toStop)) {
                dfsForward(toStop, visited, roots, componentId);
            }
            // Update root if necessary
            Stop root = roots.get(toStop);
            if (root != null && root.getSubGraphId() != componentId) {
                roots.put(stop, root);
            }
        }
    }
}
