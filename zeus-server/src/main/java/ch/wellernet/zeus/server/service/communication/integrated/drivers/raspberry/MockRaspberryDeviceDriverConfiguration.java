package ch.wellernet.zeus.server.service.communication.integrated.drivers.raspberry;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import ch.wellernet.zeus.server.service.communication.integrated.drivers.DeviceDriver;

@Configuration
@Profile("device.mock")
public class MockRaspberryDeviceDriverConfiguration {

	@Bean(GpioDigitalOutputPinDriver.BEAN_NAME)
	@Scope("prototype")
	public DeviceDriver gpioDigitalOutputPin(Properties properties) {
		return new MockGpioDigitalOutputPinDriver(properties);
	}
}
