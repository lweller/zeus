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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
public class Transition {

	private @Id @Setter(PRIVATE) UUID id;
	private String name;
	private boolean firingAutomatically;
	private @OneToMany(cascade = ALL, mappedBy = "transition") Set<Arc> arcs = emptySet();
	private @OneToMany(cascade = ALL) Set<Action> actions = emptySet();
	private @Version long version;

	protected Transition(final UUID id, final String name, final boolean firingAutomatically, final Set<Arc> arcs,
			final Set<Action> actions) {
		this.id = id;
		this.name = name;
		this.firingAutomatically = firingAutomatically;
		if (arcs != null) {
			this.arcs = arcs;
		}
		if (actions != null) {
			this.actions = actions;
		}
	}
}
