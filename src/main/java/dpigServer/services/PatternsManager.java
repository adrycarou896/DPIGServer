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
	
	//@Autowired
	//private ImageFalsePositiveRepository imageFalsePositiveRepository;
	
	//private Map<String, List<ImageFalsePositive>> imagesFalsePositiveByDevice = new HashMap<String, List<ImageFalsePositive>>();
	//private Map<String, File[]> imagesFalsePositiveByDevice = new HashMap<String, File[]>();
	
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
	
	private Match getMatch(IPCamera ipCameraModel, long personId, Date date){
		//String personName = "person"+ personId;
		Person person = personRepository.findPersonById(personId);
		//Person person = personRepository.findByName(personName);
		
		IPCamera ipCamera = ipCameraRepository.findByIPCameraId(ipCameraModel.getId());
		Match match = new Match(ipCamera, person, date);
		return match;
	}
	
	public void findPattern(Person person) {
		List<Match> personMatches = matchRepository.findByPerson(person.getId());
		
		List<Event> eventsSuccesed = new ArrayList<Event>();
		Event firstEvent = null;
		for (Event event : insertDataService.getEvents()) {
			if(event!=null) {
				if(event.isAccomplished(personMatches)) {
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
					/*if(event.getDate().before(personEventsSaved.get(0).getDate())){
						personEventsSaved.clear();
					}*/
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
				eventServer.sendData(person, alert);
			}
		}
		
		
	}
	
	/*
	public boolean isImageFalsePositive(IPCamera ipCamera, File newImage){
		File[] imagesFalsePositive = insertDataService.getImagesFalsePostive().get(ipCamera.getDeviceId());
    	for (File imageFalsePositive : imagesFalsePositive) {
			if(compareImage(imageFalsePositive, newImage)){
				return true;
			}
		}
    	return false;
    }
	
	private boolean compareImage(File fileA, File fileB) {        
	    try {
	        //take buffer data from botm image files //
	        BufferedImage biA = ImageIO.read(fileA);
	        DataBuffer dbA = biA.getData().getDataBuffer();
	        int sizeA = dbA.getSize();                      
	        BufferedImage biB = ImageIO.read(fileB);
	        DataBuffer dbB = biB.getData().getDataBuffer();
	        int sizeB = dbB.getSize();
	        //compare data-buffer objects //
	        if(sizeA == sizeB) {
	            for(int i=0; i<sizeA; i++) { 
	                if(dbA.getElem(i) != dbB.getElem(i)) {
	                    return false;
	                }
	            }
	            return true;
	        }
	        else {
	            return false;
	        }
	    } 
	    catch (Exception e) { 
	        System.out.println("Failed to compare image files ...");
	        return  false;
	    }
	}
	*/
	
	
}
