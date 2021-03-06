package dpigServer.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dpigServer.model.IPCamera;
import dpigServer.model.Match;
import dpigServer.model.Person;
import dpigServer.model.rule.Rule;
import dpigServer.model.rule.alert.Alert;
import dpigServer.model.rule.event.Event;
import dpigServer.repository.IPCameraRepository;
import dpigServer.repository.MatchRepository;
import dpigServer.repository.PersonRepository;
import dpigServer.socket.ISocketServer;

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
	private ISocketServer eventServer;
	
	private Map<String, List<Event>> lastEventPersons = new HashMap<String,List<Event>>();
	
	public Match saveMatch(IPCamera ipCameraModel, long personId, Date date){
		
		Match match = getMatch(ipCameraModel, personId, date);
		
		Match macthFind = matchRepository.findByCameraPerson(match.getIpCamera().getId(), match.getPerson().getId());
		if(macthFind!=null) {
			matchRepository.updateMatch(date, match.getIpCamera().getId(), match.getPerson().getId());
		}
		else {
			matchRepository.save(match);
		}		
		
		return match;
	}
	
	private Match getMatch(IPCamera ipCameraModel, Long personId, Date date){
		Person person = personRepository.findPersonById(personId);
		IPCamera ipCamera = ipCameraRepository.findByIPCameraId(ipCameraModel.getId());
		Match match = new Match(ipCamera, person, date);
		return match;
	}
	
	public List<Rule> findPattern(Person person) {
		List<Event> accomplishedEvents = getAccomplishedEvents(person);
		return getAccomplishedRules(accomplishedEvents, person);
	}
	
	public List<Event> getAccomplishedEvents(Person person){
		List<Match> personMatches = matchRepository.findByPerson(person.getId());
		
		List<Event> accomplishdEvents = new ArrayList<Event>();
		Event firstEvent = null;
		for (Event event : insertDataService.getEvents()) {
			if(event!=null) {
				if(event.isAccomplished(personMatches)) {
					if (firstEvent == null || event.getAccomplishedDate().equals(firstEvent.getAccomplishedDate())) {
						firstEvent = event;
						accomplishdEvents.add(event);
					}
					else if(event.getAccomplishedDate().after(firstEvent.getAccomplishedDate())) {
						firstEvent = event;
						accomplishdEvents.clear();
						accomplishdEvents.add(event);
					}
				}
			}
		}
		return accomplishdEvents;
	}
	
	public List<Rule> getAccomplishedRules(List<Event> accomplishedEvents, Person person){
		List<Rule> rulesAccomplished = new ArrayList<Rule>();
		for (Event event : accomplishedEvents) {
			List<Event> personEventsSaved = this.lastEventPersons.get(person.getName());
			if(personEventsSaved!=null){
				if(!personEventsSaved.contains(event)){
					personEventsSaved.add(event);
					rulesAccomplished.add(event);
					System.out.println(person.getName()+": "+event.getAction() +"-> "+event.getHall());
				}
			}
			else{
				personEventsSaved = new ArrayList<Event>();
				personEventsSaved.add(event);
				
				this.lastEventPersons.put(person.getName(), personEventsSaved);

				rulesAccomplished.add(event);
				System.out.println(person.getName()+": "+event.getAction() +" -> "+event.getHall());
			}
			
			List<Alert> eventAlerts = insertDataService.getAlertByEvent(event);
			for (Alert alert : eventAlerts) {
				System.out.println("Alert->"+alert.getName());
				rulesAccomplished.add(alert);
			}
		}
		return rulesAccomplished;
	}
	
	public void sendRules(List<Rule> rules, Person person){
		for (Rule rule : rules) {
			eventServer.sendData(person, rule);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.out.println("Error en la conexión a la hora de enviar hilos");
			}
		}
	}
	
	
}
