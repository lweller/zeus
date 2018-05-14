package ch.wellernet.zeus.server.model;

import java.util.Set;

public interface DeviceType {
	public Set<Command> getSupportedCommands();

	public Command getMainCommand();

	public State getInitialState();
}
