package com.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.model.Camera;
import com.model.Person;
import com.model.alert.Alert;
import com.model.event.Event;
import com.model.event.EventComplex;
import com.model.event.EventSimple;
import com.repository.CameraRepository;
import com.repository.PersonRepository;
import com.util.ReadProperties;
import com.util.entrenamiento.Entrenar;

@Service
@Scope("singleton")
public class InsertDataService {
	
	private static final int NUM_PERSONS = 3;
	
	@Autowired
	private CameraRepository cameraRepository;
	
	@Autowired
	private PersonRepository personRepository;
	
	private List<Event> events = new ArrayList<Event>();
	private List<Alert> alerts = new ArrayList<Alert>();
		  
	@PostConstruct
	public void init() {
		
		ReadProperties properties = new ReadProperties();
		Map<String, Object> data=null;
		try {
			
			data = properties.readPropertiesFile();
			int numCamaras = (int)(data.get("cameras"));
			
			for (int i = 0; i < numCamaras; i++) {
				Camera camera = new Camera("camera"+i);
				cameraRepository.save(camera);
			}
			for (int i = 0; i < NUM_PERSONS; i++) {
				Person person = new Person("person"+i);
				personRepository.save(person);
			}
			
			//GENERAR EVENTOS Y ALERTAS (REGLAS)
			generateEvents(data);
			generateAlerts(data);
			
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}
	
	private void generateEvents(Map<String,Object> data){
		Map<String,String> events = (Map<String,String>)data.get("events");
		Iterator iterator = events.entrySet().iterator();
        while (iterator.hasNext()) {
             Map.Entry me2 = (Map.Entry) iterator.next();
             String eventName = (String)me2.getKey();
             String eventValue = (String)me2.getValue();
             
             Event event = getEvent(eventName, eventValue);
 			 this.events.add(event);
        } 
	}
	
	private Event getEvent(String name, String reglaTotal) {
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
			event.setName(name);

		}catch (Exception e) {
			System.out.println("Some event has an error");
		}
		return event;
	}
	
	private void generateAlerts(Map<String,Object> data) {
		Map<String,String> alerts = (Map<String,String>)data.get("alerts");
		Iterator iterator = alerts.entrySet().iterator();
        while (iterator.hasNext()) {
             Map.Entry me2 = (Map.Entry) iterator.next();
             String alertName = (String)me2.getKey();
             String alertValue = (String)me2.getValue();
             
             Alert alert = getAlert(alertName, alertValue);
 			 this.alerts.add(alert);
        } 
	}
	
	private Alert getAlert(String name, String value) {
		String[] valueArray = value.split("-");
		String eventName = valueArray[0];
		String operator = valueArray[1];
		
		String dateString = valueArray[2];
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		try {
			
			Calendar calActual = Calendar.getInstance(); 
			calActual.setTime(new Date());

			int dayOfMonth = calActual.get(Calendar.DAY_OF_MONTH);
			int month = calActual.get(Calendar.MONTH)+1;
			int year = calActual.get(Calendar.YEAR);
		
			String[] dateArray = dateString.split(":");
			int hours = Integer.parseInt(dateArray[0]);
			int minutes = Integer.parseInt(dateArray[1]);
			
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String dateAlertString = String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(dayOfMonth)+" "+String.valueOf(hours)+":"+String.valueOf(minutes);
			Date dateAlert = format.parse(dateAlertString);
			
			return new Alert(name, eventName, operator, dateAlert);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Alert> getAlertByEvent(Event event) {
		List<Alert> alertsToRun = new ArrayList<Alert>();
		
		for (Alert alert : alerts) {
			String alertEventName = alert.getEventName();
			String eventName = event.getName();
			if(alertEventName.equals(eventName)) {
				Date actualDate = new Date();
				Date alertDate = alert.getDate();
				if(alert.getOperator().equals("max")) {
					if(actualDate.after(alertDate)) {
						alertsToRun.add(alert);
					}
				}
				
			}
		}
		return alertsToRun;
	}
	
	public List<Event> getEvents(){
		return this.events;
	}
	
	public List<Alert> getAlerts(){
		return this.alerts;
	}
}
