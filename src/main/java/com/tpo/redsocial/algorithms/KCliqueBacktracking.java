// src/main/java/com/tpo/redsocial/algorithms/KCliqueBacktracking.java
package com.tpo.redsocial.algorithms;
import java.util.*;

public class KCliqueBacktracking {
    public static Optional<Set<String>> findKClique(Map<String,Set<String>> und, int k){
        List<String> verts=new ArrayList<>(und.keySet());
        return bt(und, verts, new LinkedHashSet<>(), 0, k);
    }
    private static Optional<Set<String>> bt(Map<String,Set<String>> g, List<String> v, Set<String> cur, int idx, int k){
        if (cur.size()==k) return Optional.of(Set.copyOf(cur));
        if (idx==v.size()) return Optional.empty();
        for (int i=idx;i<v.size();i++){
            String cand=v.get(i);
            if (cur.stream().allMatch(x -> g.getOrDefault(x, Set.of()).contains(cand))) {
                cur.add(cand);
                var ok=bt(g,v,cur,i+1,k);
                if (ok.isPresent()) return ok;
                cur.remove(cand);
            }
        }
        return Optional.empty();
    }
}
