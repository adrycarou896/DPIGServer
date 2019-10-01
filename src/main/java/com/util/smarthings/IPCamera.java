package com.util.smarthings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient; 
import okhttp3.Request;
import okhttp3.Response;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class IPCamera {
	
	public static final String CAMERA_NAME = "Camera";
	
	public void saveFile(String fileUrl, String destinationFile) throws IOException {
	    URL url = new URL(fileUrl);
	    
	    URLConnection connection = url.openConnection();
	    connection.setRequestProperty("Authorization", "Bearer 82c908bc-daec-4b43-b643-08b90273923e");
	   
	    InputStream is = connection.getInputStream();
	    OutputStream os = new FileOutputStream(destinationFile);
	   
	    byte[] b = new byte[2048];
	    int length;

	    while ((length = is.read(b)) != -1) {
	        os.write(b, 0, length);
	    }

	    is.close();
	    os.close();
	}

	
	public String getDeviceURLImage(String deviceId) throws IOException{
		
		JSONObject responseJSON = getSimpleRequest("https://api.smartthings.com/v1/devices/"+deviceId+"/status");
		
		JSONObject componentsJSON = new JSONObject(responseJSON.get("components").toString());
		JSONObject mainJSON = new JSONObject(componentsJSON.get("main").toString());
		JSONObject imageCaptureJSON = new JSONObject(mainJSON.get("imageCapture").toString());
		JSONObject imageJSON = new JSONObject(imageCaptureJSON.get("image").toString());
		String valueJSON = imageJSON.get("value").toString();
		
		return valueJSON;
	}
	
	public String getDeviceId(JSONArray devices){
		for (int i = 0; i < devices.length(); i++) {
			JSONObject elementJSON = new JSONObject(devices.get(i).toString());
			String name = elementJSON.get("name").toString();
			if(name.equals(CAMERA_NAME)){
				String deviceId = elementJSON.get("deviceId").toString();
				return deviceId;
			}
		}
		return null;
	}
	
	public JSONArray getDevices() throws IOException {
		
		JSONObject responseJSON = getSimpleRequest("https://api.smartthings.com/v1/devices");

		JSONArray devices = new JSONArray(responseJSON.get("items").toString());
		return devices;

	}
	
	private JSONObject getSimpleRequest(String url) throws IOException{
		OkHttpClient client = new OkHttpClient();
		
		Request request = new Request.Builder()
			    .header("Authorization", "Bearer 82c908bc-daec-4b43-b643-08b90273923e")
			    .url(url)
			    .build();

		Response response = client.newCall(request).execute();
		String responseBody = response.body().string();
		
		return new JSONObject(responseBody);
	}
	
	public String getVideoURL(String deviceId) throws IOException{
		
		JSONObject responseJSON = getSimpleRequest("https://api.smartthings.com/v1/devices/"+deviceId+"/status");
		
		JSONObject componentsJSON = new JSONObject(responseJSON.get("components").toString());
		JSONObject mainJSON = new JSONObject(componentsJSON.get("main").toString());
		JSONObject videoCaptureJSON = new JSONObject(mainJSON.get("videoCapture").toString());
		JSONObject clipJSON = new JSONObject(videoCaptureJSON.get("clip").toString());
		JSONObject valueJSON = new JSONObject(clipJSON.get("value").toString());
		String clipPathJSON = valueJSON.get("clipPath").toString();
		return clipPathJSON;
	}
	
	public void getVideoImages(String deviceId) throws IOException{
		
		String videoURL = getVideoURL(deviceId);
		saveFile(videoURL, "img/video.mp4");
		
		FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber("img/video.mp4");
        frameGrabber.start();
        Frame i;
        OpenCVFrameConverter.ToIplImage converterToIplImage = new OpenCVFrameConverter.ToIplImage();
        try {
        	frameGrabber.setFrameNumber(3);//puede ser cualquier frame
            Frame frame = frameGrabber.grabImage();
            //System.out.println(frame);
            IplImage image = converterToIplImage.convert(frame);
            BufferedImage bi = IplImageToBufferedImage(image);
            File outputfile = new File("img/imagenPostVideo.mp4");
            ImageIO.write(bi, "jpg", outputfile);
            frameGrabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static BufferedImage IplImageToBufferedImage(IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame,1);
    }  
}
