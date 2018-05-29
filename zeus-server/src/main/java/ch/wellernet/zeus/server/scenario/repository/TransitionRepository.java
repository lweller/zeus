package ch.wellernet.zeus.server.scenario.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.server.scenario.model.Transition;

@Repository
public interface TransitionRepository extends CrudRepository<Transition, Integer> {
}
