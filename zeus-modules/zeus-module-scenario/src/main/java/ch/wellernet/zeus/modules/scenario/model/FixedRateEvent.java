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
public class FixedRateEvent extends Event {

	// default interval (1 day)
	private static final int DEFAULT_INTERVAL = 60 * 60 * 24;

	private int initialDelay;
	private int interval;

	@Builder
	private FixedRateEvent(final String name, final Set<EventDrivenTransition> transitions, final Integer initialDelay,
			final Integer interval) {
		super(name, transitions);
		this.initialDelay = initialDelay == null ? 0 : initialDelay;
		this.interval = interval == null ? DEFAULT_INTERVAL : interval;
	}

	@Override
	public void dispatch(final Dispatcher dispatcher) {
		dispatcher.execute(this);
	}
}
