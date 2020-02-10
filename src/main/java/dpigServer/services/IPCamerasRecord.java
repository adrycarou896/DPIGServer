package dpigServer.services;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;

import dpigServer.model.IPCamera;
import dpigServer.repository.IPCameraRepository;
import dpigServer.smartThings.IPCameraManager;
import dpigServer.training.Training;
import dpigServer.utils.Util;

@Service
public class IPCamerasRecord implements Runnable{
	
	private IPCameraManager ipCamerasManager;
	
	@Autowired
	private IPCameraRepository ipCameraRepository;
	
	@Autowired
	private FacialRecognition reconocimientoFacial;//Solo con train
	
	@Autowired
	private ProcessIPCamera processIPCamera;
	
	private Map<String, String> deviceIdVideoURL;
	
	public void setConf(Training entrenamiento, Util util){
		this.reconocimientoFacial.setConf(entrenamiento);
		this.ipCamerasManager = new IPCameraManager(util.getSmartThingsToken());
		this.deviceIdVideoURL = new HashMap<String, String>();
		
	}
	
	@Override
	public void run() {
	    try {
	    	//ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(4);
	    	List<IPCamera> devices = (List<IPCamera>) ipCameraRepository.findAll();
	    	for (IPCamera device : devices) {
	    		Pair<String, Date> videoURLAndCaptureTime = ipCamerasManager.getIPCameraVideoURLAndCaptureTime(device.getDeviceId());
				
				if(videoURLAndCaptureTime!=null){
					String videoURL = videoURLAndCaptureTime.left;
					Date captureTime = videoURLAndCaptureTime.right;
					
					//Comprobar que se hace el reconocmiento de esa cámara si esta ha detectado movimiento
					String anteriorVideoURL = null;
					if(this.deviceIdVideoURL.containsKey(device.getDeviceId())){
						anteriorVideoURL = this.deviceIdVideoURL.get(device.getDeviceId());
					}
					if(anteriorVideoURL==null || !anteriorVideoURL.equals(videoURL)){ 
						this.processIPCamera.setConfig(device, videoURL, captureTime, this.ipCamerasManager);
						//executor.execute(this.processIPCamera);
						//this.processIPCamera.start();
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
			//System.out.println("URL error");
		}
	}
	
	public static Mat bufferedImageToMat(BufferedImage bi) {
		  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		  mat.put(0, 0, data);
		  return mat;
	}

}
