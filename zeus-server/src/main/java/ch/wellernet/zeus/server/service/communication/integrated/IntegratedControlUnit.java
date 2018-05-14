package ch.wellernet.zeus.server.service.communication.integrated;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.UUID;

import ch.wellernet.zeus.server.model.ControlUnit;
import ch.wellernet.zeus.server.model.Device;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class IntegratedControlUnit extends ControlUnit {
	IntegratedControlUnit(UUID id, List<Device> devices) {
		super(id, devices, new IntegratedControlUnitAddress());
	}
}
