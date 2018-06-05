package ch.wellernet.zeus.modules.scenario.model;

import static javax.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.util.HashSet;
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
	private @OneToMany(cascade = ALL, mappedBy = "transition") Set<Arc> arcs;
	private @OneToMany(cascade = ALL) Set<Action> actions;
	private @Version long version;

	protected Transition(final UUID id, final String name, final boolean firingAutomatically, final Set<Arc> arcs,
			final Set<Action> actions) {
		this.id = id;
		this.name = name;
		this.firingAutomatically = firingAutomatically;
		this.arcs = arcs == null ? new HashSet<>() : arcs;
		this.actions = actions == null ? new HashSet<>() : actions;
	}
}
