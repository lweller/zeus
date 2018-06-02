package ch.wellernet.zeus.modules.scenario.model;

import static lombok.AccessLevel.PRIVATE;

import javax.persistence.Entity;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PRIVATE)
public class InputArc extends Arc {

	@Builder
	private InputArc(final State state, final Transition transition, final int weight) {
		super(state, transition, weight);
	}

	@Override
	public <ReturnValue> ReturnValue dispatch(final Dispatcher<ReturnValue> dispatcher) {
		return dispatcher.execute(this);
	}
}
