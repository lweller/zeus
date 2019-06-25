package ch.wellernet.zeus.modules.scenario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static javax.persistence.CascadeType.*;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@NoArgsConstructor(access = PROTECTED)
@JGlobalMap(excluded = {"id", "version", "scenario", "count"})
public class State {

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

  @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "state")
  @NotNull
  private Set<Arc> arcs;

  private int maxCount;

  private int initialCount;

  @JsonProperty(access = READ_ONLY)
  private int count;


  @Builder
  protected State(final UUID id, final String name, final Scenario scenario, final Set<Arc> arcs, final int maxCount,
                  final int initialCount) {
    this.id = id;
    this.name = name;
    this.scenario = scenario;
    this.arcs = arcs == null ? new HashSet<>() : arcs;
    this.arcs.forEach(arc -> arc.setState(this));
    this.maxCount = maxCount <= 0 ? 1 : maxCount;
    this.initialCount = initialCount;
    count = initialCount;
  }
}
