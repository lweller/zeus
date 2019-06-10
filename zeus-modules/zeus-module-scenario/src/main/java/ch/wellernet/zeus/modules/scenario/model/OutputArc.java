package ch.wellernet.zeus.modules.scenario.model;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

import static lombok.AccessLevel.PRIVATE;

@Entity
@NoArgsConstructor(access = PRIVATE)
public class OutputArc extends Arc {

  @Builder
  private OutputArc(final State state, final Transition transition, final int weight) {
    super(state, transition, weight);
  }

  @Override
  public <ReturnValue> ReturnValue dispatch(final Dispatcher<ReturnValue> dispatcher) {
    return dispatcher.execute(this);
  }
}