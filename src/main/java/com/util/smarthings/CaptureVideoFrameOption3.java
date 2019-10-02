package com.util.smarthings;

import java.awt.image.BufferedImage;
import java.io.File;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import marvin.util.ConverterUtil;
import marvin.video.MarvinJavaCVAdapter;
import marvin.video.MarvinVideoInterface;
import marvin.video.MarvinVideoInterfaceException;

public class CaptureVideoFrameOption3 implements Runnable{

    private MarvinVideoInterface    videoAdapter;
    private MarvinImage             videoFrame;
    
    
    private FrameGrabber	grabber;
	private IplImage		image;
	//Mat						image;
	private int				width;
	private int				height;
	private boolean			connected;
	private int[]			intArray;
	private MarvinImage 	marvinImage;
	
	

    public CaptureVideoFrameOption3(){
        try{
            // Create the VideoAdapter used to load the video file
            videoAdapter = new MarvinJavaCVAdapter();
            loadResource("img/video");

            // Start the thread for requesting the video frames 
            new Thread(this).start();
        }
        catch(MarvinVideoInterfaceException e){e.printStackTrace();}
    
    }

    @Override
    public void run() {
        try{
        	int cont=0;
            while(true){
                // Request a video frame
                this.videoFrame = this.videoAdapter.getFrame();
                MarvinImageIO.saveImage(videoFrame, "D:/TFG/DPIG_Server/img/videoImages/image"+cont+".jpg");
                cont++;
            }
        }catch(MarvinVideoInterfaceException e){e.printStackTrace();}
    }
    
    public void loadResource(String path) throws MarvinVideoInterfaceException{
		try{
			grabber= OpenCVFrameGrabber.createDefault(new File(path));
		
			//grabber = new OpenCVFrameGrabber(new File(path));
			//grabber = new FFmpegFrameGrabber(new File(path));
			//grabber.setImageWidth(width);
			//grabber.setImageHeight(height);
			grabber.start();
			BufferedImage bufImage = ConverterUtil.frametoBufferedImage(grabber.grabFrame());
			//image = converterToIpl.convert(grabber.grab());
			this.width = bufImage.getWidth();
			this.height = bufImage.getHeight();
			//this.width = image.width();
			//this.height = image.height();
			marvinImage = new MarvinImage(width, height);
			intArray = new int[height*width*4];
			connected = true;
		}
		catch(Exception e){
			throw new MarvinVideoInterfaceException("Error while trying to load resource", e);
		}
	}

}
