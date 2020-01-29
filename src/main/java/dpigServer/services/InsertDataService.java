package dpigServer.services;

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
	
	//private Map<String, File[]> imagesFalsePostive = new HashMap<String, File[]>();
		  
	@Override
	public void run(String... args) throws Exception {
		String trinningFolderPath=args[0];
		String rulesFilePath=args[1];
		String smartThingsToken=args[2];
		int socketPort=Integer.parseInt(args[3]);
		 
		this.util = new Util(trinningFolderPath, rulesFilePath, smartThingsToken, socketPort);
		
		saveCameras(util.getSmartThingsToken());
		savePersons();
		//saveFalsesPositivesImages(devices.size());
		generateRules(util.getRulesFilePath());		
	}
	
	public void generateRules(String rulesFilePath){
		ReadProperties properties = new ReadProperties(rulesFilePath);
		try {
			Map<String, Map<String, String>> data = properties.readPropertiesFile();
			generateEvents(data);
			generateAlerts(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveCameras(String smartThingsToken){
		IPCameraManager ipCamerasManager = new IPCameraManager(smartThingsToken);
		List<IPCamera> devices = ipCamerasManager.getIPCameras();
		for (IPCamera device : devices) {
			ipCameraRepository.save(device);
		}
	}
	
	public void savePersons(){
		String[] personsNames = util.getPersonsNames();
		for (int i = 0; i < personsNames.length; i++) {
			Person person = new Person(personsNames[i]);
			personRepository.save(person);
		}
	}

	private void generateEvents(Map<String,Map<String,String>> data){
		Map<String,String> events = data.get("events");
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
	
	private void generateAlerts(Map<String,Map<String,String>> data) {
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
	
	/*private void saveFalsesPositivesImages(int numCameras){
		//Recoger todas las imágenes de la carpeta donde las guardo
		FilenameFilter imgFilter = new FilenameFilter() { 
			public boolean accept(File dir, String name) { 
                name = name.toLowerCase(); 
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png"); 
            } 
        }; 
        
        for (int i = 1; i <= numCameras; i++) {
        	long ipCameraId = i;
        	IPCamera ipCamera = ipCameraRepository.findByIPCameraId(ipCameraId);
        	String ipCameraName = ipCamera.getName();
        	String fileDir = "img/Cameras/"+ipCameraName+"/falsesPositivesImages/";
        	File root = new File(fileDir); 
        	this.imagesFalsePostive.put(ipCamera.getDeviceId(), root.listFiles(imgFilter));
		}	
	}
	
	public Map<String, File[]> getImagesFalsePostive() {
		return imagesFalsePostive;
	}*/
	
	public List<Event> getEvents(){
		return this.events;
	}
	
	public List<Alert> getAlerts(){
		return this.alerts;
	}
	
	public Util getUtil(){
		return this.util;
	}

}
