package dpigServer.services;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

import dpigServer.training.Training;
import dpigServer.utils.Util;

@Service
public class FacialRecognition extends Thread{
	
    private CascadeClassifier Cascade;
    
    private MatOfRect rostros;
    
    private Training entrenamiento;
    
    private Map<String, List<Long>> devicePersons;
    
    private String deviceName;
    private Mat frame, frame_gray;
    private int iter;
    private List<Long> personIdsEncontrados;
    
    private List<Long> personIdsEncontradosEnEstaIteraccion;
    
    public void setConf(Training entrenamiento){
    	this.Cascade = new CascadeClassifier(Util.CASCADE_PATH);
    	this.rostros = new MatOfRect();
    	this.entrenamiento = entrenamiento;
		this.devicePersons = new HashMap<>();
		
    	File directorioEnEjecucion = new File("img/imagenesEnEjecucion");
    	if(!directorioEnEjecucion.exists()){
    		directorioEnEjecucion.mkdir();
    	}
		
    }
    
    public void setConf(){
    	this.Cascade = new CascadeClassifier(Util.CASCADE_PATH);
    	this.rostros = new MatOfRect();
    }
    
    public void setIdentifyValues(BufferedImage image, String deviceName, int iter){
    	this.deviceName = deviceName;
		frame = bufferedImageToMat(image);
		frame_gray = new Mat();
    	this.iter = iter;
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
        
        for (Rect rostro : rostrosLista) {
        	
    		String rutaImagen = "img/imagenesEnEjecucion/img_"+deviceName+"_"+iter+".jpg";
    	    
    		//Se recorta la imagen
    		rectCrop = new Rect(rostro.x, rostro.y, rostro.width, rostro.height); 
    		Mat frameRecortado = new Mat(frame,rectCrop);
    		
    		//Se pone en un tamaño adecuado
			Mat frameAdecuado = new Mat();
			Imgproc.resize(frameRecortado, frameAdecuado, new Size(100, 100));
			
			//Se guarda la imagen
    		Imgcodecs.imwrite(rutaImagen, frameAdecuado);

    		Pair<Integer, Double> personPair = this.entrenamiento.identify(rutaImagen);
    		if(personPair!=null){
    			long personId = (long) personPair.getFirst();
    			if(!personIdsEncontradosEnEstaIteraccion.contains(personId)){
    				//System.out.println("ENTROOOOOOOO: "+personId+", "+personPair.getSecond()+", "+deviceName);
    				 //System.out.println(personPair.getSecond());
					personIdsEncontradosEnEstaIteraccion.add(personId);
				}
        	}
        }
    }
    
    public Map<String, List<Long>>getDevicePersons(){
    	return this.devicePersons;
    }
    
    public List<Long> getPersonIdsEncontrados(){
    	return this.personIdsEncontrados;
    }

	public List<Long> getPersonIdsEncontradosEnEstaIteraccion() {
		return personIdsEncontradosEnEstaIteraccion;
	}
	
	public static Mat bufferedImageToMat(BufferedImage bi) {
		  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		  mat.put(0, 0, data);
		  return mat;
	}    
	 
}

