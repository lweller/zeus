package ch.wellernet.zeus.server.model;

import javax.persistence.Entity;

import ch.wellernet.zeus.server.service.communication.integrated.IntegratedCommunicationService;

@Entity
public class IntegratedControlUnitAddress extends ControlUnitAddress {

	public IntegratedControlUnitAddress() {
		super(IntegratedCommunicationService.NAME);
	}
}
