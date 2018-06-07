package ch.wellernet.zeus.modules.scenario.service;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import javax.transaction.Transactional;

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

import com.luckycatlabs.sunrisesunset.Zenith;

import ch.wellernet.zeus.modules.scenario.model.CronEvent;
import ch.wellernet.zeus.modules.scenario.model.DayTimeEvent;
import ch.wellernet.zeus.modules.scenario.model.Event;
import ch.wellernet.zeus.modules.scenario.model.FixedRateEvent;
import ch.wellernet.zeus.modules.scenario.repository.EventRepository;
import ch.wellernet.zeus.modules.scenario.scheduling.HighNoonTrigger;
import ch.wellernet.zeus.modules.scenario.scheduling.Location;
import ch.wellernet.zeus.modules.scenario.scheduling.MidnightTrigger;
import ch.wellernet.zeus.modules.scenario.scheduling.SunriseTrigger;
import ch.wellernet.zeus.modules.scenario.scheduling.SunsetTrigger;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(MANDATORY)
@Slf4j
@EnableConfigurationProperties(Location.class)
public class EventService {

	static class ScheduledEventRegistry {
		private final Map<UUID, ScheduledFuture<?>> scheduledEvents = new HashMap<>();

		public ScheduledFuture<?> add(final UUID eventId, final ScheduledFuture<?> scheduledFuture) {
			return scheduledEvents.put(eventId, scheduledFuture);
		}

		public ScheduledFuture<?> get(final UUID eventId) {
			return scheduledEvents.get(eventId);
		}

		public ScheduledFuture<?> remove(final UUID eventId) {
			return scheduledEvents.remove(eventId);
		}
	}

	private @Autowired(required = false) final ScheduledEventRegistry scheduledEventRegistry = new ScheduledEventRegistry();
	private @Autowired EventRepository eventRepository;
	private @Autowired PlatformTransactionManager transactionManager;
	private @Autowired TaskScheduler taskScheduler;
	private @Autowired ScenarioService scenarioService;
	private @Autowired Location location;

	public void cancelEvent(final UUID eventId) {
		final ScheduledFuture<?> scheduledFuture = scheduledEventRegistry.remove(eventId);
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
		}
	}

	public Collection<Event> findAll() {
		final Iterable<Event> events = eventRepository.findAll();
		events.forEach(this::addNextFiringDate);
		return newArrayList(events);
	}

	public Optional<Event> findById(final UUID eventId) {
		final Optional<Event> event = eventRepository.findById(eventId);
		addNextFiringDate(event.orElse(null));
		return event;
	}

	public void fireEvent(final UUID eventId) {
		final Optional<Event> event = eventRepository.findById(eventId);

		if (event.isPresent()) {
			log.info(format("firing event '%s'", event.get().getName()));
			event.get().getTransitions().forEach(scenarioService::fireTransition);
		} else {
			log.info(format("canceling event ID '%s' because it doesn't exist anymore", eventId));
			cancelEvent(eventId);
		}
	}

	public void scheduleAllExistingEvents() throws IllegalArgumentException {
		log.info(format("scheduling all existing events"));
		eventRepository.findAll().forEach(event -> {
			event.dispatch(new Event.Dispatcher() {

				@Override
				public void execute(final CronEvent event) {
					scheduleEvent(event);
				}

				@Override
				public void execute(final DayTimeEvent event) {
					scheduleEvent(event);
				}

				@Override
				public void execute(final FixedRateEvent event) {
					scheduleEvent(event);
				}

			});
		});
	}

	public void scheduleEvent(final CronEvent event) throws IllegalArgumentException {
		cancelEvent(event.getId());
		eventRepository.save(event);
		scheduledEventRegistry.add(event.getId(),
				taskScheduler.schedule(createRunnableToFireEvent(event), new CronTrigger(event.getCronExpression())));
	}

	public void scheduleEvent(final DayTimeEvent event) {
		Trigger trigger = null;
		Zenith zenith = null;
		switch (event.getDefinition()) {
		case ASTRONOMICAL:
			zenith = Zenith.ASTRONOMICAL;
		case CIVIL:
			zenith = Zenith.CIVIL;
		case NAUTICAL:
			zenith = Zenith.NAUTICAL;
		case OFFICIAL:
			zenith = Zenith.OFFICIAL;
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
		scheduledEventRegistry.add(event.getId(), taskScheduler.schedule(createRunnableToFireEvent(event), trigger));
	}

	public void scheduleEvent(final FixedRateEvent event) {
		cancelEvent(event.getId());
		eventRepository.save(event);
		scheduledEventRegistry.add(event.getId(), taskScheduler.scheduleAtFixedRate(createRunnableToFireEvent(event),
				new Date(currentTimeMillis() + event.getInitialDelay() * 1000), event.getInterval() * 1000));
	}

	private void addNextFiringDate(final Event event) {
		if (event != null) {
			final ScheduledFuture<?> scheduledEvent = scheduledEventRegistry.get(event.getId());
			if (scheduledEvent != null) {
				event.setNextFiringDate(new Date(System.currentTimeMillis() + scheduledEvent.getDelay(MILLISECONDS)));
			}
		}
	}

	private Runnable createRunnableToFireEvent(final Event event) {
		return () -> {
			new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(final TransactionStatus status) {
					fireEvent(event.getId());
				}
			});
		};
	}
}
