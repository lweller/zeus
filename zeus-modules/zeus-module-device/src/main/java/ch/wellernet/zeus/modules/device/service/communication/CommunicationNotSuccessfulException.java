package ch.wellernet.zeus.modules.device.service.communication;

import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.model.State;
import lombok.Getter;
import lombok.Setter;

public class CommunicationNotSuccessfulException extends Exception {

	private static final long serialVersionUID = 1L;

	private @Getter final State state;
	private @Getter @Setter Device device;

	public CommunicationNotSuccessfulException(final String message, final State state) {
		super(message);
		this.state = state;
	}
}
