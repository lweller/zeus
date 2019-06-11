package ch.wellernet.zeus.modules.device.service.communication;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommunicationServiceRegistry {

  // injected dependencies
  private final ApplicationContext applicationContext;

  public CommunicationService findByName(String name) {
    return applicationContext.getBean(name, CommunicationService.class);
  }
}
