package ch.wellernet.zeus.server;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ch.wellernet.zeus.modules.device.DeviceModuleConfiguration;
import ch.wellernet.zeus.modules.device.model.DeviceModelConfiguration;
import ch.wellernet.zeus.modules.device.repository.DeviceRepositoryConfiguration;
import ch.wellernet.zeus.modules.scenario.ScenarioModuleConfiguration;
import ch.wellernet.zeus.modules.scenario.model.ScenarioModelConfiguration;
import ch.wellernet.zeus.modules.scenario.repository.ScenarioRepositoryConfiguration;

@SpringBootApplication(scanBasePackageClasses = { SwaggerConfiguration.class, DeviceModuleConfiguration.class,
		ScenarioModuleConfiguration.class })
@EntityScan(basePackageClasses = { DeviceModelConfiguration.class, ScenarioModelConfiguration.class })
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = { DeviceRepositoryConfiguration.class,
		ScenarioRepositoryConfiguration.class })
public class ZeusServerApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {
		SpringApplication.run(ZeusServerApplication.class, args);
	}

	private @Autowired DeviceModuleConfiguration deviceModuleConfiguration;
	private @Autowired ZeusServerConfiguration zeusServerConfiguration;

	@PostConstruct
	private void init() {
		deviceModuleConfiguration.init();
		zeusServerConfiguration.init();
	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
		return builder.sources(ZeusServerApplication.class);
	}
}