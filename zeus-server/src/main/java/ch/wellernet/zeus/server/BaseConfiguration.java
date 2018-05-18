package ch.wellernet.zeus.server;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfiguration {
	@Bean
	public ServletContextInitializer initializer() {
		return servletContext -> servletContext.setInitParameter("spring.config.location",
				"file:./zeus-config.yml,file:/etc/zeus/zeus-config.yml");
	}
}
