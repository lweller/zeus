package ch.wellernet.zeus.modules.scenario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import ch.wellernet.zeus.modules.scenario.service.EventService;

@Configuration
@EnableScheduling
public class ScenatioEventSchedulerConfiguration {

	private @Autowired EventService eventService;
	private @Autowired PlatformTransactionManager transactionManager;

	public void initializeEvents() {
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(final TransactionStatus status) {
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
