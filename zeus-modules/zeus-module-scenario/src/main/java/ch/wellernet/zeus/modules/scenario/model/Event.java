package ch.wellernet.zeus.modules.scenario.model;

import static java.util.Collections.emptySet;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Event {
	public interface Dispatcher {
		public default void execute(final CronEvent event) {
		}

		public default void execute(final Event event) {
		}

		public default void execute(final FixedRateEvent event) {
		}
	}

	protected static final String SEQUENCE_NAME = "SEQ_EVENT";

	private static int TEMP_ID;
	private @Id @SequenceGenerator(name = SEQUENCE_NAME) @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id = --TEMP_ID;
	private String name;

	private @OneToMany(cascade = { PERSIST, DETACH, MERGE,
			REFRESH }, fetch = LAZY, mappedBy = "event") Set<EventDrivenTransition> transitions = emptySet();

	protected Event(final String name, final Set<EventDrivenTransition> transitions) {
		this.name = name;
		if (transitions != null) {
			this.transitions = transitions;
		}
	}

	public abstract void dispatch(Dispatcher dispatcher);
}
