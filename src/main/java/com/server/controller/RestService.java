package com.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.event.Event;
import com.server.eventsserver.IEventsServer;
import com.server.model.Camera;
import com.server.model.Match;
import com.server.model.Person;
import com.server.model.dto.CameraDTO;
import com.server.model.dto.MatchDTO;
import com.server.model.dto.PersonDTO;
import com.server.repository.CameraRepository;
import com.server.repository.MatchRepository;
import com.server.repository.PersonRepository;
import com.server.services.InsertDataService;

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
	private InsertDataService insertDataService;
	
	@Autowired
	private IEventsServer eventServer;
	
	private Map<String, List<Event>> lastEventPersons = new HashMap<String,List<Event>>();
	
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
		List<Match> personMatches = matchRepository.findByPerson(person.getId());
		
		List<Event> eventsSuccesed = new ArrayList<Event>();
		Event firstEvent = null;
		for (Event event : insertDataService.getEvents()) {
			if(event!=null) {
				if(event.isSuccesed(personMatches)) {
					if (firstEvent == null || event.getDate().equals(firstEvent.getDate())) {
						firstEvent = event;
						eventsSuccesed.add(event);
					}
					else if(event.getDate().after(firstEvent.getDate())) {
						firstEvent = event;
						eventsSuccesed.clear();
						eventsSuccesed.add(event);
					}
				}
			}
		}
		
		for (Event event : eventsSuccesed) {
			System.out.println(person.getName()+" -> "+event);
			List<Event> personEventsSaved = this.lastEventPersons.get(person.getName());
			if(personEventsSaved!=null){
				if(!personEventsSaved.contains(event)){
					if(event.getDate().before(personEventsSaved.get(0).getDate())){
						personEventsSaved.clear();
					}
					personEventsSaved.add(event);
					eventServer.saveData(person,event);	
				}
			}
			else{
				personEventsSaved = new ArrayList<Event>();
				personEventsSaved.add(event);
				
				this.lastEventPersons.put(person.getName(), personEventsSaved);
				eventServer.saveData(person,event);	
			}
		}
		
	}
	
	//if(event.getPriority()>eventSuccesed.getPriority()) {
	//eventSuccesed = event;
	//}
}
