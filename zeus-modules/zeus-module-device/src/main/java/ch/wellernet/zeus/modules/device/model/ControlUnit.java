package ch.wellernet.zeus.modules.device.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;
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
  private @Id @Setter(PRIVATE) UUID id;
  private @OneToOne(cascade = ALL) ControlUnitAddress address;
  private @OneToMany(cascade = ALL, mappedBy = "controlUnit") @Setter(PRIVATE) @JsonIgnore List<Device> devices;
  private @Version long versison;

  @Builder
  public ControlUnit(final UUID id, final ControlUnitAddress address, final List<Device> devices) {
    this.id = id;
    this.address = address;
    this.devices = devices;
  }
}
