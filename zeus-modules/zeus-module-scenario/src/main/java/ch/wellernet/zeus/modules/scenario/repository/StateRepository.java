package ch.wellernet.zeus.modules.scenario.repository;

import ch.wellernet.zeus.modules.scenario.model.State;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StateRepository extends CrudRepository<State, UUID> {
}
