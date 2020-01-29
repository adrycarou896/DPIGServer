package dpigServer;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

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
public class RuleManagerTest{
	
	@Autowired
	private InsertDataService insertDataService;
	
	private String filesPath = "src/test/java/files";
	//Probar con cualquier nombre para un evento y alerta.
	@Test
	public void PU16() throws FileNotFoundException, IOException {
		insertDataService.generateRules(filesPath+"/rulesP16.properties");
		assertEquals(1, insertDataService.getEvents().size());
		assertEquals(1, insertDataService.getAlerts().size());
	}

}
