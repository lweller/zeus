package ch.wellernet.zeus.modules.scenario.service;

import ch.wellernet.zeus.modules.scenario.model.CronEvent;
import ch.wellernet.zeus.modules.scenario.model.DayTimeEvent;
import ch.wellernet.zeus.modules.scenario.model.Event;
import ch.wellernet.zeus.modules.scenario.model.FixedRateEvent;
import ch.wellernet.zeus.modules.scenario.repository.EventRepository;
import ch.wellernet.zeus.modules.scenario.scheduling.*;
import com.luckycatlabs.sunrisesunset.Zenith;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javax.transaction.Transactional.TxType.MANDATORY;

@Service
@Transactional(MANDATORY)
@Slf4j
@EnableConfigurationProperties(Location.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventService {

  private final EventRepository eventRepository;
  private final PlatformTransactionManager transactionManager;
  private final ScenarioService scenarioService;
  private final Location location;
  private final TaskScheduler taskScheduler;
  // injected dependencies
  private @Setter(onMethod_ = @Autowired(required = false))
  ScheduledEventRegistrar scheduledEventRegistrar = new ScheduledEventRegistrar();


  public Event fireEvent(@NonNull final Event event) {
    log.info(format("firing event '%s'", event.getName()));
    event.setLastExecution(new Date());
    event.getTransitions().forEach(scenarioService::fireTransition);
    updateNextFiringDate(event);
    return eventRepository.save(event);
  }

  public void scheduleAllExistingEvents() throws IllegalArgumentException {
    log.info("scheduling all existing events");
    eventRepository.findAll().forEach(this::scheduleEvent);
  }

  public void scheduleEvent(final Event event) {
    event.dispatch(new Event.Dispatcher<Void>() {

      @Override
      public Void execute(final CronEvent event) {
        scheduleEvent(event);
        return null;
      }

      @Override
      public Void execute(final DayTimeEvent event) {
        scheduleEvent(event);
        return null;
      }

      @Override
      public Void execute(final FixedRateEvent event) {
        scheduleEvent(event);
        return null;
      }

    });
  }

  public void cancelEvent(final UUID eventId) {
    final ScheduledFuture<?> scheduledFuture = scheduledEventRegistrar.remove(eventId);
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
  }

  void scheduleEvent(final CronEvent event) throws IllegalArgumentException {
    cancelEvent(event.getId());
    eventRepository.save(event);
    scheduledEventRegistrar.add(event.getId(),
        taskScheduler.schedule(createRunnableToFireEvent(event), new CronTrigger(event.getCronExpression())));
  }

  void scheduleEvent(final DayTimeEvent event) {
    Trigger trigger = null;
    Zenith zenith = null;
    switch (event.getDefinition()) {
      case ASTRONOMICAL:
        zenith = Zenith.ASTRONOMICAL;
        break;
      case CIVIL:
        zenith = Zenith.CIVIL;
        break;
      case NAUTICAL:
        zenith = Zenith.NAUTICAL;
        break;
      case OFFICIAL:
        zenith = Zenith.OFFICIAL;
        break;
    }
    switch (event.getSunEvent()) {
      case MIDNIGHT:
        trigger = MidnightTrigger.builder().location(location).zenith(zenith).shift(event.getShift() * 1000)
                      .build();
        break;
      case SUNRISE:
        trigger = SunriseTrigger.builder().location(location).zenith(zenith).shift(event.getShift() * 1000).build();
        break;
      case SUNSET:
        trigger = SunsetTrigger.builder().location(location).zenith(zenith).shift(event.getShift() * 1000).build();
        break;
      case HIGH_NOON:
        trigger = HighNoonTrigger.builder().location(location).zenith(zenith).shift(event.getShift() * 1000)
                      .build();
        break;
    }
    cancelEvent(event.getId());
    eventRepository.save(event);
    scheduledEventRegistrar.add(event.getId(), taskScheduler.schedule(createRunnableToFireEvent(event), trigger));
  }

  void scheduleEvent(final FixedRateEvent event) {
    cancelEvent(event.getId());
    eventRepository.save(event);
    scheduledEventRegistrar.add(event.getId(), taskScheduler.scheduleAtFixedRate(createRunnableToFireEvent(event),
        new Date(currentTimeMillis() + event.getInitialDelay() * 1000), event.getInterval() * 1000));
  }

  private Runnable createRunnableToFireEvent(final Event event) {
    return () -> new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(final @Nonnull TransactionStatus status) {
        fireEvent(event);
      }
    });
  }

  public void updateNextFiringDate(final Event event) {
    if (event != null) {
      final ScheduledFuture<?> scheduledFuture = scheduledEventRegistrar.get(event.getId());
      if (scheduledFuture != null) {
        event.setNextScheduledExecution(new Date(currentTimeMillis() + scheduledFuture.getDelay(MILLISECONDS)));
      }
    }
  }

  static class ScheduledEventRegistrar {
    private final Map<UUID, ScheduledFuture<?>> scheduledEvents = new HashMap<>();

    void add(final UUID eventId, final ScheduledFuture<?> scheduledFuture) {
      scheduledEvents.put(eventId, scheduledFuture);
    }

    ScheduledFuture<?> get(final UUID eventId) {
      return scheduledEvents.get(eventId);
    }

    ScheduledFuture<?> remove(final UUID eventId) {
      return scheduledEvents.remove(eventId);
    }
  }
}
