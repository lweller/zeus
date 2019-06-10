package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.raspberry;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.DeviceDriver;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;

import static ch.wellernet.zeus.modules.device.model.Command.*;
import static ch.wellernet.zeus.modules.device.model.State.OFF;
import static ch.wellernet.zeus.modules.device.model.State.ON;
import static com.google.common.collect.Sets.immutableEnumSet;
import static com.pi4j.io.gpio.PinState.HIGH;
import static com.pi4j.io.gpio.PinState.getInverseState;
import static com.pi4j.io.gpio.RaspiPin.getPinByAddress;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
public class MockGpioDigitalOutputPinDriver implements DeviceDriver {

  public static final String PIN_PROPERTY = "pin";
  public static final String ACTIVE_STATE_PROPERTY = "active-state";
  private @Getter final Properties properties;
  private @Getter final Collection<Command> supportedCommands;
  private @Autowired TaskScheduler taskScheduler;
  private @Getter Pin pin;
  private @Getter PinState activePinState;
  private @Getter PinState currentState;
  private @Getter ScheduledFuture<?> timerTask;

  public MockGpioDigitalOutputPinDriver(final Properties properties) {
    this.properties = properties;
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
        final String[] agrs = data.split("\\s");
        final long timer;
        if (agrs.length > 0) {
          timer = parseLong(agrs[0]);
        } else {
          timer = 0;
        }
        timerTask = taskScheduler.schedule((Runnable) () -> {
          setPinState(getInverseState(activePinState));
        }, Instant.now().plus(timer, SECONDS));
        break;
      case SWITCH_OFF:
        setPinState(getInverseState(activePinState));
        break;
      case TOGGLE_SWITCH:
        setPinState(getInverseState(currentState));
        break;
      case GET_SWITCH_STATE:
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

  private synchronized void setPinState(final PinState pinState) {
    log.info(format("setting pin %s to %s", pin.getName(), pinState));
    currentState = pinState;
    if (timerTask != null) {
      timerTask.cancel(true);
      timerTask = null;
    }
  }
}
