package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers;

import java.util.Collection;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;

public interface DeviceDriver {

	public State execute(Command command, String data) throws UndefinedCommandException;

	public Collection<Command> getSupportedCommands();

	public void init();
}