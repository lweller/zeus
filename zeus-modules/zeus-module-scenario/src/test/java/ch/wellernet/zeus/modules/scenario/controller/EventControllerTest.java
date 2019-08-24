package ch.wellernet.zeus.modules.scenario.controller;

import ch.wellernet.zeus.modules.scenario.controller.dto.CronEventDto;
import ch.wellernet.zeus.modules.scenario.controller.dto.EventDto;
import ch.wellernet.zeus.modules.scenario.controller.mapper.EventMapper;
import ch.wellernet.zeus.modules.scenario.model.CronEvent;
import ch.wellernet.zeus.modules.scenario.model.Event;
import ch.wellernet.zeus.modules.scenario.repository.EventRepository;
import ch.wellernet.zeus.modules.scenario.service.EventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.OptimisticLockException;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(classes = EventController.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class EventControllerTest {

  // object under test
  @Autowired
  private EventController eventController;

  @MockBean
  private EventRepository eventRepository;

  @MockBean
  private EventMapper eventMapper;

  @MockBean
  private EventService eventService;

  @Test
  public void findAllShouldReturnCollectionOfEvents() {
    // given
    final Event event1 = defaults(CronEvent.builder()).id(randomUUID()).build();
    final Event event2 = defaults(CronEvent.builder()).id(randomUUID()).build();
    final Collection<Event> events = Stream.of(event1, event2).collect(toList());
    final EventDto eventDto1 = CronEventDto.builder().id(randomUUID()).build();
    final EventDto eventDto2 = CronEventDto.builder().id(randomUUID()).build();
    final Collection<EventDto> eventDtos = Stream.of(eventDto1, eventDto2).collect(toList());
    given(eventRepository.findAll()).willReturn(events);
    given(eventMapper.toDtos(events)).willReturn(eventDtos);

    // when
    final ResponseEntity<Collection<EventDto>> response = eventController.findAll();

    // then
    assertThat(response.getBody(), containsInAnyOrder(eventDto1, eventDto2));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void findByIdShouldReturnEvent() {
    // given
    final UUID eventId = randomUUID();
    final Event event = defaults(CronEvent.builder()).build();
    final EventDto eventDto = CronEventDto.builder().build();
    given(eventRepository.findById(eventId)).willReturn(Optional.of(event));
    given(eventMapper.toDto(event)).willReturn(eventDto);

    // when
    final ResponseEntity<EventDto> response = eventController.findById(eventId);

    // then
    assertThat(response.getBody(), is(eventDto));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void findByIdShouldThrowNoSuchElementExceptionIfEventDoesNotExists() {
    // given
    final UUID eventId = randomUUID();
    given(eventRepository.findById(eventId)).willReturn(Optional.empty());

    // when
    eventController.findById(eventId);

    // then an exception is expected
  }

  @Test
  public void createOrUpdateShouldReturnReattachEvent() {
    // given
    final UUID eventId = randomUUID();
    final Event event = defaults(CronEvent.builder()).id(eventId).build();
    final EventDto eventDto = CronEventDto.builder().build();
    final EventDto updatedEventDto = CronEventDto.builder().id(eventId).build();
    given(eventMapper.createOrUpdateFrom(eventDto)).willReturn(event);
    given(eventMapper.toDto(event)).willReturn(updatedEventDto);

    // when
    final ResponseEntity<EventDto> response = eventController.createOrUpdate(eventDto);

    // then
    verify(eventService).scheduleEvent(event);
    assertThat(response.getBody(), is(updatedEventDto));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void deleteShouldRemoveExistingEvent() {
    // given
    final UUID eventId = randomUUID();
    given(eventRepository.existsById(eventId)).willReturn(true);

    // when
    eventController.delete(eventId);

    // then
    verify(eventRepository).deleteById(eventId);
    verify(eventService).cancelEvent(eventId);
  }

  @Test
  public void fireShouldFireImmediately() throws NoSuchElementException {
    // given
    final UUID eventId = randomUUID();
    final CronEvent event = defaults(CronEvent.builder()).id(eventId).build();
    final CronEventDto eventDto = CronEventDto.builder().id(eventId).build();
    given(eventRepository.findById(eventId)).willReturn(Optional.of(event));
    given(eventService.fireEvent(event)).willReturn(event);
    given(eventMapper.toDto(event)).willReturn(eventDto);

    // when
    final ResponseEntity<EventDto> response = eventController.fire(event.getId());

    // then
    verify(eventService).fireEvent(event);
    assertThat(response.getBody(), is(eventDto));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void fireShouldThrowNoSuchElementExceptionIfEventDoesNotExists() {
    // given
    final UUID eventId = randomUUID();
    given(eventRepository.findById(eventId)).willReturn(Optional.empty());

    // when
    eventController.fire(eventId);

    // then an exception is expected
  }

  @Test
  public void handleOptimisticLockExceptionShouldReturnConflictStatus() {
    // given
    final UUID eventId = randomUUID();
    final CronEvent event = defaults(CronEvent.builder()).id(eventId).build();
    final CronEventDto eventDto = CronEventDto.builder().id(eventId).build();
    given(eventMapper.toDto(event)).willReturn(eventDto);

    // when
    final ResponseEntity<EventDto> response = eventController.handleOptimisticLockException(new OptimisticLockException(event));

    // then
    assertThat(response.getStatusCode(), is(PRECONDITION_FAILED));
    assertThat(response.getBody(), is(eventDto));
  }

  @Test
  public void handleNoSuchElementExceptionShouldReturnNotFoundStatus() {
    // given nothing special

    // when
    final ResponseEntity<String> response = eventController.handleNoSuchElementException();

    // then
    assertThat(response.getStatusCode(), is(NOT_FOUND));
  }

  private CronEvent.CronEventBuilder defaults(final CronEvent.CronEventBuilder builder) {
    return builder.id(randomUUID()).name("Event").cronExpression("0 0 20 * * *");
  }
}