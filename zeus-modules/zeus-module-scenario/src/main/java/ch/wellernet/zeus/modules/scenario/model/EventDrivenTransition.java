package ch.wellernet.zeus.modules.scenario.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.REFRESH;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class EventDrivenTransition extends Transition {
  private @ManyToOne(cascade = {DETACH, REFRESH}) Event event;

  @Builder
  protected EventDrivenTransition(final UUID id, final String name, final Scenario scenario, final Event event,
                                  final Set<Arc> arcs, final Set<Action> actions) {
    super(id, name, scenario, false, arcs, actions);
    this.event = event;
  }
}
