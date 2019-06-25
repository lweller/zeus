package ch.wellernet.zeus.modules.scenario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.SEQUENCE;
import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
@JsonSubTypes({
    @JsonSubTypes.Type(value = InputArc.class),
    @JsonSubTypes.Type(value = OutputArc.class),
    @JsonSubTypes.Type(value = InhibitionArc.class)
})
@JGlobalMap(excluded = {"id", "version", "state", "transition"})
public abstract class Arc {

  protected static final String SEQUENCE_NAME = "SEQ_ARC";

  private static int TEMP_ID;
  @Version
  long version;
  @Id
  @NotNull
  @SequenceGenerator(name = SEQUENCE_NAME)
  @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME)
  @Setter(PRIVATE)
  private int id = --TEMP_ID;
  @ManyToOne(cascade = {DETACH, REFRESH, PERSIST, MERGE})
  @NotNull
  @JsonIgnore
  private State state;

  @ManyToOne(cascade = {DETACH, REFRESH, PERSIST, MERGE})
  @NotNull
  @JsonIgnore
  private Transition transition;

  private int weight;

  Arc(final State state, final Transition transition, final int weight) {
    this.state = state;
    this.transition = transition;
    this.weight = weight <= 0 ? 1 : weight;
  }

  public abstract <ReturnValue> ReturnValue dispatch(Dispatcher<ReturnValue> dispatcher);

  public interface Dispatcher<ReturnValue> {
    default ReturnValue execute(final InhibitionArc event) {
      return null;
    }

    default ReturnValue execute(final InputArc event) {
      return null;
    }

    default ReturnValue execute(final OutputArc event) {
      return null;
    }
  }
}
