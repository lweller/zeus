package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.raspberry;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.DeviceDriver;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;

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
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
public class GpioDigitalOutputPinDriver implements DeviceDriver {

  private static final PinState DEFAULT_ACTIVE_STATE = LOW;
  static final String PIN_PROPERTY = "pin";
  static final String ACTIVE_STATE_PROPERTY = "active-state";
  static final String BEAN_NAME = "deviceDriver.raspberry.GpioDigitalOutputPinDriver";

  private @Getter final Collection<Command> supportedCommands;
  private @Getter PinState activePinState;
  private @Getter GpioPinDigitalOutput provisionedPin;
  private @Getter ScheduledFuture<?> timerTask;

  // injected dependencies
  private final GpioController gpioController;
  private final TaskScheduler taskScheduler;
  private @Getter final Properties properties;

  GpioDigitalOutputPinDriver(final TaskScheduler taskScheduler, final Properties properties, final GpioController gpioController) {
    this.taskScheduler = taskScheduler;
    this.properties = properties;
    this.gpioController = gpioController;
    supportedCommands = immutableEnumSet(SWITCH_ON, SWITCH_OFF, TOGGLE_SWITCH, GET_SWITCH_STATE);
  }

  /**
   * @see ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.DeviceDriver#execute(ch.wellernet.zeus.modules.device.model.Command,
   * java.lang.String)
   */
  @Override
  public synchronized State execute(final Command command, final String data) throws UndefinedCommandException {
    switch (command) {
      case SWITCH_ON:
        setPinState(activePinState);
        break;
      case SWITCH_ON_W_TIMER:
        setPinState(activePinState);
        final String[] args = data.split("\\s");
        final long timer;
        if (args.length > 0) {
          timer = parseLong(args[0]);
        } else {
          timer = 0;
        }
        timerTask = taskScheduler.schedule(() -> setPinState(getInverseState(activePinState)), Instant.now().plus(timer, SECONDS));
        break;
      case SWITCH_OFF:
        setPinState(getInverseState(activePinState));
        break;
      case TOGGLE_SWITCH:
        setPinState(getInverseState(provisionedPin.getState()));
        break;
      case GET_SWITCH_STATE:
        break;
      default:
        throw new UndefinedCommandException(
            format("Command %s is undefined in driver %s.", command, this.getClass().getSimpleName()));
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

  private synchronized void setPinState(final PinState pinState) {
    log.info(format("setting pin %s to %s", provisionedPin.getName(), pinState));
    provisionedPin.setState(pinState);
    if (timerTask != null) {
      timerTask.cancel(true);
      timerTask = null;
    }
  }
}
