package ch.wellernet.zeus.modules.scenario.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
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
  private CronEvent(@Nonnull final UUID id,
                    @Nonnull final String name,
                    @Nonnull final Set<EventDrivenTransition> transitions,
                    @Nonnull final String cronExpression) {
    super(id, name, transitions);
    this.cronExpression = cronExpression;
  }

  @Override
  public <T> T dispatch(final Dispatcher<T> dispatcher) {
    return dispatcher.execute(this);
  }
}
