package com.server.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.server.model.Camera;

public interface CameraRepository extends CrudRepository<Camera, Long>{

}
