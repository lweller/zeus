package ch.wellernet.zeus.server.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.server.model.scenario.Scenario;

@Repository
public interface ScenarioRepository extends CrudRepository<Scenario, UUID> {
}
