package ch.wellernet.zeus.modules.scenario.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Version;
import java.util.UUID;

import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Action {

  private @Id @Setter(PRIVATE) UUID id;
  private String name;
  private @Version long version;
  public Action(final UUID id, final String name) {
    this.id = id;
    this.name = name;
  }

  public abstract void dispatch(final Dispatcher dispatcher);

  public interface Dispatcher {
    public default void execute(final Action action) {
    }

    public default void execute(final SendCommandAction action) {
    }
  }

}
