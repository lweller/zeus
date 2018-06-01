package ch.wellernet.zeus.modules.scenario.model;

import static lombok.AccessLevel.PRIVATE;

import javax.persistence.Entity;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PRIVATE)
public class InputArc extends Arc {

	@Builder
	private InputArc(final Place place, final Transition transition, final int weight) {
		super(place, transition, weight);
	}
}
