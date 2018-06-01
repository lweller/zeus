package ch.wellernet.zeus.modules.scenario.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.modules.scenario.model.Event;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {
}
