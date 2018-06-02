package ch.wellernet.zeus.modules.scenario.model;

import static lombok.AccessLevel.PRIVATE;

import java.util.Set;

import javax.persistence.Entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class CronEvent extends Event {

	private String cronExpression;

	@Builder
	private CronEvent(final String name, final Set<EventDrivenTransition> transitions, final String cronExpression) {
		super(name, transitions);
		this.cronExpression = cronExpression;
	}

	@Override
	public void dispatch(final Dispatcher dispatcher) {
		dispatcher.execute(this);
	}
}
