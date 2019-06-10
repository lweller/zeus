package ch.wellernet.zeus.modules.scenario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScenarioModuleConfiguration {

  private @Autowired ScenarioEventSchedulerConfiguration scenatioEventSchedulerConfiguration;

  public void init() {
    scenatioEventSchedulerConfiguration.initializeEvents();
  }
}
