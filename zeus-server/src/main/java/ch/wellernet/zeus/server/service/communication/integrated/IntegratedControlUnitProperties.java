package ch.wellernet.zeus.server.service.communication.integrated;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;

import ch.wellernet.zeus.server.model.BuiltInDeviceType;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "zeus.integrated-control-unit")
public class IntegratedControlUnitProperties {

	private UUID id;
	private Collection<DriverMapping> driverMappings = emptyList();

	@Data
	public static class DriverMapping {
		private UUID deviceId;
		private BuiltInDeviceType deviceType;
		private List<DriverDefinition> drivers;

		@Data
		static class DriverDefinition {
			private String name;
			private Properties properties;
		}
	}
}
