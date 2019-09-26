package smarthings;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import entrenamiento.Entrenar;
import reconocimiento.ReconocimientoFacial;

public class IPCameraRecord implements Runnable{
	
	private IPCamera ipCamera;
	private ReconocimientoFacial reconocimientoFacial;
	 
	public IPCameraRecord(IPCamera ipCamera, Entrenar entrenamiento){
		this.ipCamera = ipCamera;
		this.reconocimientoFacial = new ReconocimientoFacial(entrenamiento);
	}
	
	@Override
	public void run() {
	    try {
	    	JSONArray devices = ipCamera.getDevices();
			String deviceId = ipCamera.getDeviceId(devices);
			
			String imageUrl = ipCamera.getDeviceURLImage(deviceId);
		    String destinationFile = "images/image.jpg";
			ipCamera.saveImage(imageUrl, destinationFile);
			
			Mat frame = getImageMat(imageUrl);
			Mat frame_gray = new Mat();
			this.reconocimientoFacial.reconocer(frame, frame_gray);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Mat getImageMat(String imageUrl) throws IOException{
		InputStream is = new FileInputStream(imageUrl);
		int nRead;
		byte[] data = new byte[16 * 1024];
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		while ((nRead = is.read(data, 0, data.length)) != -1) {
		    buffer.write(data, 0, nRead);
		}
		byte[] bytes = buffer.toByteArray();
		Mat mat = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
		return mat;
	}

}
