package ch.wellernet.zeus.modules.device.repository;

import ch.wellernet.zeus.modules.device.model.Device;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeviceRepository extends CrudRepository<Device, UUID> {
}
