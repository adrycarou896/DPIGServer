package com.util.smarthings;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import com.util.entrenamiento.Entrenar;
import com.util.reconocimiento.ReconocimientoFacial;

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
		    String destinationFile = "img/actualImage.jpg";
			ipCamera.saveFile(imageUrl, destinationFile);
			
			Mat frame = getImageMat(imageUrl);
			Mat frame_gray = new Mat();
			this.reconocimientoFacial.reconocer(frame, frame_gray);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Mat getImageMat(String imageUrl) throws IOException{
		//InputStream is = new FileInputStream(imageUrl);
		InputStream is = getInputStreamFromUrl(new URL(imageUrl));
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
	
	private InputStream getInputStreamFromUrl(URL url) throws IOException {
        Map<String,String> httpHeaders=new HashMap<String,String>();
        httpHeaders.put("Authorization", "Bearer 82c908bc-daec-4b43-b643-08b90273923e");
        return urlToInputStream(url,httpHeaders);
    }
	
	private InputStream urlToInputStream(URL url, Map<String, String> args) {
	    HttpURLConnection con = null;
	    InputStream inputStream = null;
	    try {
	        con = (HttpURLConnection) url.openConnection();
	        con.setConnectTimeout(15000);
	        con.setReadTimeout(15000);
	        if (args != null) {
	            for (Entry<String, String> e : args.entrySet()) {
	                con.setRequestProperty(e.getKey(), e.getValue());
	            }
	        }
	        con.connect();
	        int responseCode = con.getResponseCode();
	        /* By default the connection will follow redirects. The following
	         * block is only entered if the implementation of HttpURLConnection
	         * does not perform the redirect. The exact behavior depends to 
	         * the actual implementation (e.g. sun.net).
	         * !!! Attention: This block allows the connection to 
	         * switch protocols (e.g. HTTP to HTTPS), which is <b>not</b> 
	         * default behavior. See: https://stackoverflow.com/questions/1884230 
	         * for more info!!!
	         */
	        if (responseCode < 400 && responseCode > 299) {
	            String redirectUrl = con.getHeaderField("Location");
	            try {
	                URL newUrl = new URL(redirectUrl);
	                return urlToInputStream(newUrl, args);
	            } catch (MalformedURLException e) {
	                URL newUrl = new URL(url.getProtocol() + "://" + url.getHost() + redirectUrl);
	                return urlToInputStream(newUrl, args);
	            }
	        }
	        /*!!!!!*/

	        inputStream = con.getInputStream();
	        return inputStream;
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}

}
