// src/main/java/com/tpo/redsocial/algorithms/GreedyRecommendations.java
package com.tpo.redsocial.algorithms;

import java.util.*;

public class GreedyRecommendations {
    public static List<String> topKByMutuals(Map<String,Set<String>> und, String u, int k){
        if (!und.containsKey(u)) return List.of();
        Set<String> amigos=und.get(u);
        Map<String,Integer> score=new HashMap<>();
        for (String a: amigos){
            for (String cand: und.getOrDefault(a, Set.of())){
                if (cand.equals(u) || amigos.contains(cand)) continue;
                score.merge(cand, 1, Integer::sum);
            }
        }
        return score.entrySet().stream()
            .sorted((e1,e2)->Integer.compare(e2.getValue(), e1.getValue()))
            .limit(k).map(Map.Entry::getKey).toList();
    }
}
