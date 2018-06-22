
package ch.wellernet.zeus.modules.device.model;

import javax.persistence.Entity;

import ch.wellernet.zeus.modules.device.service.communication.integrated.IntegratedCommunicationService;

@Entity
public class IntegratedControlUnitAddress extends ControlUnitAddress {

	public IntegratedControlUnitAddress() {
		super(IntegratedCommunicationService.NAME);
	}
}
