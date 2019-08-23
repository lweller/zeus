package ch.wellernet.zeus.modules.scenario.controller.dto;

import ch.wellernet.zeus.modules.scenario.model.SunEvent;
import ch.wellernet.zeus.modules.scenario.model.SunEventDefinition;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(builder = DayTimeEventDto.DayTimeEventDtoBuilder.class)
public class DayTimeEventDto extends EventDto {

  @NotNull
  private final SunEvent sunEvent;

  @NotNull
  private final SunEventDefinition definition;

  @NotNull
  private Integer shift;

  @Builder
  public DayTimeEventDto(@NotNull final UUID id, final long version, @NotNull final String name, final Date lastExecution, final Date nextScheduledExecution, @NotNull final SunEvent sunEvent, @NotNull final SunEventDefinition definition, @NotNull final Integer shift) {
    super(id, version, name, lastExecution, nextScheduledExecution);
    this.sunEvent = sunEvent;
    this.definition = definition;
    this.shift = shift;
  }

  @Override
  public <T> T dispatch(final Dispatcher<T> dispatcher) {
    return dispatcher.execute(this);
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class DayTimeEventDtoBuilder {
  }
}
