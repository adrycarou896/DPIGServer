package dpigServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class DPIGServerApplication {

	public static void main(String[] args) {
		args = new String[4];
		args[0]="img/individuos";
		args[1]="src/main/resources/rules.properties";
		args[2]="82c908bc-daec-4b43-b643-08b90273923e";
		args[3]="4999";
		SpringApplication.run(DPIGServerApplication.class, args);
	}
}