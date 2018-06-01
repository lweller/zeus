package ch.wellernet.zeus.modules.scenario.model;

import static ch.wellernet.zeus.modules.scenario.model.Transition.SEQUENCE_NAME;
import static java.util.Collections.emptySet;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@SequenceGenerator(name = SEQUENCE_NAME)
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
public class Transition {
	protected static final String SEQUENCE_NAME = "SEQ_TRANSITION";

	private static int TEMP_ID;

	private @Id @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id = --TEMP_ID;
	private @OneToMany(cascade = ALL, mappedBy = "transition") Set<InputArc> inputArcs = emptySet();
	private @OneToMany(cascade = ALL, mappedBy = "transition") Set<OutputArc> outputArcs = emptySet();
	private @OneToMany(cascade = ALL, mappedBy = "transition") Set<InhibitionArc> inhititionArcs = emptySet();
	private @Version long version;

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
