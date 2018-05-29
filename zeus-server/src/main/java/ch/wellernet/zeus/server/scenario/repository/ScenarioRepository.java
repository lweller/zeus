package ch.wellernet.zeus.server.scenario.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.server.scenario.model.Scenario;

@Repository
public interface ScenarioRepository extends CrudRepository<Scenario, UUID> {
}
