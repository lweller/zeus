package ch.wellernet.zeus.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EntityScan(basePackages = { "ch.wellernet.zeus.server.device.model", "ch.wellernet.zeus.server.scenario.model" })
public class ZeusServerApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {
		SpringApplication.run(ZeusServerApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
		return builder.sources(ZeusServerApplication.class);
	}
}