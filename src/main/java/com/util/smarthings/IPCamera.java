package com.util.smarthings;

public class IPCamera {
	
	private String deviceId;
	private String name;
	
	public IPCamera(String id, String name){
		this.deviceId = id;
		this.name = name;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public String getName() {
		return name;
	}
}
