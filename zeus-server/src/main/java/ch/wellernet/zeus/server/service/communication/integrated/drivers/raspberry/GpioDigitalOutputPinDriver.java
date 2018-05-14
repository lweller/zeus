package ch.wellernet.zeus.server.service.communication.integrated.drivers.raspberry;

import static ch.wellernet.zeus.server.model.Command.GET_SWITCH_STATE;
import static ch.wellernet.zeus.server.model.Command.SWITCH_OFF;
import static ch.wellernet.zeus.server.model.Command.SWITCH_ON;
import static ch.wellernet.zeus.server.model.Command.TOGGLE_SWITCH;
import static ch.wellernet.zeus.server.model.State.OFF;
import static ch.wellernet.zeus.server.model.State.ON;
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

import ch.wellernet.zeus.server.model.Command;
import ch.wellernet.zeus.server.model.State;
import ch.wellernet.zeus.server.service.communication.integrated.drivers.DeviceDriver;
import ch.wellernet.zeus.server.service.communication.integrated.drivers.UndefinedCommandException;
import lombok.Getter;

public class GpioDigitalOutputPinDriver implements DeviceDriver {

	public static final String PIN_PROPERTY = "pin";
	public static final String ACTIVE_STATE_PROPERTY = "active-state";

	@Autowired
	private GpioController gpioController;

	@Getter
	private Properties properties;

	@Getter
	private PinState activePinState;

	@Getter
	private GpioPinDigitalOutput provisionedPin;

	@Getter
	private final Collection<Command> supportedCommands;

	public GpioDigitalOutputPinDriver(Properties properties) {
		this.properties = properties;
		supportedCommands = immutableEnumSet(SWITCH_ON, SWITCH_OFF, TOGGLE_SWITCH, GET_SWITCH_STATE);
	}

	/**
	 * @see ch.wellernet.zeus.server.service.communication.integrated.drivers.DeviceDriver#init()
	 */
	@Override
	@PostConstruct
	public void init() {
		this.activePinState = PinState.valueOf(properties.getProperty(ACTIVE_STATE_PROPERTY, LOW.name()));
		Pin pin = getPinByAddress(Integer.valueOf(properties.getProperty(PIN_PROPERTY)));
		provisionedPin = gpioController.provisionDigitalOutputPin(pin, getInverseState(activePinState));
		provisionedPin.setShutdownOptions(true, getInverseState(activePinState));
	}

	/**
	 * @see ch.wellernet.zeus.server.drivers.raspberrypi.DeviceDriver#execute(ch.
	 *      wellernet.zeus.server.model.Command)
	 */
	@Override
	public State execute(Command command) throws UndefinedCommandException {
		switch (command) {
		case SWITCH_ON:
			provisionedPin.setState(activePinState);
			break;
		case SWITCH_OFF:
			provisionedPin.setState(getInverseState(activePinState));
			break;
		case TOGGLE_SWITCH:
			provisionedPin.setState(getInverseState(provisionedPin.getState()));
			break;
		case GET_SWITCH_STATE:
			break;
		default:
			throw new UndefinedCommandException(
					format("Commdand %s is undefined in driver %s.", command, this.getClass().getSimpleName()));
		}
		return provisionedPin.getState() == activePinState ? ON : OFF;
	}
}
