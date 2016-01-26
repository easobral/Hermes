package edu.nav.hermes.math.algorithms;


import java.util.HashMap;
import java.util.List;

/**
 * Created by eduardo on 26/01/16.
 */
public class Loader {


    public Object load(Object extra, Object nodes) {
        GeoPointNode p = (GeoPointNode) extra;
        HashMap<GeoPointNode, List<Graph.Edge>> map = (HashMap<GeoPointNode, List<Graph.Edge>>) nodes;

        
        return map;
    }

}
