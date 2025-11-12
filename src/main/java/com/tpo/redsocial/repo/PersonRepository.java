package com.tpo.redsocial.repo;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import com.tpo.redsocial.domain.Person;

public interface PersonRepository extends Neo4jRepository<Person, String> {
    
     @Query("""
        MATCH (p:Person {id:$id})-[:FRIEND]-(f:Person)
        RETURN DISTINCT f
    """)
    List<Person> findDirectFriends(String id);

    // NUEVO: todas las aristas (tratamos FRIEND como no dirigido)
    @Query("""
        MATCH (p:Person)-[:FRIEND]-(f:Person)
        RETURN DISTINCT p.id AS u, f.id AS v
    """)
    List<EdgePair> fetchAllEdges();

    // NUEVO: todos los ids de nodos (útil para DP/Floyd)
    @Query("MATCH (p:Person) RETURN p.id")
    List<String> findAllIds();

}
