import java.util.List;

/**
 * AStar search (and Dijkstra search) uses a priority queue of partial paths
 * that the search is building.
 * Each partial path needs several pieces of information, to specify
 * the path to that point, its cost so far, and its estimated total cost
 */
public class PathItem implements Comparable<PathItem> {
    //TODO
    private Stop stop;
    private double costSoFar;
    private double estimatedTotalCost;
    private List<Edge> path;

    public PathItem(Stop stop, double costSoFar, double estimatedTotalCost, List<Edge> path) {
        this.stop = stop;
        this.costSoFar = costSoFar;
        this.estimatedTotalCost = estimatedTotalCost;
        this.path = path;
    }

    public Stop getStop() {
        return stop;
    }

    public double getCostSoFar() {
        return costSoFar;
    }

    public double getEstimatedTotalCost() {
        return estimatedTotalCost;
    }

    public List<Edge> getPath() {
        return path;
    }

    @Override
    public int compareTo(PathItem other) {
        if (this.estimatedTotalCost < other.estimatedTotalCost) {
            return -1;
        } else if (this.estimatedTotalCost > other.estimatedTotalCost) {
            return 1;
        } else {
            return 0;
        }
    }
}





