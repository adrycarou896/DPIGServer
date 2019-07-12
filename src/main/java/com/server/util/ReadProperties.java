package com.server.util;

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
	
	private static final String FILEPATH="src/main/resources/rules.properties";
	private BufferedReader bf;
	
	public Map<String,Object> readPropertiesFile() throws FileNotFoundException, IOException {
		Map<String,Object> data = new HashMap<String, Object>();
		
		int totalProperties = numberOfLines();
		events = new HashMap<String, String>();
		alerts = new HashMap<String, String>();
		
		try (InputStream input = new FileInputStream(FILEPATH)) {
			Properties prop = new Properties();
            prop.load(input);
            
            int numCameras = Integer.parseInt(prop.getProperty("cameras"));
            data.put("cameras", numCameras);
            
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
		
		FileReader fr = new FileReader(FILEPATH);
		bf = new BufferedReader(fr);
		int lNumeroLineas = 0;
		while ((bf.readLine())!=null) {
		  lNumeroLineas++;
		}
		
		return lNumeroLineas;
	}
}
