package ctrlg.gyeongdodat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GyeongdodatApplication {

	public static void main(String[] args) {
		SpringApplication.run(GyeongdodatApplication.class, args);
	}

}
