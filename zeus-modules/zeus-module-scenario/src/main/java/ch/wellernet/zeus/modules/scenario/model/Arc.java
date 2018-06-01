package ch.wellernet.zeus.modules.scenario.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
public abstract class Arc {
	protected static final String SEQUENCE_NAME = "SEQ_ARC";

	private static int TEMP_ID;

	private @Id @SequenceGenerator(name = SEQUENCE_NAME) @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id = --TEMP_ID;
	private @ManyToOne(cascade = { PERSIST, DETACH, MERGE, REFRESH }) Place place;
	private @ManyToOne(cascade = { PERSIST, DETACH, MERGE, REFRESH }) Transition transition;
	private int weight = 1;
	private @Version long version;

	protected Arc(final Place place, final Transition transition, final int weight) {
		this.place = place;
		this.transition = transition;
		this.weight = weight;
	}
}
