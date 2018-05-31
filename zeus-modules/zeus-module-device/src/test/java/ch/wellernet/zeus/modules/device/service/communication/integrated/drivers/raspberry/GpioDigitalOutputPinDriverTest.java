package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.raspberry;

import static ch.wellernet.zeus.modules.device.model.Command.GET_SWITCH_STATE;
import static ch.wellernet.zeus.modules.device.model.Command.SWITCH_OFF;
import static ch.wellernet.zeus.modules.device.model.Command.SWITCH_ON;
import static ch.wellernet.zeus.modules.device.model.Command.TOGGLE_SWITCH;
import static ch.wellernet.zeus.modules.device.model.State.OFF;
import static ch.wellernet.zeus.modules.device.model.State.ON;
import static ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.raspberry.GpioDigitalOutputPinDriver.ACTIVE_STATE_PROPERTY;
import static com.google.common.collect.Lists.newArrayList;
import static com.pi4j.io.gpio.PinState.HIGH;
import static com.pi4j.io.gpio.PinState.LOW;
import static com.pi4j.io.gpio.PinState.getInverseState;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Answers.RETURNS_MOCKS;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import java.util.List;
import java.util.Properties;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.PinState;

import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.UndefinedCommandException;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.raspberry.GpioDigitalOutputPinDriver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import lombok.Value;

@SpringBootTest(classes = GpioDigitalOutputPinDriverTest.TestGpioDigitalOutputPinDriver.class, webEnvironment = NONE)
@RunWith(JUnitParamsRunner.class)
public class GpioDigitalOutputPinDriverTest {
	// make class testable
	static class TestGpioDigitalOutputPinDriver extends GpioDigitalOutputPinDriver {

		public TestGpioDigitalOutputPinDriver() {
			super(new Properties() {
				private static final long serialVersionUID = 1L;

				{
					setProperty(PIN_PROPERTY, "1");
				}
			});
		}

		public void reinitForTest(final Properties properties) {
			getProperties().putAll(properties);
			super.init();
		}
	}

	@Value
	static class TestParameterSet {
		private PinState activePinState;
		private PinState initialPinState;
		private State initialDeviceState;

		public State getInverseInitialDeviceState() {
			return initialDeviceState == OFF ? ON : OFF;
		}

		@Override
		public String toString() {
			return String.format("active state: %s, initial state: %s", activePinState, initialPinState);
		}
	}

	// activate spring
	public static final @ClassRule SpringClassRule springClassRule = new SpringClassRule();
	public final @Rule SpringMethodRule springMethodRule = new SpringMethodRule();

	// object under test
	private @Autowired TestGpioDigitalOutputPinDriver gpioDigitalOutputPinDriver;

	private @MockBean(answer = RETURNS_MOCKS) GpioController gpioController;

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
		gpioDigitalOutputPinDriver.reinitForTest(properties);
		given(gpioDigitalOutputPinDriver.getProvisionedPin().getState()).willReturn(initialPinState);

		// when
		final State state = gpioDigitalOutputPinDriver.execute(GET_SWITCH_STATE);

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
		gpioDigitalOutputPinDriver.reinitForTest(properties);
		given(gpioDigitalOutputPinDriver.getProvisionedPin().getState()).willReturn(finalPinState);

		// when
		final State state = gpioDigitalOutputPinDriver.execute(SWITCH_OFF);

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
		final PinState finalPinState = activePinState;
		final Properties properties = new Properties();
		properties.setProperty(ACTIVE_STATE_PROPERTY, activePinState.getName());
		gpioDigitalOutputPinDriver.reinitForTest(properties);
		given(gpioDigitalOutputPinDriver.getProvisionedPin().getState()).willReturn(finalPinState);

		// when
		final State state = gpioDigitalOutputPinDriver.execute(SWITCH_ON);

		// then
		verify(gpioDigitalOutputPinDriver.getProvisionedPin()).setState(activePinState);
		assertThat(state, is(ON));
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
		gpioDigitalOutputPinDriver.reinitForTest(properties);
		given(gpioDigitalOutputPinDriver.getProvisionedPin().getState()).willReturn(initialPinState, finalPinState);

		// when
		final State state = gpioDigitalOutputPinDriver.execute(TOGGLE_SWITCH);

		// then
		verify(gpioDigitalOutputPinDriver.getProvisionedPin()).setState(finalPinState);
		assertThat(state, is(testParameterSet.getInverseInitialDeviceState()));
	}

	List<TestParameterSet> testParameterSets() {
		return newArrayList(new TestParameterSet(LOW, LOW, ON), new TestParameterSet(LOW, HIGH, OFF),
				new TestParameterSet(HIGH, LOW, OFF), new TestParameterSet(HIGH, HIGH, ON));
	}
}