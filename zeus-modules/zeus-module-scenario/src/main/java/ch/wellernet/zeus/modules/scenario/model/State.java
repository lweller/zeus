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

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor(access = PROTECTED)
public class State {
	protected static final String SEQUENCE_NAME = "SEQ_STATE";

	private @Id @SequenceGenerator(name = SEQUENCE_NAME) @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id;
	private @OneToMany(cascade = ALL, mappedBy = "state") Set<Arc> arcs = emptySet();
	private int maxCount = 1;
	private int initialCount = 0;
	private int count;
	private @Version long version;

	@Builder
	protected State(final Set<Arc> arcs, final int maxCount, final int initialCount) {
		if (arcs != null) {
			this.arcs = arcs;
		}
		this.maxCount = maxCount;
		this.initialCount = initialCount;
		count = initialCount;
	}
}
