package dpigServer;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import dpigServer.services.InsertDataService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = InsertDataService.class)
@EnableAutoConfiguration
@DataJpaTest
public class RulesManagerTest{
	
	@Autowired
	private InsertDataService insertDataService;
	
	private String filesPath = "src/test/java/files";
	private String smartThingsToken = "82c908bc-daec-4b43-b643-08b90273923e";
	
	@Before
	public void setUp(){
		insertDataService.getEvents().clear();
		insertDataService.getAlerts().clear();
	}
	
	@Test
	public void PU16() throws FileNotFoundException, IOException {
		insertDataService.saveCameras(smartThingsToken);
		boolean result = insertDataService.generateRules(filesPath+"/rulesP16.properties");
		assertTrue(result);
		assertEquals(1, insertDataService.getEvents().size());
		assertEquals(1, insertDataService.getAlerts().size());
	}
	
	@Test
	public void PU17() throws FileNotFoundException, IOException {
		boolean result = insertDataService.generateRules(filesPath+"/rulesP17.properties");
		assertFalse(result);
		assertEquals(0, insertDataService.getEvents().size());
		assertEquals(0, insertDataService.getAlerts().size());
	}
	
	@Test
	public void PU18() throws FileNotFoundException, IOException {
		boolean result = insertDataService.generateRules(filesPath+"/rulesP18.properties");
		assertFalse(result);
		assertEquals(0, insertDataService.getEvents().size());
		assertEquals(0, insertDataService.getAlerts().size());
	}
	
	@Test
	public void PU19() throws FileNotFoundException, IOException {
		boolean result = insertDataService.generateRules(filesPath+"/rulesP19.properties");
		assertFalse(result);
		assertEquals(0, insertDataService.getEvents().size());
		assertEquals(0, insertDataService.getAlerts().size());
	}
	
	@Test
	public void PU20() throws FileNotFoundException, IOException {
		boolean result = insertDataService.generateRules(filesPath+"/rulesP20.properties");
		assertFalse(result);
		assertEquals(0, insertDataService.getEvents().size());
		assertEquals(0, insertDataService.getAlerts().size());
	}
	
	

}
