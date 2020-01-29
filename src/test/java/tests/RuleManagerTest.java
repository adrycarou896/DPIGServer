package tests;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

import dpigServer.services.InsertDataService;

@RunWith(SpringRunner.class)
@WebMvcTest
public class RuleManagerTest{
	
	@Autowired
	private InsertDataService insertDataService;
	
	private String filesPath = "src/test/java/files";
	private String smartThingsToken = "82c908bc-daec-4b43-b643-08b90273923e";
	//Probar con cualquier nombre para un evento y alerta.
	@Test
	public void PU16() throws FileNotFoundException, IOException {
		insertDataService.saveCameras(smartThingsToken);
		insertDataService.generateRules(filesPath+"/rulesP16.properties");
		assertEquals(1, insertDataService.getEvents().size());
		assertEquals(1, insertDataService.getAlerts().size());
	}

}
