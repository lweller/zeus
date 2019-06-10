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
public class CronEvent extends Event {

  private String cronExpression;

  @Builder
  private CronEvent(final UUID id, final String name, final Set<EventDrivenTransition> transitions,
                    final String cronExpression) {
    super(id, name, transitions);
    this.cronExpression = cronExpression;
  }

  @Override
  public void dispatch(final Dispatcher dispatcher) {
    dispatcher.execute(this);
  }
}
