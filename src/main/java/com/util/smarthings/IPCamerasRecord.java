package com.util.smarthings;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import com.util.entrenamiento.Entrenar;
import com.util.reconocimiento.ReconocimientoFacial;

public class IPCamerasRecord implements Runnable{
	
	private IPCamerasManager ipCamerasManager;
	private ReconocimientoFacial reconocimientoFacial;
	 
	public IPCamerasRecord(IPCamerasManager ipCameraManager, Entrenar entrenamiento){
		this.ipCamerasManager = ipCameraManager;
		this.reconocimientoFacial = new ReconocimientoFacial(entrenamiento);
	}
	
	@Override
	public void run() {
	    try {
	    	List<IPCamera> devices = ipCamerasManager.getDevices();
	    	for (IPCamera device : devices) {
				
				List<BufferedImage> images = new ArrayList<BufferedImage>();
				
				try {
					String videoURL = ipCamerasManager.getVideoURL(device.getDeviceId());
					if(videoURL!=null){
						String videoFile = "img/videoFrames/"+device.getName()+".mp4";
						ipCamerasManager.saveFile(videoURL, videoFile);
						
						DecodeAndCaptureFrames decodeAndCaptureFramesnew = new DecodeAndCaptureFrames(videoFile);
						images = decodeAndCaptureFramesnew.getImages();
					}
					else{
						System.out.println("La cámara "+device.getName()+" e id "+device.getDeviceId()+" no tiene videos disponibles");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				for (BufferedImage image : images) {
					Mat frame = bufferedImageToMat(image);
					Mat frame_gray = new Mat();
					this.reconocimientoFacial.reconocer(device, frame, frame_gray);
					
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
