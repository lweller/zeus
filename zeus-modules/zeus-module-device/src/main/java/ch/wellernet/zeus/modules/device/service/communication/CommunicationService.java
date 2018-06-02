package ch.wellernet.zeus.modules.device.service.communication;

import java.util.Collection;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.UndefinedCommandException;

public interface CommunicationService {

	public String getName();

	public State sendCommand(Device device, Command command) throws UndefinedCommandException;

	public Collection<Device> scanDevices(ControlUnit controlUnit);
}