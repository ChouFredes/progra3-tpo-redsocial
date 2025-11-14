// src/main/java/com/tpo/redsocial/algorithms/KCliqueBacktracking.java
package com.tpo.redsocial.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

// Búsqueda de k-clique mediante backtracking con poda simple y ordenado por grado.

public class KCliqueBacktracking {

    public static Optional<Set<String>> findKClique(Map<String,Set<String>> und, int k) {
        if (k <= 0) return Optional.of(Set.of());       // k=0 consideramos clique vacía
        if (und == null || und.isEmpty()) return Optional.empty();
        List<String> verts = new ArrayList<>(und.keySet());
        
        if (k > verts.size()) return Optional.empty(); // Si k es mayor que el número de vértices --> no hay solución

        // Ordenar por grado descendente para encontrar rapido la solucion
        verts.sort(Comparator.comparingInt((String v) -> und.getOrDefault(v, Set.of()).size()).reversed());

        // Si un nodo no tiene entrada, rellenar con vacío
        // Copia del mapa original para evitar modificarlo
        Map<String, Set<String>> graph = new HashMap<>();
        for (String v : verts) {
            graph.put(v, new HashSet<>(und.getOrDefault(v, Set.of())));
        }

        return bt(graph, verts, new LinkedHashSet<>(), 0, k);
    }

    private static Optional<Set<String>> bt(Map<String,Set<String>> g, List<String> v, Set<String> cur, int idx, int k) {
        if (cur.size() == k) return Optional.of(Set.copyOf(cur));
        int n = v.size();
        // Poda global --> si los vértices restantes no alcanzan, no hay solución
        if (cur.size() + (n - idx) < k) return Optional.empty();

        for (int i = idx; i < n; i++) {
            // Otra poda si tampoco llegamos con los restantes a k
            if (cur.size() + (n - i) < k) break;

            String cand = v.get(i);
            Set<String> candNeighbors = g.getOrDefault(cand, Set.of());

            if (!candNeighbors.containsAll(cur)) {
                continue; // cand no es compatible con la clique parcial actual
            }

            cur.add(cand);
            Optional<Set<String>> ok = bt(g, v, cur, i + 1, k);
            if (ok.isPresent()) return ok;
            cur.remove(cand);
        }
        return Optional.empty();
    }
}
