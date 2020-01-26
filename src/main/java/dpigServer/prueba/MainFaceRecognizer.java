package dpigServer.prueba;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.math3.util.Pair;

import dpigServer.training.Training;

public class MainFaceRecognizer {
	
	public static void main(String[] args) {
		//Training train = new Training();
		//train.run();
		
		//train.test("img/test/img46cara1.jpg");
		
		
		FilenameFilter imgFilter = new FilenameFilter() { 
			public boolean accept(File dir, String name) { 
                name = name.toLowerCase(); 
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png"); 
            } 
        }; 
        
        
        String fileTestPath = "img/test";
        File fileTest = new File(fileTestPath);
        long suma = 0;
        for (File imageTest : fileTest.listFiles(imgFilter)) {
        	long inicio = System.currentTimeMillis();
        	//Pair<Integer, Double> person = train.identify(imageTest.getAbsolutePath());
        	long end = System.currentTimeMillis();
        	/*if(person!=null){
        		long result = end-inicio;
        		suma = suma + result;
        	}
        	else{
        		imageTest.delete();
        	}*/
        }
        long media = suma / fileTest.listFiles(imgFilter).length;
        System.out.println("MEDIA: "+(media/1000.0));
        
        
        
	}
	
}
