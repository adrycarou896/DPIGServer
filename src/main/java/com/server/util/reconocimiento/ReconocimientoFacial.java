package com.server.util.reconocimiento;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.math3.util.Pair;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;

import com.alert.Alert;
import com.event.Event;
import com.server.eventsserver.IEventsServer;
import com.server.model.Camera;
import com.server.model.Match;
import com.server.model.Person;
import com.server.repository.CameraRepository;
import com.server.repository.MatchRepository;
import com.server.repository.PersonRepository;
import com.server.services.InsertDataService;

public class ReconocimientoFacial {
	 
    private String RutaDelCascade = "C:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt2.xml";
    private CascadeClassifier Cascade;
    
    private MatOfRect rostros;//Guarda los rostros que va capturando
    
    private Entrenar entrenamiento;
    private Map<Long, Long> personsTimes;
    
    //Nuevo
    private long cameraId = 1;
    
	@Autowired
	private MatchRepository matchRepository;
	
	@Autowired
	private CameraRepository cameraRepository;
	
	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private InsertDataService insertDataService;
	
	@Autowired
	private IEventsServer eventServer;
	
	private Map<String, List<Event>> lastEventPersons = new HashMap<String,List<Event>>();
    
    public ReconocimientoFacial(){
    	this.Cascade = new CascadeClassifier(RutaDelCascade);
    	this.rostros = new MatOfRect();
    }
    
    public ReconocimientoFacial(Entrenar entrenamiento){
    	this.Cascade = new CascadeClassifier(RutaDelCascade);
    	this.rostros = new MatOfRect();
    	
    	this.entrenamiento = entrenamiento;
    	this.personsTimes = new HashMap<Long, Long>();
    }
    
    public void reconocer(Mat frame, Mat frame_gray) throws Exception{
		
		Imgproc.cvtColor(frame, frame_gray, Imgproc.COLOR_BGR2GRAY);//Colvierte la imagene a color a blanco y negro
        Imgproc.equalizeHist(frame_gray, frame_gray);//Valanzeamos los tonos grises
        double w = frame.width();
        double h = frame.height();
        
        Cascade.detectMultiScale(frame_gray, rostros, 1.1, 2, 0|CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(w, h));
        Rect[] rostrosLista = rostros.toArray();
        
        Rect rectCrop = new Rect();

        for (Rect rostro : rostrosLista) {
    		String rutaImagen = "img/persona.jpg";
    	    
    		//Se recorta la imagen
    		rectCrop = new Rect(rostro.x, rostro.y, rostro.width, rostro.height); 
    		frame = new Mat(frame,rectCrop);
    		
    		//Se guarda la imagen
    		Imgcodecs.imwrite(rutaImagen, frame);
    		
    		InputStream input = new FileInputStream(rutaImagen);
    		String srcSalida="img/test.jpg";
			OutputStream output = new FileOutputStream(srcSalida);
			resize(input, output, 607, 607);
			
			/*input = new FileInputStream(srcSalida);
			output = new FileOutputStream("img/usuario0/img"+cont+".jpg");
			resize(input, output, 607, 607);
			cont++;*/
			
    		Pair<Integer, Double> personPair = this.entrenamiento.test(srcSalida);
    		if(personPair!=null){
    			long personId = (long) personPair.getFirst();//La id es la label
    			long momentoActual = System.currentTimeMillis();
    			if(!this.personsTimes.containsKey(personId)) {
    				this.personsTimes.put(personId, momentoActual);
    				saveMatch(this.cameraId, personId, new Date());
    			}
    			else {
    				long momentoUltimoMatch = this.personsTimes.get(personId);
    				long tiempoTranscurrido = momentoActual - momentoUltimoMatch;
    				if(tiempoTranscurrido>=5000) {
    					this.personsTimes.replace(personId, momentoActual);
    					saveMatch(this.cameraId, personId, new Date());
    				}
    			}
    			
    		}
    		
        } 
        
    }
    
	public static void resize(InputStream input, OutputStream output, int width, int height) throws Exception {
	    BufferedImage src = ImageIO.read(input);
	    BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = dest.createGraphics();
	    AffineTransform at = AffineTransform.getScaleInstance
	    		((double)width / src.getWidth(), 
	    				(double)height / src.getHeight());
	    g.drawRenderedImage(src, at);
	    ImageIO.write(dest, "JPG", output);
	    output.close();
	}
	
	private Match saveMatch(long cameraId, long personId, Date date){
		
		String cameraName = "camera" + cameraId;
	    String personName = "person"+ personId;
	    
		Camera camera = cameraRepository.findByName(cameraName);
		Person person = personRepository.findByName(personName);
		Match match = new Match(camera, person, date);
		
		Match macthFind = matchRepository.findByCameraPerson(camera.getId(), person.getId());
		if(macthFind!=null) {
			matchRepository.updateMatch(date, camera.getId(), person.getId());
		}
		else {
			matchRepository.save(match);
		}		
		
		searchPattern(person);
		
		return match;
	}
	
	private void searchPattern(Person person) {
		List<Match> personMatches = matchRepository.findByPerson(person.getId());
		
		List<Event> eventsSuccesed = new ArrayList<Event>();
		Event firstEvent = null;
		for (Event event : insertDataService.getEvents()) {
			if(event!=null) {
				if(event.isSuccesed(personMatches)) {
					if (firstEvent == null || event.getDate().equals(firstEvent.getDate())) {
						firstEvent = event;
						eventsSuccesed.add(event);
					}
					else if(event.getDate().after(firstEvent.getDate())) {
						firstEvent = event;
						eventsSuccesed.clear();
						eventsSuccesed.add(event);
					}
				}
			}
		}
		
		for (Event event : eventsSuccesed) {
			System.out.println(person.getName()+" -> "+event);
			List<Event> personEventsSaved = this.lastEventPersons.get(person.getName());
			if(personEventsSaved!=null){
				if(!personEventsSaved.contains(event)){
					if(event.getDate().before(personEventsSaved.get(0).getDate())){
						personEventsSaved.clear();
					}
					personEventsSaved.add(event);
					eventServer.saveData(person,event);	
				}
			}
			else{
				personEventsSaved = new ArrayList<Event>();
				personEventsSaved.add(event);
				
				this.lastEventPersons.put(person.getName(), personEventsSaved);
				eventServer.saveData(person,event);	
			}
			
			List<Alert> eventAlerts = insertDataService.getAlertByEvent(event);
			for (Alert alert : eventAlerts) {
				System.out.println("Alert->"+alert.getName());
			}
		}
		
		
	}
	 
}

