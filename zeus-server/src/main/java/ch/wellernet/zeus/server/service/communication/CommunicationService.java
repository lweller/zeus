package ch.wellernet.zeus.server.service.communication;

import java.util.Collection;

import ch.wellernet.zeus.server.model.Command;
import ch.wellernet.zeus.server.model.ControlUnit;
import ch.wellernet.zeus.server.model.Device;
import ch.wellernet.zeus.server.model.State;
import ch.wellernet.zeus.server.service.communication.integrated.drivers.UndefinedCommandException;

public interface CommunicationService {

	public String getName();

	public State sendCommand(Device device, Command command) throws UndefinedCommandException;

	public Collection<Device> scanDevices(ControlUnit controlUnit);
}
