package ch.wellernet.zeus.modules.device.service.communication;

public class CommunicationInterruptedException extends Exception {
	private static final long serialVersionUID = 1L;

	public CommunicationInterruptedException(final String message) {
		super(message);
	}
}
