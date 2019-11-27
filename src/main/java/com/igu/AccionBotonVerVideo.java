package com.igu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import com.model.IPCamera;
import com.utils.Util;

public class AccionBotonVerVideo implements ActionListener {
	
	private VentanaPrincipal ventanaPrincipal;
	
	public AccionBotonVerVideo(VentanaPrincipal ventanaPrincipal){
		this.ventanaPrincipal = ventanaPrincipal;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cameraSelectedName = ventanaPrincipal.getComboCamaras().getSelectedItem().toString();
		for (IPCamera ipCamera : ventanaPrincipal.getIPCameras()) {
			if(ipCamera.getName().equals(cameraSelectedName)){
				String videoURL = ventanaPrincipal.getCameraVideoURL(ipCamera);
				//String videoURL = "https://mediaserv.euw1.st-av.net/clip?source_id=2abf098f-694c-4be2-87f1-249ac5050712&clip_id=AJFFQBkqW9CEhb1sVhSBJ";//<--- CAMBIAR
				
				String cameraFolderPath = Util.FOLDER_CAMERAS_PATH+"/"+ipCamera.getName();
				
				String videoFolderPath = cameraFolderPath+"/Video";
				File videoFolder = new File(videoFolderPath);
				if(!videoFolder.exists()){
					videoFolder.mkdirs();
				}
				
				String videoPath = videoFolderPath+"/"+ipCamera.getName()+".mp4";
				
				ventanaPrincipal.saveFile(videoURL, videoPath);
				
				ventanaPrincipal.createScene(videoPath);
				break;
			}
		}
	}

}
