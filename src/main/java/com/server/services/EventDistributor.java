package com.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.event.Event;
import com.event.EventComplex;
import com.event.EventSimple;
import com.server.model.Camera;
import com.server.repository.CameraRepository;

@Service
public class EventDistributor {
	
	@Autowired
	private CameraRepository cameraRepository;
	
	public Event getEvent(String reglaTotal) {
		String[] reglaTotalArray = reglaTotal.split(":");
		String regla = reglaTotalArray[0];
		String mensaje = reglaTotalArray[1];
		
		Event event = null;
		if(!regla.contains("->")) {//camera1:Ha entrado dentro de clase
			String cameraName = regla;
			Camera camera = cameraRepository.findByName(cameraName);
			event = new EventSimple(camera, mensaje);
		}
		else {
			String[] reglaArray = regla.split("->");
			String cameraName1 = reglaArray[0];
			String cameraName2 = reglaArray[1];
			
			Camera camera1 = cameraRepository.findByName(cameraName1);
			Camera camera2 = cameraRepository.findByName(cameraName2);
			
			event = new EventComplex(new EventSimple(camera1), new EventSimple(camera2), mensaje);
			
			for (int i = 2; i < reglaArray.length; i++) {
				String cameraName = reglaArray[i];
				Camera camera = cameraRepository.findByName(cameraName);
				Event eventSimple = new EventSimple(camera);
				event = new EventComplex(eventSimple, event, mensaje);
			}
		}
		
		return event;
		
	}
}
