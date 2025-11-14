package com.tpo.redsocial.service;

import com.tpo.redsocial.dto.EdgeDTO;
import com.tpo.redsocial.model.Edge;
import com.tpo.redsocial.repo.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphService {

    private final PersonRepository repo;
    
    public GraphService(PersonRepository repo) {
        this.repo = repo;
    }

    public Map<String, Set<String>> buildUndirected() {
        Map<String, Set<String>> graph = new HashMap<>();
        
        try {
            List<EdgeDTO> edges = repo.findAllEdges();
            
            for (EdgeDTO edge : edges) {
                String source = edge.getSource();
                String target = edge.getTarget();
                
                graph.computeIfAbsent(source, k -> new HashSet<>()).add(target);
                graph.computeIfAbsent(target, k -> new HashSet<>()).add(source);
            }
            
            List<String> allIds = repo.findAllIds();
            for (String id : allIds) {
                graph.computeIfAbsent(id, k -> new HashSet<>());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error construyendo grafo: " + e.getMessage(), e);
        }
        
        return graph;
    }

    public Map<String, List<Edge>> buildWeightedAsOne() {
        Map<String, List<Edge>> graph = new HashMap<>();
        
        try {
            List<EdgeDTO> edges = repo.findAllEdges();
            
            for (EdgeDTO edge : edges) {
                String source = edge.getSource();
                String target = edge.getTarget();
                
                graph.computeIfAbsent(source, k -> new ArrayList<>())
                     .add(new Edge(target, 1.0));
                graph.computeIfAbsent(target, k -> new ArrayList<>())
                     .add(new Edge(source, 1.0));
            }
            
            List<String> allIds = repo.findAllIds();
            for (String id : allIds) {
                graph.computeIfAbsent(id, k -> new ArrayList<>());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error construyendo grafo ponderado: " + e.getMessage(), e);
        }
        
        return graph;
    }
}