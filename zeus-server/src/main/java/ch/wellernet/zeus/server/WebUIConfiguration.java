package ch.wellernet.zeus.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebUIConfiguration implements WebMvcConfigurer {

	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {
		registry.addViewController("/devices/**").setViewName("forward:/");
		registry.addViewController("/events/**").setViewName("forward:/");
		registry.addViewController("/scenarios/**").setViewName("forward:/");
	}
}
