package ch.wellernet.zeus.modules.device.service.communication;

import ch.wellernet.zeus.modules.device.model.Device;
import lombok.Getter;
import lombok.Setter;

public class CommunicationInterruptedException extends Exception {
  private static final long serialVersionUID = 1L;

  private @Getter @Setter Device device;

  public CommunicationInterruptedException(final String message) {
    super(message);
  }
}
