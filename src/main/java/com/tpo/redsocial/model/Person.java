package com.tpo.redsocial.model;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Node ("Person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {

    @Id
    private String id;

    private String name;

    @Relationship(type = "FRIEND", direction = Relationship.Direction.OUTGOING)
    private Set<Person> friends;
    
}
