package ch.wellernet.zeus.modules.scenario.service;

import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import ch.wellernet.zeus.modules.scenario.model.Event;
import ch.wellernet.zeus.modules.scenario.model.EventDrivenTransition;
import ch.wellernet.zeus.modules.scenario.model.TimerEvent;
import ch.wellernet.zeus.modules.scenario.repository.EventRepository;

@SpringBootTest(classes = EventService.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class EventServiceTest {
	// object under test
	private @SpyBean EventService eventService;

	private @MockBean ScenarioService scenarioService;
	private @MockBean EventRepository eventRepository;

	@Test
	public void fireEventShouldFireAllTransitionsWhenItIsFired() {
		// given
		doNothing().when(scenarioService).fireTransition(any());
		final EventDrivenTransition transition1 = EventDrivenTransition.builder().build();
		final EventDrivenTransition transition2 = EventDrivenTransition.builder().build();
		final Set<EventDrivenTransition> transitions = newHashSet(transition1, transition2);
		final Event event = TimerEvent.builder().transitions(transitions).build();
		given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));

		// when
		eventService.fireEvent(event.getId());

		// then
		verify(scenarioService).fireTransition(transition1);
		verify(scenarioService).fireTransition(transition2);
	}

	@Test(expected = NoSuchElementException.class)
	public void fireEventShouldThrowExceptionWhenEventNotExists() {
		// given
		final int eventId = 42;
		given(eventRepository.findById(eventId)).willReturn(Optional.empty());

		// when
		eventService.fireEvent(eventId);

		// then an exception is expected
	}
}
