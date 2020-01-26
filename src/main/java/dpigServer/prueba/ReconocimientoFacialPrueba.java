package dpigServer.prueba;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import dpigServer.training.Training;

public class ReconocimientoFacialPrueba {
	 
    private String RutaDelCascade = "C:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt2.xml";
    private CascadeClassifier Cascade;
    
    private MatOfRect rostros;//Guarda los rostros que va capturando
    
    public void setConf(){
    	this.Cascade = new CascadeClassifier(RutaDelCascade);
    	this.rostros = new MatOfRect();
    }

    
    public void reconocerRostroYGuardar(int numImagen, Mat frame, Mat frame_gray, Training entrenar) throws Exception{
    	Imgproc.cvtColor(frame, frame_gray, Imgproc.COLOR_BGR2GRAY);//Colvierte la imagene a color a blanco y negro
        Imgproc.equalizeHist(frame_gray, frame_gray);//Valanzeamos los tonos grises
        double w = frame.width();
        double h = frame.height();
        
        Cascade.detectMultiScale(frame_gray, rostros, 1.1, 2, 0|CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(w, h));
        Rect[] rostrosLista = rostros.toArray();
        
        Rect rectCrop = new Rect();
        
        int caras=0;
        for (Rect rostro : rostrosLista) {
        	caras++;
    		//Se recorta la imagen
    		rectCrop = new Rect(rostro.x, rostro.y, rostro.width, rostro.height); 
    		Mat frameRecortado = new Mat(frame,rectCrop);
    		
    		String srcSalida="img/test/img"+numImagen+"cara"+caras+".jpg";
    		
    		Mat frameFinal = new Mat();
    		Imgproc.resize(frameRecortado, frameFinal, new Size(52,52));
    		
    		//Se guarda la imagen
    		Imgcodecs.imwrite(srcSalida, frameFinal);
    		
    		/*Pair<Integer, Double> personPair = entrenar.test(srcSalida);
    		
    		if(personPair!=null){
    			long personId = (long) personPair.getFirst();//La id es la label
    			Imgcodecs.imwrite("img/test/reconocidos/img"+numImagen+"cara"+caras+".jpg", frameFinal);
    			
    		}*/
    		
    		//----
    		
    		/*
    		File saveFile = new File(srcSalida);
    		FilenameFilter imgFilter = new FilenameFilter() { 
    			public boolean accept(File dir, String name) { 
                    name = name.toLowerCase(); 
                    return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png"); 
                } 
            }; 
            
        	String fileDir = "img/usuarioAdrian/oldFaces";
        	File root = new File(fileDir); 
        	boolean esFalsoPositivo = false;
    		for (File imageFile: root.listFiles(imgFilter)) {
    			if(compareImage(imageFile, saveFile)){
    				Imgcodecs.imwrite("img/usuarioAdrian/falsesPositivesImages/img"+numImagen+"cara"+caras+".jpg", frameFinal);
    				esFalsoPositivo = true;
    				break;
    			}
    		}	
    		
    		if(!esFalsoPositivo){
    			Imgcodecs.imwrite("img/usuarioAdrian/goodImages/img"+numImagen+"cara"+caras+".jpg", frameFinal);
    		}
    		*/

        } 
    }
    
    public static void resizePrueba(BufferedImage src, OutputStream output, int width, int height) throws Exception {
    	//BufferedImage src = GraphicsUtilities.createThumbnail(ImageIO.read(file), 300);
	    BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = dest.createGraphics();
	    AffineTransform at = AffineTransform.getScaleInstance
	    		((double)width / src.getWidth(), 
	    				(double)height / src.getHeight());
	    g.drawRenderedImage(src, at);
	    ImageIO.write(dest, "JPG", output);
	    output.close();
	}
	
    private boolean compareImage(File fileA, File fileB) {        
	    try {
	        //take buffer data from botm image files //
	        BufferedImage biA = ImageIO.read(fileA);
	        DataBuffer dbA = biA.getData().getDataBuffer();
	        int sizeA = dbA.getSize();                      
	        BufferedImage biB = ImageIO.read(fileB);
	        DataBuffer dbB = biB.getData().getDataBuffer();
	        int sizeB = dbB.getSize();
	        //compare data-buffer objects //
	        if(sizeA == sizeB) {
	            for(int i=0; i<sizeA; i++) { 
	                if(dbA.getElem(i) != dbB.getElem(i)) {
	                    return false;
	                }
	            }
	            return true;
	        }
	        else {
	            return false;
	        }
	    } 
	    catch (Exception e) { 
	        System.out.println("Failed to compare image files ...");
	        return  false;
	    }
	}
}

