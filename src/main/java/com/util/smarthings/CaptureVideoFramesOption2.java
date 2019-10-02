package com.util.smarthings;

import boofcv.alg.background.BackgroundModelMoving;
import boofcv.alg.distort.PointTransformHomography_F32;
import boofcv.factory.background.ConfigBackgroundBasic;
import boofcv.factory.background.FactoryBackgroundModel;
import boofcv.io.MediaManager;
import boofcv.io.UtilIO;
import boofcv.io.image.SimpleImageSequence;
import boofcv.io.wrapper.DefaultMediaManager;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageType;

public class CaptureVideoFramesOption2 {
	
	public void getVideoFrames(){
		String fileName = UtilIO.pathExample("D:/TFG/DPIG_Server/img/video.mp4");

		MediaManager media = DefaultMediaManager.INSTANCE;

		ConfigBackgroundBasic configBasic = new ConfigBackgroundBasic(30, 0.005f);
		ImageType imageType = ImageType.single(GrayF32.class);
		BackgroundModelMoving background = FactoryBackgroundModel.movingBasic(configBasic, new PointTransformHomography_F32(), imageType);

		SimpleImageSequence video = media.openVideo(fileName, background.getImageType());

		ImageBase nextFrame;
		while(video.hasNext()) {
		    nextFrame = video.next();
		    System.out.println("SUU");
		}

	}
}
