package ch.wellernet.zeus.server.model.scenario;

import static ch.wellernet.zeus.server.model.scenario.InputArc.SEQUENCE_NAME;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

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
public class InputArc {
	protected static final String SEQUENCE_NAME = "SEQ_INPUT_ARC";

	private static int TEMP_ID;

	private @Id @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id = --TEMP_ID;
	private @ManyToOne Place place;
	private @ManyToOne Transition transition;
	private int weight = 1;

	@Builder
	protected InputArc(final Place place, final Transition transition, final int weight) {
		this.place = place;
		this.transition = transition;
		this.weight = weight;
	}
}
