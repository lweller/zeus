package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.raspberry;

import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.DeviceDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import java.util.Properties;

@Configuration
@Profile("device.mock")
public class MockRaspberryDeviceDriverConfiguration {

  @Bean(GpioDigitalOutputPinDriver.BEAN_NAME)
  @Scope("prototype")
  public DeviceDriver gpioDigitalOutputPin(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") Properties properties) {
    return new MockGpioDigitalOutputPinDriver(properties);
  }
}
