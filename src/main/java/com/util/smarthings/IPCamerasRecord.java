package com.util.smarthings;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.IPCamera;
import com.util.entrenamiento.Entrenar;
import com.util.reconocimiento.ReconocimientoFacial;

@Service
public class IPCamerasRecord implements Runnable{
	
	private IPCamerasManager ipCamerasManager;
	
	@Autowired
	private ReconocimientoFacial reconocimientoFacial;
	
	private Map<String, String> deviceIdVideoURL;
	
	private int cont = 0;
	
	private IPCamera deviceWithImages;
	
	private List<String> orderList;
	
	public void setConf(IPCamerasManager ipCamerasManager, Entrenar entrenamiento){
		this.reconocimientoFacial.setConf(entrenamiento);
		
		this.ipCamerasManager = ipCamerasManager;
		
		this.deviceIdVideoURL = new HashMap<String, String>();
		
		
		this.orderList = new ArrayList<String>();
		this.orderList.add("Camera");
		this.orderList.add("F-CAM-VF-1");
		this.orderList.add("Camera");
		//this.orderList.add("Camera4");
		
	}
	
	@Override
	public void run() {
	    try {
	    	List<IPCamera> devices = ipCamerasManager.findDevices();
	    	for (IPCamera device : devices) {
				
				List<BufferedImage> images = new ArrayList<BufferedImage>();
				
				String videoURL = ipCamerasManager.getVideoURL(device.getDeviceId());
				/*//PRUEBAS
				if(device.getName().equals("F-CAM-VF-1") && cont==1){
					cont++;
					
					videoURL = "https://mediaserv.euw1.st-av.net/clip?source_id=2abf098f-694c-4be2-87f1-249ac5050712&clip_id=2OXDxhX3--88LetXD6HBJ";
					String videoFile = "img/videoFrames/"+device.getName()+".mp4";
					ipCamerasManager.saveFile(videoURL, videoFile);
					
					DecodeAndCaptureFrames decodeAndCaptureFramesnew = new DecodeAndCaptureFrames(videoFile);
					images = decodeAndCaptureFramesnew.getImages();
					
					for (BufferedImage image : images) {
						Mat frame = bufferedImageToMat(image);
						Mat frame_gray = new Mat();
						this.reconocimientoFacial.reconocer(device, frame, frame_gray);
						boolean encontrado = this.reconocimientoFacial.reconocer(device, frame, frame_gray);
						if(encontrado){
							break;
						}
					}
					
				}
				*/
				
				//PRARA PRUEBA - Cada cámara analizada tendrá el ultimo video de Camera 
				/*if(videoURL!=null){
					if(deviceWithImages!=null){
						videoURL = ipCamerasManager.getVideoURL(deviceWithImages.getDeviceId());
					}
				}
				else{
					deviceWithImages = device;
					videoURL = ipCamerasManager.getVideoURL(deviceWithImages.getDeviceId());
				}*/
				//
				
				videoURL = "https://mediaserv.euw1.st-av.net/clip?source_id=2abf098f-694c-4be2-87f1-249ac5050712&clip_id=8Dbi3xSLL83_U9EiJ302J";
				
				if(videoURL!=null && this.orderList.get(0).equals(device.getName())){
					this.orderList.remove(0);
					//Comprobar que se hace el reconocmiento de esa cámara si esta ha detectado movimiento
					String anteriorVideoURL = this.deviceIdVideoURL.get(device.getDeviceId());
					if(anteriorVideoURL==null || !anteriorVideoURL.equals(videoURL)){
						cont++;
						String videoFile = "img/videoFrames/"+device.getName()+".mp4";
						ipCamerasManager.saveFile(videoURL, videoFile);
						
						DecodeAndCaptureFrames decodeAndCaptureFramesnew = new DecodeAndCaptureFrames(videoFile);
						images = decodeAndCaptureFramesnew.getImages();
						
						for (BufferedImage image : images) {
							Mat frame = bufferedImageToMat(image);
							Mat frame_gray = new Mat();
							boolean encontrado = this.reconocimientoFacial.reconocer(device, frame, frame_gray);
							if(encontrado){
								break;
							}
						}
						
						if(!this.deviceIdVideoURL.containsKey(device.getDeviceId())){
							this.deviceIdVideoURL.put(device.getDeviceId(), videoURL);
						}
						else{
							this.deviceIdVideoURL.replace(device.getDeviceId(), videoURL);
						}
						
					}
					else{
						System.out.println("La cámara "+device.getName()+" e id "+device.getDeviceId()+" no se ha procesado por no detectar cambios");
					}		
	    		}
				else{
					System.out.println("La cámara "+device.getName()+" e id "+device.getDeviceId()+" no tiene videos disponibles");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Mat bufferedImageToMat(BufferedImage bi) {
		  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		  mat.put(0, 0, data);
		  return mat;
	}

}
