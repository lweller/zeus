package ch.wellernet.zeus.server.scenario.model;

import static lombok.AccessLevel.PRIVATE;

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
	private @OneToMany Set<Place> places;
	private @OneToMany Set<Transition> transitions;
	private @Version long version;

	@Builder
	protected Scenario(final UUID id, final String name, final Set<Place> places, final Set<Transition> transitions) {
		this.id = id;
		this.name = name;
		this.places = places;
		this.transitions = transitions;
	}
}
