package edu.nav.hermes.math.algorithms;

import android.content.res.AssetManager;

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

    static final double LAT_STEP = 0.02;
    static final double LON_STEP = 0.01;
    AssetManager assets;

    HashMap<Long, Entry> cache;

    public Graph(AssetManager assets) {
        this.assets = assets;
    }

    public void updateCache(GeoPoint point) {
        cache = new HashMap<>();
        String file = getFileFromGeopoint(point);

        InputStream is;
        BufferedReader br;
        try {
            is = assets.open("database/" + file, AssetManager.ACCESS_STREAMING);
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                Long id = Long.parseLong(parts[0]);
                float lat = Float.parseFloat(parts[1]);
                float lon = Float.parseFloat(parts[2]);
                GeoPoint p = new GeoPoint(lat, lon);
                ArrayList<Edge> ed = new ArrayList<>(2);
                for (int i = 3; i < parts.length; i++) {
                    ed.add(new Edge(Long.parseLong(parts[i])));
                }
                Entry e = new Entry();
                e.edges = ed;
                e.point = p;
                cache.put(id, e);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getFileFromGeopoint(GeoPoint point) {
        double x = Math.floor(point.getLatitude() / LAT_STEP);
        double y = Math.floor(point.getLongitude() / LON_STEP);
        return "" + x + "_" + y;
    }

    /**
     * @param id the node ID
     * @return the name where the file where ID can be found.
     */
    private String getFileFromID(Long id) {
        return String.valueOf(Math.floor(id / 100));
    }

    private GeoPoint getGeoPointFromID(Long id) {
        String file = getFileFromID(id);
        InputStream is;
        BufferedReader br;
        GeoPoint return_point = null;
        try {
            is = assets.open("id_database/" + file, AssetManager.ACCESS_STREAMING);
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                Long p_id = Long.parseLong(parts[0]);

                if (!id.equals(p_id)) continue;

                float lat = Float.parseFloat(parts[1]);
                float lon = Float.parseFloat(parts[2]);
                return_point = new GeoPoint(lat, lon);
                break;
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return return_point;
    }

    /**
     * @param id the node ID
     * @return the entry in the cache or null if it is not on database
     */
    private Entry getEntry(Long id) {
        Entry entry = cache.get(id);
        if (null == entry) {
            GeoPoint point = getGeoPointFromID(id);
            updateCache(point);
            entry = cache.get(id);
        }
        return entry;
    }


    public List<Graph.Edge> getEdges(Long id) {
        return getEntry(id).edges;
    }

    public List<Graph.Edge> getEdges(Graph.Node node) {
        return getEntry(node.id).edges;
    }


    public GeoPoint getData(Long id) {
        return getEntry(id).point;
    }

    public float getCost(Long current, Long node) {
        GeoPoint p1 = getEntry(current).point;
        GeoPoint p2 = getEntry(node).point;
        return p1.distanceTo(p2);
    }

    public Node getNode(Long current) {
        return new Node(current, getEntry(current).point);
    }

    public Node getClosestNode(GeoPoint geopoint) {
        return null;
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

    private class Entry {
        GeoPoint point;
        ArrayList<Edge> edges;
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
