package com.util.reconocimiento;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class ReconocimientoFacialPrueba {
	 
    private String RutaDelCascade = "C:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt2.xml";
    private CascadeClassifier Cascade;
    
    private MatOfRect rostros;//Guarda los rostros que va capturando
    
    public void setConf(){
    	this.Cascade = new CascadeClassifier(RutaDelCascade);
    	this.rostros = new MatOfRect();
    }

    
    public void reconocerRostroYGuardar(int numImagen, Mat frame, Mat frame_gray) throws Exception{
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
    		
    		String srcSalida="img/usuarioAdrian/faces/img"+numImagen+"cara"+caras+".jpg";
    		//Se guarda la imagen
    		Imgcodecs.imwrite(srcSalida, frameRecortado);
    		
    		//InputStream input = new FileInputStream(rutaImagen);
    		//System.out.println("INPUT: "+input);
    		
    		BufferedImage input=ImageIO.read(new File(srcSalida));
			OutputStream output = new FileOutputStream(srcSalida);
			resizePrueba(input, output, 52, 52);
			
			/*input = new FileInputStream(srcSalida);
			output = new FileOutputStream("img/usuario0/img"+cont+".jpg");
			resize(input, output, 607, 607);
			cont++;*/
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
    
	public static void resize(InputStream input, OutputStream output, int width, int height) throws Exception {
	    BufferedImage src = ImageIO.read(input);
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

