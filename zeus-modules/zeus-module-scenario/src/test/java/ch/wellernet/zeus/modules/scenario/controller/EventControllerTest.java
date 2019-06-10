package ch.wellernet.zeus.modules.scenario.controller;

import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
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

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(classes = EventController.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class EventControllerTest {

  // test data
  private static final Event EVENT_1 = CronEvent.builder().id(randomUUID()).name("Event 1").build();
  private static final Event EVENT_2 = CronEvent.builder().id(randomUUID()).name("Event 2").build();
  private static final Event EVENT_3 = CronEvent.builder().id(randomUUID()).name("Enevt 3").build();
  private static final List<Event> EVENTS = newArrayList(EVENT_1, EVENT_2, EVENT_3);

  // object under test
  private @Autowired EventController eventController;

  private @MockBean EventService eventService;

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
  public void findAllShouldReturnEmptyCollectionWhenNoeventsAreAvailable() {
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
    given(eventService.findById(EVENT_1.getId())).willReturn(Optional.of(EVENT_1));

    // when
    final ResponseEntity<Event> response = eventController.findById(EVENT_1.getId());

    // then
    assertThat(response.getBody(), is(EVENT_1));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void findByIdShouldThrowNoSuchElementExceptionIfEventDoesNotExists() {
    // given
    given(eventService.findById(EVENT_1.getId())).willReturn(Optional.empty());

    // when
    eventController.findById(EVENT_1.getId());

    // then an exception is expected
  }

  @Test
  public void fireShouldFireImmediately() throws NoSuchElementException, UndefinedCommandException {
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
    given(eventService.findById(eventId)).willReturn(Optional.empty());

    // when
    eventController.findById(eventId);

    // then an exception is expected
  }

  @Test
  public void handleNoSuchElementExceptionShouldReturnNotFoundStatus() {
    // given nothing special

    // when
    final ResponseEntity<String> response = eventController.handleNoSuchElementException();

    // then
    assertThat(response.getStatusCode(), is(NOT_FOUND));
  }
}