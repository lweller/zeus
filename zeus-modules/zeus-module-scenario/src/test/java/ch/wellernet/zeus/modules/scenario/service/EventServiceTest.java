package ch.wellernet.zeus.modules.scenario.service;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.System.currentTimeMillis;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;

import ch.wellernet.zeus.modules.scenario.model.CronEvent;
import ch.wellernet.zeus.modules.scenario.model.Event;
import ch.wellernet.zeus.modules.scenario.model.EventDrivenTransition;
import ch.wellernet.zeus.modules.scenario.model.FixedRateEvent;
import ch.wellernet.zeus.modules.scenario.repository.EventRepository;
import ch.wellernet.zeus.modules.scenario.service.EventService.ScheduledEventRegistry;

@SpringBootTest(classes = EventService.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class EventServiceTest {
	// object under test
	private @SpyBean EventService eventService;

	private @MockBean ScheduledEventRegistry scheduledEventRegistry;
	private @MockBean EventRepository eventRepository;
	private @MockBean PlatformTransactionManager transactionManager;
	private @MockBean TaskScheduler taskScheduler;
	private @MockBean ScenarioService scenarioService;

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void cancelEventShouldCancelAlsoTask() {
		// given
		final int eventId = 42;
		final ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);
		given(scheduledEventRegistry.remove(eventId)).willReturn(scheduledFuture);

		// when
		eventService.cancelEvent(eventId);

		// then
		verify(scheduledFuture).cancel(true);
		verify(scheduledEventRegistry).remove(eventId);
	}

	@Test
	public void fireEventShouldCancelItWhenEventNotExists() {
		// given
		final int eventId = 42;
		doNothing().when(eventService).cancelEvent(eventId);

		// when
		eventService.fireEvent(eventId);

		// then
		verify(eventService).cancelEvent(eventId);
	}

	@Test
	public void fireEventShouldFireAllTransitionsWhenItIsFired() {
		// given
		doNothing().when(scenarioService).fireTransition(any());
		final EventDrivenTransition transition1 = EventDrivenTransition.builder().build();
		final EventDrivenTransition transition2 = EventDrivenTransition.builder().build();
		final Set<EventDrivenTransition> transitions = newHashSet(transition1, transition2);
		final Event event = FixedRateEvent.builder().transitions(transitions).build();
		given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));

		// when
		eventService.fireEvent(event.getId());

		// then
		verify(scenarioService).fireTransition(transition1);
		verify(scenarioService).fireTransition(transition2);
	}

	@Test
	public void scheduleAllExistingEventShouldCreateTaskForEachEvent() {
		// given
		final CronEvent cronEvent = CronEvent.builder().build();
		final FixedRateEvent fixedRateEvent = FixedRateEvent.builder().build();
		given(eventRepository.findAll()).willReturn(newHashSet(cronEvent, fixedRateEvent));
		doNothing().when(eventService).scheduleEvent(any(CronEvent.class));
		doNothing().when(eventService).scheduleEvent(any(FixedRateEvent.class));

		// when
		eventService.scheduleAllExistingEvents();

		// then
		verify(eventService).scheduleEvent(cronEvent);
		verify(eventService).scheduleEvent(fixedRateEvent);
	}

	@Test(expected = IllegalArgumentException.class)
	public void scheduleCronEventShouldSaveAndCreateTaskWhenCronExpressionIsInvValid() {
		// given
		final String cronExpression = "not valid!!!";
		final CronEvent cronEvent = CronEvent.builder().cronExpression(cronExpression).build();

		// when
		eventService.scheduleEvent(cronEvent);

		// then an exception is expected
	}

	@Test
	public void scheduleCronEventShouldSaveAndCreateTaskWhenCronExpressionIsValid() {
		// given
		final String cronExpression = "0/5 * * * * *";
		final CronTrigger cronTrigger = new CronTrigger(cronExpression);
		final CronEvent cronEvent = CronEvent.builder().cronExpression(cronExpression).build();

		// when
		eventService.scheduleEvent(cronEvent);

		// then
		verify(eventRepository).save(cronEvent);
		verify(taskScheduler).schedule(any(Runnable.class), eq(cronTrigger));
	}

	@Test
	public void scheduleFixedRateEventShouldSaveAndCreateTask() {
		// given
		final int interval = 10;
		final int initialDelay = 15;
		final FixedRateEvent fixedRateEvent = FixedRateEvent.builder().interval(interval).initialDelay(initialDelay)
				.build();

		// when
		eventService.scheduleEvent(fixedRateEvent);

		// then
		verify(eventRepository).save(fixedRateEvent);
		final ArgumentCaptor<Date> firstRun = ArgumentCaptor.forClass(Date.class);
		verify(taskScheduler).scheduleAtFixedRate(any(Runnable.class), firstRun.capture(), eq(interval * 1000l));
		assertThat(new Double(firstRun.getValue().getTime()),
				is(closeTo(currentTimeMillis() + initialDelay * 1000, 1000)));
	}
}
