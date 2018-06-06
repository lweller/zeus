package ch.wellernet.zeus.modules.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ch.wellernet.zeus.modules.device.service.communication.integrated.IntegratedControlUnitConfiguration;

@Configuration
public class DeviceModuleConfiguration {

	private @Autowired IntegratedControlUnitConfiguration integratedControlUnitConfiguration;

	public void init() {
		integratedControlUnitConfiguration.initializeIntegratedControlUnit();
	}
}
