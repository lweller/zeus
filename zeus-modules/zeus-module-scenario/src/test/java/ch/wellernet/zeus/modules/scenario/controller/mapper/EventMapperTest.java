package ch.wellernet.zeus.modules.scenario.controller.mapper;

import ch.wellernet.zeus.modules.scenario.controller.dto.CronEventDto;
import ch.wellernet.zeus.modules.scenario.controller.dto.DayTimeEventDto;
import ch.wellernet.zeus.modules.scenario.controller.dto.EventDto;
import ch.wellernet.zeus.modules.scenario.controller.dto.FixedRateEventDto;
import ch.wellernet.zeus.modules.scenario.model.CronEvent;
import ch.wellernet.zeus.modules.scenario.model.DayTimeEvent;
import ch.wellernet.zeus.modules.scenario.model.Event;
import ch.wellernet.zeus.modules.scenario.model.FixedRateEvent;
import ch.wellernet.zeus.modules.scenario.repository.CronEventRepository;
import ch.wellernet.zeus.modules.scenario.repository.DayTimeEventRepository;
import ch.wellernet.zeus.modules.scenario.repository.FixedRateEventRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static ch.wellernet.zeus.modules.scenario.model.SunEvent.SUNRISE;
import static ch.wellernet.zeus.modules.scenario.model.SunEvent.SUNSET;
import static ch.wellernet.zeus.modules.scenario.model.SunEventDefinition.NAUTICAL;
import static ch.wellernet.zeus.modules.scenario.model.SunEventDefinition.OFFICIAL;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(classes = EventMapperImpl.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class EventMapperTest {

  // object under test
  @Autowired
  private EventMapper eventMapper;

  @MockBean
  private CronEventRepository cronEventRepository;


  @MockBean
  private DayTimeEventRepository dayTimeEventRepository;


  @MockBean
  private FixedRateEventRepository fixedRateEventRepository;

  @Test
  public void createOrUpdateShouldCreateCronEventWhenNotExisting() {
    // given
    final CronEventDto eventDto = CronEventDto.builder()
                                      .id(randomUUID())
                                      .name("New Event")
                                      .lastExecution(new Date())
                                      .nextScheduledExecution(new Date())
                                      .cronExpression("0 0 20 ? * * *")
                                      .build();
    given(cronEventRepository.findById(any())).willReturn(Optional.empty());
    given(cronEventRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

    // when
    final Event createdEvent = eventMapper.createOrUpdateFrom(eventDto);

    // then
    assertThat(createdEvent.getId(), is(eventDto.getId()));
    assertThat(createdEvent.getName(), is(eventDto.getName()));
    assertThat(createdEvent.getTransitions(), allOf(notNullValue(), empty()));
    assertThat(createdEvent.getLastExecution(), is(nullValue()));
    assertThat(createdEvent.getNextScheduledExecution(), is(nullValue()));
    assertThat(createdEvent, instanceOf(CronEvent.class));
    assertThat(((CronEvent) createdEvent).getCronExpression(), is(eventDto.getCronExpression()));
  }

  @Test
  public void createOrUpdateShouldCreateDayTimeEventWhenNotExisting() {
    // given
    final DayTimeEventDto eventDto = DayTimeEventDto.builder()
                                         .id(randomUUID())
                                         .name("New Event").lastExecution(new Date())
                                         .nextScheduledExecution(new Date())
                                         .definition(OFFICIAL)
                                         .sunEvent(SUNRISE)
                                         .build();
    given(dayTimeEventRepository.findById(any())).willReturn(Optional.empty());
    given(dayTimeEventRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

    // when
    final Event createdEvent = eventMapper.createOrUpdateFrom(eventDto);

    // then
    assertThat(createdEvent.getId(), is(eventDto.getId()));
    assertThat(createdEvent.getName(), is(eventDto.getName()));
    assertThat(createdEvent.getTransitions(), allOf(notNullValue(), empty()));
    assertThat(createdEvent.getLastExecution(), is(nullValue()));
    assertThat(createdEvent.getNextScheduledExecution(), is(nullValue()));
    assertThat(createdEvent, instanceOf(DayTimeEvent.class));
    assertThat(((DayTimeEvent) createdEvent).getDefinition(), is(eventDto.getDefinition()));
    assertThat(((DayTimeEvent) createdEvent).getSunEvent(), is(eventDto.getSunEvent()));
  }

  @Test
  public void createOrUpdateShouldCreateFixedRateEventWhenNotExisting() {
    // given
    final FixedRateEventDto eventDto = FixedRateEventDto.builder()
                                           .id(randomUUID())
                                           .name("New Event").lastExecution(new Date())
                                           .nextScheduledExecution(new Date())
                                           .initialDelay(30)
                                           .interval(3600)
                                           .build();
    given(fixedRateEventRepository.findById(any())).willReturn(Optional.empty());
    given(fixedRateEventRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

    // when
    final Event createdEvent = eventMapper.createOrUpdateFrom(eventDto);

    // then
    assertThat(createdEvent.getId(), is(eventDto.getId()));
    assertThat(createdEvent.getName(), is(eventDto.getName()));
    assertThat(createdEvent.getTransitions(), allOf(notNullValue(), empty()));
    assertThat(createdEvent.getLastExecution(), is(nullValue()));
    assertThat(createdEvent.getNextScheduledExecution(), is(nullValue()));
    assertThat(createdEvent, instanceOf(FixedRateEvent.class));
    assertThat(((FixedRateEvent) createdEvent).getInitialDelay(), is(eventDto.getInitialDelay()));
    assertThat(((FixedRateEvent) createdEvent).getInterval(), is(eventDto.getInterval()));
  }

  @Test
  public void createOrUpdateShouldUpdateCronEventWhenAlreadyExisting() {
    // given
    final UUID eventId = randomUUID();
    final CronEvent event = CronEvent.builder()
                                .id(eventId)
                                .name("Old Event")
                                .cronExpression("0 0 20 ? * * *")
                                .build();
    final Date lastExecution = new Date();
    event.setLastExecution(lastExecution);
    final Date nextScheduledExecution = new Date();
    event.setNextScheduledExecution(nextScheduledExecution);
    final CronEventDto eventDto = CronEventDto.builder()
                                      .id(eventId)
                                      .version(42)
                                      .name("Updated Event")
                                      .cronExpression("0 0 21 ? * * *")
                                      .build();
    given(cronEventRepository.findById(eventId)).willReturn(Optional.of(event));

    // when
    final Event updatedEvent = eventMapper.createOrUpdateFrom(eventDto);

    // then
    assertThat(updatedEvent, sameInstance(event));
    assertThat(updatedEvent.getId(), is(eventId));
    assertThat(updatedEvent.getVersion(), is(eventDto.getVersion()));
    assertThat(updatedEvent.getName(), is(eventDto.getName()));
    assertThat(updatedEvent.getLastExecution(), is(lastExecution));
    assertThat(updatedEvent.getNextScheduledExecution(), is(nextScheduledExecution));
    assertThat(((CronEvent) updatedEvent).getCronExpression(), is(eventDto.getCronExpression()));
  }

  @Test
  public void createOrUpdateShouldUpdateDayTimeEventWhenAlreadyExisting() {
    // given
    final UUID eventId = randomUUID();
    final DayTimeEvent event = DayTimeEvent.builder()
                                   .id(eventId)
                                   .name("Old Event")
                                   .definition(OFFICIAL)
                                   .sunEvent(SUNRISE)
                                   .build();
    final Date lastExecution = new Date();
    event.setLastExecution(lastExecution);
    final Date nextScheduledExecution = new Date();
    event.setNextScheduledExecution(nextScheduledExecution);
    final DayTimeEventDto eventDto = DayTimeEventDto.builder()
                                         .id(eventId)
                                         .version(42)
                                         .name("Updated Event")
                                         .definition(NAUTICAL)
                                         .sunEvent(SUNSET)
                                         .build();
    given(dayTimeEventRepository.findById(eventId)).willReturn(Optional.of(event));

    // when
    final Event updatedEvent = eventMapper.createOrUpdateFrom(eventDto);

    // then
    assertThat(updatedEvent, sameInstance(event));
    assertThat(updatedEvent.getId(), is(eventId));
    assertThat(updatedEvent.getVersion(), is(eventDto.getVersion()));
    assertThat(updatedEvent.getName(), is(eventDto.getName()));
    assertThat(updatedEvent.getLastExecution(), is(lastExecution));
    assertThat(updatedEvent.getNextScheduledExecution(), is(nextScheduledExecution));
    assertThat(((DayTimeEvent) updatedEvent).getDefinition(), is(eventDto.getDefinition()));
    assertThat(((DayTimeEvent) updatedEvent).getSunEvent(), is(eventDto.getSunEvent()));
  }

  @Test
  public void createOrUpdateShouldUpdateFixedRateEventWhenAlreadyExisting() {
    // given
    final UUID eventId = randomUUID();
    final FixedRateEvent event = FixedRateEvent.builder()
                                     .id(eventId)
                                     .name("Old Event")
                                     .initialDelay(30)
                                     .interval(3600)
                                     .build();
    final Date lastExecution = new Date();
    event.setLastExecution(lastExecution);
    final Date nextScheduledExecution = new Date();
    event.setNextScheduledExecution(nextScheduledExecution);
    final FixedRateEventDto eventDto = FixedRateEventDto.builder()
                                           .id(eventId)
                                           .version(42)
                                           .name("Updated Event")
                                           .initialDelay(30)
                                           .interval(3600)
                                           .build();
    given(fixedRateEventRepository.findById(eventId)).willReturn(Optional.of(event));

    // when
    final Event updatedEvent = eventMapper.createOrUpdateFrom(eventDto);

    // then
    assertThat(updatedEvent, sameInstance(event));
    assertThat(updatedEvent.getId(), is(eventId));
    assertThat(updatedEvent.getVersion(), is(eventDto.getVersion()));
    assertThat(updatedEvent.getName(), is(eventDto.getName()));
    assertThat(updatedEvent.getLastExecution(), is(lastExecution));
    assertThat(updatedEvent.getNextScheduledExecution(), is(nextScheduledExecution));
    assertThat(((FixedRateEvent) updatedEvent).getInitialDelay(), is(eventDto.getInitialDelay()));
    assertThat(((FixedRateEvent) updatedEvent).getInterval(), is(eventDto.getInterval()));
  }

  @Test
  public void toDtoShouldConvertCorrectlyCronEvent() {
    // given
    final UUID eventId = randomUUID();
    final CronEvent event = CronEvent.builder()
                                .id(eventId)
                                .name("Event")
                                .cronExpression("0 0 20 ? * * *")
                                .build();
    final Date lastExecution = new Date();
    event.setLastExecution(lastExecution);
    final Date nextScheduledExecution = new Date();
    event.setNextScheduledExecution(nextScheduledExecution);

    // when
    final EventDto eventDto = eventMapper.toDto(event);

    // then
    assertThat(eventDto.getId(), is(event.getId()));
    assertThat(eventDto.getName(), is(event.getName()));
    assertThat(eventDto.getLastExecution(), is(lastExecution));
    assertThat(eventDto.getNextScheduledExecution(), is(nextScheduledExecution));
    assertThat(eventDto, instanceOf(CronEventDto.class));
    //noinspection CastCanBeRemovedNarrowingVariableType
    assertThat(((CronEventDto) eventDto).getCronExpression(), is(event.getCronExpression()));
  }

  @Test
  public void toDtoShouldConvertCorrectlyDayTimeEvent() {
    // given
    final UUID eventId = randomUUID();
    final DayTimeEvent event = DayTimeEvent.builder()
                                   .id(eventId)
                                   .name("Event")
                                   .definition(NAUTICAL)
                                   .sunEvent(SUNSET)
                                   .build();
    final Date lastExecution = new Date();
    event.setLastExecution(lastExecution);
    final Date nextScheduledExecution = new Date();
    event.setNextScheduledExecution(nextScheduledExecution);

    // when
    final EventDto eventDto = eventMapper.toDto(event);

    // then
    assertThat(eventDto.getId(), is(event.getId()));
    assertThat(eventDto.getName(), is(event.getName()));
    assertThat(eventDto.getLastExecution(), is(lastExecution));
    assertThat(eventDto.getNextScheduledExecution(), is(nextScheduledExecution));
    assertThat(eventDto, instanceOf(DayTimeEventDto.class));
    //noinspection CastCanBeRemovedNarrowingVariableType
    assertThat(((DayTimeEventDto) eventDto).getDefinition(), is(event.getDefinition()));
    //noinspection CastCanBeRemovedNarrowingVariableType
    assertThat(((DayTimeEventDto) eventDto).getSunEvent(), is(event.getSunEvent()));
  }

  @Test
  public void toDtoShouldConvertCorrectlyFixedRateEvent() {
    // given
    final UUID eventId = randomUUID();
    final FixedRateEvent event = FixedRateEvent.builder()
                                     .id(eventId)
                                     .name("Event")
                                     .initialDelay(30)
                                     .interval(3600)
                                     .build();
    final Date lastExecution = new Date();
    event.setLastExecution(lastExecution);
    final Date nextScheduledExecution = new Date();
    event.setNextScheduledExecution(nextScheduledExecution);

    // when
    final EventDto eventDto = eventMapper.toDto(event);

    // then
    assertThat(eventDto.getId(), is(event.getId()));
    assertThat(eventDto.getName(), is(event.getName()));
    assertThat(eventDto.getLastExecution(), is(lastExecution));
    assertThat(eventDto.getNextScheduledExecution(), is(nextScheduledExecution));
    assertThat(eventDto, instanceOf(FixedRateEventDto.class));
    //noinspection CastCanBeRemovedNarrowingVariableType
    assertThat(((FixedRateEventDto) eventDto).getInitialDelay(), is(event.getInitialDelay()));
    //noinspection CastCanBeRemovedNarrowingVariableType
    assertThat(((FixedRateEventDto) eventDto).getInterval(), is(event.getInterval()));
  }
}