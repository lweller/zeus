package ch.wellernet.zeus.modules.scenario.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static lombok.AccessLevel.PRIVATE;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class EventDrivenTransition extends Transition {
	private @ManyToOne(cascade = { PERSIST, DETACH, MERGE, REFRESH }) Event event;;

	@Builder
	protected EventDrivenTransition(final UUID id, final String name, final Event event, final Set<Arc> arcs,
			final Set<InhibitionArc> inhititionArcs, final Set<Action> actions) {
		super(id, name, false, arcs, actions);
		this.event = event;
	}
}
