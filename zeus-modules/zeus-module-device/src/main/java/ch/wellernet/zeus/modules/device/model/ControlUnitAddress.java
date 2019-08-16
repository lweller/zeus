package ch.wellernet.zeus.modules.device.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;
import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(of = "id")
public abstract class ControlUnitAddress {

  protected static final String SEQUENCE_NAME = "SEQ_CONTROL_UNIT_ADDRESS";

  @Id
  @SequenceGenerator(name = SEQUENCE_NAME)
  @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME)
  @Setter(PRIVATE)
  private int id;

  @Setter(PRIVATE)
  private String communicationServiceName;

  @Version
  private long version;

  protected ControlUnitAddress(final String communicationServiceName) {
    this.communicationServiceName = communicationServiceName;
  }

  public abstract <T> T dispatch(Dispatcher<T> dispatcher);

  public interface Dispatcher<T> {
    default T execute(final IntegratedControlUnitAddress controlUnitAddress) {
      return null;
    }

    default T execute(final TcpControlUnitAddress controlUnitAddress) {
      return null;
    }
  }
}
