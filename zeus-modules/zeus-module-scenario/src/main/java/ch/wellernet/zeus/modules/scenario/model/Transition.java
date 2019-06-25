package ch.wellernet.zeus.modules.scenario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.*;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AutomaticTransition.class),
    @JsonSubTypes.Type(value = EventDrivenTransition.class)
})
@JGlobalMap(excluded = {"id", "version", "scenario", "firingAutomatically"})
public class Transition {

  @Id
  @NotNull
  @Setter(PRIVATE)
  private UUID id;

  @Version
  private long version;

  @NotNull
  private String name;

  @ManyToOne(cascade = {DETACH, REFRESH, PERSIST, MERGE})
  @NotNull
  @JsonIgnore
  private Scenario scenario;

  @OneToMany(cascade = ALL, mappedBy = "transition")
  @NotNull
  @JsonIgnore
  private Set<Arc> arcs;

  @OneToMany(cascade = ALL, orphanRemoval = true)
  @NotNull
  private Set<Action> actions;

  @Setter(PRIVATE)
  @Transient
  private boolean firingAutomatically;

  Transition(final UUID id, final String name, final Scenario scenario, final boolean firingAutomatically,
             final Set<Arc> arcs, final Set<Action> actions) {
    this.id = id;
    this.name = name;
    this.scenario = scenario;
    this.firingAutomatically = firingAutomatically;
    this.arcs = arcs == null ? new HashSet<>() : arcs;
    this.arcs.forEach(arc -> arc.setTransition(this));
    this.actions = actions == null ? new HashSet<>() : actions;
  }
}
