package ch.wellernet.zeus.server.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.server.model.ControlUnit;
import ch.wellernet.zeus.server.model.IntegratedControlUnit;

@Repository
public interface ControlUnitRepository extends CrudRepository<ControlUnit, UUID> {

	@Query("SELECT c FROM ControlUnit c WHERE TYPE(c) = IntegratedControlUnit")
	public IntegratedControlUnit findIntegratedControlUnit();
}
