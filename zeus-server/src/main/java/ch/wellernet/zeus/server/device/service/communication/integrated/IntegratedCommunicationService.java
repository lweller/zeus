package ch.wellernet.zeus.server.device.service.communication.integrated;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableMap;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import ch.wellernet.zeus.server.device.model.Command;
import ch.wellernet.zeus.server.device.model.ControlUnit;
import ch.wellernet.zeus.server.device.model.Device;
import ch.wellernet.zeus.server.device.model.State;
import ch.wellernet.zeus.server.device.service.communication.CommunicationService;
import ch.wellernet.zeus.server.device.service.communication.integrated.drivers.DeviceDriver;
import ch.wellernet.zeus.server.device.service.communication.integrated.drivers.UndefinedCommandException;
import lombok.Value;

public class IntegratedCommunicationService implements CommunicationService {

	public static final String NAME = "serivce.communication.integrated";

	private final Map<DeviceCommandKey, DeviceDriver> deviceDriverMapping;

	public IntegratedCommunicationService(Map<DeviceCommandKey, DeviceDriver> deviceDriverMapping) {
		this.deviceDriverMapping = unmodifiableMap(deviceDriverMapping);

	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Collection<Device> scanDevices(ControlUnit controlUnit) {
		return unmodifiableCollection(controlUnit.getDevices());
	}

	@Override
	public State sendCommand(Device device, Command command) throws UndefinedCommandException {
		return deviceDriverMapping.get(new DeviceCommandKey(device.getId(), command)).execute(command);
	}

	@Value
	static class DeviceCommandKey {
		private final UUID deviceId;
		private final Command command;
	}

}
