package ch.wellernet.zeus.modules.device.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
public interface DeviceType {
  State getInitialState();

  Command getMainCommand();

  Set<Command> getSupportedCommands();
}
