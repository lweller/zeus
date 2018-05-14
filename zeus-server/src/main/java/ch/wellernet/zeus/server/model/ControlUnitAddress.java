package ch.wellernet.zeus.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class ControlUnitAddress {
	private String communicationServiceName;
}
