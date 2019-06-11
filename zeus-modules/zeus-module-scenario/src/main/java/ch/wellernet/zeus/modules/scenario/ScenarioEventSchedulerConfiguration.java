package ch.wellernet.zeus.modules.scenario;

import ch.wellernet.zeus.modules.scenario.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@EnableScheduling
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScenarioEventSchedulerConfiguration {

  // injected dependencies
  private final EventService eventService;
  private final PlatformTransactionManager transactionManager;

  void initializeEvents() {
    new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(final @NonNull TransactionStatus status) {
        eventService.scheduleAllExistingEvents();
      }
    });
  }

  @Bean
  public TaskScheduler scenarioEventScheduler() {
    final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setThreadNamePrefix("scenarioEventScheduler");
    taskScheduler.setPoolSize(1);
    return taskScheduler;
  }
}
