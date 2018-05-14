package ch.wellernet.zeus.server.service.communication.integrated.drivers;

import java.util.Collection;

import ch.wellernet.zeus.server.model.Command;
import ch.wellernet.zeus.server.model.State;

public interface DeviceDriver {

	public void init();

	public State execute(Command command) throws UndefinedCommandException;

	public Collection<Command> getSupportedCommands();
}