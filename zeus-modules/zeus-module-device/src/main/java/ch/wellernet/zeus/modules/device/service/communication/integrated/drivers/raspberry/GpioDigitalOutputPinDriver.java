package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.raspberry;

import static ch.wellernet.zeus.modules.device.model.Command.GET_SWITCH_STATE;
import static ch.wellernet.zeus.modules.device.model.Command.SWITCH_OFF;
import static ch.wellernet.zeus.modules.device.model.Command.SWITCH_ON;
import static ch.wellernet.zeus.modules.device.model.Command.TOGGLE_SWITCH;
import static ch.wellernet.zeus.modules.device.model.State.OFF;
import static ch.wellernet.zeus.modules.device.model.State.ON;
import static com.google.common.collect.Sets.immutableEnumSet;
import static com.pi4j.io.gpio.PinState.LOW;
import static com.pi4j.io.gpio.PinState.getInverseState;
import static com.pi4j.io.gpio.RaspiPin.getPinByAddress;
import static java.lang.String.format;

import java.util.Collection;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.DeviceDriver;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.UndefinedCommandException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GpioDigitalOutputPinDriver implements DeviceDriver {

	public static final PinState DEFAULT_ACTIVE_STATE = LOW;

	static final String BEAN_NAME = "deviceDriver.raspberry.GpioDigitalOutputPinDriver";

	public static final String PIN_PROPERTY = "pin";
	public static final String ACTIVE_STATE_PROPERTY = "active-state";

	@Autowired
	private GpioController gpioController;

	@Getter
	private final Properties properties;

	@Getter
	private PinState activePinState;

	@Getter
	private GpioPinDigitalOutput provisionedPin;

	@Getter
	private final Collection<Command> supportedCommands;

	public GpioDigitalOutputPinDriver(final Properties properties) {
		this.properties = properties;
		supportedCommands = immutableEnumSet(SWITCH_ON, SWITCH_OFF, TOGGLE_SWITCH, GET_SWITCH_STATE);
	}

	/**
	 * @see ch.wellernet.zeus.server.drivers.raspberrypi.DeviceDriver#execute(ch.wellernet.zeus.modules.device.model.Command)
	 */
	@Override
	public State execute(final Command command) throws UndefinedCommandException {
		final PinState targetPinState;
		switch (command) {
		case SWITCH_ON:
			targetPinState = activePinState;
			break;
		case SWITCH_OFF:
			targetPinState = getInverseState(activePinState);
			break;
		case TOGGLE_SWITCH:
			targetPinState = getInverseState(provisionedPin.getState());
			break;
		case GET_SWITCH_STATE:
			targetPinState = null;
			break;
		default:
			throw new UndefinedCommandException(
					format("Commdand %s is undefined in driver %s.", command, this.getClass().getSimpleName()));
		}
		if (targetPinState != null) {
			log.info(format("setting pin %s to %s", provisionedPin.getName(), targetPinState));
			provisionedPin.setState(targetPinState);
		}
		return provisionedPin.getState() == activePinState ? ON : OFF;
	}

	/**
	 * @see ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.DeviceDriver#init()
	 */
	@Override
	@PostConstruct
	public void init() {
		activePinState = PinState.valueOf(properties.getProperty(ACTIVE_STATE_PROPERTY, DEFAULT_ACTIVE_STATE.name()));
		final Pin pin = getPinByAddress(Integer.valueOf(properties.getProperty(PIN_PROPERTY)));
		provisionedPin = gpioController.provisionDigitalOutputPin(pin, getInverseState(activePinState));
		provisionedPin.setShutdownOptions(true, getInverseState(activePinState));
	}
}
