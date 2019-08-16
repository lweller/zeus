package ch.wellernet.zeus.modules.device.controller.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

import static lombok.AccessLevel.PROTECTED;

@Value
@NonFinal
@RequiredArgsConstructor(access = PROTECTED)
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
public abstract class ControlUnitAddressDto {

  private final long version;

  public abstract <T> T dispatch(Dispatcher<T> dispatcher);

  public interface Dispatcher<T> {
    default T execute(final IntegratedControlUnitAddressDto controlUnitAddress) {
      return null;
    }

    default T execute(final TcpControlUnitAddressDto controlUnitAddress) {
      return null;
    }
  }
}
