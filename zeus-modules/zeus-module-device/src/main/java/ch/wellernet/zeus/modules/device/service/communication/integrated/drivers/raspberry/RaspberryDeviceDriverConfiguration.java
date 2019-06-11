package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.raspberry;

import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.DeviceDriver;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;

import java.util.Properties;

@Configuration
@Profile("device.raspberry")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RaspberryDeviceDriverConfiguration {

  // injected dependencies
  private final TaskScheduler taskScheduler;
  private final GpioController gpioController;

  @Bean
  public GpioController gpioController() {
    return GpioFactory.getInstance();
  }

  @Bean(GpioDigitalOutputPinDriver.BEAN_NAME)
  @Scope("prototype")
  public DeviceDriver gpioDigitalOutputPin(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") Properties properties) {
    return new GpioDigitalOutputPinDriver(taskScheduler, properties, gpioController);
  }
}
