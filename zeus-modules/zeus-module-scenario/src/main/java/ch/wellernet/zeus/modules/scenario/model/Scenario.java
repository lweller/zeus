package ch.wellernet.zeus.modules.scenario.model;

import static javax.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PRIVATE;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(of = "id")
public class Scenario {

	private @Id @Setter(PRIVATE) UUID id;
	private String name;
	private boolean enabled;
	private @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "scenario") Set<State> states;
	private @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "scenario") Set<Transition> transitions;
	private @Version long version;

	@Builder
	protected Scenario(final UUID id, final String name, final Boolean enabled, final Set<State> states,
			final Set<Transition> transitions) {
		this.id = id;
		this.name = name;
		this.enabled = enabled == null ? true : enabled;
		this.states = states == null ? new HashSet<>() : states;
		this.transitions = transitions == null ? new HashSet<>() : transitions;
	}
}
