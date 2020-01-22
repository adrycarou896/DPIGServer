package com.services;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.IPCamera;
import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import com.reader.ReadVideoFrames;
import com.repository.IPCameraRepository;
import com.smarthings.IPCamerasManager;
import com.trainning.Entrenar;
import com.utils.Util;

@Service
public class IPCamerasRecord implements Runnable{
	
	private IPCamerasManager ipCamerasManager;
	
	@Autowired
	private IPCameraRepository ipCameraRepository;
	
	@Autowired
	private ReconocimientoFacial reconocimientoFacial;
	
	@Autowired
    private PatternsManager patternsManager;
	
	@Autowired
	private ProcessIPCamera processIPCamera;
	
	private Map<String, String> deviceIdVideoURL;
	
	private IPCamera deviceWithImages;
	
	private int numIter = 0;
	
	private long start;
	
	public void setConf(IPCamerasManager ipCamerasManager, Entrenar entrenamiento){
		this.reconocimientoFacial.setConf(entrenamiento);
		this.ipCamerasManager = ipCamerasManager;
		this.deviceIdVideoURL = new HashMap<String, String>();
	}
	
	public Map<String, List<Long>> getDevicePersons(){
		return this.reconocimientoFacial.getDevicePersons();
	}
	
	public void setNumIter(int numIter){
		this.numIter=numIter;
	}
	
	public void setStart(long start){
		this.start = start;
	}
	
	@Override
	public void run() {
	    try {
	    	List<IPCamera> devices = (List<IPCamera>) ipCameraRepository.findAll();
	    	for (IPCamera device : devices) {
	    		
	    		Pair<String, Date> videoURLAndCaptureTime = ipCamerasManager.getVideoURLAndCaptureTime(device.getDeviceId());
	    		
				String videoURL = videoURLAndCaptureTime.left;
				Date captureTime = videoURLAndCaptureTime.right;
				
				if(videoURL!=null){
					if(deviceWithImages!=null){
						videoURLAndCaptureTime = ipCamerasManager.getVideoURLAndCaptureTime(deviceWithImages.getDeviceId());
						videoURL = videoURLAndCaptureTime.left;
						captureTime = videoURLAndCaptureTime.right;
					}
				}
				else{
					deviceWithImages = device;
					videoURLAndCaptureTime = ipCamerasManager.getVideoURLAndCaptureTime(deviceWithImages.getDeviceId());
					videoURL = videoURLAndCaptureTime.left;
					captureTime = videoURLAndCaptureTime.right;
				}
				
				//videoURL="https://mediaserv.euw1.st-av.net/clip?source_id=2abf098f-694c-4be2-87f1-249ac5050712&clip_id=3hvfRAnaIwlAakE00jclJ";
				if(videoURL!=null){
					//Comprobar que se hace el reconocmiento de esa cámara si esta ha detectado movimiento
					String anteriorVideoURL = null;
					if(this.deviceIdVideoURL.containsKey(device.getDeviceId())){
						anteriorVideoURL = this.deviceIdVideoURL.get(device.getDeviceId());
					}
					if(anteriorVideoURL==null || !anteriorVideoURL.equals(videoURL)){ 
						this.processIPCamera.setConfig(device, videoURL, captureTime, this.reconocimientoFacial);
						this.processIPCamera.run();
						
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
