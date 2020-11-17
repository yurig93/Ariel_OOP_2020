package ex1;

import java.io.Serializable;
import java.util.*;

public class WGraph_DS implements weighted_graph, Serializable, Cloneable {
    private static final int STATUS_NOT_FOUND = -1;
    private static int nodeIdCounter = 0;

    private int edgeCount;
    private int modeCount;
    private HashMap<Integer, node_info> nodes;
    private HashMap<Integer, HashMap<Integer, node_info>> links;
    private HashMap<Integer, HashMap<Integer, Double>> weights;

    public WGraph_DS() {
        this.nodes = new HashMap<>();
        this.links = new HashMap<>();
        this.weights = new HashMap<>();
        this.edgeCount = 0;
        this.modeCount = 0;
    }

    public WGraph_DS(weighted_graph w) {
        this();

        w.getV().forEach(node -> {
            this.addNode(node);
        });
        w.getV().forEach(node -> {
            w.getV(node.getKey()).forEach(neighbour -> {
                double weight = w.getEdge(node.getKey(), neighbour.getKey());
                if (weight != STATUS_NOT_FOUND) {
                    this.connect(node.getKey(), neighbour.getKey(), weight);
                }
            });
        });

        this.modeCount = w.getMC();
    }

    @Override
    public node_info getNode(int key) {
        return this.nodes.get(key);
    }

    @Override
    public boolean hasEdge(int node1, int node2) {
        return this.links.containsKey(node1) && this.links.get(node1).containsKey(node2);
    }

    @Override
    public double getEdge(int node1, int node2) {
        if (!(this.weights.containsKey(node1) && this.weights.get(node1).containsKey(node2))) {
            return STATUS_NOT_FOUND;
        }
        return this.weights.get(node1).get(node2);
    }

    @Override
    public void addNode(int key) {
        node_info n = new NodeInfo(key);
        this.nodes.put(key, n);
        this.links.put(key, new HashMap<>());
        this.weights.put(key, new HashMap<>());
        this.modeCount++;
    }

    public void addNode(node_info n) {
        int key = n.getKey();
        this.nodes.put(key, n);
        this.links.put(key, new HashMap<>());
        this.weights.put(key, new HashMap<>());
        this.modeCount++;
    }

    @Override
    public void connect(int node1, int node2, double w) {
        if (this.links.containsKey(node1) && this.links.containsKey(node2)) {
            if (!this.links.get(node1).containsKey(node2)) {
                this.links.get(node1).put(node2, this.getNode(node2));
                this.links.get(node2).put(node1, this.getNode(node1));
                this.edgeCount += node1 == node2 ? 0 : 1;
            }
            this.weights.get(node1).put(node2, w);
            this.weights.get(node2).put(node1, w);
            this.modeCount++;
        }
    }

    @Override
    public Collection<node_info> getV() {
        return this.nodes.values();
    }

    @Override
    public Collection<node_info> getV(int node_id) {
        return this.links.containsKey(node_id) ? this.links.get(node_id).values() : null;
    }

    @Override
    public node_info removeNode(int key) {
        node_info removedNode = this.getNode(key);

        if (removedNode == null) {
            return null;
        }

        new HashSet<>(this.links.get(key).keySet()).forEach(node2 -> {
            this.removeEdge(key, node2);
        });

        this.modeCount += 1;
        this.weights.remove(key);
        this.links.remove(key);
        this.nodes.remove(key);
        return removedNode;
    }

    @Override
    public void removeEdge(int node1, int node2) {
        node_info removed = null;

        if (this.links.containsKey(node1) && this.links.containsKey(node2)) {
            removed = this.links.get(node1).remove(node2);
            this.weights.get(node1).remove(node2);
            this.links.get(node2).remove(node1);
            this.weights.get(node2).remove(node1);
        }

        if (removed != null) {
            this.modeCount += 1;
            this.edgeCount -= node1 == node2 ? 0 : 1;
        }
    }

    @Override
    public int nodeSize() {
        return this.nodes.size();
    }

    @Override
    public int edgeSize() {
        return this.edgeCount;
    }

    @Override
    public int getMC() {
        return this.modeCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WGraph_DS)) return false;
        WGraph_DS wGraph_ds = (WGraph_DS) o;

        boolean linksEqual = true;
        Iterator it = links.entrySet().iterator();

        // Manually compare hashmaps since we store Objects and Java checks both with equals and == on them...
        while (it.hasNext() && linksEqual) {
            Map.Entry mapElement = (Map.Entry) it.next();
            HashMap<Integer, node_info> myLinkedNeighbours = (HashMap<Integer, node_info>) mapElement.getValue();
            HashMap<Integer, node_info> targetLinkedNeighbours = wGraph_ds.links.getOrDefault(mapElement.getKey(), new HashMap<>());
            linksEqual = myLinkedNeighbours.keySet().equals(targetLinkedNeighbours.keySet());
        }

        return edgeCount == wGraph_ds.edgeCount &&
                modeCount == wGraph_ds.modeCount &&
                nodes.keySet().equals(wGraph_ds.nodes.keySet()) &&
                weights.equals(wGraph_ds.weights) &&
                linksEqual;
    }

    public class NodeInfo implements node_info, Serializable {
        private int id;
        private String info;
        private double tag;

        public NodeInfo() {
            this.id = nodeIdCounter;
            this.info = "";
            this.tag = 0;
            nodeIdCounter++;
        }

        public NodeInfo(int key) {
            this();
            this.id = key;
        }

        public NodeInfo(node_info n) {
            this(n.getKey());
            n.setTag(this.tag);
            n.setInfo(this.info);
        }


        @Override
        public int getKey() {
            return this.id;
        }

        @Override
        public String getInfo() {
            return this.info;
        }

        @Override
        public void setInfo(String s) {
            this.info = s;
        }

        @Override
        public double getTag() {
            return this.tag;
        }

        @Override
        public void setTag(double t) {
            this.tag = t;
        }
    }
}
