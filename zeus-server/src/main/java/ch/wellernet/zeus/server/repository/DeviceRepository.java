package ch.wellernet.zeus.server.repository;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.server.model.Device;

@Repository
public class DeviceRepository {

	private Map<UUID, Device> devices = new HashMap<>();

	public Collection<Device> findAll() {
		return unmodifiableCollection(devices.values());
	}

	public void save(Device device) {
		devices.put(device.getId(), device);
	}

	public Device findById(UUID deviceId) {
		return devices.get(deviceId);
	}
}
