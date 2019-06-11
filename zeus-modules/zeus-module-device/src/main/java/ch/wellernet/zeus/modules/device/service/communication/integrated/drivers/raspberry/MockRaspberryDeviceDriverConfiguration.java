package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.raspberry;

import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.DeviceDriver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;

import java.util.Properties;

@Configuration
@Profile("device.mock")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MockRaspberryDeviceDriverConfiguration {

  // injected dependencies
  private final TaskScheduler taskScheduler;

  @Bean(GpioDigitalOutputPinDriver.BEAN_NAME)
  @Scope("prototype")
  public DeviceDriver gpioDigitalOutputPin(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") Properties properties) {
    return new MockGpioDigitalOutputPinDriver(taskScheduler, properties);
  }
}
