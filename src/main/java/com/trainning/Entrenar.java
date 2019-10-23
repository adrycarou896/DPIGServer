package com.trainning;

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
import org.bytedeco.javacpp.opencv_face.EigenFaceRecognizer;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_face.FisherFaceRecognizer;
import org.bytedeco.javacpp.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import com.model.trainning.ImageSample;

public class Entrenar implements Runnable{
	
	private LBPHFaceRecognizer lBPHFaceRecognizer;
	
	public static final int NUMERO_DE_USUARIOS = 2;
	
	private double mayor = 0.0;
	
	private double menor = Double.POSITIVE_INFINITY;
	
	private OpenCVFrameConverter.ToOrgOpenCvCoreMat conversorMatOpenCVCore;
	private OpenCVFrameConverter.ToMat conversorMat;
	
	public Entrenar() {
		//this.faceRecognizer = EigenFaceRecognizer.create();
		this.lBPHFaceRecognizer = LBPHFaceRecognizer.create();
		//this.faceRecognizer = FisherFaceRecognizer.create(NUMERO_DE_USUARIOS, DBL_MAX);
		this.conversorMatOpenCVCore = new OpenCVFrameConverter.ToOrgOpenCvCoreMat();
		this.conversorMat = new OpenCVFrameConverter.ToMat();
	}
	
	@Override
	public void run() {
		
		String [] trainingDirs = new String[NUMERO_DE_USUARIOS];
		
		for (int i = 0; i < NUMERO_DE_USUARIOS; i++) {
			trainingDirs[i] = "img/usuario"+i;
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
 
            labelsBuf.put(counter, imageSample.getLabel()); //Aquí se define a la imagen el id del usuario al que pertenece
            
            counter++; 
        } 
        
		lBPHFaceRecognizer.train(images, labels);
      
        
    } 
	
	public Pair<Integer, Double> test(String testImage){
		
		 Mat testImageMat = imread(testImage, IMREAD_GRAYSCALE); 
		 //Frame frame = conversorMatOpenCVCore.convert(image);
		 //Mat testImageMat = conversorMat.convert(frame);
		
		 int[] enteros = new int[1];
         double[] confidences = new double[1];
         lBPHFaceRecognizer.predict(testImageMat, enteros, confidences);
		 
		 /*if(confidences[0]>mayor){
			 mayor=confidences[0];
		 }*/
		 
		 if(confidences[0]<menor){
			 menor = confidences[0];
		 }
		 
	     //if(confidences[0]>25){
		 if(confidences[0]<1000){
			 //System.out.println("MENOR: " + menor); 
	    	 //System.out.println("MAYOR: " + mayor); 
	    	 //System.out.println("Confidences--------------------->: "+confidences[0]);
	    	 //System.out.println("El usuario que aparece en la imagen "+testImage+" es el usuario "+nombreUsuario);
	  	     //System.out.println("        *Confidencia: "+confidences[0]);
	  	     return new Pair<Integer,Double>(enteros[0], confidences[0]);
	     }
	     return null;
	}
	
}
