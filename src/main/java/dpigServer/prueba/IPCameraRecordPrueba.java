package dpigServer.prueba;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import dpigServer.smartThings.IPCameraManager;

public class IPCameraRecordPrueba implements Runnable{
	
	private IPCameraManager ipCamerasManager;
	
	private ReconocimientoFacialPrueba reconocimientoFacialPrueba;
	
	public void setConf(IPCameraManager ipCamerasManager){
		this.ipCamerasManager = ipCamerasManager;
		this.reconocimientoFacialPrueba = new ReconocimientoFacialPrueba();
		this.reconocimientoFacialPrueba.setConf();
	}
	
	@Override
	public void run() {
		//Training entrenar = new Training();
		//entrenar.run();
		
		List<String> videosURL = new ArrayList<String>();
		//videosURL.add("https://mediaserv.euw1.st-av.net/clip?source_id=2abf098f-694c-4be2-87f1-249ac5050712&clip_id=uuYSRuIQes11cDLuctcyJ");
		//videosURL.add("https://mediaserv.euw1.st-av.net/clip?source_id=2abf098f-694c-4be2-87f1-249ac5050712&clip_id=FN-9de7BuTSXT8XaNK25J");
		videosURL.add("https://mediaserv.euw1.st-av.net/clip?source_id=2abf098f-694c-4be2-87f1-249ac5050712&clip_id=yGrPSw2hI67SgMF3ZZ0dJ");
		int cont = 1;
		for (String videoURL : videosURL) {
			try {
		    	List<BufferedImage> images = new ArrayList<BufferedImage>();
					
				String videoFile = "img/test/"+"Camera"+".mp4";
				ipCamerasManager.saveFile(videoURL, videoFile);
				
				ReadVideoFramesPrueba decodeAndCaptureFramesnewPrueba = new ReadVideoFramesPrueba(videoFile);
				images = decodeAndCaptureFramesnewPrueba.getImages();

				for (BufferedImage image : images) {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(image, "jpg", os);
					InputStream is = new ByteArrayInputStream(os.toByteArray());
					
					Mat frame = readInputStreamIntoMat(is);
					
					//Mat frame = bufferedImageToMat(image);
					
					Mat frame_gray = new Mat();
					
					//this.reconocimientoFacialPrueba.reconocerRostroYGuardar(cont, frame, frame_gray, entrenar);
					
					cont++;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	    
	}
	
	private static Mat readInputStreamIntoMat(InputStream inputStream) throws IOException {
	    // Read into byte-array
	    byte[] temporaryImageInMemory = readStream(inputStream);

	    // Decode into mat. Use any IMREAD_ option that describes your image appropriately
	    //Mat outputImage = Imgcodecs.imdecode(new MatOfByte(temporaryImageInMemory), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
	    Mat outputImage = Imgcodecs.imdecode(new MatOfByte(temporaryImageInMemory), -1);
	    return outputImage;
	}
	
	private static byte[] readStream(InputStream stream) throws IOException {
	    // Copy content of the image to byte-array
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    int nRead;
	    byte[] data = new byte[16384];

	    while ((nRead = stream.read(data, 0, data.length)) != -1) {
	        buffer.write(data, 0, nRead);
	    }

	    buffer.flush();
	    byte[] temporaryImageInMemory = buffer.toByteArray();
	    buffer.close();
	    stream.close();
	    return temporaryImageInMemory;
	}
	
	public static Mat bufferedImageToMat(BufferedImage bi) {
		  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		  mat.put(0, 0, data);
		  return mat;
	}

}
