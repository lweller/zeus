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
public class CronEvent extends Event {

  @NotNull
  private String cronExpression;

  @Builder
  private CronEvent(@NonNull final UUID id,
                    @NonNull final String name,
                    @Nullable final Set<EventDrivenTransition> transitions,
                    @NonNull final String cronExpression) {
    super(id, name, transitions);
    this.cronExpression = cronExpression;
  }

  @Override
  public <T> T dispatch(final Dispatcher<T> dispatcher) {
    return dispatcher.execute(this);
  }
}
