package ch.wellernet.zeus.server.service.communication.integrated;

import ch.wellernet.zeus.server.model.ControlUnitAddress;

public class IntegratedControlUnitAddress extends ControlUnitAddress {

	public IntegratedControlUnitAddress() {
		super(IntegratedCommunicationService.NAME);
	}
}
