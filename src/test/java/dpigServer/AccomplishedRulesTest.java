package dpigServer;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import dpigServer.model.IPCamera;
import dpigServer.model.Match;
import dpigServer.model.Person;
import dpigServer.model.rule.Rule;
import dpigServer.model.rule.event.Event;
import dpigServer.services.InsertDataService;
import dpigServer.services.PatternsManager;
import dpigServer.socket.SocketServer;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PatternsManager.class, InsertDataService.class, SocketServer.class})
@EnableAutoConfiguration
@DataJpaTest
public class AccomplishedRulesTest {
	
	@Autowired
	private InsertDataService insertDataService;
	
	@Autowired
	private PatternsManager patternsManager;
	
	private String filesPath = "src/test/java/files";
	private String trainingFolderPath = filesPath+"/trainingImages";
	private String smartThingsToken = "82c908bc-daec-4b43-b643-08b90273923e";
	
	@Before
	public void setUp(){
		insertDataService.getEvents().clear();
		insertDataService.getAlerts().clear();
	}
	
	@Test
	public void PU25() {
		insertDataService.saveCameras(smartThingsToken);
		insertDataService.savePersons(trainingFolderPath);
		insertDataService.generateRules(filesPath+"/rulesP25.properties");
		
		IPCamera camera1= insertDataService.getIPCameraByName("Camera1");
		IPCamera camera2= insertDataService.getIPCameraByName("Camera2");
		Person person = insertDataService.getPersonByName("individuo1");
		
		patternsManager.saveMatch(camera1, person.getId(), new Date());
		Match match = patternsManager.saveMatch(camera2, person.getId(), new Date());
		
		List<Event> eventsAccomplished = patternsManager.getAccomplishedEvents(match.getPerson());
		assertEquals(1, eventsAccomplished.size());
		
		Date eventAccomplishedDate = eventsAccomplished.get(0).getAccomplishedDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(eventAccomplishedDate);
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		eventsAccomplished.get(0).setAccomplished(calendar.getTime());
		
		List<Rule> rulesAccomplished = patternsManager.getAccomplishedRules(eventsAccomplished, match.getPerson());
		assertEquals(2, rulesAccomplished.size());
	}
	
	@Test
	public void PU26() {
		insertDataService.saveCameras(smartThingsToken);
		insertDataService.savePersons(trainingFolderPath);
		insertDataService.generateRules(filesPath+"/rulesP26.properties");
		
		IPCamera camera2= insertDataService.getIPCameraByName("Camera2");
		Person person = insertDataService.getPersonByName("individuo2");
		Match match = patternsManager.saveMatch(camera2, person.getId(), new Date());
		
		List<Event> eventsAccomplished = patternsManager.getAccomplishedEvents(match.getPerson());
		List<Rule> rulesAccomplished = patternsManager.getAccomplishedRules(eventsAccomplished, match.getPerson());
		assertEquals(1, rulesAccomplished.size());
	}
	
	@Test
	public void PU27() {
		insertDataService.saveCameras(smartThingsToken);
		insertDataService.savePersons(trainingFolderPath);
		insertDataService.generateRules(filesPath+"/rulesP27.properties");
		
		IPCamera camera1= insertDataService.getIPCameraByName("Camera1");
		IPCamera camera2= insertDataService.getIPCameraByName("Camera2");
		Person person = insertDataService.getPersonByName("individuo1");
		
		patternsManager.saveMatch(camera2, person.getId(), new Date());
		Match match = patternsManager.saveMatch(camera1, person.getId(), new Date());
		
		List<Event> eventsAccomplished = patternsManager.getAccomplishedEvents(match.getPerson());
		assertEquals(1, eventsAccomplished.size());
		
		Date eventAccomplishedDate = eventsAccomplished.get(0).getAccomplishedDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(eventAccomplishedDate);
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		eventsAccomplished.get(0).setAccomplished(calendar.getTime());
		
		List<Rule> rulesAccomplished = patternsManager.getAccomplishedRules(eventsAccomplished, match.getPerson());
		assertEquals(2, rulesAccomplished.size());
	}

}
