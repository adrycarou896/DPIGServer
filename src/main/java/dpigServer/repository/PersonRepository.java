package dpigServer.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import dpigServer.model.Person;

public interface PersonRepository extends CrudRepository<Person, Long> {
	
	@Query(value="select p from Person p where p.id=?1")
	Person findPersonById(Long id);
	
	Person findByName(String name);
}
