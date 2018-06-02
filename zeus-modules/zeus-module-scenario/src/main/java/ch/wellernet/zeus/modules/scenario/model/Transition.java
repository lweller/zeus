package ch.wellernet.zeus.modules.scenario.model;

import static java.util.Collections.emptySet;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
public class Transition {
	protected static final String SEQUENCE_NAME = "SEQ_TRANSITION";

	private static int TEMP_ID;

	private @Id @SequenceGenerator(name = SEQUENCE_NAME) @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id = --TEMP_ID;
	private boolean firingAutomatically;
	private @OneToMany(cascade = ALL, mappedBy = "transition") Set<Arc> arcs = emptySet();
	private @OneToMany(cascade = ALL) Set<Action> actions = emptySet();
	private @Version long version;

	protected Transition(final boolean firingAutomatically, final Set<Arc> arcs, final Set<Action> actions) {
		this.firingAutomatically = firingAutomatically;
		if (arcs != null) {
			this.arcs = arcs;
		}
		if (actions != null) {
			this.actions = actions;
		}
	}
}
