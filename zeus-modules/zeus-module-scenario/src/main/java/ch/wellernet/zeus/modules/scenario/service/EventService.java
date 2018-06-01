package ch.wellernet.zeus.modules.scenario.service;

import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.wellernet.zeus.modules.scenario.repository.EventRepository;

@Service
@Transactional(MANDATORY)
public class EventService {

	private @Autowired EventRepository eventRepository;
	private @Autowired ScenarioService scenarioService;

	public void fireEvent(final int eventId) throws NoSuchElementException {
		eventRepository.findById(eventId).get().getTransitions().forEach(scenarioService::fireTransition);
	}

}
