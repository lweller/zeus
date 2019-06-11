package ch.wellernet.zeus.modules.device.service.communication;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.model.State;

import java.util.Collection;

public interface CommunicationService {

  @SuppressWarnings("unused")
  String getName();

  @SuppressWarnings("unused")
  Collection<Device> scanDevices(ControlUnit controlUnit);

  State sendCommand(Device device, Command command, String data)
      throws UndefinedCommandException, CommunicationNotSuccessfulException, CommunicationInterruptedException;
}
