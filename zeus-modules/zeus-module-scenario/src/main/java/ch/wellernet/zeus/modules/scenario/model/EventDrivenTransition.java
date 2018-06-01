package ch.wellernet.zeus.modules.scenario.model;

import static lombok.AccessLevel.PRIVATE;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class EventDrivenTransition extends Transition {
	private @OneToMany Event event;;

	@Builder
	protected EventDrivenTransition(final Event event, final Set<InputArc> inputArcs, final Set<OutputArc> outputArcs,
			final Set<InhibitionArc> inhititionArcs) {
		super(inputArcs, outputArcs, inhititionArcs);
		this.event = event;
	}
}
