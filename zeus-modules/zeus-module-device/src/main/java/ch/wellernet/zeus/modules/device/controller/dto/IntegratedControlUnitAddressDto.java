package ch.wellernet.zeus.modules.device.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(builder = IntegratedControlUnitAddressDto.IntegratedControlUnitAddressDtoBuilder.class)
public class IntegratedControlUnitAddressDto extends ControlUnitAddressDto {

  @Builder
  private IntegratedControlUnitAddressDto(final long version) {
    super(version);
  }

  @Override
  public <T> T dispatch(final Dispatcher<T> dispatcher) {
    return dispatcher.execute(this);
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class IntegratedControlUnitAddressDtoBuilder {
  }
}

