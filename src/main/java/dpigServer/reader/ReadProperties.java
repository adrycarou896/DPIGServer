package dpigServer.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReadProperties {
	
	private Map<String, String> events;
	private Map<String,String> alerts;
	
	private BufferedReader bf;
	
	private String rulesFilePath;
	
	public ReadProperties(String rulesFilePath){
		this.rulesFilePath = rulesFilePath;
	}
	
	public Map<String,Map<String, String>> readPropertiesFile() throws FileNotFoundException, IOException {
		Map<String,Map<String, String>> data = new HashMap<String, Map<String, String>>();
		
		int totalProperties = numberOfLines();
		events = new HashMap<String, String>();
		alerts = new HashMap<String, String>();
		
		try (InputStream input = new FileInputStream(rulesFilePath)) {
			Properties prop = new Properties();
            prop.load(input);
            
            for (int i = 1; i <= totalProperties; i++) {
            	String propertyName = "event"+i;
   			 	String event = prop.getProperty(propertyName);
   			 	if(event!=null) {
   			 		events.put(propertyName,event);
   			 	}
   			 	propertyName = "alert"+i;
   			 	String alert = prop.getProperty(propertyName);
   			 	if(alert!=null){
   			 		alerts.put(propertyName, alert);
   			 	}
   			}
		}
		data.put("events", events);
		data.put("alerts", alerts);
		return data;
	}
	
	private int numberOfLines() throws IOException {
		
		FileReader fr = new FileReader(rulesFilePath);
		bf = new BufferedReader(fr);
		int lNumeroLineas = 0;
		while ((bf.readLine())!=null) {
		  lNumeroLineas++;
		}
		
		return lNumeroLineas;
	}
}
