package ch.wellernet.zeus.modules.device.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.util.Optional;
import java.util.UUID;

import static ch.wellernet.zeus.modules.device.model.State.UNKNOWN;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.REFRESH;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(of = "id")
public class Device {
  @Id
  @Setter(PRIVATE)
  private UUID id;

  @Version
  private Long version;

  @Type(type = "ch.wellernet.zeus.modules.device.model.BuiltInDeviceType")
  @Setter(PRIVATE)
  private DeviceType type;

  private String name;

  @ManyToOne(cascade = {REFRESH, DETACH})
  private ControlUnit controlUnit;

  private State state;

  @Builder
  public Device(final UUID id, final DeviceType type, final String name, final ControlUnit controlUnit) {
    setId(id);
    setType(type);
    setName(name);
    setControlUnit(controlUnit);
    setState(type == null ? UNKNOWN : type.getInitialState());
  }

  public void setControlUnit(final ControlUnit controlUnit) {
    Optional.ofNullable(this.controlUnit).map(ControlUnit::getDevices).ifPresent(devices -> devices.remove(this));
    this.controlUnit = controlUnit;
    Optional.ofNullable(this.controlUnit).map(ControlUnit::getDevices).ifPresent(devices -> devices.add(this));
  }
}