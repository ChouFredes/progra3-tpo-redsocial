// src/main/java/com/tpo/redsocial/api/GraphController.java
package com.tpo.redsocial.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpo.redsocial.algorithms.Backtracking;
import com.tpo.redsocial.algorithms.BfsDfs;
import com.tpo.redsocial.algorithms.DivideAndConquerSorts;
import com.tpo.redsocial.algorithms.GreedyRecommendations;
import com.tpo.redsocial.algorithms.ShortestAndMst;
import com.tpo.redsocial.model.Edge;
import com.tpo.redsocial.repo.PersonRepository;
import com.tpo.redsocial.service.GraphService;
import com.tpo.redsocial.algorithms.FloydWarshall;
import com.tpo.redsocial.algorithms.BranchAndBoundTsp;

import lombok.RequiredArgsConstructor;

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

    @GetMapping("/dfs/{start}")
    public List<String> dfs(@PathVariable String start) {
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
                        "availableNodes", weightedGraph.keySet()));
            }

            // Ejecutar Dijkstra
            Map<String, Double> result = ShortestAndMst.dijkstra(weightedGraph, start);
            System.out.println("✅ Dijkstra completado para nodo: " + start);

            return ResponseEntity.ok(Map.of(
                    "start", start,
                    "shortestPaths", result,
                    "status", "success"));

        } catch (Exception e) {
            System.err.println("❌ Error en Dijkstra: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error en Dijkstra",
                    "message", e.getMessage(),
                    "solution", "Verificar el algoritmo y los datos del grafo"));
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
                        "availableNodes", weightedGraph.keySet()));
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
                    "status", "success"));

        } catch (Exception e) {
            System.err.println("❌ Error en Prim: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error en Prim",
                    "message", e.getMessage()));
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
                    "status", "success"));

        } catch (Exception e) {
            System.err.println("❌ Error en Kruskal: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error en Kruskal",
                    "message", e.getMessage()));
        }
    }

    // Greedy recomendaciones
    @GetMapping("/recommend-greedy/{userId}")
    public ResponseEntity<?> recommend(
            @PathVariable String userId,
            @RequestParam(defaultValue = "5") int k) {

        try {
            System.out.println("🎯 Solicitando recomendaciones para usuario: " + userId + ", k=" + k);

            Map<String, Set<String>> graph = gs.buildUndirected();

            if (!graph.containsKey(userId)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Usuario no encontrado",
                        "requestedUserId", userId,
                        "availableUsers", graph.keySet(),
                        "usingNeo4j", true));
            }

            List<String> recommendations = GreedyRecommendations.topKByMutuals(graph, userId, k);

            // Información adicional para debugging
            Set<String> userFriends = graph.get(userId);
            int totalPossibleRecommendations = graph.keySet().stream()
                    .filter(node -> !node.equals(userId) && !userFriends.contains(node))
                    .collect(Collectors.toSet())
                    .size();

            return ResponseEntity.ok(Map.of(
                    "userId", userId,
                    "recommendations", recommendations,
                    "totalRecommended", recommendations.size(),
                    "requestedK", k,
                    "userFriendsCount", userFriends.size(),
                    "totalPossibleRecommendations", totalPossibleRecommendations,
                    "usingNeo4j", true,
                    "status", "success"));

        } catch (Exception e) {
            System.err.println("❌ Error en recomendaciones: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error en recomendaciones",
                    "message", e.getMessage()));
        }
    }

    // Divide & vencerás (sorts de ejemplo)
    @PostMapping("/sort/quick")
    public double[] quick(@RequestBody double[] arr) {
        double[] copy = Arrays.copyOf(arr, arr.length);
        DivideAndConquerSorts.quickSort(copy);
        return copy;
    }

    @PostMapping("/sort/merge")
    public double[] merge(@RequestBody double[] arr) {
        return DivideAndConquerSorts.mergeSort(arr);
    }

    @GetMapping("/sort/degrees/quick")
    public ResponseEntity<?> sortDegreesQuick() {
        Map<String, Set<String>> graph = gs.buildUndirected();

        double[] degrees = graph.values().stream()
                .mapToDouble(Set::size)
                .toArray();

        double[] sorted = Arrays.copyOf(degrees, degrees.length);
        DivideAndConquerSorts.quickSort(sorted);

        return ResponseEntity.ok(Map.of(
                "description",
                "Distribución de grados (cantidad de amigos por usuario) ordenada con QuickSort (Divide & Conquer)",
                "totalNodes", sorted.length,
                "originalDegrees", degrees,
                "sortedDegrees", sorted));
    }

    @GetMapping("/sort/degrees/merge")
    public ResponseEntity<?> sortDegreesMerge() {
        Map<String, Set<String>> graph = gs.buildUndirected();

        double[] degrees = graph.values().stream()
                .mapToDouble(Set::size)
                .toArray();

        double[] sorted = DivideAndConquerSorts.mergeSort(degrees);

        return ResponseEntity.ok(Map.of(
                "description",
                "Distribución de grados (cantidad de amigos por usuario) ordenada con MergeSort (Divide & Conquer)",
                "totalNodes", sorted.length,
                "originalDegrees", degrees,
                "sortedDegrees", sorted));
    }

    @GetMapping("/clique")
    public Object clique(@RequestParam int k) {
        return Backtracking.findKClique(gs.buildUndirected(), k).orElseGet(Set::of);
    }

    private static Map<String, List<FloydWarshall.Edge>> convertToFloyd(
            Map<String, List<Edge>> src) {

        Map<String, List<FloydWarshall.Edge>> out = new HashMap<>();

        src.forEach((u, edges) -> {
            out.put(
                    u,
                    edges.stream()
                            .map(e -> new FloydWarshall.Edge(e.to(), e.weight()))
                            .toList());
        });

        return out;
    }

    @GetMapping("/fw")
    public Map<String,Object> fw(){
        var adj = gs.buildWeightedAsOne();
        var fw  = FloydWarshall.solve(adj.keySet(), convertToFloyd(adj));
        return Map.of("nodes", fw.nodes(), "dist", fw.dist());
    }

    @GetMapping("/tsp")
    public ResponseEntity<?> tsp(
            @RequestParam String start,
            @RequestParam List<String> targets) {

        try {
            System.out.println("🎯 Iniciando TSP. start=" + start + " targets=" + targets);

            Map<String, List<Edge>> adj = gs.buildWeightedAsOne();

            if (!adj.containsKey(start)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Nodo inicial no encontrado",
                        "requestedStart", start,
                        "availableNodes", adj.keySet()));
            }

            for (String t : targets) {
                if (!adj.containsKey(t)) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Nodo objetivo no encontrado",
                            "missingTarget", t,
                            "allTargets", targets,
                            "availableNodes", adj.keySet()));
                }
            }

            FloydWarshall.Result fw = FloydWarshall.solve(adj.keySet(), convertToFloyd(adj));

            BranchAndBoundTsp.Result res = BranchAndBoundTsp.solve(start, targets, fw);

            System.out.println("✅ TSP completado. Costo=" + res.cost() + " path=" + res.path());

            return ResponseEntity.ok(Map.of(
                    "start", start,
                    "targets", targets,
                    "cost", res.cost(),
                    "path", res.path(),
                    "status", "success"));

        } catch (Exception e) {
            System.err.println("❌ Error en TSP: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error al ejecutar TSP",
                    "message", e.getMessage()));
        }
    }

}
