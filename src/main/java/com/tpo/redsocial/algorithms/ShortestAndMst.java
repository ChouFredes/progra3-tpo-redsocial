// src/main/java/com/tpo/redsocial/algorithms/ShortestAndMst.java
package com.tpo.redsocial.algorithms;

import java.util.*;

public class ShortestAndMst {
    public static Map<String,Double> dijkstra(Map<String,List<Edge>> g, String s){
        Map<String,Double> dist=new HashMap<>();
        for (String v: g.keySet()) dist.put(v, Double.POSITIVE_INFINITY);
        if (!g.containsKey(s)) return dist;
        dist.put(s,0.0);
        PriorityQueue<Node> pq=new PriorityQueue<>(Comparator.comparingDouble(n->n.d));
        pq.add(new Node(s,0));
        while(!pq.isEmpty()){
            var cur=pq.poll();
            if (cur.d!=dist.get(cur.id)) continue;
            for (Edge e: g.get(cur.id)){
                double nd=cur.d+e.w();
                if (nd<dist.get(e.to())){
                    dist.put(e.to(), nd);
                    pq.add(new Node(e.to(), nd));
                }
            }
        }
        return dist;
    }

    // Prim (MST) sobre grafo no dirigido
    public static List<UEdge> prim(Map<String,List<Edge>> g, String start){
        List<UEdge> mst=new ArrayList<>();
        if (!g.containsKey(start)) return mst;
        Set<String> vis=new HashSet<>();
        PriorityQueue<UEdge> pq=new PriorityQueue<>(Comparator.comparingDouble(UEdge::w));
        vis.add(start);
        for (Edge e: g.get(start)) pq.add(new UEdge(start,e.to(),e.w()));
        while(!pq.isEmpty()){
            var e=pq.poll();
            boolean uIn=vis.contains(e.u()), vIn=vis.contains(e.v());
            if (uIn && vIn) continue;
            String nxt = uIn? e.v(): e.u();
            mst.add(e); vis.add(nxt);
            for (Edge ne: g.getOrDefault(nxt, List.of()))
                pq.add(new UEdge(nxt, ne.to(), ne.w()));
        }
        return mst;
    }

    // Kruskal (MST)
    public static List<UEdge> kruskal(Set<UEdge> edges, Set<String> nodes){
        DSU dsu=new DSU(nodes);
        List<UEdge> res=new ArrayList<>();
        edges.stream().sorted(Comparator.comparingDouble(UEdge::w)).forEach(e->{
            if (dsu.union(e.u(), e.v())) res.add(e);
        });
        return res;
    }

    public record Edge(String to, double w){}
    public record UEdge(String u, String v, double w){}
    private static class DSU {
    Map<String, String> p = new HashMap<>();
    Map<String, Integer> r = new HashMap<>();

    DSU(Set<String> nodes){
        for (String x : nodes) { p.put(x, x); r.put(x, 0); }
    }

    String find(String x){
        String px = p.get(x);
        if (px == null) { // por si llega un nodo que no estaba
            p.put(x, x); r.putIfAbsent(x, 0);
            return x;
        }
        if (px.equals(x)) return x;
        String root = find(px);
        p.put(x, root);      // path compression
        return root;
    }

    boolean union(String a, String b){
        a = find(a); b = find(b);
        if (a.equals(b)) return false;
        int ra = r.get(a), rb = r.get(b);
        if (ra < rb) { String t = a; a = b; b = t; }
        p.put(b, a);
        if (ra == rb) r.put(a, ra + 1);
        return true;
    }
}
    private record Node(String id,double d){}
}
