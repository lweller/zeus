package ch.wellernet.zeus.modules.scenario.model;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.Device;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.UUID;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class SendCommandAction extends Action {
  private @ManyToOne(cascade = {PERSIST, DETACH, MERGE, REFRESH}) Device device;
  private Command command;
  private String data;

  @Builder
  private SendCommandAction(final UUID id, final String name, final Device device, final Command command,
                            final String data) {
    super(id, name);
    this.device = device;
    this.command = command;
    this.data = data;
  }

  @Override
  public void dispatch(final Dispatcher dispatcher) {
    dispatcher.execute(this);
  }
}
