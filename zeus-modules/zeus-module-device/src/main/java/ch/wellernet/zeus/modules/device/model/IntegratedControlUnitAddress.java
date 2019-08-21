package ch.wellernet.zeus.modules.device.model;

import ch.wellernet.zeus.modules.device.service.communication.integrated.IntegratedCommunicationService;
import lombok.Builder;

import javax.persistence.Entity;

@Entity
public class IntegratedControlUnitAddress extends ControlUnitAddress {

  @Builder
  private IntegratedControlUnitAddress() {
    super(IntegratedCommunicationService.NAME);
  }

  @Override
  public <T> T dispatch(final Dispatcher<T> dispatcher) {
    return dispatcher.execute(this);
  }
}
