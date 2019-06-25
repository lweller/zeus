package ch.wellernet.zeus.modules.scenario.service;

import ch.wellernet.zeus.modules.device.service.DeviceService;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationInterruptedException;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationNotSuccessfulException;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import ch.wellernet.zeus.modules.scenario.model.*;
import ch.wellernet.zeus.modules.scenario.repository.ScenarioRepository;
import ch.wellernet.zeus.modules.scenario.repository.StateRepository;
import ch.wellernet.zeus.modules.scenario.repository.TransitionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static javax.transaction.Transactional.TxType.MANDATORY;

@Service
@Transactional(MANDATORY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScenarioService {

  // injected dependencies
  private final DeviceService deviceService;
  private final ScenarioRepository scenarioRepository;
  private final TransitionRepository transitionRepository;
  private final StateRepository stateRepository;

  public Collection<Scenario> findAll() {
    return newArrayList(scenarioRepository.findAll());
  }

  public Scenario findById(final UUID scenarioId) {
    return scenarioRepository.findById(scenarioId)
               .orElseThrow(
                   () -> new NoSuchElementException(format("scenario with ID %s does not exists", scenarioId)));
  }

  public Scenario save(@NonNull final Scenario scenario) {
    return scenarioRepository.save(scenario);
  }

  public void delete(@NonNull final UUID scenarioId) {
    if (!scenarioRepository.existsById(scenarioId)) {
      throw new NoSuchElementException(format("scenario with ID %s does not exists", scenarioId));
    }
    scenarioRepository.deleteById(scenarioId);
  }

  void fireTransition(final UUID transitionId) throws NoSuchElementException {
    fireTransition(transitionRepository.findById(transitionId).orElseThrow(NoSuchElementException::new));
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
      transition.getActions().forEach(action -> action.dispatch(new Action.Dispatcher() {
        @Override
        public void execute(final SendCommandAction action) {
          try {
            deviceService.sendCommand(action.getDevice(), action.getCommand(), action.getData());
          } catch (final UndefinedCommandException | CommunicationNotSuccessfulException
                             | CommunicationInterruptedException exception) {
            throw new RuntimeException(exception);
          }
        }
      }));
      transition.getArcs().forEach(arc -> arc.dispatch(new Arc.Dispatcher<Void>() {

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
          state.getArcs().stream().filter(nextArc -> nextArc instanceof InputArc).forEach(inputArc -> {
            if (inputArc.getTransition().isFiringAutomatically()) {
              fireTransition(inputArc.getTransition());
            }
          });
          return null;
        }

      }));
    }
  }
}
