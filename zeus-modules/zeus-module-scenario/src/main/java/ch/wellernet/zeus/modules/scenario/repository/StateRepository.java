package ch.wellernet.zeus.modules.scenario.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.modules.scenario.model.State;

@Repository
public interface StateRepository extends CrudRepository<State, Integer> {
}
