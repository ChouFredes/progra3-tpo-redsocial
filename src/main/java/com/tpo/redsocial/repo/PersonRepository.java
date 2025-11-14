package com.tpo.redsocial.repo;  // ← Debe ser 'repo' no 'repository'

import com.tpo.redsocial.dto.EdgeDTO;
import com.tpo.redsocial.model.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, String> {
    
    @Query("MATCH (p:Person {id:$id})-[:FRIEND]-(f:Person) RETURN f")
    List<Person> findDirectFriends(String id);

    @Query("MATCH (p:Person) RETURN p.id")
    List<String> findAllIds();

    @Query("MATCH (p:Person)-[:FRIEND]-(f:Person) RETURN p.id as source, f.id as target")
    List<EdgeDTO> findAllEdges();
}