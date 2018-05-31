package ch.wellernet.zeus.modules.device.service.communication.integrated.drivers;

import java.util.Collection;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.State;

public interface DeviceDriver {

	public void init();

	public State execute(Command command) throws UndefinedCommandException;

	public Collection<Command> getSupportedCommands();
}