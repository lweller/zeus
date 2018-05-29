package ch.wellernet.zeus.server.scenario.model;

import static ch.wellernet.zeus.server.scenario.model.Transition.SEQUENCE_NAME;
import static java.util.Collections.emptySet;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@SequenceGenerator(name = SEQUENCE_NAME)
@Data
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class Transition {
	protected static final String SEQUENCE_NAME = "SEQ_TRANSITION";

	private @Id @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id;
	private @OneToMany(cascade = ALL, mappedBy = "transition") Set<InputArc> inputArcs = emptySet();
	private @OneToMany(cascade = ALL, mappedBy = "transition") Set<OutputArc> outputArcs = emptySet();
	private @OneToMany(cascade = ALL, mappedBy = "transition") Set<InhibitionArc> inhititionArcs = emptySet();
	private @Version long version;

	@Builder(toBuilder = true)
	protected Transition(final Set<InputArc> inputArcs, final Set<OutputArc> outputArcs,
			final Set<InhibitionArc> inhititionArcs) {
		if (inputArcs != null) {
			this.inputArcs = inputArcs;
		}
		if (outputArcs != null) {
			this.outputArcs = outputArcs;
		}
		if (inhititionArcs != null) {
			this.inhititionArcs = inhititionArcs;
		}
	}
}
