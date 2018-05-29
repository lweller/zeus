package ch.wellernet.zeus.server.device.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Command {
	RESET(0), REBOOT(1), GET_SWITCH_STATE(10), SWITCH_ON(11), TOGGLE_SWITCH(12), SWITCH_OFF(13);

	private final int code;
}
