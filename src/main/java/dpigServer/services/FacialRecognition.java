package dpigServer.services;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;

import dpigServer.model.IPCamera;
import dpigServer.training.Training;
import dpigServer.utils.Util;

@Service
public class FacialRecognition extends Thread{
	 
	@Autowired
	private InsertDataService insertDataService;
	
    private CascadeClassifier Cascade;
    
    private MatOfRect rostros;
    
    private Training entrenamiento;
    
    private Map<String, List<Long>> devicePersons;
    
    private IPCamera device;
    private Mat frame, frame_gray;
    private int iter;
    private List<Long> personIdsEncontrados;
    
    private List<Long> personIdsEncontradosEnEstaIteraccion;
    
    //private long personIdEncontrada = -1;
    
    public void setConf(Training entrenamiento){
    	this.Cascade = new CascadeClassifier(Util.CASCADE_PATH);
    	this.rostros = new MatOfRect();
    	this.entrenamiento = entrenamiento;
		this.devicePersons = new HashMap<>();
		
    }
    
    public void setConf(){
    	this.Cascade = new CascadeClassifier(Util.CASCADE_PATH);
    	this.rostros = new MatOfRect();
    }
    
    public void setIdentifyValues(IPCamera device, Mat frame, Mat frame_gray, int iter, List<Long> personIdsEncontrados){
    	this.device = device;
    	this.frame = frame;
    	this.frame_gray = frame_gray;
    	//this.personIdEncontrada = -1;
    	this.iter = iter;
    	this.personIdsEncontrados = personIdsEncontrados;
    	this.personIdsEncontradosEnEstaIteraccion = new ArrayList<Long>();
    }
    
    @Override
    public synchronized void start(){
    	Imgproc.cvtColor(frame, frame_gray, Imgproc.COLOR_BGR2GRAY);//Colvierte la imagene a color a blanco y negro
        Imgproc.equalizeHist(frame_gray, frame_gray);//Valanzeamos los tonos grises
        double w = frame.width();
        double h = frame.height();
        
        Cascade.detectMultiScale(frame_gray, rostros, 1.1, 2, 0|CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(w, h));
        Rect[] rostrosLista = rostros.toArray();
        
        Rect rectCrop = new Rect();
        
        Pair<Integer, Double> personPair = null;
        String rutaImagen = null;
        Mat frameRecortado = null;
        Mat frameAdecuado = null;
        long personId = -1;
        
        for (Rect rostro : rostrosLista) {
    		rutaImagen = "img/imagenesEnEjecucion/img_"+device.getName()+"_"+iter+".jpg";
    	    
    		//Se recorta la imagen
    		rectCrop = new Rect(rostro.x, rostro.y, rostro.width, rostro.height); 
    		frameRecortado = new Mat(frame,rectCrop);
    		
    		//Se pone en un tamaño adecuado
			frameAdecuado = new Mat();
			Imgproc.resize(frameRecortado, frameAdecuado, new Size(52, 52));
			
			//Se guarda la imagen
    		Imgcodecs.imwrite(rutaImagen, frameAdecuado);
			
    			//System.out.println("Ruta->"+rutaImagen+", tam->"+frameAdecuado.height()+","+frameAdecuado.width());
        		personPair = this.entrenamiento.identify(rutaImagen);
        		
        		if(personPair!=null){
        			personId = (long) personPair.getFirst();//La id es la label
        				//sigueEnElMismoDevice = sigueEnElMismoDevice(device.getName(), personId);
        				//Cuando cambie de device se ejecuta el find
            			//if(!sigueEnElMismoDevice){
    					System.out.println("ENTROOOOOOOO: "+personPair.getSecond()+", "+device.getName());
    					if(!personIdsEncontrados.contains(personId)){
    						personIdsEncontrados.add(personId);
    						personIdsEncontradosEnEstaIteraccion.add(personId);
    					}
        	}
        }
    }
    
	public boolean isImageFalsePositive(IPCamera ipCamera, File newImage){
		File[] imagesFalsePositive = insertDataService.getImagesFalsePostive().get(ipCamera.getDeviceId());
    	for (File imageFalsePositive : imagesFalsePositive) {
			if(compareImage(imageFalsePositive, newImage)){
				return true;
			}
		}
    	return false;
    }
	
	private boolean compareImage(File fileA, File fileB) {        
	    try {
	        //take buffer data from botm image files //
	        BufferedImage biA = ImageIO.read(fileA);
	        DataBuffer dbA = biA.getData().getDataBuffer();
	        int sizeA = dbA.getSize();                      
	        BufferedImage biB = ImageIO.read(fileB);
	        DataBuffer dbB = biB.getData().getDataBuffer();
	        int sizeB = dbB.getSize();
	        //compare data-buffer objects //
	        if(sizeA == sizeB) {
	            for(int i=0; i<sizeA; i++) { 
	                if(dbA.getElem(i) != dbB.getElem(i)) {
	                    return false;
	                }
	            }
	            return true;
	        }
	        else {
	            return false;
	        }
	    } 
	    catch (Exception e) { 
	        System.out.println("Failed to compare image files ...");
	        return  false;
	    }
	}
	
	
    
    /**
     * 
     * @param device
     * @param person
     * @return
     * 
     * Asegura que la persona solo se enucentra en la lista de personas reconocidas por una cámara (la última que le vió)
     */
    private boolean sigueEnElMismoDevice(String device, Long person)
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
    		return false;
    	}
    	return true;
    	
    }
    
    /**
     * 
     * @param device
     * @param person
     * @return
     * 
     * Asegura que solo el último dispositivo que reconoció a una persona x tenga a esa persona x.
     */
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
    
    public Map<String, List<Long>>getDevicePersons(){
    	return this.devicePersons;
    }
    
    /*public long getPersonIdEncontrada(){
    	return this.personIdEncontrada;
    }*/
    public List<Long> getPersonIdsEncontrados(){
    	return this.personIdsEncontrados;
    }

	public List<Long> getPersonIdsEncontradosEnEstaIteraccion() {
		return personIdsEncontradosEnEstaIteraccion;
	}
    
    
	 
}

