package edu.nav.hermes.math.algorithms;

import java.util.HashMap;
import java.util.List;

/**
 * Created by eduardo on 15/01/16.
 * A graph to be used by the algorithms
 */
public class Graph<T> {

    HashMap<T, List<Graph.Edge>> nodes;

    public Graph() {
        nodes = new HashMap<>();
    }

    public List<Graph<T>.Edge> getEdges(T extra) {
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
