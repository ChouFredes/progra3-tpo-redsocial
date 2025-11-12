// src/main/java/com/tpo/redsocial/algorithms/BranchAndBoundTsp.java
package com.tpo.redsocial.algorithms;
import java.util.*;

public class BranchAndBoundTsp {
    public static Result solve(String start, List<String> targets, FloydWarshall.Result fw){
        List<String> pts=new ArrayList<>(); pts.add(start); pts.addAll(targets);
        int n=pts.size(); boolean[] used=new boolean[n];
        used[0]=true;
        Path best=new Path(Double.POSITIVE_INFINITY, new ArrayList<>());
        dfs(0, used, new ArrayList<>(List.of(0)), 0.0, fw, pts, best);
        return new Result(best.cost, idxToNodes(best.idxPath, pts));
    }

    private static void dfs(int last, boolean[] used, List<Integer> path, double cost,
                            FloydWarshall.Result fw, List<String> pts, Path best){
        if (path.size()==pts.size()){
            if (cost<best.cost){ best.cost=cost; best.idxPath=new ArrayList<>(path); }
            return;
        }
        for (int i=1;i<pts.size();i++){
            if (used[i]) continue;
            double edge=fw.dist()[ fw.index().get(pts.get(last)) ][ fw.index().get(pts.get(i)) ];
            double lb=cost+edge;
            if (lb>=best.cost) continue; // Bound
            used[i]=true; path.add(i);
            dfs(i, used, path, lb, fw, pts, best);
            path.remove(path.size()-1); used[i]=false;
        }
    }

    private static List<String> idxToNodes(List<Integer> idx, List<String> pts){
        return idx.stream().map(pts::get).toList();
    }
    private static class Path { double cost; List<Integer> idxPath; Path(double c,List<Integer> p){cost=c;idxPath=p;} }
    public record Result(double cost, List<String> path){}
}
