package ch.wellernet.zeus.modules.device.controller.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class TcpControlUnitAddressDto extends ControlUnitAddressDto {

  private final String host;
  private final int port;

  @Builder
  private TcpControlUnitAddressDto(final long version, final String host, final int port) {
    super(version);
    this.host = host;
    this.port = port;
  }

  @Override
  public <T> T dispatch(final Dispatcher<T> dispatcher) {
    return dispatcher.execute(this);
  }
}
