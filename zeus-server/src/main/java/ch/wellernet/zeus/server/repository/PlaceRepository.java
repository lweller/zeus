package ch.wellernet.zeus.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.server.model.scenario.Place;

@Repository
public interface PlaceRepository extends CrudRepository<Place, Integer> {
}
