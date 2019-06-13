package ch.wellernet.zeus.modules.scenario.model;

import lombok.*;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
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

  @NotNull
  private Integer initialDelay;

  @NotNull
  private Integer interval;

  @Builder
  private FixedRateEvent(@NonNull final UUID id,
                         @Nullable final String name,
                         @Nullable final Set<EventDrivenTransition> transitions,
                         @Nullable final Integer initialDelay,
                         @NonNull final Integer interval) {
    super(id, name, transitions);
    this.initialDelay = initialDelay == null ? 0 : initialDelay;
    this.interval = interval;
  }

  @Override
  public void dispatch(final Dispatcher dispatcher) {
    dispatcher.execute(this);
  }
}
