package edu.nav.hermes.math.algorithms;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


/**
 * Created by eduardo on 16/01/16.
 * A class that encapsulates dijkstra Algorithm
 */
public class DijkstraAlgorithm {

    private Graph graph;
    private Long start;
    private Long end;
    private LoopListener loop;
    private PriorityQueue<Long> queue;
    private Map<Long, NodeInfo> info_set;


    public DijkstraAlgorithm(Graph graph, Long start, Long end, LoopListener loopListener) {
        this.graph = graph;
        this.start = start;
        this.end = end;
        this.loop = loopListener;
        info_set = new HashMap<>();
        this.queue = new PriorityQueue<>(1000, new Comparator<Long>() {
            @Override
            public int compare(Long lhs, Long rhs) {
                double lVal = info_set.get(lhs).cost;
                double rVal = info_set.get(rhs).cost;
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
        Long val = end;

        while (null != end_info.father) {
            answer.path.add(graph.getData(val));
            val = end_info.father;
            end_info = info_set.get(val);
        }
        return answer;
    }

    private Answer execute_algorithm() {
        queue.add(start);

        while (!queue.isEmpty()) {
            Long current = queue.poll();
            loop.onLoop(graph.getNode(current));
            NodeInfo infoCurrent = info_set.get(current);
            infoCurrent.closed = true;

            if (current.equals(end)) {
                break;
            }

            for (Graph.Edge edge : graph.getEdges(current)) {
                Long node = edge.getHead();
                NodeInfo infoEdge = info_set.get(node);

                if (null == infoEdge) infoEdge = new NodeInfo();

                if (infoEdge.closed) continue;

                double newCost = infoCurrent.cost + graph.getCost(current, node);

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

    private static class NodeInfo {
        boolean open;
        boolean closed;
        Long father;
        double cost;

        public NodeInfo() {
            open = false;
            closed = false;
            father = null;
            cost = 0;
        }
    }
}
