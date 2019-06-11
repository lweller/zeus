package ch.wellernet.zeus.modules.scenario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
public class Transition {

  private @Id @Setter(PRIVATE) UUID id;
  private String name;
  private @ManyToOne(cascade = {PERSIST, MERGE, REFRESH, DETACH}) @JsonIgnore Scenario scenario;
  private @OneToMany(cascade = ALL, mappedBy = "transition") @JsonIgnore Set<Arc> arcs;
  private @OneToMany(cascade = ALL, orphanRemoval = true) Set<Action> actions;
  private @Version long version;

  private @Setter(PRIVATE) @Transient boolean firingAutomatically;

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
