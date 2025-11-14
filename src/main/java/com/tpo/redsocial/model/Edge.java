package com.tpo.redsocial.model;

public record Edge(String to, double weight) {
    @Override
    public String toString() {
        return String.format("Edge{to='%s', weight=%.1f}", to, weight);
    }
}