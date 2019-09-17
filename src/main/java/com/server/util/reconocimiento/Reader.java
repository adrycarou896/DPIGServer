package com.server.util.reconocimiento;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class Reader implements Runnable{

    private ReconocimientoFacial reconocimientoFacial;
    
	public Reader(Entrenar entrenamiento){
		this.reconocimientoFacial = new ReconocimientoFacial(entrenamiento);
	}
	
	@Override
	public void run() {
		//System.setProperty("java.library.path", "C:/opencv/build/java/x64");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		VideoCapture camera=new VideoCapture(0);
         
		if(!camera.isOpened()){
			System.out.println("Error");
		}
		else{
			Mat frame = new Mat();
			Mat frame_gray = new Mat();
			while(true){
				if(camera.read(frame)){
					
					try {
						this.reconocimientoFacial.reconocer(frame, frame_gray);
					} catch (Exception e) {
						//e.printStackTrace();
					}
					//Imgcodecs.imwrite("img/prueba/camera"+cont+".jpg", frame);
					/*String filePath = "img/camera.jpg";
					Imgcodecs.imwrite(filePath, frame);
					try {
						qr.reconocer(filePath);
						try {
							reconocimientoFacial.reconocerConSURF(frame, frame_gray);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (NotFoundException | WriterException | IOException e) {
						e.printStackTrace();
					}*/
				}
			}
		}
		camera.release();
		
	}
}