package ch.wellernet.zeus.server.device.model;

import static ch.wellernet.zeus.server.device.model.Command.GET_SWITCH_STATE;
import static ch.wellernet.zeus.server.device.model.Command.SWITCH_OFF;
import static ch.wellernet.zeus.server.device.model.Command.SWITCH_ON;
import static ch.wellernet.zeus.server.device.model.Command.TOGGLE_SWITCH;
import static ch.wellernet.zeus.server.device.model.State.OFF;
import static com.google.common.collect.Sets.immutableEnumSet;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BuiltInDeviceType implements DeviceType {
	GENERIC_SWITCH(1, immutableEnumSet(GET_SWITCH_STATE, SWITCH_ON, SWITCH_OFF, TOGGLE_SWITCH), TOGGLE_SWITCH, OFF);

	private final int code;
	private final Set<Command> supportedCommands;
	private final Command mainCommand;
	private final State initialState;
}
