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

    static final double LAT_STEP = 0.02 * 1E6;
    static final double LON_STEP = 0.01 * 1E6;

    static final int ID = 0;
    static final int LAT = 1;
    static final int LON = 2;

    AssetManager assets;

    HashMap<Long, Entry>[] cache;

    @SuppressWarnings("unchecked")
    public Graph(AssetManager assets) {
        this.assets = assets;
        cache = new HashMap[]{new HashMap<>(1000)};
    }

    @SuppressWarnings("unchecked")
    public Graph(AssetManager assets, int cache_size) {
        this.assets = assets;
        cache = new HashMap[cache_size];
        for (int i = 0; i < cache_size; i++) {
            cache[i] = new HashMap<>(1000);
        }
    }

    public void updateCache(GeoPoint point, int hash) {
        cache[hash].clear();
        String file = getFileFromGeopoint(point);

        InputStream is;
        BufferedReader br;
        try {
            is = assets.open("database/" + file, AssetManager.ACCESS_STREAMING);
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                Long id = Long.parseLong(parts[ID]);
                int lat = Integer.parseInt(parts[LAT]);
                int lon = Integer.parseInt(parts[LON]);
                GeoPoint p = new GeoPoint(lat, lon);
                ArrayList<Edge> ed = new ArrayList<>(2);
                for (int i = 3; i < parts.length; i++) {
                    ed.add(new Edge(Long.parseLong(parts[i])));
                }
                Entry e = new Entry();
                e.edges = ed;
                e.point = p;
                cache[hash].put(id, e);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                Long p_id = Long.parseLong(parts[ID]);

                if (!id.equals(p_id)) continue;

                int lat = Integer.parseInt(parts[LAT]);
                int lon = Integer.parseInt(parts[LON]);
                return_point = new GeoPoint(lat, lon);
                break;
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return return_point;
    }

    public Long getClosestNode(GeoPoint geopoint) {
        String file = getFileFromGeopoint(geopoint);
        InputStream is;
        BufferedReader br;
        Long closest = null;
        double distance = Double.POSITIVE_INFINITY;

        try {
            is = assets.open("database/" + file, AssetManager.ACCESS_STREAMING);
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                Long p_id = Long.parseLong(parts[ID]);

                int lat = Integer.parseInt(parts[LAT]);
                int lon = Integer.parseInt(parts[LON]);
                GeoPoint p = new GeoPoint(lat, lon);

                double p_distance = p.distanceTo(geopoint);
                if (p_distance < distance) {
                    distance = p_distance;
                    closest = p_id;
                }

            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return closest;
    }


    private String getFileFromGeopoint(GeoPoint point) {
        Integer x = (int) Math.floor(point.getLatitudeE6() / LAT_STEP);
        Integer y = (int) Math.floor(point.getLongitudeE6() / LON_STEP);
        return "" + x + "_" + y;
    }

    /**
     * @param id the node ID
     * @return the name where the file where ID can be found.
     */
    private String getFileFromID(Long id) {
        int hash = (int) Math.floor(id / 100);
        return String.valueOf(hash);
    }

    private int geoHash(GeoPoint point) {
        int x = (int) Math.floor(point.getLatitudeE6() / LAT_STEP);
        int y = (int) Math.floor(point.getLongitudeE6() / LON_STEP);
        return Math.abs(x * 37 + y * 53);
    }

    /**
     * @param id the node ID
     * @return the entry in the cache or null if it is not on database
     */
    private Entry getEntry(Long id) {
        int i = 0;
        Entry entry;
        for (; i < cache.length; i++) {
            entry = cache[i].get(id);
            if (null != entry) return entry;
        }
        GeoPoint point = getGeoPointFromID(id);
        updateCache(point, geoHash(point) % cache.length);
        return cache[geoHash(point) % cache.length].get(id);
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

    public double getCost(Long current, Long node) {
        GeoPoint p1 = getEntry(current).point;
        GeoPoint p2 = getEntry(node).point;
        return p1.distanceTo(p2);
    }

    public Node getNode(Long current) {
        return new Node(current, getEntry(current).point);
    }

    public Node getNode(Long current, Node reuse) {
        reuse.id = current;
        reuse.data = getEntry(current).point;
        return reuse;
    }


    public static class Edge {
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

    public static class Node {

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

    private class Entry {
        GeoPoint point;
        ArrayList<Edge> edges;
    }

}
