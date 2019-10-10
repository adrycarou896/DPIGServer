package com.util.patterns;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.IPCamera;
import com.model.Match;
import com.model.Person;
import com.model.alert.Alert;
import com.model.event.Event;
import com.repository.IPCameraRepository;
import com.repository.MatchRepository;
import com.repository.PersonRepository;
import com.services.InsertDataService;
import com.util.eventsserver.IEventsServer;

@Service
public class PatternsManager {
	
	@Autowired
	private IPCameraRepository ipCameraRepository;
	
	@Autowired
	private MatchRepository matchRepository;
	
	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private InsertDataService insertDataService;
	
	@Autowired
	private IEventsServer eventServer;
	
	private Map<String, List<Event>> lastEventPersons = new HashMap<String,List<Event>>();
	
	public void find(IPCamera ipCamera, long personId, Date fecha){
		if(ipCamera.getName().equals("F-CAM-VF-1")){
			System.out.println("SUU");
		}
		Match match = saveMatch(ipCamera, personId, fecha);
		searchPattern(match.getPerson());
	}
	
	private Match saveMatch(IPCamera ipCameraModel, long personId, Date date){
		
	    String personName = "person"+ personId;
	    System.out.println("PERSON NAME -> "+personName+", CAMERA NAME: ->"+ipCameraModel.getName());
		Person person = personRepository.findByName(personName);
		
		IPCamera ipCamera = ipCameraRepository.findByName(ipCameraModel.getName());
		Match match = new Match(ipCamera, person, date);
		
		Match macthFind = matchRepository.findByCameraPerson(ipCamera.getId(), person.getId());
		if(macthFind!=null) {
			matchRepository.updateMatch(date, ipCamera.getId(), person.getId());
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
					eventServer.sendData(person,event);	
				}
			}
			else{
				personEventsSaved = new ArrayList<Event>();
				personEventsSaved.add(event);
				
				this.lastEventPersons.put(person.getName(), personEventsSaved);
				
				eventServer.sendData(person,event);	
			}
			
			List<Alert> eventAlerts = insertDataService.getAlertByEvent(event);
			for (Alert alert : eventAlerts) {
				System.out.println("Alert->"+alert.getName());
			}
		}
		
		
	}
	
	
}
