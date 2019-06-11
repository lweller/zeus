package ch.wellernet.zeus.modules.scenario.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
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
public abstract class Arc {

  protected static final String SEQUENCE_NAME = "SEQ_ARC";
  private static int TEMP_ID;
  private @Id @SequenceGenerator(name = SEQUENCE_NAME) @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id = --TEMP_ID;
  private @ManyToOne(cascade = {PERSIST, DETACH, MERGE, REFRESH}) State state;
  private @ManyToOne(cascade = {PERSIST, DETACH, MERGE, REFRESH}) Transition transition;
  private int weight;
  private @Version long version;

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
