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
@JsonDeserialize(builder = CronEventDto.CronEventDtoBuilder.class)
public class CronEventDto extends EventDto {

  @NotNull
  private final String cronExpression;

  @Builder
  public CronEventDto(@NotNull final UUID id, final long version, @NotNull final String name, final Date lastExecution, final Date nextScheduledExecution, @NotNull final String cronExpression) {
    super(id, version, name, lastExecution, nextScheduledExecution);
    this.cronExpression = cronExpression;
  }

  @Override
  public <T> T dispatch(final Dispatcher<T> dispatcher) {
    return dispatcher.execute(this);
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class CronEventDtoBuilder {
  }
}
