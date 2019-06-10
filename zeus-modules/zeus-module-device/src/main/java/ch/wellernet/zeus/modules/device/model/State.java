package ch.wellernet.zeus.modules.device.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum State {
  UNKNOWN(-1), OFF(0), ON(1);

  private final int code;
}
