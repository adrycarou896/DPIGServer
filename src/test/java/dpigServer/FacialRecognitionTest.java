package dpigServer;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import dpigServer.services.FacialRecognition;
import dpigServer.training.Training;
import dpigServer.utils.Util;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = FacialRecognition.class)
@EnableAutoConfiguration
@DataJpaTest
public class FacialRecognitionTest {
	
	@Autowired
	private FacialRecognition facialRecognition;
	
	@Test
	public void PU21() throws IOException {
		initialConfig();
		
		String imagePath = "src/test/java/files/identify/images/oneFace.jpg";
		File image=new File(imagePath);
		BufferedImage img=ImageIO.read(image);
		facialRecognition.setIdentifyValues(img, "Camera1", 0);
		facialRecognition.start();
		List<Long> personIdsEncontrados = facialRecognition.getPersonIdsEncontradosEnEstaIteraccion();
		Long personId = (long) 1;
		assertEquals(personId, personIdsEncontrados.get(0));
	}
	
	@Test
	public void PU22() throws IOException {
		String imagePath = "src/test/java/files/identify/images/twoFaces.jpg";
		File image=new File(imagePath);
		BufferedImage img=ImageIO.read(image);
		facialRecognition.setIdentifyValues(img, "Camera1", 0);
		facialRecognition.start();
		List<Long> personIdsEncontrados = facialRecognition.getPersonIdsEncontradosEnEstaIteraccion();
		Long personId1 = (long) 1;
		Long personId2 = (long) 2;
		assertTrue(personIdsEncontrados.contains(personId1));
		assertTrue(personIdsEncontrados.contains(personId2));
	}
	
	@Test
	public void PU23() throws IOException {
		String imagePath = "src/test/java/files/identify/images/ceroFaces.jpg";
		File image=new File(imagePath);
		BufferedImage img=ImageIO.read(image);
		facialRecognition.setIdentifyValues(img, "Camera1", 0);
		facialRecognition.start();
		List<Long> personIdsEncontrados = facialRecognition.getPersonIdsEncontradosEnEstaIteraccion();
		assertEquals(0, personIdsEncontrados.size());
	}
	
	@Test
	public void PU24() throws IOException {
		String imagePath = "src/test/java/files/identify/images/noTrainingFace.jpg";
		File image=new File(imagePath);
		BufferedImage img=ImageIO.read(image);
		facialRecognition.setIdentifyValues(img, "Camera1", 0);
		facialRecognition.start();
		List<Long> personIdsEncontrados = facialRecognition.getPersonIdsEncontradosEnEstaIteraccion();
		assertEquals(0, personIdsEncontrados.size());
	}
	
	private void initialConfig(){
		System.load(Util.LOAD_OPENCV_PATH);
		Training training = new Training("src/test/java/files/trainingImages");
		training.run();
		facialRecognition.setConf(training);
	}

}
