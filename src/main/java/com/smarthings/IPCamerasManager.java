package com.smarthings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.model.IPCamera;
import com.utils.Util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IPCamerasManager {
	
	public List<IPCamera> findDevices(){
		try {
			return getDevices(getJSONDevices());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void saveFile(String fileUrl, String destinationFile) throws IOException {
	    URL url = new URL(fileUrl);
	    
	    URLConnection connection = url.openConnection();
	    connection.setRequestProperty("Authorization", "Bearer "+Util.SMARTTHINGS_TOKEN);
	   
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
		
		JSONObject responseJSON = getSimpleRequest(Util.SMARTTHINGS_DEVICES+"/"+deviceId+"/status");
		
		JSONObject componentsJSON = new JSONObject(responseJSON.get("components").toString());
		JSONObject mainJSON = new JSONObject(componentsJSON.get("main").toString());
		JSONObject imageCaptureJSON = new JSONObject(mainJSON.get("imageCapture").toString());
		JSONObject imageJSON = new JSONObject(imageCaptureJSON.get("image").toString());
		String valueJSON = imageJSON.get("value").toString();
		
		return valueJSON;
	}
	
	public List<IPCamera> getDevices(JSONArray devicesJSON){
		List<IPCamera> cameras = new ArrayList<IPCamera>();
		for (int i = 0; i < devicesJSON.length(); i++) {
			JSONObject deviceJSON = new JSONObject(devicesJSON.get(i).toString());
			
			String deviceId = deviceJSON.get("deviceId").toString();
			String name = deviceJSON.get("name").toString();
			IPCamera ipCamera = new IPCamera(deviceId, name);
			
			cameras.add(ipCamera);
			
		}
		return cameras;
	}
	
	public JSONArray getJSONDevices() throws IOException {
		
		JSONObject responseJSON = getSimpleRequest(Util.SMARTTHINGS_DEVICES);

		JSONArray devices = new JSONArray(responseJSON.get("items").toString());
		return devices;

	}
	
	private JSONObject getSimpleRequest(String url) throws IOException{
		OkHttpClient client = new OkHttpClient();
		
		Request request = new Request.Builder()
			    .header("Authorization", "Bearer "+Util.SMARTTHINGS_TOKEN)
			    .url(url)
			    .build();

		Response response = client.newCall(request).execute();
		String responseBody = response.body().string();
		
		return new JSONObject(responseBody);
	}
	
	public String getVideoURL(String deviceId) throws IOException{
		
		JSONObject responseJSON = getSimpleRequest(Util.SMARTTHINGS_DEVICES+"/"+deviceId+"/status");
		
		JSONObject componentsJSON = new JSONObject(responseJSON.get("components").toString());
		JSONObject mainJSON = new JSONObject(componentsJSON.get("main").toString());
		JSONObject videoCaptureJSON = new JSONObject(mainJSON.get("videoCapture").toString());
		JSONObject clipJSON = new JSONObject(videoCaptureJSON.get("clip").toString());
		String value = clipJSON.get("value").toString(); 
		if(!value.equals("null")){
			JSONObject valueJSON = new JSONObject(value);
			String status = valueJSON.get("status").toString();
			if(status.equals("COMPLETE")){
				return valueJSON.get("clipPath").toString();
			}
		}
		return null;
		
	}
}
