package com.server.repository;

import org.springframework.data.repository.CrudRepository;

import com.server.model.Camera;

public interface CameraRepository extends CrudRepository<Camera, Long>{
	
	public Camera findByName(String name);
}
