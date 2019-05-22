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
import com.event.EventDistributor;
import com.server.model.Camera;
import com.server.model.Match;
import com.server.model.Person;
import com.server.model.dto.CameraDTO;
import com.server.model.dto.MatchDTO;
import com.server.model.dto.PersonDTO;
import com.server.repository.CameraRepository;
import com.server.repository.MatchRepository;
import com.server.repository.PersonRepository;

@RestController
@RequestMapping(path = "/sendFrame")//Rura en la que encontramos el servicio
public class RestService{
	
	@Autowired
	private MatchRepository matchRepository;
	
	@Autowired
	private CameraRepository cameraRepository;
	
	@Autowired
	private PersonRepository personRepository;
	
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
		String[] reglas = new String[] {"camera1:Está en la clase 1","camera1->camera0:Salió de clase"};
		EventDistributor eventDistributor = new EventDistributor();
		List<Event> events = new ArrayList<Event>();
		for (int i = 0; i < reglas.length; i++) {
			String regla = reglas[i];
			Event event = eventDistributor.getEvent(regla);
			events.add(event);
		}
		
		
		List<Match> personMatches = matchRepository.findByPerson(person.getId());
		Match ultimateMatch = personMatches.get(0);
		if(ultimateMatch.getCamera().getName().equals("camera1")) {
			System.out.println("Está en la clase 1");
		}
		else if(ultimateMatch.getCamera().getName().equals("camera0")) {
			Match penUltimateMatch = personMatches.get(0);
			if(penUltimateMatch!=null) {
				if(penUltimateMatch.getCamera().getName().equals("camera1")) {
					System.out.println("Salió de clase");
				}
			}
		}
		//C1 -> C2 -> C3 -> Un evento
	}
}
