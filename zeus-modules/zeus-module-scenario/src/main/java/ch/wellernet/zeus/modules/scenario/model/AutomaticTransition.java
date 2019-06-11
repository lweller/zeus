package ch.wellernet.zeus.modules.scenario.model;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;

@Entity
@NoArgsConstructor(access = PACKAGE)
public class AutomaticTransition extends Transition {

  @Builder
  protected AutomaticTransition(final UUID id, final String name, final Scenario scenario, final Set<Arc> arcs,
                                final Set<Action> actions) {
    super(id, name, scenario, true, arcs, actions);
  }
}
