package ch.wellernet.zeus.server.service.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class CommunicationServiceRegistry {

	@Autowired
	private ApplicationContext applicationContext;

	public CommunicationService findByName(String name) {
		return applicationContext.getBean(name, CommunicationService.class);
	}
}
