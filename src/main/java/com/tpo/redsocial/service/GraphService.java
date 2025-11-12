// src/main/java/com/tpo/redsocial/service/GraphService.java
package com.tpo.redsocial.service;

import com.tpo.redsocial.repo.PersonRepository;
import com.tpo.redsocial.repo.EdgePair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GraphService {

    private final PersonRepository repo;

    @Transactional(readOnly = true)
    public Map<String, Set<String>> buildUndirected() {
        Map<String, Set<String>> g = new HashMap<>();
        for (EdgePair e : repo.fetchAllEdges()) {
            g.computeIfAbsent(e.getU(), k -> new HashSet<>()).add(e.getV());
            g.computeIfAbsent(e.getV(), k -> new HashSet<>()).add(e.getU());
        }
        // asegurar que existan todos los nodos como keys (aunque sin edges)
        for (String id : repo.findAllIds()) g.computeIfAbsent(id, k -> new HashSet<>());
        return g;
    }

    @Transactional(readOnly = true)
    public Map<String, List<Edge>> buildWeightedAsOne() {
        Map<String, List<Edge>> g = new HashMap<>();
        for (EdgePair e : repo.fetchAllEdges()) {
            g.computeIfAbsent(e.getU(), k -> new ArrayList<>()).add(new Edge(e.getV(), 1.0));
            g.computeIfAbsent(e.getV(), k -> new ArrayList<>()).add(new Edge(e.getU(), 1.0));
        }
        for (String id : repo.findAllIds()) g.computeIfAbsent(id, k -> new ArrayList<>());
        return g;
    }

    public record Edge(String to, double w) {}
}
