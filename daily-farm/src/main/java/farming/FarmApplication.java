package farming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FarmApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmApplication.class, args);
		
//		SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
//        String secret = Base64.getEncoder().encodeToString(key.getEncoded());
//        System.out.println("Generated secret: " + secret);
	}

}
