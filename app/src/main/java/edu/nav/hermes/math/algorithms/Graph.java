package edu.nav.hermes.math.algorithms;

import java.util.List;
import java.util.Set;

/**
 * Created by eduardo on 15/01/16.
 * A graph to be used by the algorithms
 */
public class Graph<T> {

    Set<Node<T>> nodeSet;

    public Graph() {

    }

    public class Node<E> {
        private List<Edge<E>> edges;
        private E extra;

        public E getExtra() {
            return extra;
        }

        public void setExtra(E extra) {
            this.extra = extra;
        }

        public List<Edge<E>> getEdges() {
            return edges;
        }

        public void setEdges(List<Edge<E>> edges) {
            this.edges = edges;
        }

        public void addEdge(Edge<E> edge) {
            edges.add(edge);
        }
    }

    public class Edge<E> {
        float cost;
        Node<E> head;

        public float getCost() {
            return cost;
        }

        public void setCost(float cost) {
            this.cost = cost;
        }

        public Node<E> getHead() {
            return head;
        }

        public void setHead(Node<E> head) {
            this.head = head;
        }

    }


}
