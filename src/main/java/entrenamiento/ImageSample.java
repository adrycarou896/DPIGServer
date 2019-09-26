package entrenamiento;

import java.io.File;

public class ImageSample {
	File imageFile;
	int label;
	
	public ImageSample(File imageFile, int label) {
		this.imageFile = imageFile;
		this.label = label;
	}

	public File getImageFile() {
		return imageFile;
	}

	public int getLabel() {
		return label;
	}
	
	
}
