package com.utils;

import java.io.File;

public class Util {
	
	public static final String LOAD_OPENCV_PATH = "C:\\opencv\\build\\java\\x64\\opencv_java400.dll"; 
	public static final String CASCADE_PATH = "C:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt2.xml";
	
	public static final String FOLDER_CAMERAS_PATH = "img/Cameras";
	public static final String FOLDERS_USERS_PATH = "img/users";
	
	public static final String RULES_FILE_PATH = "src/main/resources/rules.properties";
	
	public static final String SMARTTHINGS_TOKEN = "82c908bc-daec-4b43-b643-08b90273923e";
	public static final String SMARTTHINGS_DEVICES = "https://api.smartthings.com/v1/devices";
	
	public static final String[] getPersonsNames(){
		File folderUsers = new File(FOLDERS_USERS_PATH);
        return folderUsers.list();
	}
}
