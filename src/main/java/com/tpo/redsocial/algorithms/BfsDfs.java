// src/main/java/com/tpo/redsocial/algorithms/BfsDfs.java
package com.tpo.redsocial.algorithms;

import java.util.*;

public class BfsDfs {
    public static List<String> bfs(Map<String, Set<String>> g, String start){
        List<String> order=new ArrayList<>();
        if (!g.containsKey(start)) return order;
        Set<String> vis=new HashSet<>();
        Deque<String> q=new ArrayDeque<>();
        vis.add(start); q.add(start);
        while(!q.isEmpty()){
            String u=q.poll(); order.add(u);
            for (String v: g.get(u))
                if (vis.add(v)) q.add(v);
        }
        return order;
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
