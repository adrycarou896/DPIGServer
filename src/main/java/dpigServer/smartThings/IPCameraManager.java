package dpigServer.smartThings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;

import dpigServer.model.IPCamera;
import dpigServer.utils.Util;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IPCameraManager {
	
	private String smartThingsToken;
	
	public IPCameraManager(String smartThingsToken){
		this.smartThingsToken = smartThingsToken;
	}
	
	public List<IPCamera> getIPCameras(){
		try {
			return getIPCameras(getJSONDevices());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void saveFile(String fileUrl, String destinationFile) throws IOException {
	    URL url = new URL(fileUrl);
	    
	    URLConnection connection = url.openConnection();
	    connection.setRequestProperty("Authorization", "Bearer "+smartThingsToken);
	   
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
	
	private List<IPCamera> getIPCameras(JSONArray devicesJSON){
		List<IPCamera> cameras = new ArrayList<IPCamera>();
		for (int i = 0; i < devicesJSON.length(); i++) {
			JSONObject deviceJSON = new JSONObject(devicesJSON.get(i).toString());
			
			String deviceId = deviceJSON.get("deviceId").toString();
			String name = deviceJSON.get("name").toString();
			String type = deviceJSON.get("type").toString();
			if(type.equals("DTH")){
				IPCamera ipCamera = new IPCamera(deviceId, name);
				cameras.add(ipCamera);
			}
		}
		return cameras;
	}
	
	private JSONArray getJSONDevices() throws IOException {
		
		JSONObject responseJSON = getSimpleRequest(Util.SMARTTHINGS_DEVICES);

		JSONArray devices = new JSONArray(responseJSON.get("items").toString());
		return devices;

	}
	
	private JSONObject getSimpleRequest(String url) throws IOException{
		OkHttpClient client = new OkHttpClient();
		
		Request request = new Request.Builder()
			    .header("Authorization", "Bearer "+smartThingsToken)
			    .url(url)
			    .build();

		Response response = client.newCall(request).execute();
		String responseBody = response.body().string();
		
		return new JSONObject(responseBody);
	}
	
	public Pair<String, Date> getIPCameraVideoURLAndCaptureTime(String deviceId) throws IOException{
		
		JSONObject responseJSON = getSimpleRequest(Util.SMARTTHINGS_DEVICES+"/"+deviceId+"/status");
		
		//String components = responseJSON.get("components").toString();
		//System.out.println(components);
		JSONObject componentsJSON = new JSONObject(responseJSON.get("components").toString());
		JSONObject mainJSON = new JSONObject(componentsJSON.get("main").toString());
		JSONObject videoCaptureJSON = new JSONObject(mainJSON.get("videoCapture").toString());
		JSONObject clipJSON = new JSONObject(videoCaptureJSON.get("clip").toString());
		String value = clipJSON.get("value").toString(); 
		if(!value.equals("null")){
			JSONObject valueJSON = new JSONObject(value);
			String status = valueJSON.get("status").toString();
			if(status.equals("COMPLETE")){
				
				String videoURL = valueJSON.get("clipPath").toString();
				
				JSONObject imageCaptureJSON = new JSONObject(mainJSON.get("imageCapture").toString());
				JSONObject captureTimeJSON = new JSONObject(imageCaptureJSON.get("captureTime").toString());
				String captureTimeValue = captureTimeJSON.get("value").toString();
				
				//2020-01-21T17:33:41.000Z
				String fechaCaptureTime = captureTimeValue.substring(0, 10);
				String horaCaptureTime = captureTimeValue.substring(11, 19);
				String captureTimeString = fechaCaptureTime+" "+horaCaptureTime;
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
				Date captureTime;
				try {
					 captureTime = format.parse(captureTimeString);
					
					 Calendar calendar = Calendar.getInstance();
					 calendar.setTime(captureTime);
					 calendar.add(Calendar.HOUR, 1);
					 calendar.add(Calendar.MONTH, 1);
					 captureTime = calendar.getTime();
					 
					return new Pair<String, Date>(videoURL, captureTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}	
			}
		}
		return null;
		
	}
}
