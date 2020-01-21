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

import com.model.IPCamera;
import com.model.Person;
import com.model.rule.alert.Alert;
import com.model.rule.event.Event;
import com.model.rule.event.EventComplex;
import com.model.rule.event.EventSimple;
import com.reader.ReadProperties;
import com.repository.IPCameraRepository;
import com.repository.PersonRepository;
import com.smarthings.IPCamerasManager;
import com.utils.Util;

@Service
@Scope("singleton")
public class InsertDataService {
	
	@Autowired
	private IPCameraRepository ipCameraRepository;
	
	@Autowired
	private PersonRepository personRepository;
	
	private List<Event> events = new ArrayList<Event>();
	private List<Alert> alerts = new ArrayList<Alert>();
	
	//private static final int NUM_CAMERAS = 2;
	//@Autowired
	//private ImageFalsePositiveRepository imageFalsePositiveRepository;
	//private Map<String, File[]> imagesFalsePostive = new HashMap<String, File[]>();
		  
	@PostConstruct
	public void init() {
		
		ReadProperties properties = new ReadProperties();
		Map<String, Object> data=null;
		try {
			
			IPCamerasManager ipCamerasManager = new IPCamerasManager();
			
			List<IPCamera> devices = ipCamerasManager.findDevices();
			for (IPCamera device : devices) {
				ipCameraRepository.save(device);
			}
			
			String[] personsNames = Util.getPersonsNames();
			 
			for (int i = 0; i < personsNames.length; i++) {
				Person person = new Person(personsNames[i]);
				personRepository.save(person);
			}
			
			//saveFalsesPositivesImages();
			
			data = properties.readPropertiesFile();
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
				IPCamera ipCamera = ipCameraRepository.findByName(cameraName);
				event = new EventSimple(ipCamera, mensaje);
			}
			else {
				String[] reglaArray = regla.split("->");
				String cameraName1 = reglaArray[0];
				String cameraName2 = reglaArray[1];
				
				IPCamera ipCamera1 = ipCameraRepository.findByName(cameraName1);
				IPCamera ipCamera2 = ipCameraRepository.findByName(cameraName2);
				
				event = new EventComplex(new EventSimple(ipCamera1), new EventSimple(ipCamera2), mensaje);
				
				for (int i = 2; i < reglaArray.length; i++) {
					String cameraName = reglaArray[i];
					IPCamera camera = ipCameraRepository.findByName(cameraName);
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
		Event event = getEvent(eventName);
		if(event!=null){
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
				
				return new Alert(name, event, operator, dateAlert);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private Event getEvent(String name){
		for (Event event : events) {
			if(event.getName().equals(name)){
				return event;
			}
		}
		return null;
	}
	
	public List<Alert> getAlertByEvent(Event event) {
		List<Alert> alertsToRun = new ArrayList<Alert>();
		
		for (Alert alert : alerts) {
			Event alertEvent = alert.getEvent();
			String eventName = event.getName();
			if(alertEvent.getName().equals(eventName)) {
				Date eventDate = event.getDate();
				Date alertDate = alert.getDate();
				if(alert.getOperator().equals("max")) {
					if(eventDate.after(alertDate)) {
						alertsToRun.add(alert);
					}
				}
				else if(alert.getOperator().equals("min")){
					if(eventDate.before(alertDate)) {
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
	
	/*
	private void saveFalsesPositivesImages(){
		//Recoger todas las imágenes de la carpeta donde las guardo
		FilenameFilter imgFilter = new FilenameFilter() { 
			public boolean accept(File dir, String name) { 
                name = name.toLowerCase(); 
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png"); 
            } 
        }; 
        
        for (int i = 1; i <= NUM_CAMERAS; i++) {
        	long ipCameraId = i;
        	IPCamera ipCamera = ipCameraRepository.findByIPCameraId(ipCameraId);
        	String ipCameraName = ipCamera.getName();
        	String fileDir = "img/falsesPositivesImages/"+ipCameraName;
        	File root = new File(fileDir); 
        	this.imagesFalsePostive.put(ipCamera.getDeviceId(), root.listFiles(imgFilter));
		}	
	}
	
	public Map<String, File[]> getImagesFalsePostive() {
		return imagesFalsePostive;
	}*/
}
