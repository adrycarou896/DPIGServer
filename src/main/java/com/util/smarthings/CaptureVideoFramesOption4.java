package com.util.smarthings;

import static org.bytedeco.javacpp.helper.opencv_imgcodecs.cvSaveImage;

import java.io.IOException;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class CaptureVideoFramesOption4 {
	
	public void getResource(String path) throws IOException{
    	try {   
    		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(path);
    		grabber.setOption("timeout" , "3");
    		grabber.start();
    		while (grabber.grab() != null) {
    			System.out.println("frame grabbed");
    		}
    	} catch (FrameGrabber.Exception ex) {
    		System.err.println(ex);
    	}
    	System.out.println("end");
    }
    
    public void getVideoImage(){
         FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("img/video.mp4");
         try {
             OpenCVFrameConverter.ToIplImage converterToIplImage = new OpenCVFrameConverter.ToIplImage();
             grabber.start();
             int frame_count = grabber.getLengthInFrames();
             for(int i=0; i<frame_count; i+=grabber.getFrameRate()){
                 if (i > 0) grabber.setFrameNumber(i);
                 Frame frame = grabber.grabImage();
                 if(frame == null) break;
                 if(frame.image == null) continue;
                 IplImage image = converterToIplImage.convert(frame);
                 String img_path = "img/video.mp4/videoImages/"+ "frame-" + String.valueOf(i) +".jpg";
                 cvSaveImage(img_path, image);
             }
         }
         catch (Exception e) {
             e.printStackTrace();
         }
    }
    
}
