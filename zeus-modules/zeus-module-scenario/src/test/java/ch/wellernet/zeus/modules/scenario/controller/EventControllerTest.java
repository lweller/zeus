package ch.wellernet.zeus.modules.scenario.controller;

import ch.wellernet.zeus.modules.scenario.model.CronEvent;
import ch.wellernet.zeus.modules.scenario.model.Event;
import ch.wellernet.zeus.modules.scenario.service.EventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityExistsException;
import javax.persistence.OptimisticLockException;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(classes = EventController.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class EventControllerTest {

  // test data
  private static final Event EVENT_1 = CronEvent.builder().id(randomUUID()).name("Event 1").build();
  private static final Event EVENT_2 = CronEvent.builder().id(randomUUID()).name("Event 2").build();
  private static final Event EVENT_3 = CronEvent.builder().id(randomUUID()).name("Event 3").build();
  private static final List<Event> EVENTS = newArrayList(EVENT_1, EVENT_2, EVENT_3);

  // object under test
  private @Autowired
  EventController eventController;

  private @MockBean
  EventService eventService;

  @Test
  public void createShouldReturnSaveNewEvent() {
    // given
    given(eventService.save(EVENT_1)).willReturn(EVENT_1);

    // when
    final ResponseEntity<Event> response = eventController.create(EVENT_1);

    // then
    assertThat(response.getBody(), is(EVENT_1));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void saveShouldSaveUpdatedEvent() {
    // given
    final Event event = defaults(CronEvent.builder()).build();
    event.setVersion(42);
    given(eventService.findById(event.getId())).willReturn(event);
    given(eventService.save(event)).willReturn(event);

    // when
    final ResponseEntity<Event> response = eventController.save(event.getId(), event, 42);

    // then
    assertThat(response.getBody(), is(event));
    verify(eventService).save(event);
  }

  @Test(expected = NoSuchElementException.class)
  public void saveThrowAnExceptionWhenEventDoesNotExists() {
    // given
    given(eventService.findById(any())).willThrow(NoSuchElementException.class);

    // when
    eventController.save(EVENT_1.getId(), EVENT_1, 0);

    // then an exception is expected
  }

  @Test(expected = OptimisticLockException.class)
  public void saveThrowAnExceptionOnConcurrentModification() {
    // given
    final Event event = defaults(CronEvent.builder()).build();
    event.setVersion(42);
    given(eventService.findById(event.getId())).willReturn(event);

    // when
    eventController.save(event.getId(), event, 41);

    // then an exception is expected
  }

  @Test(expected = EntityExistsException.class)
  public void createShouldThrowEntityExistsExceptionIfEventDoesAlreadyExists() {
    // given
    given(eventService.save(any())).willThrow(EntityExistsException.class);

    // when
    eventController.create(EVENT_1);

    // then an exception is expected
  }

  @Test
  public void deleteShouldRemoveExistingEvent() {
    // given
    final UUID eventId = randomUUID();

    // when
    eventController.delete(eventId);

    // then
    verify(eventService).delete(eventId);
  }

  @Test(expected = NoSuchElementException.class)
  public void deleteShouldThrowNoSuchElementExceptionIfEventDoesNotExists() {
    // given
    doThrow(NoSuchElementException.class).when(eventService).delete(any());

    // when
    eventController.delete(randomUUID());

    // then an exception is expected
  }

  @Test
  public void findAllShouldReturnCollectionOfEvents() {
    // given
    given(eventService.findAll()).willReturn(EVENTS);

    // when
    final ResponseEntity<Collection<Event>> response = eventController.findAll();

    // then
    assertThat(response.getBody(), containsInAnyOrder(EVENT_1, EVENT_2, EVENT_3));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void findAllShouldReturnEmptyCollectionWhenNoEventsAreAvailable() {
    // given
    given(eventService.findAll()).willReturn(emptyList());

    // when
    final ResponseEntity<Collection<Event>> response = eventController.findAll();

    // then
    assertThat(response.getBody(), is(empty()));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void findByIdShouldReturnEvent() {
    // given
    given(eventService.findById(EVENT_1.getId())).willReturn(EVENT_1);

    // when
    final ResponseEntity<Event> response = eventController.findById(EVENT_1.getId());

    // then
    assertThat(response.getBody(), is(EVENT_1));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void findByIdShouldThrowNoSuchElementExceptionIfEventDoesNotExists() {
    // given
    given(eventService.findById(EVENT_1.getId())).willThrow(NoSuchElementException.class);

    // when
    eventController.findById(EVENT_1.getId());

    // then an exception is expected
  }

  @Test
  public void fireShouldFireImmediately() throws NoSuchElementException {
    // given
    final UUID eventId = EVENT_1.getId();
    given(eventService.fireEvent(eventId)).willReturn(EVENT_1);

    // when
    final ResponseEntity<Event> response = eventController.fire(eventId);

    // then
    verify(eventService).fireEvent(eventId);
    assertThat(response.getBody(), is(EVENT_1));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void fireShouldThrowNoSuchElementExceptionIfEventDoesNotExists() {
    // given
    final UUID eventId = EVENT_1.getId();
    given(eventService.findById(eventId)).willThrow(NoSuchElementException.class);

    // when
    eventController.findById(eventId);

    // then an exception is expected
  }

  @Test
  public void handleEntityExistsExceptionShouldReturnConflictStatus() {
    // given nothing special

    // when
    final ResponseEntity<String> response = eventController.handleEntityExistsException();

    // then
    assertThat(response.getStatusCode(), is(CONFLICT));
  }

  @Test
  public void handleOptimisticLockExceptionShouldReturnConflictStatus() {
    // given nothing special

    // when
    final ResponseEntity<Event> response = eventController.handleOptimisticLockException(new OptimisticLockException(EVENT_1));

    // then
    assertThat(response.getStatusCode(), is(PRECONDITION_FAILED));
    assertThat(response.getBody(), is(EVENT_1));
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
    return builder.id(randomUUID()).cronExpression("0 0 0 * * *");
  }
}