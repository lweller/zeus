package ch.wellernet.zeus.modules.scenario.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Event {
	public interface Dispatcher {
		public default void execute(final CronEvent event) {
		}

		public default void execute(final DayTimeEvent event) {
		}

		public default void execute(final Event event) {
		}

		public default void execute(final FixedRateEvent event) {
		}
	}

	private @Id @Setter(PRIVATE) UUID id;
	private String name;
	private @OneToMany(cascade = { PERSIST, DETACH, MERGE,
			REFRESH }, fetch = LAZY, mappedBy = "event") Set<EventDrivenTransition> transitions;
	private @Version long version;

	private @Transient Date lastFired;

	protected Event(final UUID id, final String name, final Set<EventDrivenTransition> transitions) {
		this.id = id;
		this.name = name;
		this.transitions = transitions == null ? new HashSet<>() : transitions;
		this.transitions.forEach(transition -> {
			transition.setEvent(this);
		});
	}

	public abstract void dispatch(Dispatcher dispatcher);
}
