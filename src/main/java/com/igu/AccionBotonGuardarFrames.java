package com.igu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class AccionBotonGuardarFrames implements ActionListener{
	
	private VentanaPrincipal ventanaPrincipal;
	
	public AccionBotonGuardarFrames(VentanaPrincipal ventanaPrincipal){
		this.ventanaPrincipal = ventanaPrincipal;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ReconocimientoFacialPrueba reconocimientoFacialPrueba = new ReconocimientoFacialPrueba();
		reconocimientoFacialPrueba.setConf();

		String cameraSelectedName = ventanaPrincipal.getComboCamaras().getSelectedItem().toString();
		String userName = ventanaPrincipal.getCampoUsuario().getText();
		if(ventanaPrincipal.getVideoPath()!=null){
			String userFolder = "img/Cameras/"+cameraSelectedName+"/Frames/"+userName;
			File videoFolder = new File(userFolder);
			if(!videoFolder.exists()){
				videoFolder.mkdirs();
			}
			
			ReadVideoFramesPrueba decodeAndCaptureFramesnewPrueba;
			try {
				decodeAndCaptureFramesnewPrueba = new ReadVideoFramesPrueba(ventanaPrincipal.getVideoPath());
				List<BufferedImage>images = decodeAndCaptureFramesnewPrueba.getImages();
				int cont = 1;
				for (BufferedImage image : images) {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(image, "jpg", os);
					InputStream is = new ByteArrayInputStream(os.toByteArray());
					
					Mat frame = readInputStreamIntoMat(is);
					Mat frame_gray = new Mat();
					
					reconocimientoFacialPrueba.reconocerRostroYGuardar(frame, frame_gray, cont, userFolder, userName);
					
					cont++;
				}
				
			} catch (Exception e1) {
				e1.printStackTrace();
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

}
