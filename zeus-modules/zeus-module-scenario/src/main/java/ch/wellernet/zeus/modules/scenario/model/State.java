package ch.wellernet.zeus.modules.scenario.model;

import static java.util.Collections.emptySet;
import static javax.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor(access = PROTECTED)
public class State {

	private @Id @Setter(PRIVATE) UUID id;
	private String name;
	private @OneToMany(cascade = ALL, mappedBy = "state") Set<Arc> arcs = emptySet();
	private int maxCount = 1;
	private int initialCount = 0;
	private int count;
	private @Version long version;

	@Builder
	protected State(final UUID id, final String name, final Set<Arc> arcs, final int maxCount, final int initialCount) {
		this.id = id;
		this.name = name;
		if (arcs != null) {
			this.arcs = arcs;
		}
		this.maxCount = maxCount;
		this.initialCount = initialCount;
		count = initialCount;
	}
}
