package ch.wellernet.zeus.modules.device.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

import static ch.wellernet.zeus.modules.device.model.Command.*;
import static ch.wellernet.zeus.modules.device.model.State.OFF;
import static com.google.common.collect.Sets.immutableEnumSet;

@AllArgsConstructor
@Getter
public enum BuiltInDeviceType implements DeviceType {

  GENERIC_SWITCH(1, immutableEnumSet(GET_SWITCH_STATE, SWITCH_ON, SWITCH_ON_W_TIMER, SWITCH_OFF, TOGGLE_SWITCH),
      TOGGLE_SWITCH, OFF);

  private final int code;
  private final Set<Command> supportedCommands;
  private final Command mainCommand;
  private final State initialState;
}
