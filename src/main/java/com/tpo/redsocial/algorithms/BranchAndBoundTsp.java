// src/main/java/com/tpo/redsocial/algorithms/BranchAndBoundTsp.java
package com.tpo.redsocial.algorithms;
import java.util.*;

public class BranchAndBoundTsp {

    public static Result solve(String start, List<String> targets, FloydWarshall.Result fw) {
        List<String> pts = new ArrayList<>();
        pts.add(start);
        pts.addAll(targets);

        Map<String, Integer> index = buildIndexMap(fw.nodes());
        Path best = new Path(Double.POSITIVE_INFINITY, new ArrayList<>());

        int n = pts.size();
        boolean[] used = new boolean[n];
        used[0] = true; 

        List<Integer> initialPath = new ArrayList<>();
        initialPath.add(0); 

        dfs(
            0,                      
            used,
            initialPath,
            0.0,                    
            fw.dist(),              
            index,                  
            pts,                    
            best
        );

        return new Result(best.cost, idxToNodes(best.idxPath, pts));
    }

    private static void dfs(
            int last,
            boolean[] used,
            List<Integer> path,
            double cost,
            double[][] dist,
            Map<String, Integer> index,
            List<String> pts,
            Path best
    ) {
        if (path.size() == pts.size()) {
            if (cost < best.cost) {
                best.cost = cost;
                best.idxPath = new ArrayList<>(path);
            }
            return;
        }

        for (int i = 1; i < pts.size(); i++) {
            if (used[i]) continue;

            String fromId = pts.get(last);
            String toId   = pts.get(i);

            Integer fromIdx = index.get(fromId);
            Integer toIdx   = index.get(toId);

            if (fromIdx == null || toIdx == null) continue;

            double edge = dist[fromIdx][toIdx];
            double lb   = cost + edge;  // lower bound

            if (lb >= best.cost) continue;

            used[i] = true;
            path.add(i);
            dfs(i, used, path, lb, dist, index, pts, best);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }

    private static Map<String, Integer> buildIndexMap(List<String> nodes) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            index.put(nodes.get(i), i);
        }
        return index;
    }

    private static List<String> idxToNodes(List<Integer> idx, List<String> pts) {
        return idx.stream()
                  .map(pts::get)
                  .toList();
    }

    private static class Path {
        double cost;
        List<Integer> idxPath;
        Path(double c, List<Integer> p) { cost = c; idxPath = p; }
    }

    public record Result(double cost, List<String> path) {}
}
