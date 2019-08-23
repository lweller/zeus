package ch.wellernet.zeus.modules.scenario.controller.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Value
@NonFinal
@RequiredArgsConstructor(access = PROTECTED)
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
public abstract class EventDto {

  @NotNull
  private final UUID id;

  private final long version;

  @NotNull
  private final String name;

  private final Date lastExecution;

  private final Date nextScheduledExecution;

  public abstract <T> T dispatch(EventDto.Dispatcher<T> dispatcher);

  public interface Dispatcher<T> {
    default T execute(final CronEventDto cronEventDto) {
      return null;
    }

    default T execute(final DayTimeEventDto event) {
      return null;
    }

    default T execute(final FixedRateEventDto fixedRateEventDto) {
      return null;
    }
  }
}
