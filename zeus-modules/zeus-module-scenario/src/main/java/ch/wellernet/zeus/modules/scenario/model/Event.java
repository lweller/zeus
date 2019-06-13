package ch.wellernet.zeus.modules.scenario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@Data
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
public abstract class Event {
  @Id
  @NotNull
  @Setter(PRIVATE)
  private UUID id;

  @Version
  private long version;

  @NotNull
  private String name;

  private Date lastExecution;

  @OneToMany(cascade = {PERSIST, DETACH, MERGE, REFRESH}, fetch = LAZY, mappedBy = "event")
  @JsonIgnore
  private Set<EventDrivenTransition> transitions = new HashSet<>();

  @Transient
  private Date nextScheduledExecution;

  Event(@NonNull final UUID id,
        @Nullable final String name,
        @Nullable final Set<EventDrivenTransition> transitions) {
    this.id = id;
    this.name = name == null ? "New Event" : name;
    this.transitions = transitions == null ? new HashSet<>() : transitions;
    this.transitions.forEach(transition -> transition.setEvent(this));
  }

  public abstract void dispatch(Dispatcher dispatcher);

  public interface Dispatcher {
    default void execute(final CronEvent event) {
    }

    default void execute(final DayTimeEvent event) {
    }

    default void execute(final FixedRateEvent event) {
    }
  }
}
