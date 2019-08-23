package ch.wellernet.zeus.modules.scenario.controller.dto;

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
@JsonDeserialize(builder = FixedRateEventDto.FixedRateDtoBuilder.class)
public class FixedRateEventDto extends EventDto {

  @NotNull
  private Integer initialDelay;

  @NotNull
  private Integer interval;

  @Builder
  public FixedRateEventDto(@NotNull final UUID id, final long version, @NotNull final String name, final Date lastExecution, final Date nextScheduledExecution, @NotNull final Integer initialDelay, @NotNull final Integer interval) {
    super(id, version, name, lastExecution, nextScheduledExecution);
    this.initialDelay = initialDelay;
    this.interval = interval;
  }

  @Override
  public <T> T dispatch(final Dispatcher<T> dispatcher) {
    return dispatcher.execute(this);
  }

  @JsonPOJOBuilder(withPrefix = "")
  static class FixedRateDtoBuilder {
  }
}
