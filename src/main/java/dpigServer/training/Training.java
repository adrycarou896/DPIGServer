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
	
	//private FaceRecognizer fr;
	//private FisherFaceRecognizer fisherFaceRecognizer;
	private LBPHFaceRecognizer lbphFaceRecognizer;
	private String trainingFolderPath;
	/*private double mayor = 0.0;
	private double menor = Double.POSITIVE_INFINITY;
	private OpenCVFrameConverter.ToOrgOpenCvCoreMat conversorMatOpenCVCore;
	private OpenCVFrameConverter.ToMat conversorMat;*/
	
	public Training(String trainingFolderPath) {
		 
		this.trainingFolderPath = trainingFolderPath;
		this.lbphFaceRecognizer = LBPHFaceRecognizer.create();
		//this.faceRecognizer = EigenFaceRecognizer.create();
		//this.fisherFaceRecognizer = FisherFaceRecognizer.create();
		//this.faceRecognizer = FisherFaceRecognizer.create(NUMERO_DE_USUARIOS, DBL_MAX);
		//this.conversorMatOpenCVCore = new OpenCVFrameConverter.ToOrgOpenCvCoreMat();
		//this.conversorMat = new OpenCVFrameConverter.ToMat();
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
	
	/*
	public void train(String[] trainingDirs) throws IOException{
		FilenameFilter imgFilter = new FilenameFilter() { 
			public boolean accept(File dir, String name) { 
                name = name.toLowerCase(); 
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png"); 
            } 
        }; 

        List<ImageSample> imageSamples = new ArrayList<>();//Cada ImageSample tiene el file y la label del individuo al que corresponde
        
		for (int label = 0; label < trainingDirs.length; label++) {
			
			String trainingDir = trainingDirs[label]; 
 
			File trainingFile = new File(trainingDir); 
			for (File imageFile: trainingFile.listFiles(imgFilter)) {
				imageSamples.add(new ImageSample(imageFile, label+1));
			}			
		}
		
		List<Mat> list = new ArrayList<Mat>(imageSamples.size());
        int[] labels = new int[imageSamples.size()];

        int counter = 0;
        int label;
        Mat grayImg = new Mat();
        Mat img = new Mat();
        for(ImageSample imageSample : imageSamples){
            img = Imgcodecs.imread(imageSample.getImageFile().getAbsolutePath());
            label = imageSample.getLabel();
            grayImg.create(img.width(), img.height(),1);
            Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);  
            list.add(grayImg);
            labels[counter] = label;
            counter++;
        }
        MatOfInt labels1 = new MatOfInt();
        labels1.fromArray(labels);
        
        fr = LBPHFaceRecognizer.create();
        fr.train(list,labels1);
	}
	
	public Pair<Integer,Double> identify(Mat testImage){
		 int[] enterosLBPH = new int[1];
         double[] confidencesLBPH = new double[1];
         fr.predict(testImage, enterosLBPH, confidencesLBPH);
		 if(confidencesLBPH[0]<140){
			 return new Pair<Integer,Double>(enterosLBPH[0], confidencesLBPH[0]);
		 }
		 return null;
	}
	*/
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
        
		//fisherFaceRecognizer.train(images, labels);
		lbphFaceRecognizer.train(images, labels);
		//eigenFaceRecognizer.train(images, labels);
        
    } 
	
	
	public Pair<Integer, Double> identify(String imagePath){
		
		 Mat testImageMat = imread(imagePath, IMREAD_GRAYSCALE); 
		 //Frame frame = conversorMatOpenCVCore.convert(image);
		 //Mat testImageMat = conversorMat.convert(frame);
		
		 //int[] enterosFisher = new int[1]; 
         //double[] confidencesFisher = new double[1];//yo->251,13 y 1 | mama->330,22 y 2 | papa->261,57 y 1
         //fisherFaceRecognizer.predict(testImageMat, enterosFisher, confidencesFisher);
		 
         //int[] enterosEigen = new int[1];
         //double[] confidencesEigen = new double[1];   
         //eigenFaceRecognizer.predict(testImageMat, enterosEigen, confidencesEigen);//yo->1212,97 y 1 | mama->1437 y 1 | papa->1252 y 1
         
         int[] enterosLBPH = new int[1];
         double[] confidencesLBPH = new double[1];
         lbphFaceRecognizer.predict(testImageMat, enterosLBPH, confidencesLBPH);//yo->115,93 y 1 | mama->138 y 2 | papa-> 153
         
		 /*if(confidences[0]>mayor){
			 mayor=confidences[0];
		 }*/
		 
		 /*if(confidences[0]<menor){
			 menor = confidences[0];
		 }*/
		
		 //LBPH -> <100
		 //Eigen -> <700
         //Fisger -> <280 o 350
		 //System.out.println("CONF: "+confidences[0]);
		 if(confidencesLBPH[0]<140){
			 //System.out.println("RECONOCIDO: "+confidences[0] + " id: "+enteros[0]);
			 //System.out.println("MENOR: " + menor); 
	    	 //System.out.println("MAYOR: " + mayor); 
	    	 //System.out.println("Confidences--------------------->: "+confidences[0]);
	    	 //System.out.println("El usuario que aparece en la imagen "+testImage+" es el usuario "+nombreUsuario);
	  	     //System.out.println("        *Confidencia: "+confidences[0]);
	  	     return new Pair<Integer,Double>(enterosLBPH[0], confidencesLBPH[0]);
	     }
	     return null;
	}
	
	
	/*public static Mat bufferedImageToMat(BufferedImage bi) {
		  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		  mat.put(0, 0, data);
		  return mat;
	} */   
}
