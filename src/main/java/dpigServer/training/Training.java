package dpigServer.training;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_face.LBPHFaceRecognizer;


public class Training implements Runnable{
	
	private LBPHFaceRecognizer lbphFaceRecognizer;
	private String trainingFolderPath;
	
	public Training(String trainingFolderPath) {
		 
		this.trainingFolderPath = trainingFolderPath;
		this.lbphFaceRecognizer = LBPHFaceRecognizer.create();
	}
	
	
	@Override
	public void run() {  
		
        String[] personsNames = new File(trainingFolderPath).list();
        
		String [] trainingDirs = new String[personsNames.length];
		
		for (int i = 0; i < personsNames.length; i++) {
			trainingDirs[i] = trainingFolderPath+"/"+personsNames[i];
		}
		
		train(trainingDirs);
		
		System.out.println("Entrenamiento realizado");
	}
	
	public void train(String[] trainingDirs) { 

		FilenameFilter imgFilter = new FilenameFilter() { 
			public boolean accept(File dir, String name) { 
                name = name.toLowerCase(); 
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png"); 
            } 
        }; 

        List<ImageSample> imageSamples = new ArrayList<>();
        
		for (int label = 0; label < trainingDirs.length; label++) {
			
			String trainingDir = trainingDirs[label]; 
 
			File trainingFile = new File(trainingDir); 
			for (File imageFile: trainingFile.listFiles(imgFilter)) {
				imageSamples.add(new ImageSample(imageFile, label+1));
			}			
		}
		
		MatVector images = new MatVector(imageSamples.size());
		Mat labels = new Mat(imageSamples.size(), 1, CV_32SC1); 
		IntBuffer labelsBuf = labels.createBuffer();
		
        int counter = 0; 
        
		for (ImageSample imageSample: imageSamples) {
			
			Mat img = imread(imageSample.getImageFile().getAbsolutePath(), IMREAD_GRAYSCALE); 
	        
            images.put(counter, img); 
 
            labelsBuf.put(counter, imageSample.getLabel()); //Aquí se define a la imagen el id del usuario al que pertenece
            
            counter++; 
        } 
        
		lbphFaceRecognizer.train(images, labels);
        
    } 
	
	
	public Pair<Integer, Double> identify(String imagePath){
		
		 Mat testImageMat = imread(imagePath, IMREAD_GRAYSCALE); 
         
         int[] enterosLBPH = new int[1];
         double[] confidencesLBPH = new double[1];
         lbphFaceRecognizer.predict(testImageMat, enterosLBPH, confidencesLBPH);
		 if(confidencesLBPH[0]<100){
	  	     return new Pair<Integer,Double>(enterosLBPH[0], confidencesLBPH[0]);
	     }
	     return null;
	}  
}
