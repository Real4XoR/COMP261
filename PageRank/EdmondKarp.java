
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import javafx.util.Pair;

/** Edmond karp algorithm to find augmentation paths and network flow.
 * 
 * This would include building the supporting data structures:
 * 
 * a) Building the residual graph(that includes original and backward (reverse) edges.)
 *     - maintain a map of Edges where for every edge in the original graph we add a reverse edge in the residual graph.
 *     - The map of edges are set to include original edges at even indices and reverse edges at odd indices (this helps accessing the corresponding backward edge easily)
 *     
 *     
 * b) Using this residual graph, for each city maintain a list of edges out of the city (this helps accessing the neighbours of a node (both original and reverse))

 * The class finds : augmentation paths, their corresponing flows and the total flow
 * 
 * 
 */

public class EdmondKarp {
    // class members

    //data structure to maintain a list of forward and reverse edges - forward edges stored at even indices and reverse edges stored at odd indices
    private static Map<String,Edge> edges; 

    // Augmentation path and the corresponding flow
    private static ArrayList<Pair<ArrayList<String>, Integer>> augmentationPaths =null;

    
    //TODO:Build the residual graph that includes original and reverse edges 
    public static void computeResidualGraph(Graph graph){
        // TODO
        
        int edgeId = 0;
        edges = new HashMap<String, Edge>();

        // Build the residual graph with original and reverse edges
        for (Edge e : graph.getOriginalEdges()) {
            String evenId = ""+edgeId+"";
            Edge forwardEdge = new Edge(e.fromCity(), e.toCity(), e.transpType(), e.capacity(), 0);
            edges.put(evenId, forwardEdge);
            forwardEdge.fromCity().addEdgeId(evenId);
            
            Edge backwardsEdge = new Edge(e.toCity(), e.fromCity(), e.transpType(), 0, 0);
            String oddId = ""+(edgeId+1)+"";
            edges.put(oddId, backwardsEdge);
            backwardsEdge.fromCity().addEdgeId(oddId);
            edgeId+=2;
        }
        
        printResidualGraphData(graph);  //may help in debugging
        // END TODO
    }

    // Method to print Residual Graph 
    public static void printResidualGraphData(Graph graph){
        System.out.println("\nResidual Graph");
        System.out.println("\n=============================\nCities:");
        for (City city : graph.getCities().values()){
            System.out.print(city.toString());

            // for each city display the out edges 
            for(String eId: city.getEdgeIds()){
                System.out.print("["+eId+"] ");
            }
            System.out.println();
        }
        System.out.println("\n=============================\nEdges(Original(with even Id) and Reverse(with odd Id):");
        edges.forEach((eId, edge)->
        System.out.println("["+eId+"] " +edge.toString()));

        System.out.println("===============");
    }

    //=============================================================================
    //  Methods to access data from the graph. 
    //=============================================================================
    /**
     * Return the corresonding edge for a given key
     */

    public static Edge getEdge(String id){
        return edges.get(id);
    }

    /** find maximum flow
     * 
     */
    // TODO: Find augmentation paths and their corresponding flows
    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {
        //TODO
        computeResidualGraph(graph);
        
        augmentationPaths = new ArrayList<Pair<ArrayList<String>, Integer>>();
        Pair<ArrayList<String>, Integer> path;
        while ((path = bfs(graph, from, to)) != null) {
            augmentationPaths.add(path);
        }
        
        // END TODO
        return augmentationPaths;
    }

    // TODO:Use BFS to find a path from s to t along with the correponding bottleneck flow
    public static Pair<ArrayList<String>, Integer>  bfs(Graph graph, City s, City t) {

        ArrayList<String> augmentationPath = new ArrayList<String>();
        HashMap<City, String> backPointer = new HashMap<City, String>();
        // TODO

        Queue<City> queue = new LinkedList<>();
        queue.add(s);
    
        // Mark the source city as visited
        HashSet<City> visited = new HashSet<>();
        visited.add(s);
    
        // BFS traversal
        while (!queue.isEmpty()) {
            City currentCity = queue.poll();
    
            // Iterate over the outgoing edges of the current city
            for (String edgeId : currentCity.getEdgeIds()) {
                Edge edge = getEdge(edgeId);
                City neighbor = edge.toCity();
    
                // If the neighbor city is not visited and the edge has remaining capacity
                if (!visited.contains(neighbor) && edge.capacity() > 0) {
                    // Update the back pointer to keep track of the previous edge
                    backPointer.put(neighbor, edgeId);
    
                    // Mark the neighbor city as visited and add it to the queue
                    visited.add(neighbor);
                    queue.add(neighbor);
    
                    // If the target city is reached, construct the augmentation path and calculate the bottleneck flow
                    if (neighbor == t) {
                        augmentationPath.add(edgeId);
                        int bottleneck = bottleNeck(augmentationPath, edges);
                        City prevCity = currentCity;
    
                        // Traverse back through the back pointer to construct the complete augmentation path
                        while (prevCity != s) {
                            String prevEdgeId = backPointer.get(prevCity);
                            augmentationPath.add(prevEdgeId);
                            prevCity = getEdge(prevEdgeId).fromCity();
                        }
    
                        // Reverse the augmentation path since it was constructed in reverse order
                        Collections.reverse(augmentationPath);
    
                        // Return the augmentation path along with the bottleneck flow
                        return new Pair<>(augmentationPath, bottleneck);
                    }
                }
            }
        }
    
        // If no path is found from s to t, return null
        return null;
    
    }
    public static int bottleNeck(ArrayList<String> path, Map<String, Edge> edges) {
        //Calculates bottle neck
        int bottleNeck = Integer.MAX_VALUE;
        for (String edgeId : path) {
            Edge e = getEdge(edgeId);
            //If capacity is less that bottleneck value then set new bottleneck
            if (e.capacity() <= bottleNeck) {
                bottleNeck = e.capacity();
            }
        }
        return bottleNeck;
    }
}


