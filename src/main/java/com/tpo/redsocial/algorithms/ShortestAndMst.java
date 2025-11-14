package com.tpo.redsocial.algorithms;

import com.tpo.redsocial.model.Edge;
import java.util.*;

public class ShortestAndMst {
    
    // Dijkstra's Algorithm - ahora usa la clase Edge común
    public static Map<String, Double> dijkstra(Map<String, List<Edge>> graph, String start) {
        Map<String, Double> distances = new HashMap<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(Edge::weight));
        
        // Inicializar distancias
        for (String node : graph.keySet()) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        pq.offer(new Edge(start, 0.0));
        
        while (!pq.isEmpty()) {
            Edge current = pq.poll();
            String u = current.to();
            double distU = current.weight();
            
            if (distU > distances.get(u)) continue;
            
            for (Edge edge : graph.getOrDefault(u, Collections.emptyList())) {
                String v = edge.to();
                double weight = edge.weight();
                double newDist = distU + weight;
                
                if (newDist < distances.get(v)) {
                    distances.put(v, newDist);
                    pq.offer(new Edge(v, newDist));
                }
            }
        }
        
        return distances;
    }
    
    // Prim's Algorithm - usa la clase Edge común
    public static List<UEdge> prim(Map<String, List<Edge>> graph, String start) {
        List<UEdge> mst = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<UEdge> pq = new PriorityQueue<>(Comparator.comparingDouble(UEdge::w));
        
        visited.add(start);
        for (Edge edge : graph.getOrDefault(start, Collections.emptyList())) {
            pq.offer(new UEdge(start, edge.to(), edge.weight()));
        }
        
        while (!pq.isEmpty() && visited.size() < graph.size()) {
            UEdge current = pq.poll();
            if (visited.contains(current.v())) continue;
            
            mst.add(current);
            visited.add(current.v());
            
            for (Edge edge : graph.getOrDefault(current.v(), Collections.emptyList())) {
                if (!visited.contains(edge.to())) {
                    pq.offer(new UEdge(current.v(), edge.to(), edge.weight()));
                }
            }
        }
        
        return mst;
    }
    
    // Kruskal's Algorithm - se mantiene igual
    public static List<UEdge> kruskal(Set<UEdge> edges, Set<String> nodes) {
        List<UEdge> mst = new ArrayList<>();
        UnionFind uf = new UnionFind(nodes);
        
        List<UEdge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort(Comparator.comparingDouble(UEdge::w));
        
        for (UEdge edge : sortedEdges) {
            if (uf.union(edge.u(), edge.v())) {
                mst.add(edge);
            }
        }
        
        return mst;
    }
    
    // Record para edges no dirigidos (solo para Prim y Kruskal)
    public record UEdge(String u, String v, double w) {
        @Override
        public String toString() {
            return String.format("UEdge{u='%s', v='%s', w=%.1f}", u, v, w);
        }
    }
    
    // Union-Find para Kruskal (se mantiene igual)
    static class UnionFind {
        private Map<String, String> parent = new HashMap<>();
        private Map<String, Integer> rank = new HashMap<>();
        
        public UnionFind(Set<String> nodes) {
            for (String node : nodes) {
                parent.put(node, node);
                rank.put(node, 0);
            }
        }
        
        public String find(String x) {
            if (!parent.get(x).equals(x)) {
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }
        
        public boolean union(String x, String y) {
            String rootX = find(x);
            String rootY = find(y);
            
            if (rootX.equals(rootY)) return false;
            
            if (rank.get(rootX) < rank.get(rootY)) {
                parent.put(rootX, rootY);
            } else if (rank.get(rootX) > rank.get(rootY)) {
                parent.put(rootY, rootX);
            } else {
                parent.put(rootY, rootX);
                rank.put(rootX, rank.get(rootX) + 1);
            }
            
            return true;
        }
    }
}