package ch.wellernet.zeus.modules.device;

import ch.wellernet.zeus.modules.device.service.communication.integrated.IntegratedControlUnitConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeviceModuleConfiguration {

  private @Autowired IntegratedControlUnitConfiguration integratedControlUnitConfiguration;

  public void init() {
    integratedControlUnitConfiguration.initializeIntegratedControlUnit();
  }
}
