package com.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.event.Event;
import com.server.model.Camera;
import com.server.model.Match;
import com.server.model.Person;
import com.server.model.dto.CameraDTO;
import com.server.model.dto.MatchDTO;
import com.server.model.dto.PersonDTO;
import com.server.repository.CameraRepository;
import com.server.repository.MatchRepository;
import com.server.repository.PersonRepository;
import com.server.services.EventDistributor;

@RestController
@RequestMapping(path = "/sendFrame")//Rura en la que encontramos el servicio
public class RestService{
	
	@Autowired
	private MatchRepository matchRepository;
	
	@Autowired
	private CameraRepository cameraRepository;
	
	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private EventDistributor eventDistributor;
	
	@RequestMapping(method = RequestMethod.POST, path = "/insertMatch", //dirección del servicio
			consumes = "application/json", produces = "application/json")
	public @ResponseBody Match //Convierte Camara a JSON
	validateUser(@RequestBody MatchDTO matchDto) throws Exception {
		CameraDTO cameraDto = matchDto.getCamera();
		PersonDTO personDto = matchDto.getPerson();
		Date date = matchDto.getDate();
		
		Camera camera = cameraRepository.findByName(cameraDto.getName());
		Person person = personRepository.findByName(personDto.getName());
		Match match = new Match(camera, person, date);
		
		Match macthFind = matchRepository.findByCameraPerson(camera.getId(), person.getId());
		if(macthFind!=null) {
			matchRepository.updateMatch(date, camera.getId(), person.getId());
		}
		else {
			matchRepository.save(match);
		}		
		
		searchPattern(person);
		
		return match;
	}
	
	private void searchPattern(Person person) {
		String[] reglas = new String[] {"camera1:Está en la clase 1","camera1->camera0:Salió de la clase 1"};
		List<Match> personMatches = matchRepository.findByPerson(person.getId());

		Event eventSuccesed = null;
		for (int i = 0; i < reglas.length; i++) {
			String regla = reglas[i];
			Event event = this.eventDistributor.getEvent(regla);
			if(event.isSuccesed(personMatches)) {
				if(eventSuccesed!=null) {
					if(event.getPriority()>eventSuccesed.getPriority()) {
						eventSuccesed = event;
					}
				}
				else {
					eventSuccesed = event;
				}
				
			}
		}
		System.out.println(eventSuccesed.getMensaje());
	}
}
