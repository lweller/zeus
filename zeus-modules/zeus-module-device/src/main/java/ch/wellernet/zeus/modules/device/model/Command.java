package ch.wellernet.zeus.modules.device.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Command {
  RESET(0), REBOOT(1), GET_SWITCH_STATE(10), SWITCH_ON(11), TOGGLE_SWITCH(12), SWITCH_OFF(13), SWITCH_ON_W_TIMER(14);

  private final int code;
}
