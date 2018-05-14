package ch.wellernet.zeus.server.model;

import static lombok.AccessLevel.PRIVATE;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(of = "id")
public class Device {
	private @Setter(PRIVATE) UUID id;
	private @Setter(PRIVATE) DeviceType type;
	private String name;
	private ControlUnit controlUnit;
	private State state;

	@Builder
	public Device(UUID id, DeviceType type, String name, ControlUnit controlUnit) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.controlUnit = controlUnit;
		this.state = type.getInitialState();
	}
}
