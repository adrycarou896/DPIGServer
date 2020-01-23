package com.services;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.Calendar;
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
import com.reader.ReadVideoFrames;
import com.smarthings.IPCamerasManager;
import com.utils.Util;

@Service
public class ProcessIPCamera implements Runnable{
	
	private IPCamerasManager ipCamerasManager;
	
	@Autowired
	private FacialRecognition facialRecognition;
	
	@Autowired
    private PatternsManager patternsManager;
	
	private IPCamera device;
	private String videoURL;
	private Date captureTime;
	
	public void setConfig(IPCamera device, String videoURL, Date captureTime, FacialRecognition facialRecognition){
		this.device = device;
		this.videoURL = videoURL;
		this.captureTime = captureTime;
		this.facialRecognition = facialRecognition;
		this.ipCamerasManager = new IPCamerasManager();
	}
	
	@Override
	public void run(){
		try{
			String videoFolderPath = Util.FOLDER_CAMERAS_PATH+"/"+device.getName()+"/Video/";
			File videoFolder = new File(videoFolderPath);
			if(!videoFolder.exists()){
				videoFolder.mkdirs();
			}
			String videoFilePath = videoFolderPath + device.getName()+".mp4";
			
			ipCamerasManager.saveFile(videoURL, videoFilePath);
			
			ReadVideoFrames decodeAndCaptureFramesnew = new ReadVideoFrames(videoFilePath);
			List<BufferedImage> images = decodeAndCaptureFramesnew.getImages();
			
			ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(4);
			
			Map<Long, Integer> imagenesIdentificadas = new HashMap<Long, Integer>();
			int numImagen=0;
			for (BufferedImage image : images) {
				numImagen++;
				Mat frame = bufferedImageToMat(image);
				Mat frame_gray = new Mat();
				this.facialRecognition.setIdentifyValues(device, frame, frame_gray, numImagen);
				//executor.execute(this.reconocimientoFacial);
				this.facialRecognition.run();
				long personIdEncontrada = this.facialRecognition.getPersonIdEncontrada();
				
				if(!imagenesIdentificadas.containsKey(personIdEncontrada)){
					imagenesIdentificadas.put(personIdEncontrada, 1);
				}
				else{
					imagenesIdentificadas.replace(personIdEncontrada, imagenesIdentificadas.get(personIdEncontrada)+1);
				}
				
				if(personIdEncontrada!=-1 /*&& imagenesIdentificadas.size()>=3*/){
					int identifyScondOfTheVideo= (numImagen*10)/images.size();//Segundo en el que el usuario ha sido identificado dentro de los 10 segundos
					int secondsToRest = 10 - identifyScondOfTheVideo;
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(captureTime);
					calendar.add(Calendar.SECOND, -secondsToRest); 
					captureTime=calendar.getTime();
					this.patternsManager.find(device, personIdEncontrada, captureTime);
					//System.out.println("individuo "+personIdEncontrada+" encontrado por "+device.getName());
					break;
				}
				
			}
		}catch(Exception e){
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
