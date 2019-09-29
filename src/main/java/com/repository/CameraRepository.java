package com.repository;

import org.springframework.data.repository.CrudRepository;

import com.model.Camera;

public interface CameraRepository extends CrudRepository<Camera, Long>{
	
	public Camera findByName(String name);
}
