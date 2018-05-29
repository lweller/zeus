package ch.wellernet.zeus.server.scenario.model;

import static java.util.Collections.emptySet;

import java.util.Set;

import javax.persistence.Entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class EventListener extends Transition {
	private String event;

	@Builder(builderMethodName = "eventListenerBuilder")
	protected EventListener(final String event, final Set<OutputArc> outputArcs,
			final Set<InhibitionArc> inhititionArcs) {
		super(emptySet(), outputArcs, inhititionArcs);
		this.event = event;
	}
}
