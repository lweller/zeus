package ch.wellernet.zeus.modules.device.model;

import static javax.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "devices" })
public class ControlUnit {
	private @Id @Setter(PRIVATE) UUID id;
	private @OneToOne(cascade = ALL) ControlUnitAddress address;
	private @OneToMany(cascade = ALL, mappedBy = "controlUnit") @Setter(PRIVATE) @JsonIgnore List<Device> devices;
	private @Version long versison;

	@Builder
	public ControlUnit(final UUID id, final ControlUnitAddress address, final List<Device> devices) {
		this.id = id;
		this.address = address;
		this.devices = devices;
	}
}
