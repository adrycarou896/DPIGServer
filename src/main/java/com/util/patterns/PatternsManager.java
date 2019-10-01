package com.util.patterns;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.Camera;
import com.model.Match;
import com.model.Person;
import com.model.alert.Alert;
import com.model.event.Event;
import com.repository.CameraRepository;
import com.repository.MatchRepository;
import com.repository.PersonRepository;
import com.services.InsertDataService;
import com.util.eventsserver.IEventsServer;

@Service
public class PatternsManager {
	
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
	
	public void find(long cameraId, long personId, Date fecha){
		
		Match match = saveMatch(cameraId, personId, fecha);
		searchPattern(match.getPerson());
	}
	
	private Match saveMatch(long cameraId, long personId, Date date){
		
		String cameraName = "camera" + (cameraId-1);
	    String personName = "person"+ (personId-1);
	    
	    Camera camera = cameraRepository.findByName(cameraName);
		Person person = personRepository.findByName(personName);

		Match match = new Match(camera, person, date);
		
		Match macthFind = matchRepository.findByCameraPerson(camera.getId(), person.getId());
		if(macthFind!=null) {
			matchRepository.updateMatch(date, camera.getId(), person.getId());
		}
		else {
			matchRepository.save(match);
		}		
		
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
			
			List<Alert> eventAlerts = insertDataService.getAlertByEvent(event);
			for (Alert alert : eventAlerts) {
				System.out.println("Alert->"+alert.getName());
			}
		}
		
		
	}
	
	
}
