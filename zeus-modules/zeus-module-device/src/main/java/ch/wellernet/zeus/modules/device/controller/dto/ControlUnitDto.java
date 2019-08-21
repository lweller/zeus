package ch.wellernet.zeus.modules.device.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
@Builder
@EqualsAndHashCode(of = "id")
@JsonDeserialize(builder = ControlUnitDto.ControlUnitDtoBuilder.class)
public class ControlUnitDto {

  private final UUID id;
  private final long version;
  private final ControlUnitAddressDto address;
  private final Set<DeviceDto> devices;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ControlUnitDtoBuilder {
  }
}
