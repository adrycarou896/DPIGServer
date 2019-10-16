package com.prueba;

import com.smarthings.IPCamerasManager;

public class Main {
	
	public static void main(String[] args) {
		System.load("C:\\opencv\\build\\java\\x64\\opencv_java400.dll");
		
		IPCamerasManager ipCamerasManager = new IPCamerasManager();
		IPCameraRecordPrueba ipCameraRecordPrueba = new IPCameraRecordPrueba();
		ipCameraRecordPrueba.setConf(ipCamerasManager);
		ipCameraRecordPrueba.run();
	}

}