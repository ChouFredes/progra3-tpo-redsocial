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

}
