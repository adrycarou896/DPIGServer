package dpigServer.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import dpigServer.model.Match;

public interface MatchRepository extends CrudRepository<Match, Long> {
	
	@Query(value="select m from Match m where m.ipCamera.id=?1 AND m.person.id=?2")
	Match findByCameraPerson(Long cameraId, Long personId);
	
	@Transactional
	@Modifying
	@Query(value="update Match m set m.date=?1 where m.ipCamera.id=?2 and m.person.id=?3")
	void updateMatch(Date date,Long cameraId, Long personId);
	
	@Query(value="select m from Match m where m.person.id=?1 order by m.date desc")
	List<Match> findByPerson(Long personId);
}
