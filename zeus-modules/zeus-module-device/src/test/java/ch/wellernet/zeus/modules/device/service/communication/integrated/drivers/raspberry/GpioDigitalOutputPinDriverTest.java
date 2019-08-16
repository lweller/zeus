package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.raspberry;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.PinState;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import lombok.Value;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;

import static ch.wellernet.zeus.modules.device.model.Command.*;
import static ch.wellernet.zeus.modules.device.model.State.OFF;
import static ch.wellernet.zeus.modules.device.model.State.ON;
import static ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.raspberry.GpioDigitalOutputPinDriver.ACTIVE_STATE_PROPERTY;
import static com.google.common.collect.Lists.newArrayList;
import static com.pi4j.io.gpio.PinState.*;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Answers.RETURNS_MOCKS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(classes = GpioDigitalOutputPinDriverTest.TestGpioDigitalOutputPinDriver.class, webEnvironment = NONE)
@RunWith(JUnitParamsRunner.class)
public class GpioDigitalOutputPinDriverTest {
  // activate spring
  public static final @ClassRule
  SpringClassRule springClassRule = new SpringClassRule();
  public final @Rule
  SpringMethodRule springMethodRule = new SpringMethodRule();
  // object under test
  private @Autowired
  TestGpioDigitalOutputPinDriver gpioDigitalOutputPinDriver;
  private @MockBean(answer = RETURNS_MOCKS)
  GpioController gpioController;
  private @MockBean
  TaskScheduler taskScheduler;

  @Test
  public void executeCommandGetStateShouldNotCancelPreviousScheduledTaskWhenOneExists()
      throws UndefinedCommandException, IllegalAccessException {
    // given
    final ScheduledFuture<?> timerTask = mock(ScheduledFuture.class);
    writeField(gpioDigitalOutputPinDriver, "timerTask", timerTask, true);

    // when
    gpioDigitalOutputPinDriver.execute(GET_SWITCH_STATE, null);

    // then
    verifyZeroInteractions(timerTask);
    assertThat(gpioDigitalOutputPinDriver.getTimerTask(), is(timerTask));
  }

  @Test
  @Parameters(method = "testParameterSets")
  @TestCaseName("[{index}] {0}")
  public void executeCommandGetStateShouldSetPinStateToInverseOfInitialState(final TestParameterSet testParameterSet)
      throws UndefinedCommandException {
    // given
    final PinState activePinState = testParameterSet.getActivePinState();
    final PinState initialPinState = testParameterSet.getInitialPinState();
    final Properties properties = new Properties();
    properties.setProperty(ACTIVE_STATE_PROPERTY, activePinState.getName());
    gpioDigitalOutputPinDriver.reinitializeForTest(properties);
    given(gpioDigitalOutputPinDriver.getProvisionedPin().getState()).willReturn(initialPinState);

    // when
    final State state = gpioDigitalOutputPinDriver.execute(GET_SWITCH_STATE, null);

    // then
    assertThat(state, is(testParameterSet.getInitialDeviceState()));
  }

  @Test
  @Parameters(method = "testParameterSets")
  @TestCaseName("[{index}] {0}")
  public void executeCommandOffShouldSetPinStateToInverseOfActiveState(final TestParameterSet testParameterSet)
      throws UndefinedCommandException {
    // given
    final PinState activePinState = testParameterSet.getActivePinState();
    final PinState finalPinState = getInverseState(activePinState);
    final Properties properties = new Properties();
    properties.setProperty(ACTIVE_STATE_PROPERTY, activePinState.getName());
    gpioDigitalOutputPinDriver.reinitializeForTest(properties);
    given(gpioDigitalOutputPinDriver.getProvisionedPin().getState()).willReturn(finalPinState);

    // when
    final State state = gpioDigitalOutputPinDriver.execute(SWITCH_OFF, null);

    // then
    verify(gpioDigitalOutputPinDriver.getProvisionedPin()).setState(getInverseState(activePinState));
    assertThat(state, is(OFF));
  }

  @Test
  @Parameters(method = "testParameterSets")
  @TestCaseName("[{index}] {0}")
  public void executeCommandOnShouldSetPinStateToActiveState(final TestParameterSet testParameterSet)
      throws UndefinedCommandException {
    // given
    final PinState activePinState = testParameterSet.getActivePinState();
    @SuppressWarnings("UnnecessaryLocalVariable") final PinState finalPinState = activePinState;
    final Properties properties = new Properties();
    properties.setProperty(ACTIVE_STATE_PROPERTY, activePinState.getName());
    gpioDigitalOutputPinDriver.reinitializeForTest(properties);
    given(gpioDigitalOutputPinDriver.getProvisionedPin().getState()).willReturn(finalPinState);

    // when
    final State state = gpioDigitalOutputPinDriver.execute(SWITCH_ON, null);

    // then
    verify(gpioDigitalOutputPinDriver.getProvisionedPin()).setState(activePinState);
    assertThat(state, is(ON));
  }

  @Test
  @Parameters(method = "testParameterSets")
  @TestCaseName("[{index}] {0}")
  public void executeCommandOnWTimerShouldSetPinStateToActiveStateAndScheduleTimer(
      final TestParameterSet testParameterSet) throws UndefinedCommandException {
    final long timerDelay = 1800;
    final PinState activePinState = testParameterSet.getActivePinState();
    @SuppressWarnings("UnnecessaryLocalVariable") final PinState finalPinState = activePinState;
    final Properties properties = new Properties();
    properties.setProperty(ACTIVE_STATE_PROPERTY, activePinState.getName());
    gpioDigitalOutputPinDriver.reinitializeForTest(properties);
    given(gpioDigitalOutputPinDriver.getProvisionedPin().getState()).willReturn(finalPinState);

    // when
    final State state = gpioDigitalOutputPinDriver.execute(Command.SWITCH_ON_W_TIMER, Long.toString(timerDelay));

    // then
    verify(gpioDigitalOutputPinDriver.getProvisionedPin()).setState(activePinState);
    final ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);
    verify(taskScheduler).schedule(any(Runnable.class), instantCaptor.capture());
    assertThat(instantCaptor.getValue().getEpochSecond() * 1000d,
        is(closeTo(currentTimeMillis() + timerDelay * 1000, 1000)));
    assertThat(state, is(ON));
  }

  @Test
  public void executeCommandToggleShouldCancelPreviousScheduledTaskWhenOneExists()
      throws UndefinedCommandException, IllegalAccessException {
    // given
    final ScheduledFuture<?> timerTask = mock(ScheduledFuture.class);
    writeField(gpioDigitalOutputPinDriver, "timerTask", timerTask, true);

    // when
    gpioDigitalOutputPinDriver.execute(TOGGLE_SWITCH, null);

    // then
    verify(timerTask).cancel(true);
    assertThat(gpioDigitalOutputPinDriver.getTimerTask(), is(nullValue()));
  }

  @Test
  @Parameters(method = "testParameterSets")
  @TestCaseName("[{index}] {0}")
  public void executeCommandToggleShouldSetPinStateToInverseOfInitialState(final TestParameterSet testParameterSet)
      throws UndefinedCommandException {
    // given
    final PinState activePinState = testParameterSet.getActivePinState();
    final PinState initialPinState = testParameterSet.getInitialPinState();
    final PinState finalPinState = getInverseState(initialPinState);
    final Properties properties = new Properties();
    properties.setProperty(ACTIVE_STATE_PROPERTY, activePinState.getName());
    gpioDigitalOutputPinDriver.reinitializeForTest(properties);
    given(gpioDigitalOutputPinDriver.getProvisionedPin().getState()).willReturn(initialPinState, finalPinState);

    // when
    final State state = gpioDigitalOutputPinDriver.execute(TOGGLE_SWITCH, null);

    // then
    verify(gpioDigitalOutputPinDriver.getProvisionedPin()).setState(finalPinState);
    assertThat(state, is(testParameterSet.getInverseInitialDeviceState()));
  }

  @SuppressWarnings("unused")
  List<TestParameterSet> testParameterSets() {
    return newArrayList(new TestParameterSet(LOW, LOW, ON), new TestParameterSet(LOW, HIGH, OFF),
        new TestParameterSet(HIGH, LOW, OFF), new TestParameterSet(HIGH, HIGH, ON));
  }

  // make class testable
  static class TestGpioDigitalOutputPinDriver extends GpioDigitalOutputPinDriver {

    public TestGpioDigitalOutputPinDriver(final TaskScheduler taskScheduler, final GpioController gpioController) {
      super(taskScheduler, new Properties() {
        private static final long serialVersionUID = 1L;

        {
          setProperty(PIN_PROPERTY, "1");
        }
      }, gpioController);
    }

    void reinitializeForTest(final Properties properties) {
      getProperties().putAll(properties);
      super.init();
    }
  }

  @Value
  static class TestParameterSet {
    private PinState activePinState;
    private PinState initialPinState;
    private State initialDeviceState;

    State getInverseInitialDeviceState() {
      return initialDeviceState == OFF ? ON : OFF;
    }

    @Override
    public String toString() {
      return String.format("active state: %s, initial state: %s", activePinState, initialPinState);
    }
  }
}