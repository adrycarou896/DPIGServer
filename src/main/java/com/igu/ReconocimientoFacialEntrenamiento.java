package com.igu;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import com.utils.Util;

public class ReconocimientoFacialEntrenamiento {
	 
    private CascadeClassifier Cascade;
    
    private MatOfRect rostros;//Guarda los rostros que va capturando
    
    public void setConf(){
    	this.Cascade = new CascadeClassifier(Util.CASCADE_PATH);
    	this.rostros = new MatOfRect();
    }

    
    public void reconocerRostroYGuardar(Mat frame, Mat frame_gray, int cont, String userFolderPath, String cameraName) throws Exception{
    	Imgproc.cvtColor(frame, frame_gray, Imgproc.COLOR_BGR2GRAY);//Colvierte la imagene a color a blanco y negro
        Imgproc.equalizeHist(frame_gray, frame_gray);//Valanzeamos los tonos grises
        double w = frame.width();
        double h = frame.height();
        
        Cascade.detectMultiScale(frame_gray, rostros, 1.1, 2, 0|CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(w, h));
        Rect[] rostrosLista = rostros.toArray();
        
        Rect rectCrop = new Rect();
        
        int numRostro = 1;
        for (Rect rostro : rostrosLista) {
    		//Se recorta la imagen
    		rectCrop = new Rect(rostro.x, rostro.y, rostro.width, rostro.height); 
    		Mat frameRecortado = new Mat(frame,rectCrop);
    		
    		String srcSalida = userFolderPath+"/"+cameraName+"_img"+cont+"_"+numRostro+".jpg";
    		
    		Mat frameFinal = new Mat();
    		Imgproc.resize(frameRecortado, frameFinal, new Size(52,52));
    		
    		//Se guarda la imagen
    		Imgcodecs.imwrite(srcSalida, frameFinal);
    		
    		numRostro++;

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
	
}

