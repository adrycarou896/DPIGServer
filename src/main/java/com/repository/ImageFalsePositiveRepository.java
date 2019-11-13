package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.model.ImageFalsePositive;

public interface ImageFalsePositiveRepository extends CrudRepository<ImageFalsePositive, Long>{
	
	@Query(value="select c from ImageFalsePositive c where c.deviceId=?1")
	public List<ImageFalsePositive> findByDeviceId(String deviceId);
}
