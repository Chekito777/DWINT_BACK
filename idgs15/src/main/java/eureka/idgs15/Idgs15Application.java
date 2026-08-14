package eureka.idgs15;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@SpringBootApplication
@EnableEurekaServer
public class Idgs15Application {

	public static void main(String[] args) {
		SpringApplication.run(Idgs15Application.class, args);
	}

}
