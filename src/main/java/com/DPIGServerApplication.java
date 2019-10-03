package com;

import java.io.IOException;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.json.JSONArray;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.util.smarthings.CaptureVideoFrameOption3;
import com.util.smarthings.CaptureVideoFramesOption2;
import com.util.smarthings.CaptureVideoFramesOption4;
import com.util.smarthings.DecodeAndCaptureFrames;
import com.util.smarthings.IPCamera;

@SpringBootApplication
@EnableAutoConfiguration
public class DPIGServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DPIGServerApplication.class, args);
		
		/*
		try {
		    Loader.load(FFmpegFrameGrabber.class);
		} catch (UnsatisfiedLinkError e) {
		    String path;
			try {
				path = Loader.cacheResource(FFmpegFrameGrabber.class, "windows-x86_64/jniFFmpegFrameGrabber.dll").getPath();
				try {
					new ProcessBuilder("lib/Dependencies_x64_Release/DependenciesGui.exe", path).start().waitFor();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		  
		}*/
		
		IPCamera ipCamera = new IPCamera();
		
		JSONArray devices;
		try {
			devices = ipCamera.getDevices();
			String deviceId = ipCamera.getDeviceId(devices);
			
			//ipCamera.getVideoImages(deviceId);
			try {
				new DecodeAndCaptureFrames("img/video.mp4","img/videoFrames/");
			} catch (Exception e) {
				e.printStackTrace();
			}
			//new CaptureVideoFramesOption2().getVideoFrames();
			
			//new CaptureVideoFrameOption3().run();
			
			//String videoURL = ipCamera.getVideoURL(deviceId);
			//new CaptureVideoFramesOption4().getVideoImage();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
