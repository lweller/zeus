package ch.wellernet.zeus.server.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.server.model.Device;

@Repository
public interface DeviceRepository extends CrudRepository<Device, UUID> {

	/*
	 * private Map<UUID, Device> devices = new HashMap<>();
	 * 
	 * @Override public Collection<Device> findAll() { return
	 * unmodifiableCollection(devices.values()); }
	 * 
	 * @Override public void save(Device device) { devices.put(device.getId(),
	 * device); }
	 * 
	 * @Override public Device findById(UUID deviceId) { return
	 * devices.get(deviceId); }
	 */
}
