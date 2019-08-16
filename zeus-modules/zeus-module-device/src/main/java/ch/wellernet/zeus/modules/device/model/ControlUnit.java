package ch.wellernet.zeus.modules.device.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"devices"})
public class ControlUnit {

  @Id
  @Setter(PRIVATE)
  private UUID id;

  @Version
  private long version;

  @OneToOne(cascade = ALL, orphanRemoval = true)
  private ControlUnitAddress address;

  @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "controlUnit")
  @Setter(PRIVATE)
  private Set<Device> devices;

  @Builder
  public ControlUnit(final UUID id, final ControlUnitAddress address, final Set<Device> devices) {
    setId(id);
    setAddress(address);
    setDevices(Optional.ofNullable(devices).orElse(new HashSet<>()));
  }
}
