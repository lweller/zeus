package ch.wellernet.zeus.modules.scenario.model;

import static lombok.AccessLevel.PRIVATE;

import java.util.Set;

import javax.persistence.Entity;

import com.luckycatlabs.sunrisesunset.Zenith;

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
	private Zenith zenith;
	private int offset;

	@Builder
	private DayTimeEvent(final String name, final Set<EventDrivenTransition> transitions, final SunEvent sunEvent,
			final Zenith zenith, final int offset) {
		super(name, transitions);
		this.sunEvent = sunEvent;
		this.zenith = zenith;
		this.offset = offset;
	}

	@Override
	public void dispatch(final Dispatcher dispatcher) {
		dispatcher.execute(this);
	}
}
