package ch.wellernet.zeus.server.device.service.communication.integrated.drivers;

import java.util.Collection;

import ch.wellernet.zeus.server.device.model.Command;
import ch.wellernet.zeus.server.device.model.State;

public interface DeviceDriver {

	public void init();

	public State execute(Command command) throws UndefinedCommandException;

	public Collection<Command> getSupportedCommands();
}