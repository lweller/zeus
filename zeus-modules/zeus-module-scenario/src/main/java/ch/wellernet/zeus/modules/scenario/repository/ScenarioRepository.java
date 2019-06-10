package ch.wellernet.zeus.modules.scenario.repository;

import ch.wellernet.zeus.modules.scenario.model.Scenario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScenarioRepository extends CrudRepository<Scenario, UUID> {
}
