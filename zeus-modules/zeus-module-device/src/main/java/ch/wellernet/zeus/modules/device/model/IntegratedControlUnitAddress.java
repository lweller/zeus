package ch.wellernet.zeus.modules.device.model;

import ch.wellernet.zeus.modules.device.service.communication.integrated.IntegratedCommunicationService;

import javax.persistence.Entity;

@Entity
public class IntegratedControlUnitAddress extends ControlUnitAddress {

  public IntegratedControlUnitAddress() {
    super(IntegratedCommunicationService.NAME);
  }
}
