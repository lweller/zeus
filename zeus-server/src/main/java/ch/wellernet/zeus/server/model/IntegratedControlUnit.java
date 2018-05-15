package ch.wellernet.zeus.server.model;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PRIVATE)
public class IntegratedControlUnit extends ControlUnit {

	public IntegratedControlUnit(UUID id, List<Device> devices) {
		super(id, devices, new IntegratedControlUnitAddress());
	}
}
