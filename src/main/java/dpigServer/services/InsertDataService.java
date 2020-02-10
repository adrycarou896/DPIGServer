package dpigServer.services;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import dpigServer.model.IPCamera;
import dpigServer.model.Person;
import dpigServer.model.rule.alert.Alert;
import dpigServer.model.rule.event.Event;
import dpigServer.model.rule.event.EventComplex;
import dpigServer.model.rule.event.EventSimple;
import dpigServer.reader.ReadProperties;
import dpigServer.repository.IPCameraRepository;
import dpigServer.repository.PersonRepository;
import dpigServer.smartThings.IPCameraManager;
import dpigServer.utils.Util;

@Service
@Scope("singleton")
public class InsertDataService implements CommandLineRunner{
	
	@Autowired
	private IPCameraRepository ipCameraRepository;
	
	@Autowired
	private PersonRepository personRepository;
	
	private List<Event> events = new ArrayList<Event>();
	private List<Alert> alerts = new ArrayList<Alert>();
	
	private Util util;

	@Override
	public void run(String... args) throws Exception {
		if (args.length != 4) return;
		String trinningFolderPath=args[0];
		String rulesFilePath=args[1];
		String smartThingsToken=args[2];
		int socketPort=Integer.parseInt(args[3]);
		 
		this.util = new Util(trinningFolderPath, rulesFilePath, smartThingsToken, socketPort);
		
		saveCameras(util.getSmartThingsToken());
		savePersons(util.getTrainingFolderPath());
		//saveFalsesPositivesImages(devices.size());
		generateRules(util.getRulesFilePath());		
	}
	
	public boolean generateRules(String rulesFilePath){
		ReadProperties properties = new ReadProperties(rulesFilePath);
		try {
			Map<String, Map<String, String>> data = properties.readPropertiesFile();
			if(!generateEvents(data) || !generateAlerts(data)){
				events.clear();
				alerts.clear();
				System.out.println("Error en el fichero de reglas");
			}
			else{
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al generar reglas");
		}
		return false;
	}
	
	public void saveCameras(String smartThingsToken){
		IPCameraManager ipCamerasManager = new IPCameraManager(smartThingsToken);
		List<IPCamera> devices = ipCamerasManager.getIPCameras();
		for (IPCamera device : devices) {
			ipCameraRepository.save(device);
		}
	}
	
	public void savePersons(String trainingFolderPath){
		String[] personsNames = new File(trainingFolderPath).list();
		for (int i = 0; i < personsNames.length; i++) {
			Person person = new Person(personsNames[i]);
			personRepository.save(person);
		}
	}

	private boolean generateEvents(Map<String,Map<String,String>> data){
		Map<String,String> events = data.get("events");
		if(events!=null){
			Iterator iterator = events.entrySet().iterator();
	        while (iterator.hasNext()) {
	             Map.Entry me2 = (Map.Entry) iterator.next();
	             String eventName = (String)me2.getKey();
	             String eventValue = (String)me2.getValue();
	             
	             Event event = getEvent(eventName, eventValue);
	             if(event!=null){
	            	 this.events.add(event);
	             }
	             else{
	            	 return false;
	             }
	        }
	        return true;
		}
        return false;
	}
	
	private Event getEvent(String name, String reglaTotal) {
		Event event = null;
		try {
			String[] reglaTotalArray = reglaTotal.split(":");
			if(reglaTotalArray.length!=2)//ruta:mensaje
			{
				return null;
			}
			
			String regla = reglaTotalArray[0];
			regla = regla.replace(" ","");
			String mensaje = reglaTotalArray[1];
			String[] actionHallArray = mensaje.split(" ");
			String action = "";
			int hall = -1;
			for (String actionHall : actionHallArray) {
				if(!actionHall.isEmpty()){
					if(action.isEmpty()){
						action = actionHall;
					}
					else{
						hall = Integer.parseInt(actionHall);
					}
				}
			}
			
			if(!regla.contains("->")) {//camera1:Ha entrado dentro de clase
				String cameraName = regla;
				IPCamera ipCamera = ipCameraRepository.findByName(cameraName);
				if(ipCamera!=null){
					event = new EventSimple(ipCamera, action, hall);
					event.setName(name);
				}
			}
			else {
				String[] reglaArray = regla.split("->");
				String cameraName1 = reglaArray[0];
				String cameraName2 = reglaArray[1];
				
				IPCamera ipCamera1 = ipCameraRepository.findByName(cameraName1);
				IPCamera ipCamera2 = ipCameraRepository.findByName(cameraName2);
				
				if(cameraName1==null || ipCamera2==null){
					return null;
				}
				event = new EventComplex(new EventSimple(ipCamera1), new EventSimple(ipCamera2), action, hall);
				for (int i = 2; i < reglaArray.length; i++) {
					String cameraName = reglaArray[i];
					IPCamera camera = ipCameraRepository.findByName(cameraName);
					if(camera==null){
						return null;
					}
					Event eventSimple = new EventSimple(camera);
					event = new EventComplex(event, eventSimple, action, hall);
				}
				event.setName(name);
			}
			return event;

		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean generateAlerts(Map<String,Map<String,String>> data) {
		Map<String,String> alerts = (Map<String,String>)data.get("alerts");
		if(alerts!=null){
			List<Alert> alertasCumplidas = new ArrayList<Alert>();
			Iterator iterator = alerts.entrySet().iterator();
	        while (iterator.hasNext()) {
	             Map.Entry me2 = (Map.Entry) iterator.next();
	             String alertName = (String)me2.getKey();
	             String alertValue = (String)me2.getValue();
	             
	             Alert alert = getAlert(alertName, alertValue);
	             if(alert!=null){
	            	 this.alerts.add(alert);
	             }
	             else{
	            	 return false;
	             }
	        } 
	        return true;
		}
		return false;
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
				Date eventDate = event.getAccomplishedDate();
				Date alertDate = alert.getDateAlert();
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
	
	public IPCamera getIPCameraByName(String name){
		return this.ipCameraRepository.findByName(name);
	}
	
	public Person getPersonByName(String name){
		return this.personRepository.findByName(name);
	}
	
	public Util getUtil(){
		return this.util;
	}

}
