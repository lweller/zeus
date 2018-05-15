package ch.wellernet.zeus.server.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static lombok.AccessLevel.PRIVATE;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(of = "id")
public class Device {
	private @Id @Setter(PRIVATE) UUID id;
	private @Type(type = "ch.wellernet.zeus.server.model.BuiltInDeviceType") @Setter(PRIVATE) DeviceType type;
	private String name;
	private @ManyToOne(cascade = { PERSIST, MERGE, REFRESH, DETACH }) ControlUnit controlUnit;
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
