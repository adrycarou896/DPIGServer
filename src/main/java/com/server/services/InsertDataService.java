package com.server.services;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.model.Camera;
import com.server.model.Person;
import com.server.repository.CameraRepository;
import com.server.repository.PersonRepository;

@Service
public class InsertDataService {
	
	private static final int NUM_CAMERAS = 2;
	private static final int NUM_PERSONS = 3;
	
	@Autowired
	private CameraRepository cameraRepository;
	
	@Autowired
	private PersonRepository personRepository;
	
	@PostConstruct
	public void init() {
		System.out.println("ENTROO");
		for (int i = 0; i < NUM_CAMERAS; i++) {
			Camera camera = new Camera("camera"+i,new ArrayList<>());
			cameraRepository.save(camera);
		}
		for (int i = 0; i < NUM_PERSONS; i++) {
			Person person = new Person("person"+i);
			personRepository.save(person);
		}
	}
}
