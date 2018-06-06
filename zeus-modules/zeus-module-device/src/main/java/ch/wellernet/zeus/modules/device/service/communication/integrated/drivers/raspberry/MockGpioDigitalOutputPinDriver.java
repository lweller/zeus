package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.raspberry;

import static ch.wellernet.zeus.modules.device.model.Command.GET_SWITCH_STATE;
import static ch.wellernet.zeus.modules.device.model.Command.SWITCH_OFF;
import static ch.wellernet.zeus.modules.device.model.Command.SWITCH_ON;
import static ch.wellernet.zeus.modules.device.model.Command.TOGGLE_SWITCH;
import static ch.wellernet.zeus.modules.device.model.State.OFF;
import static ch.wellernet.zeus.modules.device.model.State.ON;
import static com.google.common.collect.Sets.immutableEnumSet;
import static com.pi4j.io.gpio.PinState.HIGH;
import static com.pi4j.io.gpio.PinState.getInverseState;
import static com.pi4j.io.gpio.RaspiPin.getPinByAddress;
import static java.lang.String.format;

import java.util.Collection;
import java.util.Properties;

import javax.annotation.PostConstruct;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.DeviceDriver;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.UndefinedCommandException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockGpioDigitalOutputPinDriver implements DeviceDriver {

	public static final String PIN_PROPERTY = "pin";
	public static final String ACTIVE_STATE_PROPERTY = "active-state";

	@Getter
	private final Properties properties;

	@Getter
	private Pin pin;

	@Getter
	private PinState activePinState;

	@Getter
	private PinState currentState;

	@Getter
	private final Collection<Command> supportedCommands;

	public MockGpioDigitalOutputPinDriver(final Properties properties) {
		this.properties = properties;
		supportedCommands = immutableEnumSet(SWITCH_ON, SWITCH_OFF, TOGGLE_SWITCH, GET_SWITCH_STATE);
	}

	/**
	 * @see ch.wellernet.zeus.server.drivers.raspberrypi.DeviceDriver#execute(ch.wellernet.zeus.modules.device.model.Command)
	 */
	@Override
	public State execute(final Command command) throws UndefinedCommandException {
		switch (command) {
		case SWITCH_ON:
			currentState = activePinState;
			log.info(String.format("setting pin %s to %s", pin, currentState));
			break;
		case SWITCH_OFF:
			currentState = getInverseState(activePinState);
			log.info(String.format("setting pin %s to %s", pin, currentState));
			break;
		case TOGGLE_SWITCH:
			currentState = getInverseState(currentState);
			log.info(String.format("setting pin %s to %s", pin, currentState));
			break;
		case GET_SWITCH_STATE:
			log.info(String.format("pin %s is %s", pin, currentState));
			break;
		default:
			throw new UndefinedCommandException(
					format("Commdand %s is undefined in driver %s.", command, this.getClass().getSimpleName()));
		}
		return currentState == activePinState ? ON : OFF;
	}

	/**
	 * @see ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.DeviceDriver#init()
	 */
	@Override
	@PostConstruct
	public void init() {
		pin = getPinByAddress(Integer.valueOf(properties.getProperty(PIN_PROPERTY)));
		activePinState = PinState.valueOf(properties.getProperty(ACTIVE_STATE_PROPERTY, HIGH.name()));
		currentState = getInverseState(activePinState);
	}
}
