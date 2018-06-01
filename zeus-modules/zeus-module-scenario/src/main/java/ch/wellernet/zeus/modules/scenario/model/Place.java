package ch.wellernet.zeus.modules.scenario.model;

import static java.util.Collections.emptySet;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor(access = PROTECTED)
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class Place {
	protected static final String SEQUENCE_NAME = "SEQ_PLACE";

	private @Id @SequenceGenerator(name = SEQUENCE_NAME) @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id;
	private @OneToMany(cascade = ALL, mappedBy = "place") Set<InputArc> inputArcs = emptySet();
	private @OneToMany(cascade = ALL, mappedBy = "place") Set<OutputArc> outputArcs = emptySet();
	private @OneToMany(cascade = ALL, mappedBy = "place") Set<InhibitionArc> inhititionArcs = emptySet();
	private int maxCount = 1;
	private int initialCount = 0;
	private int count;
	private @Version long version;

	@Builder
	protected Place(final Set<InputArc> inputArcs, final Set<OutputArc> outputArcs,
			final Set<InhibitionArc> inhititionArcs, final int maxCount, final int initialCount) {
		if (inputArcs != null) {
			this.inputArcs = inputArcs;
		}
		if (outputArcs != null) {
			this.outputArcs = outputArcs;
		}
		if (inhititionArcs != null) {
			this.inhititionArcs = inhititionArcs;
		}
		this.maxCount = maxCount;
		this.initialCount = initialCount;
		count = initialCount;
	}
}
