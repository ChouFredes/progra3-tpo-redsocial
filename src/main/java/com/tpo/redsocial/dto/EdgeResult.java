package com.tpo.redsocial.dto;

public class EdgeResult {
    private String source;
    private String target;
    
    // Constructor vacío necesario para Spring Data
    public EdgeResult() {}
    
    public EdgeResult(String source, String target) {
        this.source = source;
        this.target = target;
    }
    
    // Getters y setters
    public String getSource() { return source; }
    public String getTarget() { return target; }
    public void setSource(String source) { this.source = source; }
    public void setTarget(String target) { this.target = target; }
    
    @Override
    public String toString() {
        return "EdgeResult{source='" + source + "', target='" + target + "'}";
    }
}
