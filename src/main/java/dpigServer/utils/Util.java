package dpigServer.utils;

import java.io.File;

public class Util {
	
	public static final String LOAD_OPENCV_PATH = "C:\\opencv\\build\\java\\x64\\opencv_java400.dll"; 
	public static final String CASCADE_PATH = "C:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt2.xml";
	
	
	public static final String FOLDER_CAMERAS_PATH = "img/Cameras";
	//public static final String FOLDERS_INDIVIDUOS_PATH = "img/individuos";
	
	//public static final String RULES_FILE_PATH = "src/main/resources/rules.properties";
	
	//public static final String SMARTTHINGS_TOKEN = "82c908bc-daec-4b43-b643-08b90273923e";
	public static final String SMARTTHINGS_DEVICES = "https://api.smartthings.com/v1/devices";
	
	
	 private String trainingFolderPath;
	 
	 private String rulesFilePath;
	 
	 private String smartThingsToken;
	 
	 private int socketPort;
	 
	 public Util(String trainingFolderPath, String rulesFilePath, String smartThingsToken, int socketPort) {
		this.trainingFolderPath = trainingFolderPath;
		this.rulesFilePath = rulesFilePath;
		this.smartThingsToken = smartThingsToken;
		this.socketPort = socketPort;
	}


	 
	public String getTrainingFolderPath() {
		return trainingFolderPath;
	}



	public void setTrainingFolderPath(String trinningFolderPath) {
		this.trainingFolderPath = trinningFolderPath;
	}



	public String getRulesFilePath() {
		return rulesFilePath;
	}



	public void setRulesFilePath(String rulesFilePath) {
		this.rulesFilePath = rulesFilePath;
	}



	public String getSmartThingsToken() {
		return smartThingsToken;
	}



	public void setSmartThingsToken(String smartThingsToken) {
		this.smartThingsToken = smartThingsToken;
	}



	public int getSocketPort() {
		return socketPort;
	}



	public void setSocketPort(int socketPort) {
		this.socketPort = socketPort;
	}



	public String[] getPersonsNames(){
		File folderUsers = new File(trainingFolderPath);
	    return folderUsers.list();
	}
	 

}
