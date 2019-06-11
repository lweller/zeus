package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;

import java.util.Collection;

public interface DeviceDriver {

  State execute(Command command, String data) throws UndefinedCommandException;

  Collection<Command> getSupportedCommands();

  void init();
}