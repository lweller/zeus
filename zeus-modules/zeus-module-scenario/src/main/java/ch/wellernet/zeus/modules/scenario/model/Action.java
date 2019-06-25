package ch.wellernet.zeus.modules.scenario.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SendCommandAction.class)
})
@JGlobalMap(excluded = {"id", "version"})
public abstract class Action {

  @Id
  @NotNull
  @Setter(PRIVATE)
  private UUID id;

  @Version
  private long version;

  @NotNull
  private String name;

  Action(final UUID id, final String name) {
    this.id = id;
    this.name = name;
  }

  public abstract void dispatch(final Dispatcher dispatcher);

  public interface Dispatcher {
    default void execute(final SendCommandAction action) {
    }
  }

}
