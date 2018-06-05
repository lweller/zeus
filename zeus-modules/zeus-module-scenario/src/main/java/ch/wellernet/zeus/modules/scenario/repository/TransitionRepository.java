package ch.wellernet.zeus.modules.scenario.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.modules.scenario.model.Transition;

@Repository
public interface TransitionRepository extends CrudRepository<Transition, UUID> {
}
