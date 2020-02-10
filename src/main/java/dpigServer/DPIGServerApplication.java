package dpigServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import dpigServer.utils.InputCheck;

@SpringBootApplication
@EnableAutoConfiguration 
public class DPIGServerApplication {
  
	public static void main(String[] args) {
		args = new String[4];
		args[0]="D:/TFG/PRUEBA/entrenamiento";
		args[1]="D:/TFG/PRUEBA/rules.properties";
		args[2]="82c908bc-daec-4b43-b643-08b90273923e";
		args[3]="4999";
		if (InputCheck.validParams(args)) SpringApplication.run(DPIGServerApplication.class, args);
	} 
	
	
}