package com.prueba;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MainCambiarNombre {

	public static void main(String[] args) {
		
		FilenameFilter imgFilter = new FilenameFilter() { 
			public boolean accept(File dir, String name) { 
                name = name.toLowerCase(); 
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png"); 
            } 
        }; 
        
        int numUsuarios = 2;
        for (int i = 0; i < numUsuarios; i++) {
        	String fileDir = "img/usuario"+i;
        	File root = new File(fileDir); 
        	int cont=1;
    		for (File imageFile: root.listFiles(imgFilter)) {
    			
    			BufferedImage bImage;
				try {
					bImage = ImageIO.read(imageFile);
					ImageIO.write(bImage, "jpg", new File("img/usuario"+i+"/Camera_img"+cont+".jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
    			
    			cont++;
        		
    		}
		}
    	
	}

}
