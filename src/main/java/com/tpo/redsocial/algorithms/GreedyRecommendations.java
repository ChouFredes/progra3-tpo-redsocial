package com.tpo.redsocial.algorithms;

import java.util.*;
import java.util.stream.Collectors;

public class GreedyRecommendations {
    
    /**
     * Recomendaciones greedy basadas en amigos en común
     * @param und Grafo no dirigido de amistades
     * @param u Usuario para el que se hacen recomendaciones
     * @param k Número de recomendaciones a retornar
     * @return Lista de k usuarios recomendados
     */
    public static List<String> topKByMutuals(Map<String, Set<String>> und, String u, int k) {
        System.out.println("🔍 Calculando recomendaciones para: " + u + ", k=" + k);
        
        if (!und.containsKey(u)) {
            System.out.println("❌ Usuario no encontrado en el grafo: " + u);
            return List.of();
        }
        
        Set<String> amigos = und.get(u);
        System.out.println("👥 Amigos de " + u + ": " + amigos.size() + " -> " + amigos);
        
        Map<String, Integer> score = new HashMap<>();
        
        // Para cada amigo del usuario
        for (String amigo : amigos) {
            Set<String> amigosDelAmigo = und.getOrDefault(amigo, Set.of());
            
            // Para cada posible candidato (amigo de mi amigo)
            for (String candidato : amigosDelAmigo) {
                // Saltar si es el mismo usuario o ya son amigos
                if (candidato.equals(u) || amigos.contains(candidato)) {
                    continue;
                }
                
                // Incrementar score por amigo en común
                score.merge(candidato, 1, Integer::sum);
                System.out.println("   📊 Candidato: " + candidato + " +1 (amigo en común: " + amigo + ")");
            }
        }
        
        System.out.println("📈 Scores calculados: " + score.size() + " candidatos");
        
        // Ordenar por score descendente y tomar los top k
        List<String> recomendaciones = score.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .limit(k)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        System.out.println("✅ Recomendaciones finales: " + recomendaciones);
        
        return recomendaciones;
    }
    
    /**
     * Versión alternativa que incluye el score en la respuesta
     */
    public static Map<String, Object> topKByMutualsWithScores(Map<String, Set<String>> und, String u, int k) {
        if (!und.containsKey(u)) {
            return Map.of(
                "recommendations", Collections.emptyList(),
                "scores", Collections.emptyMap(),
                "totalCandidates", 0
            );
        }
        
        Set<String> amigos = und.get(u);
        Map<String, Integer> score = new HashMap<>();
        
        for (String amigo : amigos) {
            for (String candidato : und.getOrDefault(amigo, Set.of())) {
                if (candidato.equals(u) || amigos.contains(candidato)) continue;
                score.merge(candidato, 1, Integer::sum);
            }
        }
        
        // Usar List<Object> en lugar de List<Map<String, Object>> para evitar problemas de genéricos
        List<Object> scoredRecommendations = score.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .limit(k)
            .map(entry -> {
                Map<String, Object> item = new HashMap<>();
                item.put("userId", entry.getKey());
                item.put("commonFriends", entry.getValue());
                return item;
            })
            .collect(Collectors.toList());
        
        return Map.of(
            "recommendations", scoredRecommendations,
            "totalCandidates", score.size()
        );
    }
}