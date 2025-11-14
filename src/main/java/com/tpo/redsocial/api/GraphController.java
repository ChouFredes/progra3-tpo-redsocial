// src/main/java/com/tpo/redsocial/api/GraphController.java
package com.tpo.redsocial.api;

import com.tpo.redsocial.service.GraphService;
import com.tpo.redsocial.algorithms.*;
import com.tpo.redsocial.model.Edge;
import com.tpo.redsocial.repo.PersonRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/graph")
@RequiredArgsConstructor
public class GraphController {

    private final GraphService gs;;
    private final PersonRepository repo;

    @GetMapping("/debug-edges")
    public ResponseEntity<?> debugEdges() {
        Map<String, Object> debugInfo = new HashMap<>();
        
        try {
            debugInfo.put("status", "Testing with DTO approach");
            
            // Probar consulta de IDs (esto funciona)
            List<String> allIds = repo.findAllIds();
            debugInfo.put("totalNodesInDB", allIds.size());
            debugInfo.put("sampleNodeIds", allIds);
            
            // Probar GraphService completo
            Map<String, Set<String>> graph = gs.buildUndirected();
            debugInfo.put("graphTotalNodes", graph.size());
            
            // Mostrar el grafo completo para debugging
            Map<String, Object> graphDetails = new HashMap<>();
            for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
                graphDetails.put(entry.getKey(), entry.getValue());
            }
            debugInfo.put("fullGraph", graphDetails);
            
            debugInfo.put("overallStatus", "SUCCESS");
            debugInfo.put("message", "✅ DTO approach working!");
            
        } catch (Exception e) {
            System.err.println("Debug error: " + e.getMessage());
            debugInfo.put("error", e.toString());
            debugInfo.put("message", e.getMessage());
            debugInfo.put("solution", "Using EdgeResult DTO");
        }
        
        return ResponseEntity.ok(debugInfo);
    }

    // BFS / DFS
    @GetMapping("/bfs/{start}")
    public ResponseEntity<?> bfs(@PathVariable String start) {
        try {
            Map<String, Set<String>> graph = gs.buildUndirected();
            
            System.out.println("Ejecutando BFS desde nodo: " + start);
            System.out.println("Grafo disponible: " + graph.keySet());
            
            if (!graph.containsKey(start)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Nodo no encontrado");
                error.put("requested", start);
                error.put("availableNodes", graph.keySet());
                return ResponseEntity.badRequest().body(error);
            }
            
            List<String> result = BfsDfs.bfs(graph, start);
            
            Map<String, Object> response = new HashMap<>();
            response.put("start", start);
            response.put("traversal", result);
            response.put("totalNodesVisited", result.size());
            response.put("status", "success");
            response.put("message", "BFS ejecutado exitosamente");
            
            System.out.println("✅ BFS completado: " + result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en BFS: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al ejecutar BFS");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/dfs/{start}") public List<String> dfs(@PathVariable String start){
        return BfsDfs.dfs(gs.buildUndirected(), start);
    }

    @GetMapping("/dijkstra/{start}")
    public ResponseEntity<?> dijkstra(@PathVariable String start) {
        try {
            System.out.println("🎯 Iniciando Dijkstra desde: " + start);
            
            Map<String, List<Edge>> weightedGraph = gs.buildWeightedAsOne();
            System.out.println("📊 Grafo ponderado cargado con " + weightedGraph.size() + " nodos");
            
            if (!weightedGraph.containsKey(start)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Nodo no encontrado",
                    "requested", start,
                    "availableNodes", weightedGraph.keySet()
                ));
            }
            
            // Ejecutar Dijkstra
            Map<String, Double> result = ShortestAndMst.dijkstra(weightedGraph, start);
            System.out.println("✅ Dijkstra completado para nodo: " + start);
            
            return ResponseEntity.ok(Map.of(
                "start", start,
                "shortestPaths", result,
                "status", "success"
            ));
            
        } catch (Exception e) {
            System.err.println("❌ Error en Dijkstra: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error en Dijkstra",
                "message", e.getMessage(),
                "solution", "Verificar el algoritmo y los datos del grafo"
            ));
        }
    }

    @GetMapping("/prim/{start}")
    public ResponseEntity<?> prim(@PathVariable String start) {
        try {
            System.out.println("🎯 Iniciando Prim desde: " + start);
            
            Map<String, List<Edge>> weightedGraph = gs.buildWeightedAsOne();
            
            if (!weightedGraph.containsKey(start)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Nodo no encontrado",
                    "requested", start,
                    "availableNodes", weightedGraph.keySet()
                ));
            }
            
            List<ShortestAndMst.UEdge> mst = ShortestAndMst.prim(weightedGraph, start);
            
            List<Map<String, Object>> result = mst.stream().map(edge -> {
                Map<String, Object> edgeMap = new LinkedHashMap<>();
                edgeMap.put("from", edge.u());
                edgeMap.put("to", edge.v());
                edgeMap.put("weight", edge.w());
                return edgeMap;
            }).collect(Collectors.toList());
            
            double totalWeight = mst.stream().mapToDouble(ShortestAndMst.UEdge::w).sum();
            
            System.out.println("✅ Prim completado. MST con " + mst.size() + " aristas, peso total: " + totalWeight);
            
            return ResponseEntity.ok(Map.of(
                "start", start,
                "mstEdges", result,
                "totalWeight", totalWeight,
                "totalEdges", mst.size(),
                "status", "success"
            ));
            
        } catch (Exception e) {
            System.err.println("❌ Error en Prim: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error en Prim",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/kruskal")
    public ResponseEntity<?> kruskal() {
        try {
            System.out.println("🎯 Iniciando Kruskal...");
            
            Map<String, List<Edge>> adj = gs.buildWeightedAsOne();
            Set<String> nodes = adj.keySet();
            Set<ShortestAndMst.UEdge> edges = new HashSet<>();
            
            System.out.println("📊 Procesando " + nodes.size() + " nodos para Kruskal");
            
            // Construir conjunto de edges no dirigidos
            adj.forEach((u, edgeList) -> {
                for (Edge edge : edgeList) {
                    if (u.compareTo(edge.to()) < 0) {
                        edges.add(new ShortestAndMst.UEdge(u, edge.to(), edge.weight()));
                    }
                }
            });
            
            System.out.println("📊 Total de aristas únicas: " + edges.size());
            
            List<ShortestAndMst.UEdge> mst = ShortestAndMst.kruskal(edges, nodes);
            
            List<Map<String, Object>> result = mst.stream().map(edge -> {
                Map<String, Object> edgeMap = new LinkedHashMap<>();
                edgeMap.put("from", edge.u());
                edgeMap.put("to", edge.v());
                edgeMap.put("weight", edge.w());
                return edgeMap;
            }).collect(Collectors.toList());
            
            double totalWeight = mst.stream().mapToDouble(ShortestAndMst.UEdge::w).sum();
            
            System.out.println("✅ Kruskal completado. MST con " + mst.size() + " aristas");
            
            return ResponseEntity.ok(Map.of(
                "mstEdges", result,
                "totalWeight", totalWeight,
                "totalEdges", mst.size(),
                "totalNodes", nodes.size(),
                "status", "success"
            ));
            
        } catch (Exception e) {
            System.err.println("❌ Error en Kruskal: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error en Kruskal",
                "message", e.getMessage()
            ));
        }
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
/*
    // Programación dinámica (Floyd)
    @GetMapping("/fw")
    public Map<String,Object> fw(){
        var adj = gs.buildWeightedAsOne();
        var fw  = FloydWarshall.solve(adj.keySet(), convertToFloyd(adj));
        return Map.of("nodes", fw.nodes(), "dist", fw.dist());
    } */

    // Backtracking (k-clique)
    @GetMapping("/clique")
    public Object clique(@RequestParam int k){
        return KCliqueBacktracking.findKClique(gs.buildUndirected(), k).orElseGet(Set::of);
    }

    /*
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
    }*/
/*
    private static Map<String,List<FloydWarshall.Edge>> convertToFloyd(
            Map<String,List<com.tpo.redsocial.service.GraphService.Edge>> src){
        Map<String,List<FloydWarshall.Edge>> out = new HashMap<>();
        src.forEach((k,v) -> out.put(k, v.stream()
            .map(e -> new FloydWarshall.Edge(e.to(), e.w()))
            .toList()));
        return out;
}
 */
}
