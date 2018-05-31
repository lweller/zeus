package ch.wellernet.zeus.modules.device.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.modules.device.model.Device;

@Repository
public interface DeviceRepository extends CrudRepository<Device, UUID> {
}
