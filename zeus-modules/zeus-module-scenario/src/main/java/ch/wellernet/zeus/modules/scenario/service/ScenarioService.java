package ch.wellernet.zeus.modules.scenario.service;

import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.NoSuchElementException;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.wellernet.zeus.modules.device.service.DeviceService;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationInterruptedException;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationNotSuccessfulException;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import ch.wellernet.zeus.modules.scenario.model.Action;
import ch.wellernet.zeus.modules.scenario.model.Arc;
import ch.wellernet.zeus.modules.scenario.model.InhibitionArc;
import ch.wellernet.zeus.modules.scenario.model.InputArc;
import ch.wellernet.zeus.modules.scenario.model.OutputArc;
import ch.wellernet.zeus.modules.scenario.model.Scenario;
import ch.wellernet.zeus.modules.scenario.model.SendCommandAction;
import ch.wellernet.zeus.modules.scenario.model.State;
import ch.wellernet.zeus.modules.scenario.model.Transition;
import ch.wellernet.zeus.modules.scenario.repository.ScenarioRepository;
import ch.wellernet.zeus.modules.scenario.repository.StateRepository;
import ch.wellernet.zeus.modules.scenario.repository.TransitionRepository;

@Service
@Transactional(MANDATORY)
public class ScenarioService {

	private @Autowired ScenarioRepository scenarioRepository;
	private @Autowired TransitionRepository transitionRepository;
	private @Autowired StateRepository stateRepository;

	private @Autowired DeviceService deviceService;

	public void create(final Scenario scenario) {
		scenarioRepository.save(scenario);
	}

	public void fireTransition(final UUID transitionId) throws NoSuchElementException {
		fireTransition(transitionRepository.findById(transitionId).get());
	}

	boolean canFireInhibitionArc(final InhibitionArc inhibitionArc) {
		return inhibitionArc.getState() != null && inhibitionArc.getState().getCount() < inhibitionArc.getWeight();
	}

	boolean canFireInputArc(final InputArc inputArc) {
		return inputArc.getState() != null && inputArc.getState().getCount() >= inputArc.getWeight();
	}

	boolean canFireOutputArc(final OutputArc outputArc) {
		return outputArc.getState() != null
				&& outputArc.getState().getCount() + outputArc.getWeight() <= outputArc.getState().getMaxCount();
	}

	boolean canFireTransition(final Transition transition) {
		if (!transition.getScenario().isEnabled()) {
			return false;
		}
		boolean result = true;
		for (final Arc arc : transition.getArcs()) {
			result &= arc.dispatch(new Arc.Dispatcher<Boolean>() {

				@Override
				public Boolean execute(final InhibitionArc arc) {
					return canFireInhibitionArc(arc);
				}

				@Override
				public Boolean execute(final InputArc arc) {
					return canFireInputArc(arc);
				}

				@Override
				public Boolean execute(final OutputArc arc) {
					return canFireOutputArc(arc);
				}
			});
		}
		return result;
	}

	void fireTransition(final Transition transition) {
		if (canFireTransition(transition)) {
			transition.getActions().forEach(action -> {
				action.dispatch(new Action.Dispatcher() {
					@Override
					public void execute(final SendCommandAction action) {
						try {
							deviceService.sendCommand(action.getDevice(), action.getCommand(), action.getData());
						} catch (final UndefinedCommandException | CommunicationNotSuccessfulException
								| CommunicationInterruptedException exception) {
							throw new RuntimeException(exception);
						}
					}
				});
			});
			transition.getArcs().forEach(arc -> {
				arc.dispatch(new Arc.Dispatcher<Void>() {

					@Override
					public Void execute(final InputArc arc) {
						final State state = arc.getState();
						state.setCount(state.getCount() - arc.getWeight());
						stateRepository.save(state);
						return null;
					}

					@Override
					public Void execute(final OutputArc arc) {
						final State state = arc.getState();
						state.setCount(state.getCount() + arc.getWeight());
						stateRepository.save(state);
						state.getArcs().stream().filter(nextArc -> {
							return nextArc instanceof InputArc;
						}).forEach(inputArc -> {
							if (inputArc.getTransition().isFiringAutomatically()) {
								fireTransition(inputArc.getTransition());
							}
						});
						return null;
					}

				});
			});
		}
	}
}
