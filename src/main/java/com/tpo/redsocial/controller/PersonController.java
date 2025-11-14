package com.tpo.redsocial.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpo.redsocial.model.Person;
import com.tpo.redsocial.repo.PersonRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
public class PersonController {

    private final PersonRepository repo;

    @GetMapping("/{id}/friends")
    public List<Person> getFriends(@PathVariable String id){
        return repo.findDirectFriends(id);
    }
}
