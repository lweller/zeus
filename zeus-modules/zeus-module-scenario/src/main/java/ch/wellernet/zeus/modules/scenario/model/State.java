package ch.wellernet.zeus.modules.scenario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.*;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@NoArgsConstructor(access = PROTECTED)
public class State {

  private @Id @Setter(PRIVATE) UUID id;
  private String name;
  private @ManyToOne(cascade = {PERSIST, MERGE, REFRESH, DETACH}) @JsonIgnore Scenario scenario;
  private @OneToMany(cascade = ALL, mappedBy = "state") @JsonIgnore Set<Arc> arcs;
  private int maxCount;
  private int initialCount;
  private int count;
  private @Version long version;

  @Builder
  protected State(final UUID id, final String name, final Scenario scenario, final Set<Arc> arcs, final int maxCount,
                  final int initialCount) {
    this.id = id;
    this.name = name;
    this.scenario = scenario;
    this.arcs = arcs == null ? new HashSet<>() : arcs;
    this.arcs.forEach(arc -> {
      arc.setState(this);
    });
    this.maxCount = maxCount <= 0 ? 1 : maxCount;
    this.initialCount = initialCount;
    count = initialCount;
  }
}
