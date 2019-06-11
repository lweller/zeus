package ch.wellernet.zeus.modules.device;

import ch.wellernet.zeus.modules.device.service.communication.integrated.IntegratedControlUnitConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DeviceModuleConfiguration {

  // injected dependencies
  private final IntegratedControlUnitConfiguration integratedControlUnitConfiguration;

  public void init() {
    integratedControlUnitConfiguration.initializeIntegratedControlUnit();
  }
}
