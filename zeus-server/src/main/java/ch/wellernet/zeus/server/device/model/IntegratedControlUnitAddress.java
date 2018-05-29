package ch.wellernet.zeus.server.device.model;

import javax.persistence.Entity;

import ch.wellernet.zeus.server.device.service.communication.integrated.IntegratedCommunicationService;

@Entity
public class IntegratedControlUnitAddress extends ControlUnitAddress {

	public IntegratedControlUnitAddress() {
		super(IntegratedCommunicationService.NAME);
	}
}
