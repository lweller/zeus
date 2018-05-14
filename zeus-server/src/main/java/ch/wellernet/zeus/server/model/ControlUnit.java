package ch.wellernet.zeus.server.model;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
public class ControlUnit {
	private @Setter(PRIVATE) UUID id;
	private @Setter(PRIVATE) @JsonIgnore List<Device> devices;
	private ControlUnitAddress address;
}
