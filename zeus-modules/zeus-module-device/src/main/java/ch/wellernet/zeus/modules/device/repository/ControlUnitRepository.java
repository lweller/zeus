package ch.wellernet.zeus.modules.device.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.modules.device.model.ControlUnit;

@Repository
public interface ControlUnitRepository extends CrudRepository<ControlUnit, UUID> {

	@Query("SELECT c FROM ControlUnit c JOIN c.address a WHERE TYPE(a) = IntegratedControlUnitAddress")
	public Optional<ControlUnit> findIntegrated();
}
