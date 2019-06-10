package ch.wellernet.zeus.modules.device.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import static javax.persistence.GenerationType.SEQUENCE;
import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(of = "communicationServiceName")
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class ControlUnitAddress {
  protected static final String SEQUENCE_NAME = "SEQ_CONTROL_UNIT_ADDRESS";

  private @Id @SequenceGenerator(name = SEQUENCE_NAME) @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id;
  private @Setter(PRIVATE) String communicationServiceName;
  private @Version long version;

  public ControlUnitAddress(final String communicationServiceName) {
    this.communicationServiceName = communicationServiceName;
  }
}
