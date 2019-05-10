package com.server.repository;

import org.springframework.data.repository.CrudRepository;

import com.server.model.Person;

public interface PersonRepository extends CrudRepository<Person, Long> {
	
	Person findByName(String name);
}
