package ch.wellernet.zeus.modules.device.service.communication;

public class UndefinedCommandException extends Exception {

  private static final long serialVersionUID = 1L;

  public UndefinedCommandException(String message) {
    super(message);
  }
}
