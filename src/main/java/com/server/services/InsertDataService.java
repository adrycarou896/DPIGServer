package com.server.services;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.event.Event;
import com.event.EventComplex;
import com.event.EventSimple;
import com.server.model.Camera;
import com.server.model.Person;
import com.server.repository.CameraRepository;
import com.server.repository.PersonRepository;

@Service
public class InsertDataService {
	
	private static final int NUM_CAMERAS = 2;
	private static final int NUM_PERSONS = 3;
	
	private String[] reglas = new String[] {
			"camera0->camera1:Entró en clase 1",
			"camera1:Está en la clase 1",
			"camera1->camera0:Salió de la clase 1"
	};
	
	@Autowired
	private CameraRepository cameraRepository;
	
	@Autowired
	private PersonRepository personRepository;
	
	private List<Event> events = new ArrayList<Event>();
	
	@PostConstruct
	public void init() {
		for (int i = 0; i < NUM_CAMERAS; i++) {
			Camera camera = new Camera("camera"+i,new ArrayList<>());
			cameraRepository.save(camera);
		}
		for (int i = 0; i < NUM_PERSONS; i++) {
			Person person = new Person("person"+i);
			personRepository.save(person);
		}
		
		//GENERAR EVENTOS
		generateEvents();
	}
	
	private void generateEvents() {
		for (int i = 0; i < reglas.length; i++) {
			String regla = reglas[i];
			Event event = getEvent(regla);
			this.events.add(event);
		}
	}
	
	private Event getEvent(String reglaTotal) {
		Event event = null;
		try {
			String[] reglaTotalArray = reglaTotal.split(":");
			String regla = reglaTotalArray[0];
			String mensaje = reglaTotalArray[1];
			
			if(!regla.contains("->")) {//camera1:Ha entrado dentro de clase
				String cameraName = regla;
				Camera camera = cameraRepository.findByName(cameraName);
				event = new EventSimple(camera, mensaje);
			}
			else {
				String[] reglaArray = regla.split("->");
				String cameraName1 = reglaArray[0];
				String cameraName2 = reglaArray[1];
				
				Camera camera1 = cameraRepository.findByName(cameraName1);
				Camera camera2 = cameraRepository.findByName(cameraName2);
				
				event = new EventComplex(new EventSimple(camera1), new EventSimple(camera2), mensaje);
				
				for (int i = 2; i < reglaArray.length; i++) {
					String cameraName = reglaArray[i];
					Camera camera = cameraRepository.findByName(cameraName);
					Event eventSimple = new EventSimple(camera);
					event = new EventComplex(event, eventSimple, mensaje);
				}
			}
			
		}catch (Exception e) {
			System.out.println("Some rule has an error");
		}
		return event;
	}
	
	private void readRulesFile() throws FileNotFoundException, IOException {
		int ruleNumber=1;
		try (InputStream input = new FileInputStream("resources/rules.properties")) {
			Properties prop = new Properties();

            prop.load(input);
            
            prop.getProperty("rule"+ruleNumber);
            
            ruleNumber++;

		}
	}
	
	private long numberOfLines(String filePath) throws IOException {
		
		FileReader fr = new FileReader("fichero.txt");
		BufferedReader bf = new BufferedReader(fr);
		long lNumeroLineas = 0;
		String sCadena = "";
		while ((sCadena = bf.readLine())!=null) {
		  lNumeroLineas++;
		}
		
		return lNumeroLineas;
	}
	
	public List<Event> getEvents(){
		return this.events;
	}
}
