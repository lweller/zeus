package ch.wellernet.zeus.server.model.scenario;

import static ch.wellernet.zeus.server.model.scenario.OutputArc.SEQUENCE_NAME;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@SequenceGenerator(name = SEQUENCE_NAME)
@Data
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
public class OutputArc {
	protected static final String SEQUENCE_NAME = "SEQ_OUTPUT_ARC";

	private static int TEMP_ID;

	private @Id @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id = --TEMP_ID;
	private @ManyToOne(cascade = { PERSIST, DETACH, MERGE, REFRESH }) Transition transition;
	private @ManyToOne(cascade = { PERSIST, DETACH, MERGE, REFRESH }) Place place;
	private int weight = 1;
	private @Version long version;

	@Builder
	protected OutputArc(final Place place, final Transition transition, final int weight) {
		super();
		this.place = place;
		this.transition = transition;
		this.weight = weight;
	}
}
