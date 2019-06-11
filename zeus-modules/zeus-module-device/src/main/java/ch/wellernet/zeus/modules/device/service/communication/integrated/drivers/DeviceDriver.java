package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;

public interface DeviceDriver {

  State execute(Command command, String data) throws UndefinedCommandException, NotImplementedException;

  Collection<Command> getSupportedCommands();

  void init();
}