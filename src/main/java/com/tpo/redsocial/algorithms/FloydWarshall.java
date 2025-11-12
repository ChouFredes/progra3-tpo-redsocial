// src/main/java/com/tpo/redsocial/algorithms/FloydWarshall.java
package com.tpo.redsocial.algorithms;
import java.util.*;

public class FloydWarshall {
    public static Result solve(Set<String> nodes, Map<String,List<Edge>> g){
        List<String> list=new ArrayList<>(nodes);
        int n=list.size(); double[][] d=new double[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) d[i][j]=(i==j)?0:Double.POSITIVE_INFINITY;
        Map<String,Integer> idx=new HashMap<>();
        for (int i=0;i<n;i++) idx.put(list.get(i), i);
        for (String u: nodes){
            int i=idx.get(u);
            for (Edge e: g.getOrDefault(u, List.of())){
                int j=idx.get(e.to()); d[i][j]=Math.min(d[i][j], e.w());
            }
        }
        for (int k=0;k<n;k++)
            for (int i=0;i<n;i++)
                for (int j=0;j<n;j++)
                    d[i][j]=Math.min(d[i][j], d[i][k]+d[k][j]);
        return new Result(list, idx, d);
    }
    public record Edge(String to,double w){}
    public record Result(List<String> nodes, Map<String,Integer> index, double[][] dist){}
}
