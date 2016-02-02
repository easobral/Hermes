package edu.nav.hermes.math.algorithms;

import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by eduardo on 15/01/16.
 * A graph to be used by the algorithms
 * Map data for RIo de Janeiro City extracted at 16/01/16 at 22:35
 * from https://mapzen.com/data/metro-extracts
 */

public class Graph {

    HashMap<Long, List<Graph.Edge>> edges;
    HashMap<Long, GeoPoint> nodes;

    public Graph(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(" ");
            Long id = Long.parseLong(parts[0]);
            float lat = Float.parseFloat(parts[1]);
            float lon = Float.parseFloat(parts[2]);
            GeoPoint point = new GeoPoint(lat, lon);
            ArrayList<Edge> ed = new ArrayList<>(2);
            for (int i = 3; i < parts.length; i++) {
                ed.add(new Edge(Long.parseLong(parts[i])));
            }
            nodes.put(id, point);
            edges.put(id, ed);
        }
    }


    public List<Graph.Edge> getEdges(Long id) {
        return edges.get(id);
    }

    public List<Graph.Edge> getEdges(Graph.Node node) {
        return edges.get(node.id);
    }


    public GeoPoint getData(Long id) {
        return nodes.get(id);
    }

    public float getCost(Long current, Long node) {
        GeoPoint p1 = nodes.get(current);
        GeoPoint p2 = nodes.get(node);
        return p1.distanceTo(p2);
    }

    public Node getNode(Long current) {
        return new Node(current, nodes.get(current));
    }

    public class Edge {
        Long head;

        Edge(Long id) {
            head = id;
        }

        public Long getHead() {
            return head;
        }

        public void setHead(Long head) {
            this.head = head;
        }

    }

    public class Node {

        private GeoPoint data;
        private Long id;

        public Node() {
        }

        public Node(Long id) {
            this.id = id;
        }

        public Node(Long id, GeoPoint point) {
            this.id = id;
            this.data = point;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public GeoPoint getData() {
            return data;
        }

        public void setData(GeoPoint data) {
            this.data = data;
        }

    }

}
