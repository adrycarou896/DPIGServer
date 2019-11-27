package com.igu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.utils.Util;

public class AccionBotonGuardarParaUso implements ActionListener{
	
	private VentanaPrincipal ventanaPrincipal;
	
	public AccionBotonGuardarParaUso(VentanaPrincipal ventanaPrincipal){
		this.ventanaPrincipal = ventanaPrincipal;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		String cameraSelectedName = ventanaPrincipal.getComboCamaras().getSelectedItem().toString();
		String userName = ventanaPrincipal.getCampoUsuario().getText();
		
		if(!userName.equals("")){
			FilenameFilter imgFilter = new FilenameFilter() { 
				public boolean accept(File dir, String name) { 
	                name = name.toLowerCase(); 
	                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png"); 
	            } 
	        }; 
	        
	        String userFolderPath = Util.FOLDER_CAMERAS_PATH+"/"+cameraSelectedName+"/Frames/"+userName;
	        File userFolder = new File(userFolderPath); 
	        
	        String userMainFolderPath = Util.FOLDERS_USERS_PATH+"/"+userName;
	        File userMainFolder = new File(userMainFolderPath);
	        
	        if(userFolder.exists() && userMainFolder.exists()){
	        	
	        	 for (File userImage : userFolder.listFiles(imgFilter)) {
	        		 try{
	 	        		FileInputStream fis = new FileInputStream(userImage); //inFile -> Archivo a copiar
	 	        		
	 	        		String userImageExtensionName = userImage.getAbsolutePath().split(cameraSelectedName)[3];
	 	        		FileOutputStream fos = new FileOutputStream(userMainFolderPath+"/"+cameraSelectedName+userImageExtensionName); //outFile -> Copia del archivo
	 	        		
	 	        		FileChannel inChannel = fis.getChannel();
	 	        		FileChannel outChannel = fos.getChannel();
	 	        		inChannel.transferTo(0, inChannel.size(), outChannel);
	 	        		fos.close();
	 	        		fis.close();	
	 	        	
	 	        	}catch (IOException ioe) {
	 	        		System.err.println("Error al Generar Copia 1");
	 		        }
	        	 }
	        }
		}
	}
	
}
