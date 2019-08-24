package ch.wellernet.zeus.modules.scenario.service;

import ch.wellernet.zeus.modules.scenario.model.*;
import ch.wellernet.zeus.modules.scenario.repository.EventRepository;
import ch.wellernet.zeus.modules.scenario.scheduling.HighNoonTrigger;
import ch.wellernet.zeus.modules.scenario.scheduling.MidnightTrigger;
import ch.wellernet.zeus.modules.scenario.scheduling.SunriseTrigger;
import ch.wellernet.zeus.modules.scenario.scheduling.SunsetTrigger;
import ch.wellernet.zeus.modules.scenario.service.EventService.ScheduledEventRegistrar;
import com.luckycatlabs.sunrisesunset.Zenith;
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

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import static ch.wellernet.zeus.modules.scenario.model.SunEvent.*;
import static ch.wellernet.zeus.modules.scenario.model.SunEventDefinition.NAUTICAL;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;


@SpringBootTest(classes = EventService.class, properties = {"zeus.location.latitude=46.948877",
    "zeus.location.longitude=7.439949"}, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class EventServiceTest {

  // object under test
  @SpyBean
  private EventService eventService;

  @MockBean
  private PlatformTransactionManager transactionManager;

  @MockBean
  private ScheduledEventRegistrar scheduledEventRegistrar;

  @MockBean
  private TaskScheduler taskScheduler;

  @MockBean
  private EventRepository eventRepository;

  @MockBean
  private ScenarioService scenarioService;

  @Test
  public void cancelEventShouldCancelAlsoTask() {
    // given
    final UUID eventId = new UUID(0, 42);
    final ScheduledFuture<?> scheduledFuture = mock(ScheduledFuture.class);
    //noinspection unchecked
    given(scheduledEventRegistrar.remove(eventId)).willReturn((ScheduledFuture) scheduledFuture);

    // when
    eventService.cancelEvent(eventId);

    // then
    verify(scheduledFuture).cancel(true);
    verify(scheduledEventRegistrar).remove(eventId);
  }

  @Test
  public void cancelEventShouldNoNothingIdEventDoesNotExist() {
    // given
    final UUID eventId = new UUID(0, 42);
    given(scheduledEventRegistrar.remove(eventId)).willReturn(null);

    // when
    eventService.cancelEvent(eventId);

    // then
    verify(scheduledEventRegistrar).remove(eventId);
  }

  @Test
  public void fireEventShouldFireAllTransitionsWhenItIsFired() {
    // given
    doNothing().when(scenarioService).fireTransition(any(UUID.class));
    final EventDrivenTransition transition1 = EventDrivenTransition.builder().id(new UUID(0, 1)).build();
    final EventDrivenTransition transition2 = EventDrivenTransition.builder().id(new UUID(0, 2)).build();
    final Set<EventDrivenTransition> transitions = newHashSet(transition1, transition2);
    final Event event = defaults(FixedRateEvent.builder()).transitions(transitions).build();
    given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));
    given(eventRepository.save(event)).willReturn(event);

    // when
    final Event updatedEvent = eventService.fireEvent(event);

    // then
    verify(scenarioService).fireTransition(transition1);
    verify(scenarioService).fireTransition(transition2);
    verify(eventRepository).save(event);
    assertThat(updatedEvent, is(event));
    verify(eventService).updateNextFiringDate(event);
  }

  @Test
  public void scheduleAllExistingEventShouldCreateTaskForEachEvent() {
    // given
    final CronEvent event = defaults(CronEvent.builder()).build();
    final FixedRateEvent fixedRateEvent = defaults(FixedRateEvent.builder()).build();
    given(eventRepository.findAll()).willReturn(newHashSet(event, fixedRateEvent));
    doNothing().when(eventService).scheduleEvent(any(CronEvent.class));
    doNothing().when(eventService).scheduleEvent(any(FixedRateEvent.class));

    // when
    eventService.scheduleAllExistingEvents();

    // then
    verify(eventService).scheduleEvent(event);
    verify(eventService).scheduleEvent(fixedRateEvent);
  }

  @Test(expected = IllegalArgumentException.class)
  public void scheduleCronEventShouldSaveAndCreateTaskWhenCronExpressionIsInvValid() {
    // given
    final String cronExpression = "not valid!!!";
    final CronEvent event = defaults(CronEvent.builder()).cronExpression(cronExpression).build();

    // when
    eventService.scheduleEvent(event);

    // then an exception is expected
  }

  @Test
  public void scheduleCronEventShouldSaveAndCreateTaskWhenCronExpressionIsValid() {
    // given
    final String cronExpression = "0/5 * * * * *";
    new CronTrigger(cronExpression);
    final CronEvent event = defaults(CronEvent.builder()).cronExpression(cronExpression).build();

    // when
    eventService.scheduleEvent(event);

    // then
    verify(eventRepository).save(event);
    final ArgumentCaptor<CronTrigger> trigger = ArgumentCaptor.forClass(CronTrigger.class);
    verify(taskScheduler).schedule(any(Runnable.class), trigger.capture());
    assertThat(trigger.getValue().getExpression(), is(cronExpression));
  }

  @Test
  public void scheduleDayTimeEventHighNoonShouldSaveAndCreateTask() {
    // given
    final DayTimeEvent dayTimeEvent = defaults(DayTimeEvent.builder()).sunEvent(HIGH_NOON).definition(NAUTICAL).build();

    // when
    eventService.scheduleEvent(dayTimeEvent);

    // then
    verify(eventRepository).save(dayTimeEvent);
    final ArgumentCaptor<HighNoonTrigger> trigger = ArgumentCaptor.forClass(HighNoonTrigger.class);
    verify(taskScheduler).schedule(any(Runnable.class), trigger.capture());
    assertThat(trigger.getValue().getZenith(), is(Zenith.NAUTICAL));
  }

  @Test
  public void scheduleDayTimeEventMidnightShouldSaveAndCreateTask() {
    // given
    final DayTimeEvent dayTimeEvent = defaults(DayTimeEvent.builder()).sunEvent(MIDNIGHT).definition(NAUTICAL).build();

    // when
    eventService.scheduleEvent(dayTimeEvent);

    // then
    verify(eventRepository).save(dayTimeEvent);
    final ArgumentCaptor<MidnightTrigger> trigger = ArgumentCaptor.forClass(MidnightTrigger.class);
    verify(taskScheduler).schedule(any(Runnable.class), trigger.capture());
    assertThat(trigger.getValue().getZenith(), is(Zenith.NAUTICAL));
  }

  @Test
  public void scheduleDayTimeEventSunriseShouldSaveAndCreateTask() {
    // given
    final DayTimeEvent dayTimeEvent = defaults(DayTimeEvent.builder()).sunEvent(SUNRISE).definition(NAUTICAL).build();

    // when
    eventService.scheduleEvent(dayTimeEvent);

    // then
    verify(eventRepository).save(dayTimeEvent);
    final ArgumentCaptor<SunriseTrigger> trigger = ArgumentCaptor.forClass(SunriseTrigger.class);
    verify(taskScheduler).schedule(any(Runnable.class), trigger.capture());
    assertThat(trigger.getValue().getZenith(), is(Zenith.NAUTICAL));
  }

  @Test
  public void scheduleDayTimeEventSunsetShouldSaveAndCreateTask() {
    // given
    final DayTimeEvent dayTimeEvent = defaults(DayTimeEvent.builder()).sunEvent(SUNSET).definition(NAUTICAL).build();

    // when
    eventService.scheduleEvent(dayTimeEvent);

    // then
    verify(eventRepository).save(dayTimeEvent);
    final ArgumentCaptor<SunsetTrigger> trigger = ArgumentCaptor.forClass(SunsetTrigger.class);
    verify(taskScheduler).schedule(any(Runnable.class), trigger.capture());
    assertThat(trigger.getValue().getZenith(), is(Zenith.NAUTICAL));
  }

  @Test
  public void scheduleFixedRateEventShouldSaveAndCreateTask() {
    // given
    final int interval = 10;
    final int initialDelay = 15;
    final FixedRateEvent fixedRateEvent = defaults(FixedRateEvent.builder()).interval(interval).initialDelay(initialDelay)
                                              .build();

    // when
    eventService.scheduleEvent(fixedRateEvent);

    // then
    verify(eventRepository).save(fixedRateEvent);
    final ArgumentCaptor<Date> date = ArgumentCaptor.forClass(Date.class);
    verify(taskScheduler).scheduleAtFixedRate(any(Runnable.class), date.capture(), eq(interval * 1000L));
    assertThat((double) date.getValue().getTime(), is(closeTo(currentTimeMillis() + initialDelay * 1000, 50)));
  }

  private CronEvent.CronEventBuilder defaults(final CronEvent.CronEventBuilder builder) {
    return builder.id(randomUUID()).name("Event").cronExpression("0 0 20 * * *");
  }

  private DayTimeEvent.DayTimeEventBuilder defaults(final DayTimeEvent.DayTimeEventBuilder builder) {
    return builder.id(randomUUID()).name("Event").sunEvent(SUNRISE);
  }

  private FixedRateEvent.FixedRateEventBuilder defaults(final FixedRateEvent.FixedRateEventBuilder builder) {
    return builder.id(randomUUID()).name("Event").interval(1000);
  }
}

