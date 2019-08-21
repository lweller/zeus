package ch.wellernet.zeus.modules.device.controller.dto;

import ch.wellernet.zeus.modules.device.model.DeviceType;
import ch.wellernet.zeus.modules.device.model.State;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Value
@Builder
@RequiredArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(of = "id")
@JsonDeserialize(builder = DeviceDto.DeviceDtoBuilder.class)
public class DeviceDto {

  private final UUID id;
  private final long version;
  private final String name;
  private final Reference controlUnit;
  private DeviceType type;
  private State state;

  @JsonPOJOBuilder(withPrefix = "")
  public static class DeviceDtoBuilder {
  }
}
