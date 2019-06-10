package ch.wellernet.zeus.modules.device.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.util.UUID;

import static ch.wellernet.zeus.modules.device.model.State.UNKNOWN;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(of = "id")
public class Device {
  private @Id @Setter(PRIVATE) UUID id;
  private @Type(type = "ch.wellernet.zeus.modules.device.model.BuiltInDeviceType") @Setter(PRIVATE) DeviceType type;
  private String name;
  private @ManyToOne(cascade = {PERSIST, MERGE, REFRESH, DETACH}) ControlUnit controlUnit;
  private State state;
  private @Version long version;

  @Builder
  public Device(final UUID id, final DeviceType type, final String name, final ControlUnit controlUnit) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.controlUnit = controlUnit;
    state = type == null ? UNKNOWN : type.getInitialState();
  }
}
