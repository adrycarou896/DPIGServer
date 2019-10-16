package com.reader;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class ReadVideoFrames extends MediaListenerAdapter 
{
	  private int mVideoStreamIndex = -1;
	  private boolean gotFirst = false;
	  private Exception e;
	  private int cont = 0;
	  private List<BufferedImage> images;
	  /** Construct a DecodeAndCaptureFrames which reads and captures
	   * frames from a video file.
	   * 
	   * @param filename the name of the media file to read
	   */

	  public ReadVideoFrames(String videoFile)throws Exception
	  {
		 this.images = new ArrayList<BufferedImage>();
		  
	    // create a media reader for processing video
	    this.e = null;
	     IMediaReader reader = ToolFactory.makeReader(videoFile);

	    // stipulate that we want BufferedImages created in BGR 24bit color space
	    reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);


	    // note that DecodeAndCaptureFrames is derived from
	    // MediaReader.ListenerAdapter and thus may be added as a listener
	    // to the MediaReader. DecodeAndCaptureFrames implements
	    // onVideoPicture().

	    reader.addListener(this);

	    // read out the contents of the media file, note that nothing else
	    // happens here.  action happens in the onVideoPicture() method
	    // which is called when complete video pictures are extracted from
	    // the media source

	      while (reader.readPacket() == null && !gotFirst);

	      if (e != null)
	          throw e;
	  }



	  /** 
	   * Called after a video frame has been decoded from a media stream.
	   * Optionally a BufferedImage version of the frame may be passed
	   * if the calling {@link IMediaReader} instance was configured to
	   * create BufferedImages.
	   * 
	   * This method blocks, so return quickly.
	   */

	  public void onVideoPicture(IVideoPictureEvent event)
	  {
	    try
	    {
	      // if the stream index does not match the selected stream index,
	      // then have a closer look

	      if (event.getStreamIndex() != mVideoStreamIndex)
	      {
	        // if the selected video stream id is not yet set, go ahead an
	        // select this lucky video stream

	        if (-1 == mVideoStreamIndex)
	          mVideoStreamIndex = event.getStreamIndex();

	        // otherwise return, no need to show frames from this video stream

	        else
	          return;
	      }
	      
	      this.images.add(event.getImage());
	      //ImageIO.write(event.getImage(), "jpg", new File(saveFile + cont + ".jpg"));
	      this.cont++;
	      //gotFirst = true;

	    }
	    catch (Exception e)
	    {
	      this.e = e;
	    }
	  }
	  
	  public List<BufferedImage> getImages(){
		  return this.images;
	  }
}
