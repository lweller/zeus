package ch.wellernet.zeus.modules.device.service.communication.integrated;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationService;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.DeviceDriver;
import lombok.Value;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableMap;

public class IntegratedCommunicationService implements CommunicationService {

  public static final String NAME = "serivce.communication.integrated";
  private final Map<DeviceCommandKey, DeviceDriver> deviceDriverMapping;

  public IntegratedCommunicationService(final Map<DeviceCommandKey, DeviceDriver> deviceDriverMapping) {
    this.deviceDriverMapping = unmodifiableMap(deviceDriverMapping);

  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Collection<Device> scanDevices(final ControlUnit controlUnit) {
    return unmodifiableCollection(controlUnit.getDevices());
  }

  @Override
  public State sendCommand(final Device device, final Command command, final String data)
      throws UndefinedCommandException {
    return deviceDriverMapping.get(new DeviceCommandKey(device.getId(), command)).execute(command, data);
  }

  @Value
  static class DeviceCommandKey {
    private final UUID deviceId;
    private final Command command;
  }

}
