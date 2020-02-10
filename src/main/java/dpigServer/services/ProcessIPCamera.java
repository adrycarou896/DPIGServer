package dpigServer.services;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dpigServer.model.IPCamera;
import dpigServer.model.Match;
import dpigServer.model.rule.Rule;
import dpigServer.reader.ReadVideoFrames;
import dpigServer.smartThings.IPCameraManager;
import dpigServer.utils.Util;

@Service
public class ProcessIPCamera implements Runnable{
	
	private IPCameraManager ipCamerasManager;
	
	@Autowired
	private FacialRecognition facialRecognition;
	
	@Autowired
    private PatternsManager patternsManager;
	
	private IPCamera device;
	private String videoURL;
	private Date captureTime;
	
	public void setConfig(IPCamera device, String videoURL, Date captureTime, IPCameraManager ipCamerasManager){
		this.device = device;
		this.videoURL = videoURL;
		this.captureTime = captureTime;
		this.ipCamerasManager = ipCamerasManager;
	}
	
	 @Override
	 public void run(){
		try{
			String videoFilePath = saveVideo();
			
			if(videoFilePath!=null){
				ReadVideoFrames decodeAndCaptureFramesnew = new ReadVideoFrames(videoFilePath);
				List<BufferedImage> images = decodeAndCaptureFramesnew.getImages();
				
				//ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(4);
				
				int numImagen=0;
				int contDecision = 0;
				List<Long> personIdsEncontrados = new ArrayList<Long>();
				for (BufferedImage image : images) {
					numImagen++;
					contDecision++;
					if(contDecision==10){
						contDecision=0;
						this.facialRecognition.setIdentifyValues(image, device.getName(), numImagen);
						//executor.execute(this.facialRecognition);
						this.facialRecognition.start();
						//this.facialRecognition.run();
						List<Long> personIdsEncontradosEnEstaIteraccion = this.facialRecognition.getPersonIdsEncontradosEnEstaIteraccion();
						
						boolean hayPersonas = false;
						for (Long personIdEncontradoEnEstaIteraccion : personIdsEncontradosEnEstaIteraccion) {
							if(!personIdsEncontrados.contains(personIdEncontradoEnEstaIteraccion)){
								personIdsEncontrados.add(personIdEncontradoEnEstaIteraccion);
								
								System.out.println("MATCHH: "+personIdEncontradoEnEstaIteraccion+", "+device.getName());
								captureTime=getCaptureTime(numImagen, images.size());
								Match match = this.patternsManager.saveMatch(device, personIdEncontradoEnEstaIteraccion, captureTime);
								List<Rule> accomplishedRules = this.patternsManager.findPattern(match.getPerson());
								this.patternsManager.sendRules(accomplishedRules, match.getPerson());
							}
							//hayPersonas=true;
						}
						if(hayPersonas){
							break;
						}
					}
				}
			}
		}catch(Exception e){
			System.out.println("Error de video");
		}
		
	}
	
	private Date getCaptureTime(int numImagen, int imagesSize){
		int identifyScondOfTheVideo= (numImagen*10)/imagesSize;//Segundo en el que el usuario ha sido identificado dentro de los 10 segundos
		int secondsToRest = 10 - identifyScondOfTheVideo;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(captureTime);
		calendar.add(Calendar.SECOND, -secondsToRest);
		return calendar.getTime();
	}
	
	private String saveVideo() throws IOException{
		String videoFolderPath = Util.FOLDER_CAMERAS_PATH+"/"+device.getName()+"/Video/";
		File videoFolder = new File(videoFolderPath);
		if(!videoFolder.exists()){
			videoFolder.mkdirs();
		}
		String videoFilePath = videoFolderPath + device.getName()+".mp4";
		try{
			ipCamerasManager.saveFile(videoURL, videoFilePath);
			return videoFilePath;
		}catch(Exception e){
			
		}
		return null;
	}
	
	public static Mat bufferedImageToMat(BufferedImage bi) {
		  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		  mat.put(0, 0, data);
		  return mat;
	}
}
