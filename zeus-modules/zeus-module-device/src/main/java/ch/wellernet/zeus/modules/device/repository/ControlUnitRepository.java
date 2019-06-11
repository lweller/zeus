package ch.wellernet.zeus.modules.device.repository;

import ch.wellernet.zeus.modules.device.model.ControlUnit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ControlUnitRepository extends CrudRepository<ControlUnit, UUID> {

  @Query("SELECT c FROM ControlUnit c JOIN c.address a WHERE TYPE(a) = IntegratedControlUnitAddress")
  Optional<ControlUnit> findIntegrated();
}
