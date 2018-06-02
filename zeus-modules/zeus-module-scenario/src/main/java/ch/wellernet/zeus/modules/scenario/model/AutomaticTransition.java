package ch.wellernet.zeus.modules.scenario.model;

import static lombok.AccessLevel.PRIVATE;

import java.util.Set;

import javax.persistence.Entity;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PRIVATE)
public class AutomaticTransition extends Transition {

	@Builder
	protected AutomaticTransition(final Set<Arc> arcs, final Set<Action> actions) {
		super(true, arcs, actions);
	}
}
