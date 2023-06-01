import javafx.util.Pair;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Write a description of class PageRank here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class PageRank
{
    //class members 
    private static double dampingFactor = .85;
    private static int iter = 10;
    /**
     * build the fromLinks and toLinks 
     */
    //TODO: Build the data structure to support Page rank. For each edge in the graph add the corresponding cities to the fromLinks and toLinks
    public static void computeLinks(Graph graph){
        
        // Build the data structure dto support PageRank
        for (Edge edge : graph.getOriginalEdges()) {
            City fromCity = edge.fromCity();
            City toCity = edge.toCity();

            fromCity.addToLinks(toCity);  // Add toCity as an outlink for fromCity
            toCity.addFromLinks(fromCity);  // Add fromCity as an inlink for toCity
        }
        //printPageRankGraphData(graph);  ////may help in debugging
        // END TODO
    }

    public static void printPageRankGraphData(Graph graph){
        System.out.println("\nPage Rank Graph");

        for (City city : graph.getCities().values()){
            System.out.print("\nCity: "+city.toString());
            //for each city display the in edges 
            System.out.print("\nIn links to cities:");
            for(City c:city.getFromLinks()){

                System.out.print("["+c.getId()+"] ");
            }

            System.out.print("\nOut links to cities:");
            //for each city display the out edges 
            for(City c: city.getToLinks()){
                System.out.print("["+c.getId()+"] ");
            }
            System.out.println();;

        }    
        System.out.println("=================");
    }
    //TODO: Compute rank of all nodes in the network and display them at the console
    public static void computePageRank(Graph graph){
        // TODO

        // Compute rank of all nodes in the network and display them
        Map<String, City> cities = graph.getCities();
        int numCities = cities.size();

        // Initialize page ranks with equal values
        double initialPageRank = 1.0 / numCities;
        Map<String, Double> pageRanks = new HashMap<>();
        for (City city : cities.values()) {
            pageRanks.put(city.getId(), initialPageRank);
        }
        // Perform PageRank iterations
        for (int i = 0; i < iter; i++) {
            Map<String, Double> newPageRanks = new HashMap<>();
            // Calculate new page ranks for each city
            for (City city : cities.values()) {
                double nRank = 0.0;
                // Calculate the sum of page ranks from inlinks
                for (City fromCity : city.getFromLinks()) {
                    double fromPageRank = pageRanks.get(fromCity.getId());
                    int neighbourShare = fromCity.getToLinks().size();
                    nRank += (fromPageRank / neighbourShare);
                }
                // Calculate the new page rank for the current city
                double newPageRank = (1 - dampingFactor) / numCities + dampingFactor * nRank;
                newPageRanks.put(city.getId(), newPageRank);
            }
            // Update page ranks for the next iteration
            pageRanks = newPageRanks;
        }
        // Display the page ranks
        System.out.println("\nPage Ranks:");
        for (Map.Entry<String, Double> entry : pageRanks.entrySet()) {
            String cityId = entry.getKey();
            double pageRank = entry.getValue();
            System.out.println("City: " + cityId + ", Page Rank: " + pageRank);
        }
        // END TODO
    }
}
