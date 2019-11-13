package com.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.model.IPCamera;

public interface IPCameraRepository extends CrudRepository<IPCamera, Long>{
	
	public IPCamera findByName(String name);
	
	@Query(value="select c from IPCamera c where c.id=?1")
	public IPCamera findByIPCameraId(Long id);
}
