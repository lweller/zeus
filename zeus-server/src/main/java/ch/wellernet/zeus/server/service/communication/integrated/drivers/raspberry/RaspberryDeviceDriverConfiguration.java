package ch.wellernet.zeus.server.service.communication.integrated.drivers.raspberry;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import ch.wellernet.zeus.server.service.communication.integrated.drivers.DeviceDriver;

@Configuration
@Profile("device.raspberry")
public class RaspberryDeviceDriverConfiguration {

	@Bean
	@Lazy
	public GpioController gpioController() {
		return GpioFactory.getInstance();
	}

	@Bean("deviceDriver.raspberry.GpioDigitalOutputPinDriver")
	@Lazy
	public DeviceDriver gpioDigitalOutputPin(Properties properties) {
		return new GpioDigitalOutputPinDriver(properties);
	}
}
