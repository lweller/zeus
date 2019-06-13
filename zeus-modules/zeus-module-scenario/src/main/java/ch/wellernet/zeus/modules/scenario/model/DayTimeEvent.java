package ch.wellernet.zeus.modules.scenario.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

import static ch.wellernet.zeus.modules.scenario.model.SunEventDefinition.OFFICIAL;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class DayTimeEvent extends Event {

  @NotNull
  private SunEvent sunEvent;

  @NotNull
  private SunEventDefinition definition;

  @NotNull
  private Integer shift;

  @Builder
  private DayTimeEvent(@NonNull final UUID id,
                       @Nullable final String name,
                       @Nullable final Set<EventDrivenTransition> transitions,
                       @NonNull final SunEvent sunEvent,
                       @Nullable final SunEventDefinition definition,
                       @Nullable final Integer shift) {
    super(id, name, transitions);
    this.sunEvent = sunEvent;
    this.definition = definition == null ? OFFICIAL : definition;
    this.shift = shift == null ? 0 : shift;
  }

  @Override
  public void dispatch(final Dispatcher dispatcher) {
    dispatcher.execute(this);
  }
}
