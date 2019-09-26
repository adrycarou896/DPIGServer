package entrenamiento;

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
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_face.FisherFaceRecognizer;

public class Entrenar implements Runnable{
	
	private FaceRecognizer faceRecognizer;
	
	public static final int NUMERO_DE_USUARIOS = 3;
	
	public Entrenar() {
		this.faceRecognizer = FisherFaceRecognizer.create();
	}
	
	@Override
	public void run() {
		
		String [] trainingDirs = new String[NUMERO_DE_USUARIOS];
		
		for (int i = 0; i < NUMERO_DE_USUARIOS; i++) {
			trainingDirs[i] = "img/usuario"+i;
		}
		
		train(trainingDirs);
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
 
			File root = new File(trainingDir); 
			for (File imageFile: root.listFiles(imgFilter)) {
				imageSamples.add(new ImageSample(imageFile, label));
			}			
		}
		
		MatVector images = new MatVector(imageSamples.size());
		Mat labels = new Mat(imageSamples.size(), 1, CV_32SC1); 
		IntBuffer labelsBuf = labels.createBuffer();
		
        int counter = 0; 
		for (ImageSample imageSample: imageSamples) {

			Mat img = imread(imageSample.getImageFile().getAbsolutePath(), IMREAD_GRAYSCALE); 
	        
            images.put(counter, img); 
 
            labelsBuf.put(counter, imageSample.getLabel()); 
            
            counter++; 
        } 
        
        faceRecognizer.train(images, labels);
        
    } 
	
	public Pair<Integer, Double> test(String testImage){
		
		 Mat testImageMat = imread(testImage, IMREAD_GRAYSCALE); 
		
		 int[] enteros = new int[1];
         double[] confidences = new double[1];
		 faceRecognizer.predict(testImageMat, enteros, confidences);
		
	     if(confidences[0]<425){
	    	 //System.out.println("El usuario que aparece en la imagen "+testImage+" es el usuario "+nombreUsuario);
	  	     //System.out.println("        *Confidencia: "+confidences[0]);
	  	     return new Pair<Integer,Double>(enteros[0], confidences[0]);
	     }
	     return null;
	}
	
}
