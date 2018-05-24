package ch.wellernet.zeus.server.service.communication.integrated;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.wellernet.zeus.server.model.Command;
import ch.wellernet.zeus.server.model.ControlUnit;
import ch.wellernet.zeus.server.model.Device;
import ch.wellernet.zeus.server.model.IntegratedControlUnitAddress;
import ch.wellernet.zeus.server.repository.ControlUnitRepository;
import ch.wellernet.zeus.server.service.communication.integrated.IntegratedCommunicationService.DeviceCommandKey;
import ch.wellernet.zeus.server.service.communication.integrated.IntegratedControlUnitProperties.DriverMapping;
import ch.wellernet.zeus.server.service.communication.integrated.IntegratedControlUnitProperties.DriverMapping.DriverDefinition;
import ch.wellernet.zeus.server.service.communication.integrated.drivers.DeviceDriver;

@Configuration
@EnableConfigurationProperties(IntegratedControlUnitProperties.class)
public class IntegratedControlUnitConfiguration {

	private static final String DEVICE_DRIVER_BEAN_PREFIX = "deviceDriver.";

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private IntegratedControlUnitProperties properties;

	@Autowired
	private ControlUnitRepository controlUnitRepository;

	@Bean(IntegratedCommunicationService.NAME)
	public IntegratedCommunicationService integratedCommunicationService() {
		final Map<DeviceCommandKey, DeviceDriver> deviceDriverMapping = new HashMap<>();
		for (final DriverMapping driverMapping : properties.getDriverMappings()) {
			final Set<Command> supportedCommands = new HashSet<>();
			for (final DriverDefinition driverDefinition : driverMapping.getDrivers()) {
				final DeviceDriver deviceDriver = (DeviceDriver) applicationContext.getBean(
						DEVICE_DRIVER_BEAN_PREFIX + driverDefinition.getName(), driverDefinition.getProperties());
				supportedCommands.addAll(deviceDriver.getSupportedCommands());
				for (final Command command : deviceDriver.getSupportedCommands()) {
					deviceDriverMapping.put(new DeviceCommandKey(driverMapping.getDeviceId(), command), deviceDriver);
				}
			}
		}
		return new IntegratedCommunicationService(deviceDriverMapping);
	}

	@PostConstruct
	private void initializeIntegratedControlUnit() {
		ControlUnit integratedControlUnit = controlUnitRepository.findIntegrated().orElse(null);
		if (integratedControlUnit == null) {
			final List<Device> devices = new ArrayList<>();
			for (final DriverMapping driverMapping : properties.getDriverMappings()) {
				devices.add(new Device(driverMapping.getDeviceId(), driverMapping.getDeviceType(),
						format("Device %s", devices.size() + 1), null));
			}
			integratedControlUnit = new ControlUnit(properties.getId(), new IntegratedControlUnitAddress(), devices);
			for (final Device device : devices) {
				device.setControlUnit(integratedControlUnit);
			}
			controlUnitRepository.save(integratedControlUnit);
		}
	}
}
