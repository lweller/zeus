package ch.wellernet.zeus.modules.scenario.service;

import static javax.transaction.Transactional.TxType.MANDATORY;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import java.util.Date;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import ch.wellernet.zeus.modules.scenario.repository.EventRepository;

@Service
@Transactional(MANDATORY)
public class EventService {

	private @Autowired EventRepository eventRepository;
	private @Autowired ScenarioService scenarioService;
	private @Autowired TaskScheduler taskScheduler;

	@Transactional(SUPPORTS)
	public void createEvent() {
		taskScheduler.schedule((Runnable) () -> System.err.println("*********** Event **************"),
				new Date(System.currentTimeMillis() + 30000));
	}

	public void fireEvent(final int eventId) throws NoSuchElementException {
		eventRepository.findById(eventId).get().getTransitions().forEach(scenarioService::fireTransition);
	}
}
