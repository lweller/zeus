package ch.wellernet.zeus.server.repository;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import ch.wellernet.zeus.server.model.ControlUnit;

@Repository
public class ControlUnitRepository {

	private Map<UUID, ControlUnit> controlUnits = new HashMap<>();

	public Collection<ControlUnit> findAll() {
		return unmodifiableCollection(controlUnits.values());
	}

	public void save(ControlUnit controlUnit) {
		controlUnits.put(controlUnit.getId(), controlUnit);
	}

	public ControlUnit findById(UUID controlUnitId) {
		return controlUnits.get(controlUnitId);
	}
}
