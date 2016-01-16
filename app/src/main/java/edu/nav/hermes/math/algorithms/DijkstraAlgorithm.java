package edu.nav.hermes.math.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by eduardo on 16/01/16.
 * A class that encapsulates dijkstra Algorithm
 */
public class DijkstraAlgorithm<T> {

    private Graph<T> graph;
    private T start;
    private T end;
    private LoopListener<T> loop;
    private PriorityQueue<T> queue;
    private Map<T, NodeInfo> info_set;


    public DijkstraAlgorithm(Graph<T> graph, T start, T end, LoopListener<T> loopListener) {
        this.graph = graph;
        this.start = start;
        this.end = end;
        this.loop = loopListener;
        info_set = new HashMap<>();
        this.queue = new PriorityQueue<>(1000, new Comparator<T>() {
            @Override
            public int compare(T lhs, T rhs) {
                float lVal = info_set.get(lhs).cost;
                float rVal = info_set.get(rhs).cost;
                if (lVal < rVal)
                    return -1;
                if (lVal > rVal)
                    return 1;
                return 0;
            }
        });

        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.open = true;
        info_set.put(start, nodeInfo);
    }

    public Answer start() {

        return execute_algorithm();
    }

    private Answer returnRoute() {
        Answer answer = new Answer();
        NodeInfo end_info = info_set.get(end);
        answer.cost = end_info.cost;
        T val = end;

        while (null != end_info.father) {
            answer.path.add(val);
            val = end_info.father;
            end_info = info_set.get(val);
        }
        return answer;
    }

    private Answer execute_algorithm() {
        queue.add(start);

        while (!queue.isEmpty()) {
            T current = queue.poll();
            loop.onLoop(current);
            NodeInfo infoCurrent = info_set.get(current);
            infoCurrent.closed = true;

            if (current.equals(end)) {
                break;
            }

            for (Graph<T>.Edge edge : graph.getEdges(current)) {
                T node = edge.getHead();
                NodeInfo infoEdge = info_set.get(node);
                if (null == infoEdge) infoEdge = new NodeInfo();

                if (infoEdge.closed) continue;

                float newCost = infoCurrent.cost + edge.getCost();

                if (!infoEdge.open) {
                    //edge has never been visited
                    infoEdge.open = true;
                    infoEdge.father = current;
                    infoEdge.cost = newCost;
                    info_set.put(node, infoEdge);
                    queue.add(node);
                } else if (infoCurrent.cost > newCost) {
                    //has been visited and cost is smaller than before
                    queue.remove(node);
                    infoEdge.father = current;
                    infoEdge.cost = newCost;
                    queue.add(node);
                }
            }
        }

        return returnRoute();
    }

    private class NodeInfo {
        boolean open;
        boolean closed;
        T father;
        float cost;

        public NodeInfo() {
            open = false;
            closed = false;
            father = null;
            cost = 0;
        }
    }

    public class Answer {
        float cost;
        List<T> path = new ArrayList<>();
    }

}
