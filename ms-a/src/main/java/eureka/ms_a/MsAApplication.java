package eureka.ms_a;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsAApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAApplication.class, args);
	}

}
