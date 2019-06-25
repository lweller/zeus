package ch.wellernet.zeus.modules.scenario.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static javax.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Data
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(of = "id")
@JGlobalMap(excluded = {"id", "version", "enabled"})
public class Scenario {

  @Id
  @NotNull
  @Setter(PRIVATE)
  private UUID id;


  @Version
  private long version;

  @NotNull
  private String name;

  @JsonProperty(access = READ_ONLY)
  private boolean enabled;

  @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "scenario")
  @NotNull
  private Set<State> states;

  @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "scenario")
  @NotNull
  private Set<Transition> transitions;

  @Builder
  protected Scenario(final UUID id, final String name, final Boolean enabled, final Set<State> states,
                     final Set<Transition> transitions) {
    this.id = id;
    this.name = name;
    this.enabled = enabled == null ? true : enabled;
    this.states = states == null ? new HashSet<>() : states;
    this.states.forEach(state -> state.setScenario(this));
    this.transitions = transitions == null ? new HashSet<>() : transitions;
    this.transitions.forEach(transition -> transition.setScenario(this));
  }
}
