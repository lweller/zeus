package ch.wellernet.zeus.modules.scenario.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class FixedRateEvent extends Event {

  // default interval (1 day)
  private static final int DEFAULT_INTERVAL = 60 * 60 * 24;

  private int initialDelay;
  private int interval;

  @Builder
  private FixedRateEvent(final UUID id, final String name, final Set<EventDrivenTransition> transitions,
                         final Integer initialDelay, final Integer interval) {
    super(id, name, transitions);
    this.initialDelay = initialDelay == null ? 0 : initialDelay;
    this.interval = interval == null ? DEFAULT_INTERVAL : interval;
  }

  @Override
  public void dispatch(final Dispatcher dispatcher) {
    dispatcher.execute(this);
  }
}
