// src/main/java/com/tpo/redsocial/algorithms/BfsDfs.java
package com.tpo.redsocial.algorithms;

import java.util.*;

public class BfsDfs {
    public static List<String> bfs(Map<String, Set<String>> graph, String start) {
        List<String> traversalOrder = new ArrayList<>();
        
        if (graph == null || start == null || !graph.containsKey(start)) {
            return traversalOrder;
        }
        
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        
        visited.add(start);
        queue.offer(start);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            traversalOrder.add(current);
            
            // Verificar que el nodo tenga vecinos
            Set<String> neighbors = graph.get(current);
            if (neighbors != null) {
                for (String neighbor : neighbors) {
                    if (neighbor != null && !visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.offer(neighbor);
                    }
                }
            }
        }
        
        return traversalOrder;
    }

    public static List<String> dfs(Map<String, Set<String>> g, String start){
        List<String> order=new ArrayList<>();
        Set<String> vis=new HashSet<>();
        dfsRec(g,start,vis,order);
        return order;
    }
    private static void dfsRec(Map<String, Set<String>> g, String u, Set<String> vis, List<String> out){
        if (!g.containsKey(u) || !vis.add(u)) return;
        out.add(u);
        for (String v: g.get(u)) dfsRec(g,v,vis,out);
    }
}
