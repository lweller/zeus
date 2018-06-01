package ch.wellernet.zeus.modules.scenario.model;

import static ch.wellernet.zeus.modules.scenario.model.Event.SEQUENCE_NAME;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@SequenceGenerator(name = SEQUENCE_NAME)
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
public abstract class Event {
	protected static final String SEQUENCE_NAME = "SEQ_EVENT";

	private static int TEMP_ID;

	private @Id @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id = --TEMP_ID;
	private @OneToMany(cascade = { PERSIST, DETACH, MERGE,
			REFRESH }, fetch = LAZY) Set<EventDrivenTransition> transitions;

	protected Event(final Set<EventDrivenTransition> transitions) {
		this.transitions = transitions;
	}
}
