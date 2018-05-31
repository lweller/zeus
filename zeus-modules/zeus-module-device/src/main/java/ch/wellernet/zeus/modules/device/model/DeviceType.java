package ch.wellernet.zeus.modules.device.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface DeviceType {
	public State getInitialState();

	public Command getMainCommand();

	public Set<Command> getSupportedCommands();
}
