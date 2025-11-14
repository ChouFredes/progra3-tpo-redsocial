// src/main/java/com/tpo/redsocial/dto/EdgeDTO.java
package com.tpo.redsocial.dto;

public class EdgeDTO {
    private String source;
    private String target;
    
    // Constructor vacío REQUERIDO
    public EdgeDTO() {}
    
    // Getters y setters REQUERIDOS
    public String getSource() { return source; }
    public String getTarget() { return target; }
    public void setSource(String source) { this.source = source; }
    public void setTarget(String target) { this.target = target; }
}