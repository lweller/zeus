package ch.wellernet.zeus.modules.device.controller.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class IntegratedControlUnitAddressDto extends ControlUnitAddressDto {

  @Builder
  private IntegratedControlUnitAddressDto(final long version) {
    super(version);
  }

  @Override
  public <T> T dispatch(final Dispatcher<T> dispatcher) {
    return dispatcher.execute(this);
  }
}

