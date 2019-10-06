package com.repository;

import org.springframework.data.repository.CrudRepository;

import com.model.IPCamera;

public interface IPCameraRepository extends CrudRepository<IPCamera, Long>{
	
	public IPCamera findByName(String name);
}
