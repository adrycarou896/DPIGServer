package com.services;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.IPCamera;
import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import com.repository.IPCameraRepository;
import com.smarthings.IPCamerasManager;
import com.trainning.Trainning;

@Service
public class IPCamerasRecord implements Runnable{
	
	private IPCamerasManager ipCamerasManager;
	
	@Autowired
	private IPCameraRepository ipCameraRepository;
	
	@Autowired
	private FacialRecognition reconocimientoFacial;
	
	@Autowired
	private ProcessIPCamera processIPCamera;
	
	private Map<String, String> deviceIdVideoURL;
	
	private IPCamera deviceWithImages;
	
	public void setConf(IPCamerasManager ipCamerasManager, Trainning entrenamiento){
		this.reconocimientoFacial.setConf(entrenamiento);
		this.ipCamerasManager = ipCamerasManager;
		this.deviceIdVideoURL = new HashMap<String, String>();
		
	}
	
	public Map<String, List<Long>> getDevicePersons(){
		return this.reconocimientoFacial.getDevicePersons();
	}
	
	@Override
	public void run() {
	    try {
	    	ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(4);
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
				
				if(videoURL!=null){
					//Comprobar que se hace el reconocmiento de esa cámara si esta ha detectado movimiento
					String anteriorVideoURL = null;
					if(this.deviceIdVideoURL.containsKey(device.getDeviceId())){
						anteriorVideoURL = this.deviceIdVideoURL.get(device.getDeviceId());
					}
					if(anteriorVideoURL==null || !anteriorVideoURL.equals(videoURL)){ 
						this.processIPCamera.setConfig(device, videoURL, captureTime, this.reconocimientoFacial);
						//executor.execute(this.processIPCamera);
						this.processIPCamera.run();
						
						if(!this.deviceIdVideoURL.containsKey(device.getDeviceId())){
							this.deviceIdVideoURL.put(device.getDeviceId(), videoURL);
						}
						else{
							this.deviceIdVideoURL.replace(device.getDeviceId(), videoURL);
						}
						
					}
					/*else{
						System.out.println("La cámara "+device.getName()+" e id "+device.getDeviceId()+" no se ha procesado por no detectar cambios");
					} */	
	    		}
				/*else{
					System.out.println("La cámara "+device.getName()+" e id "+device.getDeviceId()+" no tiene videos disponibles");
				}*/
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
