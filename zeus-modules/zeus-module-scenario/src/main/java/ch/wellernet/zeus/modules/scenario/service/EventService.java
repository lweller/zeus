package ch.wellernet.zeus.modules.scenario.service;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import ch.wellernet.zeus.modules.scenario.model.CronEvent;
import ch.wellernet.zeus.modules.scenario.model.Event;
import ch.wellernet.zeus.modules.scenario.model.FixedRateEvent;
import ch.wellernet.zeus.modules.scenario.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(MANDATORY)
@Slf4j
public class EventService {

	static class ScheduledEventRegistry {
		private final Map<Integer, ScheduledFuture<?>> scheduledEvents = new HashMap<>();

		public void add(final int eventId, final ScheduledFuture<?> scheduledFuture) {
			scheduledEvents.put(eventId, scheduledFuture);
		}

		public ScheduledFuture<?> remove(final int eventId) {
			return scheduledEvents.remove(eventId);
		}
	}

	private @Autowired(required = false) final ScheduledEventRegistry scheduledEventRegistry = new ScheduledEventRegistry();
	private @Autowired EventRepository eventRepository;
	private @Autowired PlatformTransactionManager transactionManager;
	private @Autowired TaskScheduler taskScheduler;
	private @Autowired ScenarioService scenarioService;

	public void cancelEvent(final int eventId) {
		final ScheduledFuture<?> scheduledFuture = scheduledEventRegistry.remove(eventId);
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
		}
	}

	public void fireEvent(final int eventId) {
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
				public void execute(final FixedRateEvent event) {
					scheduleEvent(event);
				}
			});
		});
	}

	public void scheduleEvent(final CronEvent event) throws IllegalArgumentException {
		eventRepository.save(event);
		scheduledEventRegistry.add(event.getId(),
				taskScheduler.schedule(createRunnableToFireEvent(event), new CronTrigger(event.getCronExpression())));
	}

	public void scheduleEvent(final FixedRateEvent event) {
		eventRepository.save(event);
		scheduledEventRegistry.add(event.getId(), taskScheduler.scheduleAtFixedRate(createRunnableToFireEvent(event),
				new Date(currentTimeMillis() + event.getInitialDelay() * 1000), event.getInterval() * 1000));
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