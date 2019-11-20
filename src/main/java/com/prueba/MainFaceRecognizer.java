package com.prueba;

import com.trainning.Entrenar;

public class MainFaceRecognizer {
	
	public static void main(String[] args) {
		Entrenar train = new Entrenar();
		train.run();
		
		long inicio = System.currentTimeMillis();
		//train.test("img/test/img284cara2.jpg");
		long end = System.currentTimeMillis();
		long result = end-inicio;
		System.out.println("TIME: "+result);
	}
	
}
