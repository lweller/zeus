package ch.wellernet.zeus.server.service.communication.integrated.drivers.raspberry;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import ch.wellernet.zeus.server.service.communication.integrated.drivers.DeviceDriver;

@Configuration
@Profile("device.mock")
public class MockRaspberryDeviceDriverConfiguration {

	@Bean("deviceDriver.raspberry.GpioDigitalOutputPinDriver")
	@Lazy
	public DeviceDriver gpioDigitalOutputPin(Properties properties) {
		return new MockGpioDigitalOutputPinDriver(properties);
	}
}
