// src/main/java/com/tpo/redsocial/api/GraphController.java
package com.tpo.redsocial.api;

import com.tpo.redsocial.service.GraphService;
import com.tpo.redsocial.algorithms.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/graph")
@RequiredArgsConstructor
public class GraphController {

    private final GraphService gs;

    // BFS / DFS
    @GetMapping("/bfs/{start}") public List<String> bfs(@PathVariable String start){
        return BfsDfs.bfs(gs.buildUndirected(), start);
    }
    @GetMapping("/dfs/{start}") public List<String> dfs(@PathVariable String start){
        return BfsDfs.dfs(gs.buildUndirected(), start);
    }

    // Dijkstra
    @GetMapping("/dijkstra/{start}")
    public Map<String,Double> dijkstra(@PathVariable String start){
        return ShortestAndMst.dijkstra(convertW(gs.buildWeightedAsOne()), start);
    }

    // Prim y Kruskal (MST)
    @GetMapping("/prim/{start}")
    public List<Map<String,Object>> prim(@PathVariable String start){
        var mst = ShortestAndMst.prim(convertW(gs.buildWeightedAsOne()), start);
        return mst.stream().map(e -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("u", e.u());
            m.put("v", e.v());
            m.put("w", e.w());
            return m;
        }).toList();

    }
    @GetMapping("/kruskal")
    public List<Map<String,Object>> kruskal(){
        var adj = gs.buildWeightedAsOne();
        Set<String> nodes = adj.keySet();
        Set<ShortestAndMst.UEdge> edges = new HashSet<>();
        adj.forEach((u, list)-> list.forEach(e -> {
            // evitar duplicados: solo agrego u < v lexicográficamente
            if (u.compareTo(e.to())<0) edges.add(new ShortestAndMst.UEdge(u, e.to(), e.w()));
        }));
        var mst = ShortestAndMst.kruskal(edges, nodes);
        return mst.stream().map(e -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("u", e.u());
            m.put("v", e.v());
            m.put("w", e.w());
            return m;
        }).toList();
    }

    // Greedy recomendaciones
    @GetMapping("/recommend/{userId}")
    public List<String> recommend(@PathVariable String userId, @RequestParam(defaultValue="5") int k){
        return GreedyRecommendations.topKByMutuals(gs.buildUndirected(), userId, k);
    }

    // Divide & vencerás (sorts de ejemplo)
    @PostMapping("/sort/quick")
    public double[] quick(@RequestBody double[] arr){
        var a=Arrays.copyOf(arr, arr.length);
        DivideAndConquerSorts.quickSort(a,0,a.length-1);
        return a;
    }
    @PostMapping("/sort/merge")
    public double[] merge(@RequestBody double[] arr){
        return DivideAndConquerSorts.mergeSort(arr);
    }

    // Programación dinámica (Floyd)
    @GetMapping("/fw")
    public Map<String,Object> fw(){
        var adj = gs.buildWeightedAsOne();
        var fw  = FloydWarshall.solve(adj.keySet(), convertToFloyd(adj));
        return Map.of("nodes", fw.nodes(), "dist", fw.dist());
    }

    // Backtracking (k-clique)
    @GetMapping("/clique")
    public Object clique(@RequestParam int k){
        return KCliqueBacktracking.findKClique(gs.buildUndirected(), k).orElseGet(Set::of);
    }

    // Branch & Bound (TSP)
    @GetMapping("/tsp")
    public Object tsp(@RequestParam String start, @RequestParam List<String> targets){
        var adj = gs.buildWeightedAsOne();
        var fw  = FloydWarshall.solve(adj.keySet(), convertToFloyd(adj));
        var res = BranchAndBoundTsp.solve(start, targets, fw);
        return Map.of("cost", res.cost(), "path", res.path());
    }

    private static Map<String,List<ShortestAndMst.Edge>> convertW(Map<String,List<com.tpo.redsocial.service.GraphService.Edge>> src){
        Map<String,List<ShortestAndMst.Edge>> out=new HashMap<>();
        src.forEach((k,v)-> out.put(k, v.stream().map(e->new ShortestAndMst.Edge(e.to(), e.w())).toList()));
        return out;
    }

    // En GraphController (o en un Utils), dejá AMBOS helpers:

    private static Map<String,List<ShortestAndMst.Edge>> convertToShortest(
            Map<String,List<com.tpo.redsocial.service.GraphService.Edge>> src){
        Map<String,List<ShortestAndMst.Edge>> out = new HashMap<>();
        src.forEach((k,v) -> out.put(k, v.stream()
            .map(e -> new ShortestAndMst.Edge(e.to(), e.w()))
            .toList()));
        return out;
    }

    private static Map<String,List<FloydWarshall.Edge>> convertToFloyd(
            Map<String,List<com.tpo.redsocial.service.GraphService.Edge>> src){
        Map<String,List<FloydWarshall.Edge>> out = new HashMap<>();
        src.forEach((k,v) -> out.put(k, v.stream()
            .map(e -> new FloydWarshall.Edge(e.to(), e.w()))
            .toList()));
        return out;
}

}
