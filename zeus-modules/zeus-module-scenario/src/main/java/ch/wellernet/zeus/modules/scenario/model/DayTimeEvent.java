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
public class DayTimeEvent extends Event {

	private SunEvent sunEvent;
	private SunEventMode definition;
	private int shift;

	@Builder
	private DayTimeEvent(final String name, final Set<EventDrivenTransition> transitions, final SunEvent sunEvent,
			final SunEventMode definition, final int shift) {
		super(name, transitions);
		this.sunEvent = sunEvent;
		this.definition = definition;
		this.shift = shift;
	}

	@Override
	public void dispatch(final Dispatcher dispatcher) {
		dispatcher.execute(this);
	}
}
