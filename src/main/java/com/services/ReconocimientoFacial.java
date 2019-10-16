package com.services;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.IPCamera;
import com.trainning.Entrenar;

@Service
public class ReconocimientoFacial {
	 
    private String RutaDelCascade = "C:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt2.xml";
    private CascadeClassifier Cascade;
    
    private MatOfRect rostros;//Guarda los rostros que va capturando
    
    private Entrenar entrenamiento;
    
    @Autowired
    private PatternsManager patternsManager;
    
    private Map<Long, List<String>> personsEncontradas;
    
    private List<String> orderList;
    
    private Map<String, List<Long>> devicePersons;
    
    public void setConf(Entrenar entrenamiento){
    	this.Cascade = new CascadeClassifier(RutaDelCascade);
    	this.rostros = new MatOfRect();
    	
    	this.entrenamiento = entrenamiento;
    	
    	orderList = new ArrayList<String>();
    	orderList.add("Camera");
		orderList.add("F-CAM-VF-1");
		orderList.add("Camera");
		
		this.personsEncontradas = new HashMap<Long, List<String>>();
		
		this.devicePersons = new HashMap<>();
		
    }
    
    public void setConf(){
    	this.Cascade = new CascadeClassifier(RutaDelCascade);
    	this.rostros = new MatOfRect();
    }
    
    public long reconocer(IPCamera device, Mat frame, Mat frame_gray, int numIter) throws Exception{
		
		Imgproc.cvtColor(frame, frame_gray, Imgproc.COLOR_BGR2GRAY);//Colvierte la imagene a color a blanco y negro
        Imgproc.equalizeHist(frame_gray, frame_gray);//Valanzeamos los tonos grises
        double w = frame.width();
        double h = frame.height();
        
        Cascade.detectMultiScale(frame_gray, rostros, 1.1, 2, 0|CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(w, h));
        Rect[] rostrosLista = rostros.toArray();
        
        Rect rectCrop = new Rect();

        for (Rect rostro : rostrosLista) {
    		String rutaImagen = "img/imagenAdecuada.jpg";
    	    
    		//Se recorta la imagen
    		rectCrop = new Rect(rostro.x, rostro.y, rostro.width, rostro.height); 
    		Mat frameRecortado = new Mat(frame,rectCrop);
    		
    		//Se pone en un tamaño adecuado
			Mat frameAdecuado = new Mat();
			Imgproc.resize(frameRecortado, frameAdecuado, new Size(52, 52));
			
			//Se guarda la imagen
    		Imgcodecs.imwrite(rutaImagen, frameAdecuado);

    		Pair<Integer, Double> personPair = this.entrenamiento.test(rutaImagen);
    		if(personPair!=null){
    			long personId = (long) personPair.getFirst();//La id es la label

    			//NUEVO-PRUEBA
				if(!personsEncontradas.containsKey(personId)){
					personsEncontradas.put(personId, orderList);
				}
				
				if(personsEncontradas.get(personId).size()>0 && personsEncontradas.get(personId).get(0).equals(device.getName())){
					personsEncontradas.get(personId).remove(0);
					
					boolean realizarElFind = addPersonToDeviceList(device.getName(), personId);
					//Cuando cambie de device se ejecuta el find
	    			if(realizarElFind){
	    				Imgcodecs.imwrite("img/paso.jpg", frameAdecuado);
	    		    	System.out.println("ENTROOOOOOOO: "+personPair.getSecond()+", "+device.getName()+", num_iter: "+numIter);
	    		    	 
	    				this.patternsManager.find(device, personId, new Date());
	    			}
					
				}
    			
    		}
    		
        } 
        return -1;
        
    }
    
    private boolean addPersonToDeviceList(String device, Long person)
    {
    	boolean sigueEnElMismoDevice = cleanPersonOfDevicesList(device, person);
    	if(!sigueEnElMismoDevice){
    		if(!devicePersons.containsKey(device)){
        		List<Long> persons = new ArrayList<>();
    			persons.add(person);
    			devicePersons.put(device, persons);
        	}
        	else{
        		List<Long> persons = devicePersons.get(device);
        		persons.add(person);
        	}
    		return true;
    	}
    	return false;
    	
    }
    
    private boolean cleanPersonOfDevicesList(String device, Long person){
    	for (Map.Entry<String, List<Long>> entry : devicePersons.entrySet()) {
    		String keyDevice = entry.getKey();
    		List<Long> personsList = entry.getValue();
    		if(personsList.contains(person)){
    			if(!keyDevice.equals(device)){
    				personsList.remove(person);
    			}
    			else{
    				return true;
    			}
    		}
    	}
    	return false;
    }
	 
}

