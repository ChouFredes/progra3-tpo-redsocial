package com.tpo.redsocial.service;

import com.tpo.redsocial.model.Edge;
import com.tpo.redsocial.repo.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphService {

    private final PersonRepository repo;
    
    public GraphService(PersonRepository repo) {
        this.repo = repo;
        System.out.println("✅ GraphService inicializado");
    }

    public Map<String, Set<String>> buildUndirected() {
        Map<String, Set<String>> graph = new HashMap<>();
        
        try {
            System.out.println("🔄 Construyendo grafo no dirigido desde Neo4j...");
            
            var edges = repo.findAllEdges();
            System.out.println("📊 Se encontraron " + edges.size() + " aristas en Neo4j");
            
            for (var edge : edges) {
                String source = edge.getSource();
                String target = edge.getTarget();
                
                if (source != null && target != null) {
                    graph.computeIfAbsent(source, k -> new HashSet<>()).add(target);
                    graph.computeIfAbsent(target, k -> new HashSet<>()).add(source);
                }
            }
            
            var allIds = repo.findAllIds();
            System.out.println("📋 Nodos en la base de datos: " + allIds.size());
            
            for (String id : allIds) {
                graph.computeIfAbsent(id, k -> new HashSet<>());
            }
            
            System.out.println("✅ Grafo no dirigido construido con " + graph.size() + " nodos");
            
        } catch (Exception e) {
            System.err.println("❌ Error al construir grafo desde Neo4j: " + e.getMessage());
            throw new RuntimeException("Error conectando con Neo4j: " + e.getMessage(), e);
        }
        
        return graph;
    }

    public Map<String, List<Edge>> buildWeightedAsOne() {
        Map<String, List<Edge>> graph = new HashMap<>();
        
        try {
            System.out.println("🔄 Construyendo grafo ponderado desde Neo4j...");
            
            var edges = repo.findAllEdges();
            System.out.println("📊 Procesando " + edges.size() + " aristas ponderadas");
            
            for (var edge : edges) {
                String source = edge.getSource();
                String target = edge.getTarget();
                
                if (source != null && target != null) {
                    graph.computeIfAbsent(source, k -> new ArrayList<>())
                         .add(new Edge(target, 1.0));
                    graph.computeIfAbsent(target, k -> new ArrayList<>())
                         .add(new Edge(source, 1.0));
                }
            }
            
            var allIds = repo.findAllIds();
            for (String id : allIds) {
                graph.computeIfAbsent(id, k -> new ArrayList<>());
            }
            
            System.out.println("✅ Grafo ponderado construido con " + graph.size() + " nodos");
            
        } catch (Exception e) {
            System.err.println("❌ Error al construir grafo ponderado desde Neo4j: " + e.getMessage());
            throw new RuntimeException("Error construyendo grafo ponderado: " + e.getMessage(), e);
        }
        
        return graph;
    }
}