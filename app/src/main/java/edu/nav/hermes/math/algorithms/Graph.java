package edu.nav.hermes.math.algorithms;

import java.util.HashMap;
import java.util.List;

/**
 * Created by eduardo on 15/01/16.
 * A graph to be used by the algorithms
 * Map data for RIo de Janeiro City extracted at 16/01/16 at 22:35
 * from https://mapzen.com/data/metro-extracts
 */

public class Graph<T> {

    HashMap<T, List<Graph.Edge>> nodes;
    Loader loader;

    public Graph(Loader loader) {
        nodes = new HashMap<>();
        this.loader = loader;
    }

    public List<Graph<T>.Edge> getEdges(T extra) {
        if (nodes.containsKey(extra))
            return nodes.get(extra);
        nodes = (HashMap<T, List<Graph.Edge>>) loader.load(extra, nodes);
        return nodes.get(extra);
    }

    public class Edge {
        float cost;
        T head;

        public float getCost() {
            return cost;
        }

        public void setCost(float cost) {
            this.cost = cost;
        }

        public T getHead() {
            return head;
        }

        public void setHead(T head) {
            this.head = head;
        }

    }

}
