package ch.wellernet.zeus.modules.scenario;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScenarioModuleConfiguration {

  // injected dependencies
  private final ScenarioEventSchedulerConfiguration scenarioEventSchedulerConfiguration;

  public void init() {
    scenarioEventSchedulerConfiguration.initializeEvents();
  }
}
