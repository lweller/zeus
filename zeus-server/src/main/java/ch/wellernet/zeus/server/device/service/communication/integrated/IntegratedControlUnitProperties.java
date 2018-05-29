package ch.wellernet.zeus.server.device.service.communication.integrated;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;

import ch.wellernet.zeus.server.device.model.BuiltInDeviceType;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "zeus.integrated-control-unit")
public class IntegratedControlUnitProperties {

	@Data
	public static class DriverMapping {
		@Data
		static class DriverDefinition {
			private String name;
			private Properties properties;
		}

		private UUID deviceId;
		private BuiltInDeviceType deviceType;

		private List<DriverDefinition> drivers;
	}

	private UUID id;

	private List<DriverMapping> driverMappings = emptyList();
}
