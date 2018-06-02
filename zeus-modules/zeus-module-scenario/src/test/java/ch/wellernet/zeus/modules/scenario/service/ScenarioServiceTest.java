package ch.wellernet.zeus.modules.scenario.service;

import static ch.wellernet.zeus.modules.device.model.BuiltInDeviceType.GENERIC_SWITCH;
import static ch.wellernet.zeus.modules.device.model.Command.SWITCH_ON;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.service.DeviceService;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.UndefinedCommandException;
import ch.wellernet.zeus.modules.scenario.model.Arc;
import ch.wellernet.zeus.modules.scenario.model.AutomaticTransition;
import ch.wellernet.zeus.modules.scenario.model.EventDrivenTransition;
import ch.wellernet.zeus.modules.scenario.model.InhibitionArc;
import ch.wellernet.zeus.modules.scenario.model.InputArc;
import ch.wellernet.zeus.modules.scenario.model.OutputArc;
import ch.wellernet.zeus.modules.scenario.model.SendCommandAction;
import ch.wellernet.zeus.modules.scenario.model.State;
import ch.wellernet.zeus.modules.scenario.model.Transition;
import ch.wellernet.zeus.modules.scenario.repository.StateRepository;
import ch.wellernet.zeus.modules.scenario.repository.TransitionRepository;

@SpringBootTest(classes = ScenarioService.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class ScenarioServiceTest {

	// object under test
	private @SpyBean ScenarioService scenarioService;

	private @MockBean TransitionRepository transitionRepository;
	private @MockBean StateRepository stateRepository;
	private @MockBean DeviceService deviceService;

	@Test
	public void canFireInhibitionArcShouldReturnFalseWhenStateNotSet() {
		// given
		final InhibitionArc inhibitionArc = InhibitionArc.builder().state(null).weight(1).build();

		// when
		final boolean result = scenarioService.canFireInhibitionArc(inhibitionArc);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireInhibitionArcShouldReturnFalseWhenWeightIsReached() {
		// given
		final State state = State.builder().initialCount(1).build();
		final InhibitionArc inhibitionArc = InhibitionArc.builder().state(state).weight(1).build();

		// when
		final boolean result = scenarioService.canFireInhibitionArc(inhibitionArc);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireInhibitionArcShouldReturnTrueWhenWeightIsNotReached() {
		// given
		final State state = State.builder().initialCount(0).build();
		final InhibitionArc inhibitionArc = InhibitionArc.builder().state(state).weight(1).build();

		// when
		final boolean result = scenarioService.canFireInhibitionArc(inhibitionArc);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void canFireInputArcShouldReturnFalseWhenNotEnoughTokenAvailable() {
		// given
		final State state = State.builder().initialCount(0).build();
		final InputArc inputArc = InputArc.builder().state(state).weight(1).build();

		// when
		final boolean result = scenarioService.canFireInputArc(inputArc);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireInputArcShouldReturnFalseWhenstateNotSet() {
		// given
		final InputArc inputArc = InputArc.builder().state(null).weight(1).build();

		// when
		final boolean result = scenarioService.canFireInputArc(inputArc);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireInputArcShouldReturnTrueWhenEnoughTokenAvailable() {
		// given
		final State state = State.builder().initialCount(1).build();
		final InputArc inputArc = InputArc.builder().state(state).weight(1).build();

		// when
		final boolean result = scenarioService.canFireInputArc(inputArc);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void canFireOutputArcShouldReturnFalseWhenNotEnoughSpaceLeft() {
		// given
		final State state = State.builder().maxCount(1).initialCount(1).build();
		final OutputArc outputArc = OutputArc.builder().state(state).weight(1).build();

		// when
		final boolean result = scenarioService.canFireOutputArc(outputArc);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireOutputArcShouldReturnFalseWhenstateNotSet() {
		// given
		final OutputArc outputArc = OutputArc.builder().state(null).weight(1).build();

		// when
		final boolean result = scenarioService.canFireOutputArc(outputArc);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireOutputArcShouldReturnTrueWhenEnoughSpaceLeft() {
		// given
		final State state = State.builder().maxCount(1).initialCount(0).build();
		final OutputArc outputArc = OutputArc.builder().state(state).weight(1).build();

		// when
		final boolean result = scenarioService.canFireOutputArc(outputArc);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void canFireTransitionShouldReturnFalseWhenAtLeastOneInhibitionArcCannotFire() {
		// given
		doReturn(true).when(scenarioService).canFireInputArc(any());
		doReturn(true).when(scenarioService).canFireOutputArc(any());
		doReturn(true, false).when(scenarioService).canFireInhibitionArc(any());
		final Transition transition = AutomaticTransition.builder()
				.arcs(newHashSet((Arc) InputArc.builder().build(), (Arc) InputArc.builder().build(),
						(Arc) OutputArc.builder().build(), (Arc) OutputArc.builder().build(),
						(Arc) InhibitionArc.builder().build(), (Arc) InhibitionArc.builder().build()))
				.build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireTransitionShouldReturnFalseWhenAtLeastOneInputArcCannotFire() {
		// given
		doReturn(true, false).when(scenarioService).canFireInputArc(any());
		doReturn(true).when(scenarioService).canFireOutputArc(any());
		doReturn(true).when(scenarioService).canFireInhibitionArc(any());
		final Transition transition = AutomaticTransition.builder()
				.arcs(newHashSet((Arc) InputArc.builder().build(), (Arc) InputArc.builder().build(),
						(Arc) OutputArc.builder().build(), (Arc) OutputArc.builder().build(),
						(Arc) InhibitionArc.builder().build(), (Arc) InhibitionArc.builder().build()))
				.build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireTransitionShouldReturnFalseWhenAtLeastOneOutputArcCannotFire() {
		// given
		doReturn(true).when(scenarioService).canFireInputArc(any());
		doReturn(true, false).when(scenarioService).canFireOutputArc(any());
		doReturn(true).when(scenarioService).canFireInhibitionArc(any());
		final Transition transition = AutomaticTransition.builder()
				.arcs(newHashSet((Arc) InputArc.builder().build(), (Arc) InputArc.builder().build(),
						(Arc) OutputArc.builder().build(), (Arc) OutputArc.builder().build(),
						(Arc) InhibitionArc.builder().build(), (Arc) InhibitionArc.builder().build()))
				.build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireTransitionShouldReturnTrueWhenAllArcsCanFire() {
		// given
		doReturn(true).when(scenarioService).canFireInputArc(any());
		doReturn(true).when(scenarioService).canFireOutputArc(any());
		doReturn(true).when(scenarioService).canFireInhibitionArc(any());
		final Transition transition = AutomaticTransition.builder()
				.arcs(newHashSet((Arc) InputArc.builder().build(), (Arc) InputArc.builder().build(),
						(Arc) OutputArc.builder().build(), (Arc) OutputArc.builder().build(),
						(Arc) InhibitionArc.builder().build(), (Arc) InhibitionArc.builder().build()))
				.build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void canFireTransitionShouldReturnTrueWhenNoInhibitionArcIsDefined() {
		// given
		doReturn(true).when(scenarioService).canFireInputArc(any());
		doReturn(true).when(scenarioService).canFireOutputArc(any());
		final Transition transition = AutomaticTransition.builder().arcs(newHashSet((Arc) InputArc.builder().build(),
				(Arc) InputArc.builder().build(), (Arc) OutputArc.builder().build(), (Arc) OutputArc.builder().build()))
				.build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void canFireTransitionShouldReturnTrueWhenNoInputArcIsDefined() {
		// given
		doReturn(true).when(scenarioService).canFireOutputArc(any());
		doReturn(true).when(scenarioService).canFireInhibitionArc(any());
		final Transition transition = AutomaticTransition.builder()
				.arcs(newHashSet((Arc) OutputArc.builder().build(), (Arc) OutputArc.builder().build(),
						(Arc) InhibitionArc.builder().build(), (Arc) InhibitionArc.builder().build()))
				.build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void canFireTransitionShouldReturnTrueWhenNoOutputArcIsDefined() {
		// given
		doReturn(true).when(scenarioService).canFireInputArc(any());
		doReturn(true).when(scenarioService).canFireInhibitionArc(any());
		final Transition transition = AutomaticTransition.builder()
				.arcs(newHashSet((Arc) InputArc.builder().build(), (Arc) InputArc.builder().build(),
						(Arc) InhibitionArc.builder().build(), (Arc) InhibitionArc.builder().build()))
				.build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void fireTransitionShouldChangeNothingWhenIfItCannotFire() {
		// given
		doReturn(false).when(scenarioService).canFireTransition(any());
		final State inputState = State.builder().initialCount(1).build();
		final State outputState = State.builder().initialCount(0).build();
		final Transition transition = AutomaticTransition.builder()
				.arcs(newHashSet((Arc) InputArc.builder().state(inputState).build(),
						(Arc) OutputArc.builder().state(outputState).build()))
				.actions(newHashSet(SendCommandAction.builder().build())).build();
		given(transitionRepository.findById(transition.getId())).willReturn(Optional.of(transition));

		// when
		scenarioService.fireTransition(transition.getId());

		// then
		assertThat(inputState.getCount(), is(1));
		assertThat(outputState.getCount(), is(0));
		verifyZeroInteractions(stateRepository);
		verifyZeroInteractions(deviceService);
	}

	@Test(expected = RuntimeException.class)
	public void fireTransitionShouldThrowRuntimeExceptionWhenActionFails() throws UndefinedCommandException {
		// given
		doReturn(true).when(scenarioService).canFireTransition(any());
		final State inputState = State.builder().initialCount(2).build();
		final State outputState = State.builder().initialCount(0).build();
		final Device device = Device.builder().type(GENERIC_SWITCH).build();
		final Command command = SWITCH_ON;
		final Transition transition = AutomaticTransition.builder()
				.arcs(newHashSet((Arc) InputArc.builder().weight(2).state(inputState).build(),
						(Arc) OutputArc.builder().weight(3).state(outputState).build()))
				.actions(newHashSet(SendCommandAction.builder().device(device).command(command).build())).build();
		given(transitionRepository.findById(transition.getId())).willReturn(Optional.of(transition));
		given(deviceService.sendCommand(device, command)).willThrow(new UndefinedCommandException("command not found"));

		// when
		scenarioService.fireTransition(transition.getId());

		// then an exception is expected
	}

	@Test
	public void fireTransitionShouldTransferTokensAndExecuteActionsAndRecusivelyFireIfItCanFire()
			throws UndefinedCommandException {
		// given
		final Transition nextAutomaticTransition = AutomaticTransition.builder().build();
		final Transition nextEventDrivenTransisiton = EventDrivenTransition.builder().build();
		doReturn(true).when(scenarioService).canFireTransition(any());
		final State inputState = State.builder().initialCount(2).build();
		final State outputState = State.builder().initialCount(0)
				.arcs(newHashSet(InputArc.builder().transition(nextAutomaticTransition).build(),
						InputArc.builder().transition(nextEventDrivenTransisiton).build()))
				.build();
		final Device device = Device.builder().type(GENERIC_SWITCH).build();
		final Command command = SWITCH_ON;
		final Transition transition = AutomaticTransition.builder()
				.arcs(newHashSet((Arc) InputArc.builder().weight(2).state(inputState).build(),
						(Arc) OutputArc.builder().weight(3).state(outputState).build()))
				.actions(newHashSet(SendCommandAction.builder().device(device).command(command).build())).build();
		given(transitionRepository.findById(transition.getId())).willReturn(Optional.of(transition));

		// when
		scenarioService.fireTransition(transition.getId());

		// then
		// tokens should have been withdrawn from input state
		assertThat(inputState.getCount(), is(0));
		verify(stateRepository).save(inputState);
		// tokens should have been added to output state
		assertThat(outputState.getCount(), is(3));
		verify(stateRepository).save(outputState);
		// actions should have executed
		verify(deviceService).sendCommand(device, command);
		// next automatic transition should have been called recursively
		verify(scenarioService).fireTransition(nextAutomaticTransition);
		// but not next event driven transition
		verify(scenarioService, never()).fireTransition(nextEventDrivenTransisiton);
	}
}