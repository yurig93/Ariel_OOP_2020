package ex1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;


public class WGraph_Algo implements weighted_graph_algorithms {
    public static final double DEFAULT_INF = Double.POSITIVE_INFINITY;
    public static final int DEFAULT_NOT_FOUND = -1;

    private static final int STATUS_NOT_VISITED = 0;
    private static final int STATUS_VISITED = 1;
    private static final int STATUS_QUEUED = 2;

    private weighted_graph g;
    private int lastVisitedCount;
    private double lastVisitedEdgeSum;

    public WGraph_Algo() {
        this.g = null;

        // Not thread safe
        this.lastVisitedCount = 0;
        this.lastVisitedEdgeSum = 0;
    }

    @Override
    public void init(weighted_graph g) {
        this.g = g;
        this.lastVisitedCount = 0;
    }

    @Override
    public weighted_graph getGraph() {
        return this.g;
    }

    @Override
    public weighted_graph copy() {
        return new WGraph_DS(this.g);
    }

    @Override
    public boolean isConnected() {
        if (this.g.nodeSize() <= 0) {
            return true;
        }

        this.shortestPath(this.g.getV().iterator().next().getKey(), DEFAULT_NOT_FOUND);
        return this.g.nodeSize() == this.lastVisitedCount;
    }

    @Override
    public double shortestPathDist(int src, int dest) {
        this.shortestPath(src, dest);
        return this.lastVisitedEdgeSum;
    }

    public List<node_info> backtrackPath(int src, node_info neededNode) {
        LinkedList<node_info> path = new LinkedList<>();

        if (neededNode != null && !neededNode.getInfo().isEmpty()) {
            node_info backtrackingNode = neededNode;
            while (backtrackingNode.getKey() != src) {
                path.offerFirst(backtrackingNode);
                if (!backtrackingNode.getInfo().isEmpty()) {
                    backtrackingNode = this.g.getNode(Integer.parseInt(backtrackingNode.getInfo()));
                }
            }
            path.offerFirst(backtrackingNode);
        }
        return path;
    }

    @Override
    public List<node_info> shortestPath(int src, int dest) {
        this.lastVisitedCount = 0;

        HashMap<Integer, Double> distances = new HashMap<>();
        Queue<node_info> q = new PriorityQueue<>(new NodeDistanceComperator(distances));
        LinkedList<node_info> visitedNodes = new LinkedList<>();

        q.add(this.g.getNode(src));
        distances.put(src, 0.0);

        node_info neededNode = null;

        while (!q.isEmpty() && this.g.nodeSize() != visitedNodes.size()) {

            // Update needed node info and tracking info
            node_info node = q.remove();
            visitedNodes.add(node);
            node.setTag(STATUS_VISITED);
            neededNode = node.getKey() == dest ? node : neededNode;

            this.g.getV(node.getKey()).forEach(neighbor -> {
                if (neighbor.getTag() != STATUS_VISITED) {
                    // Found a shorter commutative distance, update and requeue.
                    double newNeigbourDistance = distances.get(node.getKey()) + this.g.getEdge(node.getKey(), neighbor.getKey());
                    if (newNeigbourDistance < distances.getOrDefault(neighbor.getKey(), DEFAULT_INF)) {
                        distances.put(neighbor.getKey(), newNeigbourDistance);
                        q.add(neighbor);
                        neighbor.setTag(STATUS_QUEUED);
                        neighbor.setInfo(Integer.toString(node.getKey()));
                    }
                }
            });
        }

        // Build path
        List<node_info> path = this.backtrackPath(src, neededNode);

        // Revert previous state, can also be done on start of scan, but this code was reused from BFS.
        visitedNodes.forEach(node ->
                node.setTag(STATUS_NOT_VISITED));

        this.lastVisitedCount = visitedNodes.size();
        this.lastVisitedEdgeSum = distances.getOrDefault(dest, (double) DEFAULT_NOT_FOUND);
        return path;
    }

    @Override
    public boolean save(String file) {
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this.g);
            objectOut.close();
        } catch (Exception ex) {
            // Should do something with it, print trace?
            return false;
        }
        return true;
    }

    @Override
    public boolean load(String file) {
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            this.g = (weighted_graph) objectIn.readObject();
            objectIn.close();
        } catch (Exception ex) {
            // Should do something with it, print trace?
            return false;
        }
        return true;
    }

    class NodeDistanceComperator implements Comparator<node_info> {
        HashMap<Integer, Double> distances;

        public NodeDistanceComperator(HashMap<Integer, Double> d) {
            this.distances = d;
        }

        public int compare(node_info n1, node_info n2) {
            double n1Distance = this.distances.getOrDefault(n1.getKey(), DEFAULT_INF);
            double n2Distance = this.distances.getOrDefault(n2.getKey(), DEFAULT_INF);
            return Double.compare(n1Distance, n2Distance);
        }
    }
}
