package com.server.repository;
import java.sql.Date;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.server.model.Match;

public interface MatchRepository extends CrudRepository<Match, Long> {

}
