package com.server.repository;

import org.springframework.data.repository.CrudRepository;

import com.server.model.Match;

public interface MatchRepository extends CrudRepository<Match, Long> {

}
